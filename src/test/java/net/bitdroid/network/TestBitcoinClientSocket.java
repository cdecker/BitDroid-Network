package net.bitdroid.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Arrays;

import junit.framework.TestCase;

import net.bitdroid.network.wire.LittleEndianInputStream;
import net.bitdroid.network.wire.LittleEndianOutputStream;

import org.junit.Test;

public class TestBitcoinClientSocket extends TestCase {

	protected BitcoinClientSocket prepareWithDump(String filename){
		BitcoinClientSocket s = new BitcoinClientSocket();
		s.inputStream = ClassLoader.getSystemResourceAsStream(filename);
		return s;
	}
	
	@Test
	public void testReadDump() throws IOException{
		InputStream is = ClassLoader.getSystemResourceAsStream("bitcoin-version-0.dump");
		is.read();
	}
	
	@Test
	public void testReadVerackMessage() throws IOException {
		BitcoinClientSocket s = prepareWithDump("bitcoin-verack-1.dump");
		// There's not much to test, it's an empty message.
		Message m = s.readMessage();
		assert(m instanceof VerackMessage);
		assertEquals(m.getSize(), 0);
		assertTrue("Checksum is set on the socket", s.checksumAvailable);
	}
	
	@Test
	public void testReadVersionMessage() throws IOException {
		/**
		 * This packet was captured 12 / 07 / 10 @ 6:47:29am 
		 * with version number 212.
		 */
		BitcoinClientSocket s = prepareWithDump("bitcoin-version-0.dump");
		VersionMessage m = (VersionMessage)s.readMessage();
		assert(m instanceof VersionMessage);
		assertEquals(m.getSize(), 85);
		assertEquals(212, m.getProtocolVersion());
		assertEquals(1291726049, m.getTimestamp());
		assertFalse("Checksum is set not yet enabled on the socket", s.checksumAvailable);
	}
	
	@Test
	public void testReadAddress() throws IOException {
		LittleEndianInputStream leis = new LittleEndianInputStream(ClassLoader.getSystemResourceAsStream("address.dump"));
		PeerAddress a = new PeerAddress(null);
		a.read(leis);
		assertEquals("/213.200.193.129", a.getAddress().toString());
		assertEquals(1, a.getServices());
		assertEquals(36747, a.getPort());
	}
	
	@Test
	public void testWriteAddress() throws IOException {
		PeerAddress a = new PeerAddress(null);
		a.setAddress(InetAddress.getByName("213.200.193.129"));
		a.setServices(1);
		a.setPort(36747);
		a.setReserved(new byte[]{(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
				(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
				(byte)0x00,(byte)0xFF,(byte)0xFF});
		byte b[] = new byte[26];
		LittleEndianOutputStream leos = LittleEndianOutputStream.wrap(b);
		a.toWire(leos);
		byte c[] = new byte[26];
		System.out.println(c);
		ClassLoader.getSystemResourceAsStream("address.dump").read(c);
		System.out.println(b);
		System.out.println(c);
		assert(Arrays.equals(c, b));
	}
}
