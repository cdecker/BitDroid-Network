package net.bitdroid.network;

import java.io.IOException;

import net.bitdroid.network.wire.LittleEndianInputStream;
import net.bitdroid.network.wire.LittleEndianOutputStream;

public class VersionMessage extends Message {
	public VersionMessage(BitcoinClientSocket clientSocket) {
		super(clientSocket);
	}

	private long timestamp;
	
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	private long protocolVersion;
	private byte[] localServices = new byte[8];
	private PeerAddress myAddress, yourAddress;
	private long nonce;
	
	
	long getNonce() {
		return nonce;
	}

	void setNonce(long nonce) {
		this.nonce = nonce;
	}

	PeerAddress getYourAddress() {
		return yourAddress;
	}

	void setYourAddress(PeerAddress yourAddress) {
		this.yourAddress = yourAddress;
	}

	public long getProtocolVersion() {
		return protocolVersion;
	}

	public void setProtocolVersion(long l) {
		this.protocolVersion = l;
	}

	@Override
	void read(LittleEndianInputStream in) throws IOException {
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
		setMyAddress(new PeerAddress(LittleEndianInputStream.wrap(b)));
		in.read(b);
		setYourAddress(new PeerAddress(LittleEndianInputStream.wrap(b)));
		setNonce(in.readLong());
	}

	public void toWire(LittleEndianOutputStream leos){
		throw new RuntimeException("Not yet implemented");
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
}
