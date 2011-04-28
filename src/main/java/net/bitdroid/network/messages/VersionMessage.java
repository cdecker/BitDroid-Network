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
import java.net.ProtocolException;

import net.bitdroid.network.BitcoinReactorNetwork;
import net.bitdroid.network.Event.EventType;
import net.bitdroid.network.ProtocolVersion;
import net.bitdroid.network.wire.LittleEndianInputStream;
import net.bitdroid.network.wire.LittleEndianOutputStream;

public class VersionMessage extends Message {
	public EventType getType(){
		return EventType.VERSION_TYPE;
	}

	private long timestamp;
	
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	private long protocolVersion = BitcoinReactorNetwork.PROTOCOL_VERSION;
	
	// Default services for this client 
	private byte[] localServices = new byte[]{1,0,0,0,0,0,0,0};
	private PeerAddress myAddress, yourAddress;
	private long nonce;
	private String clientVersion = ProtocolVersion.CLIENT_NAME;
	private long height;
	
	
	/**
	 * @return the height
	 */
	public final long getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public final void setHeight(long height) {
		this.height = height;
	}

	public long getNonce() {
		return nonce;
	}

	public void setNonce(long nonce) {
		this.nonce = nonce;
	}

	public PeerAddress getYourAddress() {
		return yourAddress;
	}

	public void setYourAddress(PeerAddress yourAddress) {
		this.yourAddress = yourAddress;
	}

	public long getProtocolVersion() {
		return protocolVersion;
	}

	public void setProtocolVersion(long l) {
		this.protocolVersion = l;
	}

	@Override
	public void read(LittleEndianInputStream in) throws IOException {
		// Read the actual version
		setProtocolVersion(in.readUnsignedInt());
		if(getProtocolVersion() < 20900)
			throw new IOException("Unsupported client version.");
		in.read(localServices);
		// We ignore that it's unsigned because the highest bit is not 1
		// for any value until the end of the world...
		timestamp = in.readLong();
		byte b[] = new byte[26];
		in.read(b);
		PeerAddress a = new PeerAddress();
		a.read(LittleEndianInputStream.wrap(b));
		setMyAddress(a);
		in.read(b);
		a = new PeerAddress();
		a.read(LittleEndianInputStream.wrap(b));
		setYourAddress(a);
		setNonce(in.readLong());
		setClientVersion(in.readString());
		setHeight(in.readUnsignedInt());
	}

	public void toWire(LittleEndianOutputStream leos) throws IOException{
		leos.writeUnsignedInt(this.protocolVersion);
		leos.write(localServices);
		leos.writeLong(timestamp);
		myAddress.toWire(leos);
		yourAddress.toWire(leos);
		leos.writeLong(getNonce());
		leos.writeString(getClientVersion());
		leos.writeUnsignedInt(getHeight());
	}

	/**
	 * @param myAddress the myAddress to set
	 */
	public void setMyAddress(PeerAddress myAddress) {
		this.myAddress = myAddress;
	}

	/**
	 * @return the myAddress
	 */
	public PeerAddress getMyAddress() {
		return myAddress;
	}

	/**
	 * @param clientVersion the clientVersion to set
	 */
	public void setClientVersion(String clientVersion) {
		this.clientVersion = clientVersion;
	}

	/**
	 * @return the clientVersion
	 */
	public String getClientVersion() {
		return clientVersion;
	}

	@Override
	public String getCommand() {
		return "version";
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("VersionMessage[proto=");
		sb.append(protocolVersion).append("]");
		return sb.toString();
	}
}
