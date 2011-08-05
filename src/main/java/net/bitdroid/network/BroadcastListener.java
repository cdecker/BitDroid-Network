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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import net.bitdroid.network.Event.EventType;
import net.bitdroid.network.messages.BlockMessage;

/**
 * @author cdecker
 *
 */
public class BroadcastListener implements BitcoinEventListener {

	/**
	 * How many events shall we remember?
	 */
	private int memorySize = 1000;

	private HashSet<byte[]> memoryContent = new HashSet<byte[]>();
	private List<byte[]> memoryOrder = new LinkedList<byte[]>();
	/* (non-Javadoc)
	 * @see net.bitdroid.network.BitcoinEventListener#eventReceived(net.bitdroid.network.Event)
	 */
	public void eventReceived(Event e) throws Exception {
		byte[] hash = null;
		if(e.getType() == EventType.BLOCK_TYPE){
			hash = ((BlockMessage)e.getSubject()).getHash();
		}
	}

	/* (non-Javadoc)
	 * @see net.bitdroid.network.BitcoinEventListener#messageSent(net.bitdroid.network.Event)
	 */
	public void messageSent(Event e) throws Exception {
	}

}
