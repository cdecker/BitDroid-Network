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

import net.bitdroid.network.wire.LittleEndianInputStream;
import net.bitdroid.network.wire.LittleEndianOutputStream;

/**
 * This class simply adds empty implementations of the read and toWire methods.
 * Mainly to refrain from having to state it all over again in the various
 * classes.
 * 
 * @author cdecker
 * 
 */
public abstract class EmptyMessage extends Message{
	
	/* (non-Javadoc)
	 * @see net.bitdroid.network.messages.Message#read(net.bitdroid.network.wire.LittleEndianInputStream)
	 */
	@Override
	public void read(LittleEndianInputStream in) throws IOException {
		// It's empty, nothing to do here	
	}
	
	/* (non-Javadoc)
	 * @see net.bitdroid.network.messages.Message#toWire(net.bitdroid.network.wire.LittleEndianOutputStream)
	 */
	@Override
	public void toWire(LittleEndianOutputStream leos) throws IOException {
		// Nothing here either :-)	
	}
	
}
