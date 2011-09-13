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

package net.bitdroid.network.messages;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import net.bitdroid.network.wire.LittleEndianInputStream;
import net.bitdroid.network.wire.LittleEndianOutputStream;
import net.bitdroid.utils.StringUtils;

public class InventoryMessage extends Message {
	public EventType getType(){
		return EventType.INVENTORY_TYPE;
	}

	public static final int ERROR = 0, MSG_TX = 1, MSG_BLOCK = 2;

	private List<InventoryItem> items = new LinkedList<InventoryMessage.InventoryItem>();

	@Override
	public void read(LittleEndianInputStream in) throws IOException {
		long count = in.readVariableSize();
		for(int i=0; i<count; i++){
			byte[] buffer = new byte[32];
			int t = in.readInt();
			in.read(buffer);
			// We like hashes with leading 0s, and it's the format used by the BBE.
			StringUtils.reverse(buffer);
			items.add(new InventoryItem(t, buffer));
		}
	}

	@Override
	public void toWire(LittleEndianOutputStream leos) throws IOException {
		leos.writeVariableSize(items.size());
		for(InventoryItem i : items){
			leos.writeInt(i.getType());
			byte b[] = i.getHash().clone();
			StringUtils.reverse(b);
			leos.write(b);
		}
	}

	public class InventoryItem {
		private byte[] hash;
		/**
		 * @return the hash
		 */
		public final byte[] getHash() {
			return hash;
		}
		/**
		 * @return the type
		 */
		public final int getType() {
			return type;
		}
		private int type;
		public InventoryItem(int type, byte hash[]){
			this.hash = hash;
			this.type = type;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("InventoryItem[type=");
			switch (type) {
			case 0:
				sb.append("ERROR");
				break;
			case 1:
				sb.append("MSG_TX");
				break;
			case 2:
				sb.append("MSG_BLOCK");
				break;
			default:
				break;
			}

			try {
				sb.append(",hash=").append(StringUtils.getHexString(hash));
			} catch (UnsupportedEncodingException e) {}
			sb.append("]");
			return sb.toString();
		}
	}
	@Override
	public String getCommand() {
		return "inv";
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(List<InventoryItem> items) {
		this.items = items;
	}

	/**
	 * @return the items
	 */
	public List<InventoryItem> getItems() {
		return items;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("InventoryMessage[count=");
		sb.append(items.size()).append(",");
		for(InventoryItem i : items)
			sb.append(i.toString()).append(" ");
		sb.append("]");
		return sb.toString();
	}
}
