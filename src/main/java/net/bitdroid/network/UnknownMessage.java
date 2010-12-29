/**
 * 
 */
package net.bitdroid.network;

import java.io.IOException;

import net.bitdroid.network.wire.LittleEndianInputStream;
import net.bitdroid.network.wire.LittleEndianOutputStream;

/**
 * @author cdecker
 *
 */
public class UnknownMessage extends Message {
	private byte[] content;
	private String command;
	
	public UnknownMessage(BitcoinClientSocket clientSocket) {
		super(clientSocket);
	}

	/* (non-Javadoc)
	 * @see net.bitdroid.network.Message#getCommand()
	 */
	@Override
	public String getCommand() {
		return command;
	}
	public void setCommand(String command){
		this.command = command;
	}
	
	/* (non-Javadoc)
	 * @see net.bitdroid.network.Message#read(net.bitdroid.network.wire.LittleEndianInputStream)
	 */
	@Override
	void read(LittleEndianInputStream in) throws IOException {
		content = new byte[getSize()];
		in.read(content);
	}

	/* (non-Javadoc)
	 * @see net.bitdroid.network.Message#toWire(net.bitdroid.network.wire.LittleEndianOutputStream)
	 */
	@Override
	void toWire(LittleEndianOutputStream leos) throws IOException {
		throw new RuntimeException("Why would I ever try to send a message I don't know the meaning of?");
	}
	public String toString(){
		return "UnknownMessage[" + getCommand() + "]";
	}
}
