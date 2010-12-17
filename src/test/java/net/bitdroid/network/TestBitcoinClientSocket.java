package net.bitdroid.network;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import net.bitdroid.network.wire.LittleEndianInputStream;

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
	
//	@Test
	public void testReadAddress() throws IOException {
		LittleEndianInputStream leis = new LittleEndianInputStream(ClassLoader.getSystemResourceAsStream("address.dump"));
		PeerAddress a = new PeerAddress(null);
		a.read(leis);
		assertEquals("/213.200.193.129", a.getAddress().toString());
		assertEquals(1, a.getServices());
		assertEquals(36747, a.getPort());
	}

}
