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


/**
 * @author cdecker
 *
 */
public class GetDataMessage extends InventoryMessage {
	public EventType getType(){
		return EventType.GET_DATA_TYPE;
	}

	/* (non-Javadoc)
	 * @see net.bitdroid.network.InventoryMessage#getCommand()
	 */
	@Override
	public String getCommand() {
		return "getdata";
	}
	
	/* (non-Javadoc)
	 * @see net.bitdroid.network.messages.InventoryMessage#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("GetDataMessage[count=");
		sb.append(getItems().size()).append(",");
		for(InventoryItem i : getItems())
			sb.append(i.toString()).append(" ");
		sb.append("]");
		return sb.toString();
	}
}
