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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UTFDataFormatException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;

import javax.sound.sampled.ReverbType;

/**
 * A little endian output stream writes primitive Java numbers 
 * and characters to an output stream in a little endian format. 
 * The standard java.io.DataOutputStream class which this class
 * imitates uses big-endian integers.
 *
 * @see     net.bitdroid.network.wire.LittleEndianInputStream
 * @see     java.io.DataOutputStream
 */
public class LittleEndianOutputStream extends FilterOutputStream {

	/**
	 * The number of bytes written so far to the little endian output stream. 
	 */
	protected int written;

	/**
	 * Creates a new little endian output stream and chains it to the  
	 * output stream specified by the out argument. 
	 *
	 * @param   out   the underlying output stream.
	 * @see     java.io.FilterOutputStream#out
	 */
	public LittleEndianOutputStream(OutputStream out) {
		super(out);
	}

	/**
	 * Writes the specified byte value to the underlying output stream. 
	 *
	 * @param      b   the <code>byte</code> value to be written.
	 * @exception  IOException  if the underlying stream throws an IOException.
	 */
	public synchronized void write(int b) throws IOException {
		out.write(b);
		written++;
	}

	/**
	 * Writes <code>length</code> bytes from the specified byte array 
	 * starting at <code>offset</code> to the underlying output stream.
	 *
	 * @param      data     the data.
	 * @param      offset   the start offset in the data.
	 * @param      length   the number of bytes to write.
	 * @exception  IOException  if the underlying stream throws an IOException.
	 */
	public synchronized void write(byte[] data, int offset, int length) 
	throws IOException {
		out.write(data, offset, length);
		written += length;
	}

	public void writeUnsignedShort(int s) throws IOException{
		byte b[] = new byte[2];
		b[1] = (byte)(s >> 8 & 0xFF);
		b[0] = (byte)(s & 0xFF);
		write(b);
	}
	
	public void writeUnsignedLong(BigInteger i) throws IOException{
		// Dirty little trick, let's just hope the last bit is never set...
		writeLong(i.longValue());
		//byte[] b = i.toByteArray(), 8);
		//reverse(b);
		//write(b);
	}
	
	public void writeUnsignedInt(long v) throws IOException {
		byte[] b = new byte[]{(byte)(v & 0xFF),(byte)(v >> 8 & 0xFF),
				(byte)(v >> 16 & 0xFF), (byte)(v >> 24 & 0xFF)};
		write(b);
	}

	/**
	 * Writes a <code>boolean</code> to the underlying output stream as 
	 * a single byte. If the argument is true, the byte value 1 is written.
	 * If the argument is false, the byte value <code>0</code> in written.
	 *
	 * @param      b   the <code>boolean</code> value to be written.
	 * @exception  IOException  if the underlying stream throws an IOException.
	 */
	public void writeBoolean(boolean b) throws IOException {

		if (b) this.write(1);
		else this.write(0);

	}

	/**
	 * Writes out a <code>byte</code> to the underlying output stream
	 *
	 * @param      b   the <code>byte</code> value to be written.
	 * @exception  IOException  if the underlying stream throws an IOException.
	 */
	public void writeByte(int b) throws IOException {
		out.write(b);
		written++;
	}

	/**
	 * Writes a two byte <code>short</code> to the underlying output stream in
	 * little endian order, low byte first. 
	 *
	 * @param      s   the <code>short</code> to be written.
	 * @exception  IOException  if the underlying stream throws an IOException.
	 */
	public void writeShort(int s) throws IOException {

		out.write(s & 0xFF);
		out.write((s >>> 8) & 0xFF);
		written += 2;

	}

	/**
	 * Writes a two byte <code>char</code> to the underlying output stream 
	 * in little endian order, low byte first. 
	 *
	 * @param      c   the <code>char</code> value to be written.
	 * @exception  IOException  if the underlying stream throws an IOException.
	 */
	public void writeChar(int c) throws IOException {

		out.write(c & 0xFF);
		out.write((c >>> 8) & 0xFF);
		written += 2;

	}

	/**
	 * Writes a four-byte <code>int</code> to the underlying output stream 
	 * in little endian order, low byte first, high byte last
	 *
	 * @param      i   the <code>int</code> to be written.
	 * @exception  IOException  if the underlying stream throws an IOException.
	 */
	public void writeInt(int i) throws IOException {

		out.write(i & 0xFF);
		out.write((i >>> 8) & 0xFF);
		out.write((i >>> 16) & 0xFF);
		out.write((i >>> 24) & 0xFF);
		written += 4;

	}

	/**
	 * Writes an eight-byte <code>long</code> to the underlying output stream 
	 * in little endian order, low byte first, high byte last
	 *
	 * @param      l   the <code>long</code> to be written.
	 * @exception  IOException  if the underlying stream throws an IOException.
	 */
	public void writeLong(long l) throws IOException {

		out.write((int) l & 0xFF);
		out.write((int) (l >>> 8) & 0xFF);
		out.write((int) (l >>> 16) & 0xFF);
		out.write((int) (l >>> 24) & 0xFF);
		out.write((int) (l >>> 32) & 0xFF);
		out.write((int) (l >>> 40) & 0xFF);
		out.write((int) (l >>> 48) & 0xFF);
		out.write((int) (l >>> 56) & 0xFF);
		written += 8;

	}

	/**
	 * Writes a 4 byte Java float to the underlying output stream in
	 * little endian order.
	 *
	 * @param      f   the <code>float</code> value to be written.
	 * @exception  IOException  if an I/O error occurs.
	 */
	public final void writeFloat(float f) throws IOException {

		this.writeInt(Float.floatToIntBits(f));

	}

	/**
	 * Writes an 8 byte Java double to the underlying output stream in
	 * little endian order.
	 *
	 * @param      d   the <code>double</code> value to be written.
	 * @exception  IOException  if an I/O error occurs.
	 */
	public final void writeDouble(double d) throws IOException {

		this.writeLong(Double.doubleToLongBits(d));

	}

	/**
	 * Writes a string of no more than 65,535 characters 
	 * to the underlying output stream using UTF-8 
	 * encoding. This method first writes a two byte short 
	 * in <b>big</b> endian order as required by the 
	 * UTF-8 specification. This gives the number of bytes in the 
	 * UTF-8 encoded version of the string, not the number of characters
	 * in the string. Next each character of the string is written
	 * using the UTF-8 encoding for the character.
	 *
	 * @param      s   the string to be written.
	 * @exception  UTFDataFormatException if the string is longer than 
	 *             65,535 characters.
	 * @exception  IOException  if the underlying stream throws an IOException.
	 */
//	public void writeUTF(String s) throws IOException {
//
//		int numchars = s.length();
//		int numbytes = 0;
//
//		for (int i = 0 ; i < numchars ; i++) {
//			int c = s.charAt(i);
//			if ((c >= 0x0001) && (c <= 0x007F)) numbytes++;
//			else if (c > 0x07FF) numbytes += 3;
//			else numbytes += 2;
//		}
//
//		if (numbytes > 65535) throw new UTFDataFormatException();     
//
//		out.write((numbytes >>> 8) & 0xFF);
//		out.write(numbytes & 0xFF);
//		for (int i = 0 ; i < numchars ; i++) {
//			int c = s.charAt(i);
//			if ((c >= 0x0001) && (c <= 0x007F)) {
//				out.write(c);
//			}
//			else if (c > 0x07FF) {
//				out.write(0xE0 | ((c >> 12) & 0x0F));
//				out.write(0x80 | ((c >>  6) & 0x3F));
//				out.write(0x80 | (c & 0x3F));
//				written += 2;
//			} 
//			else {
//				out.write(0xC0 | ((c >>  6) & 0x1F));
//				out.write(0x80 | (c & 0x3F));
//				written += 1;
//			}
//		}
//
//		written += numchars + 2;
//
//	}

	public void writeVariableSize(long size) throws IOException {
		if(size < 253)
			out.write((int) size);
		else if(size < 65535){
			write((int)253);
			writeUnsignedShort((int) size);
		}else{
			write((int)254);
			writeUnsignedInt(size);
		}
	}
	
	public void writeString(String s) throws IOException {
		writeVariableSize(s.length());
		out.write(s.getBytes());
	}
	
	public static LittleEndianOutputStream wrap(final byte[] b){
		return wrap(ByteBuffer.wrap(b));
	}

	public static LittleEndianOutputStream wrap(final ByteBuffer b){
		LittleEndianOutputStream leis = new LittleEndianOutputStream(new OutputStream() {
			ByteBuffer bb = b;

			@Override
			public synchronized void write(int arg0) throws IOException {
				bb.put((byte)arg0);
			}
		});
		return leis;
	}
}
