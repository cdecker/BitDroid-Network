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
		content = new byte[getPayloadSize()];
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
