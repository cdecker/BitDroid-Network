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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import net.bitdroid.network.Event.EventType;
import net.bitdroid.network.messages.PeerAddress;
import net.bitdroid.network.messages.PingMessage;
import net.bitdroid.network.messages.VerackMessage;
import net.bitdroid.network.messages.VersionMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default driver that automatically responds to certain messages to keep the
 * connection alive.
 * 
 * @author cdecker
 *
 */
public class BitcoinClientDriver implements BitcoinEventListener {
	private BitcoinNetwork network;
	Logger log = LoggerFactory.getLogger(BitcoinClientDriver.class);

	public BitcoinClientDriver(BitcoinNetwork network){
		this.network = network;
	}

	Map<Object, Integer> handshakeState = new HashMap<Object, Integer>(); 

	public void eventReceived(Event event) {
		if(event.getType() == EventType.OUTGOING_CONNECTION_TYPE){
			try {
				VersionMessage version = new VersionMessage();
				PeerAddress p = new PeerAddress();
				p.setAddress(InetAddress.getLocalHost());
				p.setPort(8334);
				version.setMyAddress(p);
				version.setYourAddress(p);
				version.setTimestamp(System.currentTimeMillis());
				network.sendMessage(new Event(event.getOrigin(), version));
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(event.getType() == EventType.VERSION_TYPE){
			// If we got the message out here in userland the protocol version is supported
			// Create a verack and send it back
			VersionMessage hisVersion = (VersionMessage)event.getSubject();
			VerackMessage verack = new VerackMessage();
			VersionMessage version = new VersionMessage();
			version.setMyAddress(hisVersion.getYourAddress());
			version.setYourAddress(hisVersion.getMyAddress());
			version.setTimestamp(System.currentTimeMillis());
			try {
				network.sendMessage(new Event(event.getOrigin(), version));
				network.sendMessage(new Event(event.getOrigin(), verack));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else if(event.getType() == EventType.ADDR_TYPE){
			try {
				// Answer with a ping, just piggybacking it here
				network.sendMessage(new Event(event.getOrigin(), new PingMessage()));
			} catch (IOException e){
log.error("Error sending ping", e);
			}

		}
	}

	public void messageSent(Event event) {}

}
