package net.bitdroid.network;

import java.io.IOException;

import net.bitdroid.network.wire.LittleEndianInputStream;
import net.bitdroid.network.wire.LittleEndianOutputStream;

public abstract class Message {
	private int size;
	protected BitcoinClientSocket clientSocket;
	
	Message(LittleEndianInputStream in) throws IOException{
		this.read(in);
	}
	
	public Message(BitcoinClientSocket clientSocket){
		this.clientSocket = clientSocket;
	}
	
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	abstract void read(LittleEndianInputStream in) throws IOException;
	abstract void toWire(LittleEndianOutputStream leos) throws IOException;

}
