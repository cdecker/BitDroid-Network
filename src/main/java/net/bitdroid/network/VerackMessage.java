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

import net.bitdroid.network.BitcoinClientSocket.ClientState;
import net.bitdroid.network.wire.LittleEndianInputStream;
import net.bitdroid.network.wire.LittleEndianOutputStream;

public class VerackMessage extends Message {
	protected final String command = "verack";
	public VerackMessage(BitcoinClientSocket clientSocket) {
		super(clientSocket);
		// TODO Auto-generated constructor stub
	}

	@Override
	void read(LittleEndianInputStream in) {
		// This is easy, don't read anything...
		// Just set the socket to require checksum flag
		clientSocket.currentState = ClientState.OPEN;
		
	}

	@Override
	public void toWire(LittleEndianOutputStream leos){
		// This is easy: it's empty :-)
	}

	@Override
	public String getCommand() {
		return "verack";
	}
}
