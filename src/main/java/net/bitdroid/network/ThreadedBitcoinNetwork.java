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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.bitdroid.network.Event.EventType;
import net.bitdroid.network.messages.Message;
import net.bitdroid.network.wire.LittleEndianInputStream;
import net.bitdroid.network.wire.LittleEndianOutputStream;

/**
 * @deprecated The threaded network is not supported, use {@link BitcoinReactorNetwork} instead.
 * @author cdecker
 *
 */
public class ThreadedBitcoinNetwork extends BitcoinNetwork implements Runnable {
	protected InputStream inputStream;
	protected OutputStream outputStream;
	protected List<BitcoinEventListener> eventListeners = new LinkedList<BitcoinEventListener>();
	protected long nonce;
	protected Socket socket;

	protected SocketState state = new SocketState();

	long getNonce() {
		return nonce;
	}

	public void setNonce(long nonce) {
		this.nonce = nonce;
	}

	/**
	 * C'tor only for unit tests.
	 */
	ThreadedBitcoinNetwork() {
	}

	public ThreadedBitcoinNetwork(Socket socket) throws IOException{
		inputStream = socket.getInputStream();
		outputStream = socket.getOutputStream();
		this.socket = socket;
	}

	public void addListener(BitcoinEventListener listener){
		this.eventListeners.add(listener);
	}

	protected Message readMessage() throws IOException{
		// Read magic
		byte buf[] = new byte[4];
		inputStream.read(buf);
		if(!Arrays.equals(buf, ProtocolVersion.getMagic()))
			throw new IOException("Stream is out of sync. Probably the other client is missbehaving?");
		buf = new byte[12];
		inputStream.read(buf);
		String command = (new String(buf)).trim();

		// Poor mans read unsigned int :-)
		buf = new byte[4];
		inputStream.read(buf);
		int size = (int)((buf[3] & 0xFF) << 24 | (buf[2] & 0xFF) << 16 |
				(buf[1] & 0xFF) << 8 | (buf[0] & 0xFF));

		if(state.currentState != SocketState.HANDSHAKE){
			byte[] checksum = new byte[4];
			inputStream.read(checksum);
			// TODO add switch to check the checksum.
		}

		// Now read the buffer and wrap it into a LittleEndianInputStream
		// This is mainly done to isolate the messages from each other and
		// keep the data stream in sync.
		final byte b[] = new byte[size];
		inputStream.read(b);
		LittleEndianInputStream leis = LittleEndianInputStream.wrap(b);

		// Boilerplate to select the right message to initialize.
		Message message = createMessage(command);

		// Just set the socket to require checksum flag
		if(message.getType() == EventType.VERACK_TYPE)
			state.currentState = SocketState.OPEN;
		if(socket != null)
			message.setOrigin(new PeerInfo(socket.getInetAddress(), socket.getPort()));

		message.setPayloadSize(size);
		// And now each message knows how to read its format:
		message.read(leis);
		return message;

	}

	public void run() {
		addListener(new BitcoinClientDriver(this));
		try{
			while(state.currentState != SocketState.SHUTDOWN && isConnected()){
				// Read the message
				Event mess = readMessage();

				// React to the messages
				// Dispatch to listeners
				for(BitcoinEventListener listener : eventListeners)
					try{
						listener.eventReceived(mess);
					}catch(Throwable t){}
			}
		}catch(IOException ioe){
			state.currentState = SocketState.SHUTDOWN;
		}finally{
			if(state.currentState == SocketState.SHUTDOWN)
				close();
		}
	}

	public boolean isConnected(){
		return socket.isConnected() && state.currentState != SocketState.SHUTDOWN;
	}

	public synchronized void sendMessage(Message event) throws IOException {
		for(BitcoinEventListener listener : eventListeners)
			try{
				listener.messageSent(event);
			}catch(Throwable t){}
		ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
		event.toWire(new LittleEndianOutputStream(outBuffer));
		outputStream.write(ProtocolVersion.getMagic());
		// write the command
		String command = event.getCommand();
		outputStream.write(command.getBytes());
		// Pad with 0s
		for(int i=command.length(); i<12; i++)
			outputStream.write(0);
		// write the size of the packet
		int size = outBuffer.toByteArray().length;
		outputStream.write(new byte[]{(byte)(size & 0xFF),(byte)(size >> 8 & 0xFF),
				(byte)(size >> 16 & 0xFF), (byte)(size >> 24 & 0xFF)});
		if(state.currentState == SocketState.OPEN)
			outputStream.write(calculateChecksum(outBuffer.toByteArray()));

		outputStream.write(outBuffer.toByteArray());
	}

	public void close(){
		try {
			socket.close();
			inputStream.close();
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			state.currentState = SocketState.SHUTDOWN;
		}
	}

	/* (non-Javadoc)
	 * @see net.bitdroid.network.BitcoinNetwork#broadcast(net.bitdroid.network.messages.Message, java.lang.Object)
	 */
	@Override
	public void broadcast(Message message, Object exclude) {
		// TODO Auto-generated method stub
		
	}

}
