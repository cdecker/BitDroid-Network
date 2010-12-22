package net.bitdroid.network;

import java.io.IOException;
import java.net.InetAddress;

import net.bitdroid.network.wire.LittleEndianInputStream;
import net.bitdroid.network.wire.LittleEndianOutputStream;

public class PeerAddress extends Message {
	private long services;
	private byte reserved[];
	byte[] getReserved() {
		return reserved;
	}

	void setReserved(byte[] reserved) {
		this.reserved = reserved;
	}

	private InetAddress address;
	private int port;
	
	PeerAddress(LittleEndianInputStream in) throws IOException{
		super(in);
	}
	
	public PeerAddress(BitcoinClientSocket clientSocket) {
		super(clientSocket);
	}

	/**
	 * @param services the services to set
	 */
	public void setServices(long services) {
		this.services = services;
	}

	/**
	 * @return the services
	 */
	public long getServices() {
		return services;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(InetAddress address) {
		this.address = address;
	}

	/**
	 * @return the address
	 */
	public InetAddress getAddress() {
		return address;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	@Override
	void read(LittleEndianInputStream in) throws IOException {
		setServices(in.readLong());
		reserved = new byte[12];
		in.read(reserved);
		byte[] b = new byte[4];
		in.read(b);
		setAddress(InetAddress.getByAddress(b));
		// Port uses network byte order, goddamn mix of ordering...
		b = new byte[2];
		in.read(b);
		setPort((b[0] & 0xFF) << 8 | (b[1] & 0xFF));
		//setPort(in.readUnsignedShort());
	}

	@Override
	void toWire(LittleEndianOutputStream leos) throws IOException {
		leos.writeLong(services);
		leos.write(reserved);
		leos.write(address.getAddress());
		// Again: this is big-endian...
		leos.write(new byte[]{(byte)(port >> 8 & 0xFF), (byte)(port & 0xFF)});
	}

}
