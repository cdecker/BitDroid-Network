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
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author cdecker
 *
 */
public abstract class BitcoinNetwork {
	public static final int PROTOCOL_VERSION = 31700;
	public abstract void sendMessage(Event event) throws IOException;
	protected List<BitcoinEventListener> eventListeners = new LinkedList<BitcoinEventListener>();
	private Logger log = LoggerFactory.getLogger(BitcoinNetwork.class);
	public void addListener(BitcoinEventListener listener){
		this.eventListeners.add(listener);
	}

	public void publishReceivedEvent(Event e){
		log.debug("Publishing received message {}", e);
		for(BitcoinEventListener listener : eventListeners)
			try{
				listener.eventReceived(e);
			}catch(Exception ex){
				log.error("Possible error in a listener publishing incoming event.", ex);
			}
	}

	public void publishSentEvent(Event e){
		log.debug("Publishing sent message {}", e);
		for(BitcoinEventListener listener : eventListeners)
			try{
				listener.messageSent(e);
			}catch(Exception ex){
				log.error("Possible error in a listener publishing outgoing event.", ex);
			}
	}


}
