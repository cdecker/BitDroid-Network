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

import net.bitdroid.network.Event.EventType;
import net.bitdroid.network.messages.PeerAddress;
import net.bitdroid.network.messages.PingMessage;
import net.bitdroid.network.messages.VerackMessage;
import net.bitdroid.network.messages.VersionMessage;

/**
 * Default driver that automatically responds to certain messages to keep the
 * connection alive.
 * 
 * @author cdecker
 *
 */
public class BitcoinClientDriver implements BitcoinEventListener {
	private BitcoinNetwork network;

	public BitcoinClientDriver(BitcoinNetwork network){
		this.network = network;
	}

	public void eventReceived(Event event) {
		if(event.getType() == EventType.VERSION_TYPE){
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(event.getType() == EventType.CONNECTION_ACCEPTED_TYPE){
			VerackMessage verack = new VerackMessage();
			VersionMessage version = new VersionMessage();
			version.setMyAddress(new PeerAddress());
			version.setYourAddress(new PeerAddress());
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
			} catch (IOException e) {

			}

		}
	}

	public void messageSent(Event event) {}

}
