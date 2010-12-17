/**
 * 
 */
package net.bitdroid.network;

import static org.junit.Assert.*;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

import net.bitdroid.network.wire.LittleEndianInputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author cdecker
 *
 */
public class TestLittleEndianInputStream {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link net.bitdroid.network.wire.LittleEndianInputStream#readUnsignedShort()}.
	 * @throws IOException 
	 */
	@Test
	public void testReadUnsignedShort() throws IOException {
		byte b[] = new byte[]{(byte) 0xD4, (byte)0x7B};
		LittleEndianInputStream leis = LittleEndianInputStream.wrap(b);
		assertEquals(31700, leis.readUnsignedShort());
	}

	/**
	 * Test method for {@link net.bitdroid.network.wire.LittleEndianInputStream#readUnsignedInt()}.
	 * @throws IOException 
	 */
	@Test
	public void testReadUnsignedInt() throws IOException {
		byte b[] = new byte[]{(byte) 0xD4, (byte)0x7B, (byte)0x00, (byte)0x00};
		LittleEndianInputStream leis = LittleEndianInputStream.wrap(b);
		assertEquals(31700, leis.readUnsignedInt());
	}

	/**
	 * Test method for {@link net.bitdroid.network.wire.LittleEndianInputStream#readUnsignedLong()}.
	 * @throws IOException 
	 */
	@Test
	public void testReadUnsignedLong() throws IOException {
		BigInteger c = new BigInteger("1311768467463790320");
		byte b[] = c.toByteArray();
		reverse(b);
		BigInteger r = LittleEndianInputStream.wrap(b).readUnsignedLong(); 
		assertEquals(c, r);
	}

	/**
	 * Test method for {@link net.bitdroid.network.wire.LittleEndianInputStream#readBoolean()}.
	 */
	//	@Test
	//	public void testReadBoolean() {
	//		fail("Not yet implemented"); // TODO
	//	}

	/**
	 * Test method for {@link net.bitdroid.network.wire.LittleEndianInputStream#readByte(int)}.
	 */
	//	@Test
	//	public void testReadByte() {
	//		fail("Not yet implemented"); // TODO
	//	}

	/**
	 * Test method for {@link net.bitdroid.network.wire.LittleEndianInputStream#readUnsignedByte()}.
	 */
	//	@Test
	//	public void testReadUnsignedByte() {
	//		fail("Not yet implemented"); // TODO
	//	}

	/**
	 * Test method for {@link net.bitdroid.network.wire.LittleEndianInputStream#readShort()}.
	 * @throws IOException 
	 */
	@Test
	public void testReadShort() throws IOException {
		short value = 12345; // Just a random number
		byte b[] = new byte[]{(byte)((value >> 8) & 0xFF),(byte)(value & 0xFF)};
		reverse(b);
		assertEquals(value, LittleEndianInputStream.wrap(b).readShort());
		value = -12345;
		b = new byte[]{(byte)((value >> 8) & 0xFF),(byte)(value & 0xFF)};
		reverse(b);
		assertEquals(value, LittleEndianInputStream.wrap(b).readShort());
	}

	/**
	 * Test method for {@link net.bitdroid.network.wire.LittleEndianInputStream#readChar()}.
	 * @throws IOException 
	 */
	//	@Test
	//	public void testReadChar() throws IOException {
	//		fail("Not yet implemented"); // TODO
	//	}

	/**
	 * Test method for {@link net.bitdroid.network.wire.LittleEndianInputStream#readInt()}.
	 * @throws IOException 
	 */
		@Test
		public void testReadInt() throws IOException {
			int value = 12345000; // Just a random number
			byte b[] = new byte[]{(byte)((value >> 24) & 0xFF),(byte)((value >> 16) & 0xFF),(byte)((value >> 8) & 0xFF),(byte)(value & 0xFF)};
			reverse(b);
			assertEquals(value, LittleEndianInputStream.wrap(b).readInt());
			value = -12345000;
			b = new byte[]{(byte)((value >> 24) & 0xFF),(byte)((value >> 16) & 0xFF),(byte)((value >> 8) & 0xFF),(byte)(value & 0xFF)};
			reverse(b);
			assertEquals(value, LittleEndianInputStream.wrap(b).readInt());
		}

	/**
	 * Test method for {@link net.bitdroid.network.wire.LittleEndianInputStream#readLong()}.
	 * @throws IOException 
	 */
	@Test
	public void testReadLong() throws IOException {
		long value = 1311768467463790320L; // Just a random number
		byte b[] = new byte[]{(byte)((value >> 56) & 0xFF),(byte)((value >> 48) & 0xFF),
				(byte)((value >> 40) & 0xFF),(byte)((value >> 32) & 0xFF),
				(byte)((value >> 24) & 0xFF),(byte)((value >> 16) & 0xFF),
				(byte)((value >> 8) & 0xFF),(byte)(value & 0xFF)};
		reverse(b);
		assertEquals(value, LittleEndianInputStream.wrap(b).readLong());
		value = -1311768467463790320L;
		b = new byte[]{(byte)((value >> 56) & 0xFF),(byte)((value >> 48) & 0xFF),
				(byte)((value >> 40) & 0xFF),(byte)((value >> 32) & 0xFF),
				(byte)((value >> 24) & 0xFF),(byte)((value >> 16) & 0xFF),
				(byte)((value >> 8) & 0xFF),(byte)(value & 0xFF)};
		reverse(b);
		assertEquals(value, LittleEndianInputStream.wrap(b).readLong());
	}

	/**
	 * Test method for {@link net.bitdroid.network.wire.LittleEndianInputStream#readUTF()}.
	 */
	//	@Test
	//	public void testReadUTF() {
	//		fail("Not yet implemented"); // TODO
	//	}

	/**
	 * Test method for {@link net.bitdroid.network.wire.LittleEndianInputStream#readDouble()}.
	 */
	//	@Test
	//	public void testReadDouble() {
	//		fail("Not yet implemented"); // TODO
	//	}

	/**
	 * Test method for {@link net.bitdroid.network.wire.LittleEndianInputStream#readFloat()}.
	 */
	//	@Test
	//	public void testReadFloat() {
	//		fail("Not yet implemented"); // TODO
	//	}

	/**
	 * Test method for {@link net.bitdroid.network.wire.LittleEndianInputStream#skipBytes(int)}.
	 * @throws IOException 
	 */
	@Test
	public void testSkipBytes() throws IOException {
		byte[] b = new byte[]{0x12,0x34,0x56,0x78,(byte) 0x9A};
		LittleEndianInputStream leis = LittleEndianInputStream.wrap(b);
		leis.skipBytes(2);
		assertEquals(0x56, leis.read());
		leis.skipBytes(1);
		assertEquals((byte)0x9A, leis.read());
	}

	/**
	 * Test method for {@link net.bitdroid.network.wire.LittleEndianInputStream#wrap(byte[])}.
	 * @throws IOException 
	 */
	@Test
	public void testWrap() throws IOException {
		byte[] b = new byte[]{0x12,0x34,0x56,0x78,(byte) 0x9A};
		byte[] o = new byte[5];
		LittleEndianInputStream.wrap(b).read(o);
		assert(Arrays.equals(b, o));
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
