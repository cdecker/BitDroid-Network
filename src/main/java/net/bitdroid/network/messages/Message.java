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

import net.bitdroid.network.Event.EventType;
import net.bitdroid.network.wire.LittleEndianInputStream;
import net.bitdroid.network.wire.LittleEndianOutputStream;

public abstract class Message {
	public abstract EventType getType();
	
	/**
	 * @return the command
	 */
	public abstract String getCommand();
	private int payloadSize;

	public int getPayloadSize() {
		return payloadSize;
	}
	
	public void setPayloadSize(int size) {
		this.payloadSize = size;
	}
	
	public abstract void read(LittleEndianInputStream in) throws IOException;
	public abstract void toWire(LittleEndianOutputStream leos) throws IOException;

}
