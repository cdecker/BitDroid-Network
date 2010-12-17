package net.bitdroid.network;

import java.io.IOException;
import java.net.InetAddress;

import net.bitdroid.network.wire.LittleEndianInputStream;
import net.bitdroid.network.wire.LittleEndianOutputStream;

public class PeerAddress extends Message {
	private long services;
	private byte reserved[] = new byte[12];
	private InetAddress address;
	private int port;
	
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
		in.read(reserved);
		byte[] b = new byte[4];
		in.read(b);
		setAddress(InetAddress.getByAddress(b));
		// Port uses network byte order, goddamn mix of ordering...
		setPort((((in.readUnsignedByte() & 0xFF) << 8) | (in.readUnsignedByte() & 0xFF)));
	}

	@Override
	void toWire(LittleEndianOutputStream leos) throws IOException {
		leos.writeLong(services);
		leos.write(reserved);
		leos.write(address.getAddress());
		//leos.writeUnsi
		
		throw new RuntimeException("Not yet implemented");
	}

}
