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

public abstract class Message {
	/**
	 * @return the command
	 */
	abstract String getCommand();
	private int size;
	protected BitcoinClientSocket clientSocket;
	
	/**
	 * @return the clientSocket
	 */
	public BitcoinClientSocket getClientSocket() {
		return clientSocket;
	}

	Message(LittleEndianInputStream in) throws IOException{
		this.read(in);
	}
	
	public Message(BitcoinClientSocket clientSocket){
		this.clientSocket = clientSocket;
	}
	
	public int getSize() {
		return size;
	}
	void setSize(int size) {
		this.size = size;
	}
	abstract void read(LittleEndianInputStream in) throws IOException;
	abstract void toWire(LittleEndianOutputStream leos) throws IOException;

}
