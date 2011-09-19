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

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

import net.bitdroid.network.messages.AddrMessage;
import net.bitdroid.network.messages.BlockMessage;
import net.bitdroid.network.messages.GetAddrMessage;
import net.bitdroid.network.messages.GetDataMessage;
import net.bitdroid.network.messages.InventoryMessage;
import net.bitdroid.network.messages.Message;
import net.bitdroid.network.messages.Transaction;
import net.bitdroid.network.messages.UnknownMessage;
import net.bitdroid.network.messages.VerackMessage;
import net.bitdroid.network.messages.VersionMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author cdecker
 *
 */
public abstract class BitcoinNetwork {
	public static final int PROTOCOL_VERSION = 31700;
	public abstract void sendMessage(Message event) throws IOException;

	public void sendMessage(PeerInfo destination, Message message) throws IOException{
		message.setOrigin(destination);
		sendMessage(message);
	}
	protected List<BitcoinEventListener> eventListeners = new LinkedList<BitcoinEventListener>();
	private Logger log = LoggerFactory.getLogger(BitcoinNetwork.class);

	/**
	 * Add a listener to be notified upon incoming or outgoing events.
	 *
	 * @param listener
	 */
	public void addListener(BitcoinEventListener listener){
		this.eventListeners.add(listener);
	}

	/**
	 * Publish an incoming event to all registered listeners.
	 *
	 * @param e
	 */
	protected final void publishReceivedEvent(Event e){
		log.debug("Publishing received message {}", e);
		for(BitcoinEventListener listener : eventListeners)
			try{
				listener.eventReceived(e);
			}catch(Exception ex){
				log.error("Possible error in a listener publishing incoming event.", ex);
			}
	}

	/**
	 * Publish events to all registered listeners.
	 *
	 * @param e
	 */
	protected final void publishSentEvent(Event e){
		log.debug("Publishing sent message {}", e);
		for(BitcoinEventListener listener : eventListeners)
			try{
				listener.messageSent(e);
			}catch(Exception ex){
				log.error("Possible error in a listener publishing outgoing event.", ex);
			}
	}

	// Just caching the hash object creation.
	private MessageDigest hasher = null;

	/**
	 * Calculate the 4 byte checksum of the message content.
	 *
	 * @param content to be hashed
	 * @return 4 byte array with the checksum
	 * @throws RuntimeException if we cannot continue (due to missing hash functions)
	 */
	public final byte[] calculateChecksum(byte[] b) throws RuntimeException{
		byte[] res = new byte[4];
		if(hasher == null){
			try {
				hasher = MessageDigest.getInstance("SHA-256");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				throw new RuntimeException("Cannot continue without a SHA-256 implementation.", e);
			}
		}
		hasher.reset();
		byte[] o = hasher.digest(b);
		hasher.reset();
		o = hasher.digest(o);
		for(int i=0; i<4; i++)
			res[i] = o[i];
		return res;
	}

	public class SocketState {
		public final static int HANDSHAKE = 0;
		public final static int OPEN = 1;
		public final static int SHUTDOWN = 2;
		protected int currentState = SocketState.HANDSHAKE;
	}

	/**
	 * This method creates an instance of the message type according to the
	 * given <em>command</em>. The <em>command</em> is the String in the
	 * protocol identifying the type of the message.
	 *
	 * @param command protocol level identity of the message type.
	 * @return an instance of Message according to the type in the protocol.
	 */
	protected final Message createMessage(String command){
		Message message;
		if("version".equalsIgnoreCase(command)){
			message = new VersionMessage();

		}else if("verack".equalsIgnoreCase(command)){
			message = new VerackMessage();

		}else if("inv".equalsIgnoreCase(command)){
			message = new InventoryMessage();

		}else if("addr".equalsIgnoreCase(command)){
			message = new AddrMessage();

		}else if("tx".equalsIgnoreCase(command)){
			message = new Transaction();

		}else if("getdata".equalsIgnoreCase(command)){
			message = new GetDataMessage();

		}else if("getaddr".equalsIgnoreCase(command)){
			message = new GetAddrMessage();

		}else if("block".equalsIgnoreCase(command)){
			message = new BlockMessage();

		}else{
			message = new UnknownMessage();
			((UnknownMessage)message).setCommand(command);
		}

		return message;
	}

	/**
	 * Broadcasting to the network means sending a Message to all connected
	 * peers, except the excluded the source of the broadcast.
	 * 
	 * @param message
	 * @param exclude
	 */
	public abstract void broadcast(Message message, Object exclude);

	/**
	 * Shortcut to broadcast to everybody
	 * @param message
	 */
	public final void broadcast(Message message){
		this.broadcast(message, null);
	}

}
