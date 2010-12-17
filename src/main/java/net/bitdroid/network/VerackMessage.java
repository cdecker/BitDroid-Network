package net.bitdroid.network;

import net.bitdroid.network.wire.LittleEndianInputStream;
import net.bitdroid.network.wire.LittleEndianOutputStream;

public class VerackMessage extends Message {

	public VerackMessage(BitcoinClientSocket clientSocket) {
		super(clientSocket);
		// TODO Auto-generated constructor stub
	}

	@Override
	void read(LittleEndianInputStream in) {
		// This is easy, don't read anything...
		// Just set the socket to require checksum flag
		clientSocket.setChecksumRequired(true);
		
	}

	@Override
	public void toWire(LittleEndianOutputStream leos){
		// This is easy: it's empty :-)
	}
}
