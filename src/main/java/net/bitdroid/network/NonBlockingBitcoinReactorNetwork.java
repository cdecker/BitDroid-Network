/**
 * Copyright 2011 Christian Decker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This file is part the BitDroidNetwork Project.
 */

package net.bitdroid.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import net.bitdroid.network.Event.EventType;
import net.bitdroid.network.messages.AddrMessage;
import net.bitdroid.network.messages.BlockMessage;
import net.bitdroid.network.messages.GetAddrMessage;
import net.bitdroid.network.messages.GetDataMessage;
import net.bitdroid.network.messages.InventoryMessage;
import net.bitdroid.network.messages.Message;
import net.bitdroid.network.messages.PeerAddress;
import net.bitdroid.network.messages.Transaction;
import net.bitdroid.network.messages.UnknownMessage;
import net.bitdroid.network.messages.VerackMessage;
import net.bitdroid.network.messages.VersionMessage;
import net.bitdroid.network.tasks.DeferredTask;
import net.bitdroid.network.tasks.RepeatingDeferredTask;
import net.bitdroid.network.wire.LittleEndianInputStream;
import net.bitdroid.network.wire.LittleEndianOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NonBlockingBitcoinReactorNetwork extends BitcoinNetwork implements Runnable {


	private PriorityQueue<DeferredTask> taskQueue = new PriorityQueue<DeferredTask>();
	private InetAddress hostAddress = InetAddress.getByName("0.0.0.0");
	private int port;

	private Selector selector;
	private ServerSocketChannel serverChannel; 
	private Logger log = LoggerFactory.getLogger(NonBlockingBitcoinReactorNetwork.class);

	// A list of PendingChange instances
	private List<ChangeRequest> pendingChanges = new LinkedList<ChangeRequest>();
	// Pending messages to be sent:
	private Map<SocketChannel, Queue<Event>> pendingMessages = new HashMap<SocketChannel, Queue<Event>>();
	// Tracking the state of the sockets
	private Map<SocketChannel, SocketState> socketStates = new HashMap<SocketChannel, SocketState>();

	public NonBlockingBitcoinReactorNetwork(int port) throws IOException {
		this.port = port;
		this.init();
	}

	public void init() throws IOException{
		log.debug("Starting non-blocking reactor");
		log.info("Listening to {}:{}", hostAddress, port);
		// Create a new selector
		selector = SelectorProvider.provider().openSelector();

		// Create a new non-blocking server socket channel
		this.serverChannel = ServerSocketChannel.open();
		serverChannel.configureBlocking(false);

		// Bind the server socket to the specified address and port
		InetSocketAddress isa = new InetSocketAddress(this.hostAddress, this.port);
		serverChannel.socket().bind(isa);

		// Register the server socket channel, indicating an interest in 
		// accepting new connections
		serverChannel.register(selector, SelectionKey.OP_ACCEPT);

	}

	/**
	 * A map of buffers for messages that are in flight, not yet completely read.
	 */
	protected Map<SocketChannel, IncompleteMessage> incompleteBuffer = new HashMap<SocketChannel, IncompleteMessage>();

	protected Event readMessage(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();

		// For the case of an incomplete read, we create an Incomplete message
		// and then try to fill it. Should there already be an incomplete
		// message we use that one
		IncompleteMessage im = null;
		if(incompleteBuffer.containsKey(socketChannel)){
			im = incompleteBuffer.get(socketChannel);
			incompleteBuffer.remove(socketChannel);
		}else{
			im = new IncompleteMessage();

			// Read magic
			ByteBuffer buf = ByteBuffer.allocate(4);
			socketChannel.read(buf);
			if(!Arrays.equals(buf.array(), ProtocolVersion.getMagic()))
				throw new IOException("Stream is out of sync. Probably the other client is missbehaving?");

			// Read the command
			buf = ByteBuffer.allocate(12);
			socketChannel.read(buf);
			im.command = (new String(buf.array())).trim();

			// Poor mans read unsigned int :-)
			buf = ByteBuffer.allocate(4);
			socketChannel.read(buf);
			byte b[] = buf.array();
			im.size = (int)((b[3] & 0xFF) << 24 | (b[2] & 0xFF) << 16 | 
					(b[1] & 0xFF) << 8 | (b[0] & 0xFF));

			if(socketStates.get(socketChannel).currentState != SocketState.HANDSHAKE){
				im.checksum = ByteBuffer.allocate(4);
				socketChannel.read(im.checksum);
				// TODO add switch to check the checksum.
			}
			im.buffer = ByteBuffer.allocate(im.size);
		}

		// Now attempt to fill the rest of the buffer, should we be unable to
		// fill it completely push it back to the buffers
		socketChannel.read(im.buffer);

		// If we haven't read everything, push it back in the buffer map
		if(im.buffer.position() < im.buffer.capacity()){
			incompleteBuffer.put(socketChannel, im);
			return null;
		}else{
			// We finished reading this message, prepare and process it:

			// We wrap it into a LittleEndianInputStream
			// This is mainly done to isolate the messages from each other and
			// keep the data stream in sync.
			im.buffer.rewind();
			LittleEndianInputStream leis = LittleEndianInputStream.wrap(im.buffer);
			// Boilerplate to select the right message to initialize.
			Message message;
			Event event = new Event();
			event.setOrigin(socketChannel);
			if("version".equalsIgnoreCase(im.command)){
				message = new VersionMessage();

			}else if("verack".equalsIgnoreCase(im.command)){
				message = new VerackMessage();
				// Just set the socket to require checksum flag
				socketStates.get(socketChannel).currentState = SocketState.OPEN;

			}else if("inv".equalsIgnoreCase(im.command)){
				message = new InventoryMessage();

			}else if("addr".equalsIgnoreCase(im.command)){
				message = new AddrMessage();

			}else if("tx".equalsIgnoreCase(im.command)){
				message = new Transaction();

			}else if("getdata".equalsIgnoreCase(im.command)){
				message = new GetDataMessage();

			}else if("getaddr".equalsIgnoreCase(im.command)){
				message = new GetAddrMessage();

			}else if("block".equalsIgnoreCase(im.command)){
				message = new BlockMessage();
			}else{
				message = new UnknownMessage();
				((UnknownMessage)message).setCommand(im.command);
			}

			message.setPayloadSize(im.size);
			// And now each message knows how to read its format:
			message.read(leis);
			event.setSubject(message);
			return event;
		}
	}

	/**
	 * Process requested changes to the sockets and register new interests to
	 * the selector keys.
	 * 
	 * @throws ClosedChannelException
	 */
	private void processChanges() throws ClosedChannelException{
		// Process any pending changes
		synchronized (this.pendingChanges) {
			Iterator<ChangeRequest> changes = this.pendingChanges.iterator();
			while (changes.hasNext()) {
				ChangeRequest change = (ChangeRequest) changes.next();
				switch (change.type) {
				case ChangeRequest.CHANGEOPS:
					SelectionKey key = change.socket.keyFor(selector);
					key.interestOps(change.ops);
				case ChangeRequest.REGISTER:
					change.socket.register(selector, change.ops);
					break;
				}
			}
			this.pendingChanges.clear();
		}
	}

	/**
	 * 
	 */
	public void run() {
		addListener(new BitcoinClientDriver(this));
		while (true) {
			try {
				processChanges();
				long next = executeTasks();
				// Wait for an event one of the registered channels
				selector.select(next);

				// Iterate over the set of keys for which events are available
				Iterator<SelectionKey> selectedKeys = this.selector.selectedKeys().iterator();
				while (selectedKeys.hasNext()) {
					SelectionKey key = (SelectionKey) selectedKeys.next();
					selectedKeys.remove();

					if (!key.isValid())
						continue;

					// Check what event is available and deal with it
					if (key.isConnectable()) {
						try {
							SocketChannel channel =(SocketChannel) key.channel(); 
							channel.finishConnect();
							socketStates.put(channel, new SocketState());
						} catch (IOException e) {
							// Cancel the channel's registration with our selector
							key.cancel();
							continue;
						}
						//key.interestOps(SelectionKey.OP_READ); // By default register interest in reading, this will be overwritten by the below listeners
						Event e = new Event();
						e.setOrigin(key.channel());
						e.setType(EventType.OUTGOING_CONNECTION_TYPE);
						publishReceivedEvent(e);
					} else if (key.isAcceptable()) {
						accept(key);
					} else if (key.isReadable()) {
						try{
							Event m = readMessage(key);
							if(m != null)
								publishReceivedEvent(m);
						}catch(IOException ioe){
							disconnect((SocketChannel)key.channel());
						}
					} else if (key.isWritable()) {
						write(key);
					}
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Execute tasks that are due now. It returns the time the selector is
	 * allowed to sleep until the next task is due.
	 * 
	 * @return milliseconds until the next scheduled task.
	 */
	protected long executeTasks(){
		// So now we execute scheduled tasks
		while(taskQueue.peek() != null &&
				taskQueue.peek().getDelay(TimeUnit.MILLISECONDS) <= 0){
			DeferredTask task = taskQueue.poll();
			try{
				task.execute();
			}catch(Throwable t){
				log.error("Error while executing deferred task", t);
			}
			if(task instanceof RepeatingDeferredTask){
				((RepeatingDeferredTask) task).reschedule();
				this.queueTask(task);
			}
		}
		if(taskQueue.peek() == null)
			return 0;
		else
			return taskQueue.peek().getDelay(TimeUnit.MILLISECONDS);
	}


	/**
	 * Cleanly disconnect a socketChannel and clean out all the state maintained
	 * along with it.
	 * 
	 * @param socketChannel
	 */
	protected void disconnect(SocketChannel socketChannel){
		try {
			socketChannel.close();
			socketChannel.keyFor(this.selector).cancel();
			pendingMessages.remove(socketChannel);
			socketStates.remove(socketChannel);
			incompleteBuffer.remove(socketChannel);
		} catch (IOException e) {
			e.printStackTrace();
		}
		socketStates.remove(socketChannel);
		Event e = new Event();
		e.setOrigin(socketChannel);
		e.setType(EventType.DISCONNECTED_TYPE);
		publishReceivedEvent(e);
	}

	private void write(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();

		synchronized (this.pendingMessages) {
			Queue<Event> queue = this.pendingMessages.get(socketChannel);

			// Write until there's not more data ...
			while (!queue.isEmpty()) {
				//OutputStream outputStream = socketChannel.socket().getOutputStream();
				Event event = queue.poll(); 
				publishSentEvent(event);
				ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
				event.getSubject().toWire(new LittleEndianOutputStream(outBuffer));
				socketChannel.write(ByteBuffer.wrap(ProtocolVersion.getMagic()));
				// write the command
				String command = event.getSubject().getCommand();
				socketChannel.write(ByteBuffer.wrap(command.getBytes()));
				// Pad with 0s
				socketChannel.write(ByteBuffer.allocate(12 - command.length()));
				// write the size of the packet
				int size = outBuffer.toByteArray().length;
				socketChannel.write(ByteBuffer.wrap(new byte[]{(byte)(size & 0xFF),(byte)(size >> 8 & 0xFF),
						(byte)(size >> 16 & 0xFF), (byte)(size >> 24 & 0xFF)}));
				if(socketStates.get(socketChannel).currentState == SocketState.OPEN)
					try{
						socketChannel.write(ByteBuffer.wrap(calculateChecksum(outBuffer.toByteArray())));
					}catch(NoSuchAlgorithmException e){
						log.error("Error initializing hashing algorithm", e);
						this.disconnect(socketChannel);
						return;
					}

					socketChannel.write(ByteBuffer.wrap(outBuffer.toByteArray()));
			}

			if (queue.isEmpty()) {
				// We wrote away all data, so we're no longer interested
				// in writing on this socket. Switch back to waiting for
				// data.
				key.interestOps(SelectionKey.OP_READ);
			}
		}
	} 
	private void accept(SelectionKey key) throws IOException {
		// For an accept to be pending the channel must be a server socket channel.
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

		// Accept the connection and make it non-blocking
		SocketChannel socketChannel = serverSocketChannel.accept();
		/*Socket socket = */socketChannel.socket();
		socketChannel.configureBlocking(false);

		// Register the new SocketChannel with our Selector, indicating
		// we'd like to be notified when there's data waiting to be read
		socketChannel.register(this.selector, SelectionKey.OP_READ);

		socketStates.put(socketChannel, new SocketState());
		Event e = new Event();
		e.setOrigin(socketChannel);
		e.setType(EventType.INCOMING_CONNECTION_TYPE);
		publishReceivedEvent(e);
	}

	/* (non-Javadoc)
	 * @see net.bitdroid.network.BitcoinNetwork#sendMessage(net.bitdroid.network.messages.Event)
	 */
	public void sendMessage(Event event) throws IOException {
		SocketChannel socket = (SocketChannel)event.getOrigin();
		synchronized (pendingChanges) {
			// Indicate we want the interest ops set changed
			pendingChanges.add(new ChangeRequest(socket, ChangeRequest.CHANGEOPS, SelectionKey.OP_WRITE));

			// And queue the data we want written
			synchronized (pendingMessages) {
				Queue<Event> queue = pendingMessages.get(socket);
				if (queue == null) {
					queue = new LinkedList<Event>();
					pendingMessages.put(socket, queue);
				}
				queue.add(event);
			}
		}

		// Finally, wake up our selecting thread so it can make the required changes
		this.selector.wakeup();
	}

	public void connect(PeerAddress a){
		log.debug("Connecting to {}", a);
		try{
			SocketChannel socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(false);
			socketChannel.connect(new InetSocketAddress(a.getAddress(), a.getPort()));
			// Queue a channel registration since the caller is not the 
			// selecting thread. As part of the registration we'll register
			// an interest in connection events. These are raised when a channel
			// is ready to complete connection establishment.
			synchronized(this.pendingChanges) {
				this.pendingChanges.add(new ChangeRequest(socketChannel, ChangeRequest.REGISTER, SelectionKey.OP_CONNECT));
			}
			selector.wakeup();
		}catch(IOException ioe){
			log.error("IOException while connecting", ioe);
		}
	}

	private MessageDigest hasher; 

	byte[] calculateChecksum(byte[] b) throws NoSuchAlgorithmException {
		byte[] res = new byte[4];
		if(hasher == null)
			hasher = MessageDigest.getInstance("SHA-256");
		hasher.reset();
		byte[] o = hasher.digest(b);
		hasher.reset();
		o = hasher.digest(o);
		for(int i=0; i<4; i++)
			res[i] = o[i];
		return res;
	}

	/**
	 * Enqueue a new task to be run by the reactor.
	 * @param task
	 */
	public void queueTask(DeferredTask task){
		taskQueue.add(task);
		// If we scheduled a new next task we have to artificially wake up the
		// selector
		if(task.getDelay(TimeUnit.MILLISECONDS) < taskQueue.peek().getDelay(TimeUnit.MILLISECONDS))
			this.selector.wakeup();
	}

	public class ChangeRequest {
		public static final int REGISTER = 1;
		public static final int CHANGEOPS = 2;

		public SocketChannel socket;
		public int type;
		public int ops;

		public ChangeRequest(SocketChannel socket, int type, int ops) {
			this.socket = socket;
			this.type = type;
			this.ops = ops;
		}
	}

	public class SocketState {
		public final static int HANDSHAKE = 0;
		public final static int OPEN = 1;
		public final static int SHUTDOWN = 2;
		protected int currentState = SocketState.HANDSHAKE;
	}

	public class IncompleteMessage {
		ByteBuffer buffer;
		String command;
		ByteBuffer checksum;
		int size;
	}
}