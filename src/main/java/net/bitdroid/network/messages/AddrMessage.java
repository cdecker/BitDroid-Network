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
import java.util.LinkedList;
import java.util.List;

import net.bitdroid.network.Event.EventType;
import net.bitdroid.network.wire.LittleEndianInputStream;
import net.bitdroid.network.wire.LittleEndianOutputStream;

/**
 * @author cdecker
 *
 */
public class AddrMessage extends Message {

	public EventType getType(){
		return EventType.ADDR_TYPE;
	}

	/* (non-Javadoc)
	 * @see net.bitdroid.network.Message#getCommand()
	 */
	@Override
	public String getCommand() {
		return "addr";
	}

	private List<PeerAddress> addresses = new LinkedList<PeerAddress>();

	/**
	 * @return the addresses
	 */
	public List<PeerAddress> getAddresses() {
		return addresses;
	}

	/* (non-Javadoc)
	 * @see net.bitdroid.network.Message#read(net.bitdroid.network.wire.LittleEndianInputStream)
	 */
	@Override
	public void read(LittleEndianInputStream in) throws IOException {
		// Read the variable length:
		long count = in.readVariableSize();
		for(long i=0; i<count; i++){
			int timestamp = in.readInt();
			PeerAddress peer = new PeerAddress();
			peer.setLastSeen(timestamp);
			peer.read(in);
			addresses.add(peer);
		}
	}

	/* (non-Javadoc)
	 * @see net.bitdroid.network.Message#toWire(net.bitdroid.network.wire.LittleEndianOutputStream)
	 */
	@Override
	public void toWire(LittleEndianOutputStream leos) throws IOException {
		leos.writeVariableSize(addresses.size());
		for(PeerAddress p : addresses){
			leos.writeInt(p.getLastSeen());
			p.toWire(leos);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("AddrMessage[");
		sb.append("addresses=").append(addresses.size());
//		sb.append(" ");
//		for(PeerAddress a : addresses)
//			sb.append(a.toString()).append(" ");
		sb.append("]");
		return sb.toString();
	}
}
