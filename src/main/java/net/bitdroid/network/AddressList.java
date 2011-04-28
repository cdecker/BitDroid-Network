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

import java.util.Collection;
import java.util.PriorityQueue;

import net.bitdroid.network.messages.PeerAddress;

/**
 * @author cdecker
 *
 */
public class AddressList {
	private PriorityQueue<PeerAddress> addresses = new PriorityQueue<PeerAddress>();

	/**
	 *
	 */
	public AddressList(int timeout) {
		// TODO Auto-generated constructor stub
	}

	public void addAll(Collection<PeerAddress> addresses){
		this.addresses.addAll(addresses);
	}

	public void invalidate(PeerAddress a){
		addresses.remove(a);
	}
}
