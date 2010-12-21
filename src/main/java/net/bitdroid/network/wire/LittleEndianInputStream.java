package net.bitdroid.network.wire;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UTFDataFormatException;
import java.math.BigInteger;
import java.nio.ByteBuffer;

public class LittleEndianInputStream extends FilterInputStream {

	/**
	 * Creates a new little endian input stream and chains it to the  
	 * input stream specified by the in argument. 
	 *
	 * @param   in   the underlying input stream.
	 * @see     java.io.FilterInputStream#out
	 */
	public LittleEndianInputStream(InputStream in) {
		super(in);
	}

	public int readUnsignedShort() throws IOException{
		byte b[] = new byte[2];
		read(b);
		return (b[1] & 0xFF) << 8 | (b[0] & 0xFF);  
	}

	public long readUnsignedInt() throws IOException {
		byte[] b = new byte[4];
		read(b);
		return ((long)b[3] & 0xFF) << 24 | ((long)b[2] & 0xFF) << 16 | ((long)b[1] & 0xFF) << 8 | ((long)b[0] & 0xFF);
	}

	public BigInteger readUnsignedLong() throws IOException{
		byte b[] = new byte[8];
		in.read(b);
		reverse(b);
		return new BigInteger(1, b);
	}

	public long readVariableSize() throws IOException{
		int b = readUnsignedByte();
        if(b == 255){
        	throw new RuntimeException("I was never expecting a length of UInt64, please slap the developer.");
	}else if(b == 254){
        	return readUnsignedInt();
        }else if(b == 253)
        	return readUnsignedShort();
        else
        	return b;
	}
	
	public String readString() throws IOException{
		long length = readVariableSize();
		byte[] b = new byte[(int) length];
		in.read(b);
		return new String(b);
	}
	
	/**
	 * Reverse a byte array.
	 * @param b
	 */
	private static void reverse(byte[] b) {
		int left  = 0, right = b.length-1;
		while (left < right) {
			byte temp = b[left]; 
			b[left]  = b[right]; 
			b[right] = temp;
			left++;
			right--;
		}
	}
	// ========================================================================

	/**
	 * Reads a <code>boolean</code> from the underlying input stream by 
	 * reading a single byte. If the byte is zero, false is returned.
	 * If the byte is positive, true is returned. 
	 *
	 * @return      b   the <code>boolean</code> value read.
	 * @exception  EOFException  if the end of the underlying input stream
	 *              has been reached
	 * @exception  IOException  if the underlying stream throws an IOException.
	 */
	public boolean readBoolean() throws IOException {

		int bool = in.read();
		return (bool != 0);

	}

	/**
	 * Reads a signed <code>byte</code> from the underlying input stream
	 * with value between -128 and 127
	 *
	 * @return     the <code>byte</code> value read.
	 * @exception  EOFException  if the end of the underlying input stream
	 *              has been reached
	 * @exception  IOException  if the underlying stream throws an IOException.
	 */
	public byte readByte(int b) throws IOException {

		int temp = in.read();
		return (byte) temp;

	}

	/**
	 * Reads an unsigned <code>byte</code> from the underlying 
	 * input stream with value between 0 and 255
	 *
	 * @return     the <code>byte</code> value read.
	 * @exception  EOFException  if the end of the underlying input 
	 *              stream has been reached
	 * @exception  IOException  if the underlying stream throws an IOException.
	 */
	public int readUnsignedByte() throws IOException {

		int temp = in.read();
		return temp;

	}

	/**
	 * Reads a two byte signed <code>short</code> from the underlying 
	 * input stream in little endian order, low byte first. 
	 *
	 * @return     the <code>short</code> read.
	 * @exception  EOFException  if the end of the underlying input stream 
	 *              has been reached
	 * @exception  IOException  if the underlying stream throws an IOException.
	 */
	public short readShort() throws IOException {
		short b[] = new short[2];
		b[0] = (short) in.read();
		b[1] = (short) in.read();
		return (short) (((b[1] & 0xFF) << 8) + (b[0] & 0xFF));

	}

	/**
	 * Reads a two byte unsigned <code>short</code> from the underlying 
	 * input stream in little endian order, low byte first. 
	 *
	 * @return     the int value of the unsigned short read.
	 * @exception  EOFException  if the end of the underlying input stream 
	 *              has been reached
	 * @exception  IOException  if the underlying stream throws an IOException.
	 */
	/*  
  public int readUnsignedShort() throws IOException {

    int byte1 = in.read();
    int byte2 = in.read();
    if (byte2 == -1) throw new EOFException();
    return ((byte2 << 24) >> 16) + ((byte1 << 24) >> 24);

  }
	 */
	/**
	 * Reads a two byte Unicode <code>char</code> from the underlying 
	 * input stream in little endian order, low byte first. 
	 *
	 * @return     the int value of the unsigned short read.
	 * @exception  EOFException  if the end of the underlying input stream 
	 *              has been reached
	 * @exception  IOException  if the underlying stream throws an IOException.
	 */
	public char readChar() throws IOException {

		int byte1 = in.read();
		int byte2 = in.read();
		return (char) (((byte2 << 24) >>> 16) + ((byte1 << 24) >>> 24));

	}


	/**
	 * Reads a four byte signed <code>int</code> from the underlying 
	 * input stream in little endian order, low byte first. 
	 *
	 * @return     the <code>int</code> read.
	 * @exception  EOFException  if the end of the underlying input stream 
	 *              has been reached
	 * @exception  IOException  if the underlying stream throws an IOException.
	 */
	public int readInt() throws IOException {

		int byte1, byte2, byte3, byte4;

		synchronized (this) {
			byte1 = in.read();
			byte2 = in.read();
			byte3 = in.read();
			byte4 = in.read();
		}
		return (byte4 << 24) 
		+ ((byte3 << 24) >>> 8) 
		+ ((byte2 << 24) >>> 16) 
		+ ((byte1 << 24) >>> 24);

	}

	/**
	 * Reads an eight byte signed <code>int</code> from the underlying 
	 * input stream in little endian order, low byte first. 
	 *
	 * @return     the <code>int</code> read.
	 * @exception  EOFException  if the end of the underlying input stream 
	 *              has been reached
	 * @exception  IOException  if the underlying stream throws an IOException.
	 */
	public long readLong() throws IOException {

		long byte1 = in.read();
		long byte2 = in.read();
		long byte3 = in.read();
		long byte4 = in.read();
		long byte5 = in.read();
		long byte6 = in.read();
		long byte7 = in.read();
		long byte8 = in.read();
		return (byte8 << 56) 
		+ ((byte7 << 56) >>> 8) 
		+ ((byte6 << 56) >>> 16) 
		+ ((byte5 << 56) >>> 24) 
		+ ((byte4 << 56) >>> 32) 
		+ ((byte3 << 56) >>> 40) 
		+ ((byte2 << 56) >>> 48) 
		+ ((byte1 << 56) >>> 56);

	}

	/**
	 * Reads a string of no more than 65,535 characters 
	 * from the underlying input stream using UTF-8 
	 * encoding. This method first reads a two byte short 
	 * in <b>big</b> endian order as required by the 
	 * UTF-8 specification. This gives the number of bytes in 
	 * the UTF-8 encoded version of the string.
	 * Next this many bytes are read and decoded as UTF-8
	 * encoded characters. 
	 *
	 * @return     the decoded string
	 * @exception  UTFDataFormatException if the string cannot be decoded
	 * @exception  IOException  if the underlying stream throws an IOException.
	 */
	public String readUTF() throws IOException {

		int byte1 = in.read();
		int byte2 = in.read();
		if (byte2 == -1) throw new EOFException();
		int numbytes = (byte1 << 8) + byte2;    
		char result[] = new char[numbytes];
		int numread = 0;
		int numchars = 0;

		while (numread < numbytes) {

			int c1 = readUnsignedByte();
			int c2, c3;

			// The first four bits of c1 determine how many bytes are in this char
			int test = c1 >> 4;
		if (test < 8) {  // one byte
			numread++;
			result[numchars++] = (char) c1;
		}
		else if (test == 12 || test == 13) { // two bytes
			numread += 2;
			if (numread > numbytes) throw new UTFDataFormatException(); 
			c2 = readUnsignedByte();
			if ((c2 & 0xC0) != 0x80) throw new UTFDataFormatException();     
			result[numchars++] = (char) (((c1 & 0x1F) << 6) | (c2 & 0x3F));
		}
		else if (test == 14) { // three bytes
			numread += 3;
			if (numread > numbytes) throw new UTFDataFormatException();    
			c2 = readUnsignedByte();
			c3 = readUnsignedByte();
			if (((c2 & 0xC0) != 0x80) || ((c3 & 0xC0) != 0x80)) {
				throw new UTFDataFormatException();
			}
			result[numchars++] = (char) 
			(((c1 & 0x0F) << 12) | ((c2 & 0x3F) << 6) | (c3 & 0x3F));
		}
		else { // malformed
			throw new UTFDataFormatException();
		}    

		}  // end while

		return new String(result, 0, numchars); 

	}

	/**
	 *
	 * @return     the next eight bytes of this input stream, interpreted as a
	 *             little endian <code>double</code>.
	 * @exception  EOFException if end of stream occurs before eight bytes 
	 *             have been read.
	 * @exception  IOException   if an I/O error occurs.
	 */
	public final double readDouble() throws IOException {

		return Double.longBitsToDouble(this.readLong());

	}

	/**
	 *
	 * @return     the next four bytes of this input stream, interpreted as a
	 *             little endian <code>int</code>.
	 * @exception  EOFException if end of stream occurs before four bytes 
	 *             have been read.
	 * @exception  IOException  if an I/O error occurs.
	 */
	public final float readFloat() throws IOException {

		return Float.intBitsToFloat(this.readInt());

	}

	/**
	 * Skip exactly <code>n</code> bytes of input in the underlying 
	 * input stream. This method blocks until all the bytes are skipped, 
	 * the end of the stream is detected, or an exception is thrown. 
	 *
	 * @param      n   the number of bytes to skip.
	 * @return     the number of bytes skipped, generally n
	 * @exception  EOFException  if this input stream reaches the end before
	 *               skipping all the bytes.
	 * @exception  IOException  if the underlying stream throws an IOException.
	 */
	public final int skipBytes(int n) throws IOException {

		for (int i = 0; i < n; i += (int) skip(n - i));
		return n;

	}

	public static LittleEndianInputStream wrap(final byte[] b){
		LittleEndianInputStream leis = new LittleEndianInputStream(new InputStream() {
			ByteBuffer bb = ByteBuffer.wrap(b);
			public synchronized int read() throws IOException {
				if (!bb.hasRemaining()) {
					return -1;
				}
				return bb.get();
			}
			public synchronized int read(byte[] bytes, int off, int len) throws IOException {
				// Read only what's left
				len = Math.min(len, bb.remaining());
				bb.get(bytes, off, len); return len;
			}
		});
		return leis;
	}
}

