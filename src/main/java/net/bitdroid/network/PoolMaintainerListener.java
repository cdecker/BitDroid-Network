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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import net.bitdroid.network.Event.EventType;
import net.bitdroid.network.messages.AddrMessage;
import net.bitdroid.network.messages.GetAddrMessage;
import net.bitdroid.network.messages.PeerAddress;

/**
 * @author cdecker
 *
 */
public class PoolMaintainerListener implements BitcoinEventListener {
	private Set<PeerAddress> addresses = new LinkedHashSet<PeerAddress>();
	private Set<PeerAddress> connectedAddresses = new LinkedHashSet<PeerAddress>();
	private int connected = 0;
	private int maxConnected = 50;
	private NonBlockingBitcoinReactorNetwork network;
	private long lastAttempt = 0;

	public PoolMaintainerListener(NonBlockingBitcoinReactorNetwork network){
		this.network = network;
	}

	/* (non-Javadoc)
	 * @see net.bitdroid.network.BitcoinEventListener#eventReceived(net.bitdroid.network.Event)
	 */
	public synchronized void eventReceived(Event e) {
		if(e.getType() == EventType.INCOMING_CONNECTION_TYPE){
			connected++;
		}else if(e.getType() == EventType.DISCONNECTED_TYPE){
			connected--;
		}else if(e.getSubject() instanceof GetAddrMessage){
			Event event = new Event(e.getOrigin(), new GetAddrMessage());
			try {
				network.sendMessage(event);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}else if(e.getSubject() instanceof AddrMessage){
			AddrMessage am = (AddrMessage)e.getSubject();
			addresses.addAll(am.getAddresses());
			Iterator<PeerAddress> i = addresses.iterator();
			while(addresses.size() > 1000){
				i.next();
				i.remove();
			}
		}
		
		if(connected < maxConnected && !connectedAddresses.containsAll(addresses) && lastAttempt < System.currentTimeMillis() - 10000){
			lastAttempt = System.currentTimeMillis();
			// Attempt a new connection
			Set<PeerAddress> unconnected = new HashSet<PeerAddress>();
			unconnected.addAll(addresses);
			unconnected.removeAll(connectedAddresses);
			PeerAddress a = unconnected.iterator().next();
			network.connect(a);
			connectedAddresses.add(a);
		}
	}

	/* (non-Javadoc)
	 * @see net.bitdroid.network.BitcoinEventListener#messageSent(net.bitdroid.network.Event)
	 */
	public void messageSent(Event e) {
		// TODO Auto-generated method stub

	}

}
