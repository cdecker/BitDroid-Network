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

package net.bitdroid.network.wire;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestLittleEndianOutputStream {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testWriteUnsignedShort() throws IOException {
		byte[] b = new byte[2];
		int s = 65123;
		LittleEndianOutputStream leos = LittleEndianOutputStream.wrap(b);
		leos.writeUnsignedShort(s);
		assertEquals(s, LittleEndianInputStream.wrap(b).readUnsignedShort());
	}

	@Test
	public final void testWriteBoolean() throws IOException {
		byte b[] = new byte[1];
		LittleEndianOutputStream leos = LittleEndianOutputStream.wrap(b);
		leos.writeBoolean(true);
		assertEquals(true, LittleEndianInputStream.wrap(b).readBoolean());
		leos = LittleEndianOutputStream.wrap(b);
		leos.writeBoolean(false);
		assertEquals(false, LittleEndianInputStream.wrap(b).readBoolean());
	}

	@Test
	public final void testWriteShort() throws IOException {
		byte[] b = new byte[2];
		short s = 25123;
		LittleEndianOutputStream leos = LittleEndianOutputStream.wrap(b);
		leos.writeShort(s);
		assertEquals(s, LittleEndianInputStream.wrap(b).readShort());
		s = -25123;
		leos = LittleEndianOutputStream.wrap(b);
		leos.writeShort(s);
		assertEquals(s, LittleEndianInputStream.wrap(b).readShort());
	}

//	@Test
//	public final void testWriteChar() {
//		fail("Not yet implemented"); // TODO
//	}

	@Test
	public final void testWriteInt() throws IOException {
		byte b[] = new byte[4];
		int s = 1225123;
		LittleEndianOutputStream leos = LittleEndianOutputStream.wrap(b);
		leos.writeInt(s);
		assertEquals(s, LittleEndianInputStream.wrap(b).readInt());
		s = -25123;
		leos = LittleEndianOutputStream.wrap(b);
		leos.writeInt(s);
		assertEquals(s, LittleEndianInputStream.wrap(b).readInt());
	}

	@Test
	public final void testWriteLong() throws IOException {
		byte b[] = new byte[8];
		long s = 4147483647L;
		LittleEndianOutputStream leos = LittleEndianOutputStream.wrap(b);
		leos.writeLong(s);
		assertEquals(s, LittleEndianInputStream.wrap(b).readLong());
		s = -4147483647L;
		leos = LittleEndianOutputStream.wrap(b);
		leos.writeLong(s);
		assertEquals(s, LittleEndianInputStream.wrap(b).readLong());
	}
	
	@Test
	public final void testWriteUnisgnedInt() throws IOException {
		byte b[] = new byte[8];
		long s = 414747L;
		LittleEndianOutputStream leos = LittleEndianOutputStream.wrap(b);
		leos.writeUnsignedInt(s);
		assertEquals(s, LittleEndianInputStream.wrap(b).readUnsignedInt());
	}

	public static void reverse(byte[] b) {
		int left  = 0;          // index of leftmost element
		int right = b.length-1; // index of rightmost element

		while (left < right) {
			// exchange the left and right elements
			byte temp = b[left]; 
			b[left]  = b[right]; 
			b[right] = temp;

			// move the bounds toward the center
			left++;
			right--;
		}
	}//endmethod reverse
}
