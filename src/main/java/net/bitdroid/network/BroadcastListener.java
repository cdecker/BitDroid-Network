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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import net.bitdroid.network.Event.EventType;
import net.bitdroid.network.messages.BlockMessage;
import net.bitdroid.network.messages.GetDataMessage;
import net.bitdroid.network.messages.InventoryMessage;
import net.bitdroid.network.messages.InventoryMessage.InventoryItem;
import net.bitdroid.network.messages.Message;
import net.bitdroid.network.messages.Transaction;
import net.bitdroid.utils.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author cdecker
 *
 */
public class BroadcastListener implements BitcoinEventListener {
	private static Logger log = LoggerFactory.getLogger(BroadcastListener.class);
	/**
	 * 
	 */
	public BroadcastListener(BitcoinNetwork network) {
		this.network = network;
	}

	private BitcoinNetwork network = null;
	/**
	 * How many events shall we remember?
	 */
	private int memorySize = 1000;

	private HashMap<String, Message> memoryContent = new HashMap<String, Message>();
	private Queue<String> memoryOrder = new LinkedList<String>();
	private long lastBroadcast = System.currentTimeMillis();
	private InventoryMessage delayedInventoryMessage = new InventoryMessage();
	private static long MININMUM_DELAY_MILLI = 15*1000;
	/* (non-Javadoc)
	 * @see net.bitdroid.network.BitcoinEventListener#eventReceived(net.bitdroid.network.Event)
	 */
	public void eventReceived(Event e) throws Exception {
		if(e.getType() == EventType.GET_DATA_TYPE){
			for(InventoryItem ii : ((GetDataMessage)e).getItems()){
				Message m = memoryContent.get(new String(ii.getHash()));
				if(m != null){
					log.debug("Peer {} asked for {}, sending item back", e.getOrigin(), StringUtils.getHexString(ii.getHash()));
					network.sendMessage(e.getOrigin(), m);
				}
				if(m != null)
					network.sendMessage(e.getOrigin(), m);
			}
			return;
		}

		if(e instanceof InventoryMessage){
			GetDataMessage gdm = new GetDataMessage();
			for(InventoryItem ii : ((InventoryMessage)e).getItems()){
				String h = new String(ii.getHash());
				if(!memoryContent.containsKey(h)){
					gdm.getItems().add(ii);
					// Insert in order to avoid duplicate requests.
					memoryContent.put(h, null);
				}
			}
			if(!gdm.getItems().isEmpty()){
				log.debug("Asking {} for Inventory items {}", new Object[]{e.getOrigin(), gdm.getItems()});
				network.sendMessage(e.getOrigin(), gdm);
			}
			return;
		}

		byte[] hash = null;
		int type = 0;
		if(e instanceof BlockMessage){
			hash = ((BlockMessage)e).getHash();
			type = InventoryMessage.MSG_BLOCK;
		}else if(e instanceof Transaction){
			hash = ((Transaction)e).getHash();
			type = InventoryMessage.MSG_TX;
		}
		if(hash == null)
			return;
		StringUtils.reverse(hash);
		log.debug("Got Inventory item {} from {}" , new Object[]{StringUtils.getHexString(hash), e.getOrigin()});
		
		if(!memoryContent.containsKey(new String(hash))){
			memoryOrder.add(new String(hash));
			memoryContent.put(new String(hash), (Message)e);

			// Remove an element if we are bigger than the maximum size.
			if(memoryOrder.size() > memorySize)
				memoryContent.remove(memoryOrder.poll());
			// now that we have the Inventory Item for sure, broadcast an announcement:
			delayedInventoryMessage.getItems().add(delayedInventoryMessage.new InventoryItem(type, hash));
			if(System.currentTimeMillis() > lastBroadcast + MININMUM_DELAY_MILLI){
				network.broadcast(delayedInventoryMessage, e.getOrigin());
				lastBroadcast = System.currentTimeMillis();
				delayedInventoryMessage = new InventoryMessage();
			}
		}
	}

	/* (non-Javadoc)
	 * @see net.bitdroid.network.BitcoinEventListener#messageSent(net.bitdroid.network.Event)
	 */
	public void messageSent(Event e) throws Exception {
	}
}
