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
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Arrays;

import junit.framework.TestCase;
import net.bitdroid.network.BitcoinNetwork.SocketState;
import net.bitdroid.network.Event.EventType;
import net.bitdroid.network.messages.BlockMessage;
import net.bitdroid.network.messages.InventoryMessage;
import net.bitdroid.network.messages.Message;
import net.bitdroid.network.messages.PeerAddress;
import net.bitdroid.network.messages.Transaction;
import net.bitdroid.network.messages.Transaction.TxInput;
import net.bitdroid.network.messages.VerackMessage;
import net.bitdroid.network.messages.VersionMessage;
import net.bitdroid.network.wire.LittleEndianInputStream;
import net.bitdroid.network.wire.LittleEndianOutputStream;
import net.bitdroid.utils.StringUtils;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestBitcoinClientSocket extends TestCase {

	Logger LOG = LoggerFactory.getLogger(TestBitcoinClientSocket.class);

	protected ThreadedBitcoinNetwork prepareWithDump(String filename) throws IOException{
		ThreadedBitcoinNetwork s = new ThreadedBitcoinNetwork();
		s.inputStream = ClassLoader.getSystemResourceAsStream(filename);
		return s;
	}

	protected byte[] readDump(String filename, int length) throws IOException{
		byte[] buffer = new byte[length];
		ClassLoader.getSystemResourceAsStream(filename).read(buffer);
		return buffer;
	}

	@Test
	public void testReadDump() throws IOException{
		InputStream is = ClassLoader.getSystemResourceAsStream("bitcoin-version-0.dump");
		is.read();
	}

	@Test
	public void testReadVerackMessage() throws IOException {
		ThreadedBitcoinNetwork s = prepareWithDump("bitcoin-verack-1.dump");
		// There's not much to test, it's an empty message.
		Message m = s.readMessage().getSubject();
		assert(m instanceof VerackMessage);
		assertEquals(m.getPayloadSize(), 0);
		assertTrue("Checksum is set on the socket", s.state.currentState == SocketState.OPEN);
	}

	@Test
	public void testReadVersionMessage() throws IOException {
		ThreadedBitcoinNetwork s = prepareWithDump("bitcoin-version-1.dump");
		VersionMessage m = (VersionMessage)s.readMessage().getSubject();
		assert(m instanceof VersionMessage);
		assertEquals(m.getPayloadSize(), 85);
		assertEquals(31700, m.getProtocolVersion());
		assertEquals(1292970988, m.getTimestamp());
		assertEquals("Checksum is set not yet enabled on the socket", SocketState.HANDSHAKE, s.state.currentState);
		assertEquals("/87.118.94.169", m.getYourAddress().getAddress().toString());
		assertEquals("/213.200.193.129", m.getMyAddress().getAddress().toString());
		assertEquals("", m.getClientVersion());
		assertEquals(98806, m.getHeight());
	}

	@Test
	public void testFullVersionMessageCycle() throws IOException {
		// Read the original
		ThreadedBitcoinNetwork s = prepareWithDump("bitcoin-version-1.dump");
		VersionMessage m = (VersionMessage)s.readMessage().getSubject();
		byte[] buf = readDump("bitcoin-version-1.dump", 105);
		byte[] output = new byte[105];
		s.outputStream = LittleEndianOutputStream.wrap(output);
		s.sendMessage(new Event(null, EventType.VERSION_TYPE, m));
		assert(Arrays.equals(buf, output));
	}

	@Test
	public void testReadAddress() throws IOException {
		LittleEndianInputStream leis = new LittleEndianInputStream(ClassLoader.getSystemResourceAsStream("address.dump"));
		PeerAddress a = new PeerAddress();
		a.read(leis);
		assertEquals("/213.200.193.129", a.getAddress().toString());
		assertEquals(1, a.getServices());
		assertEquals(36747, a.getPort());
	}

	@Test
	public void testWriteAddress() throws IOException {
		PeerAddress a = new PeerAddress();
		a.setAddress(InetAddress.getByName("213.200.193.129"));
		a.setServices(1);
		a.setPort(36747);
		//a.setReserved(new byte[]{(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
		//		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
		//		(byte)0x00,(byte)0xFF,(byte)0xFF});
		byte b[] = new byte[26];
		LittleEndianOutputStream leos = LittleEndianOutputStream.wrap(b);
		a.toWire(leos);
		byte c[] = new byte[26];
		ClassLoader.getSystemResourceAsStream("address.dump").read(c);
		LOG.info(StringUtils.getHexString(b));
		LOG.info(StringUtils.getHexString(c));
		assertTrue(Arrays.equals(c, b));
	}

	@Test
	public void testReadInvMessage() throws IOException {
		ThreadedBitcoinNetwork s = prepareWithDump("bitcoin-inv-2.dump");
		s.state.currentState = SocketState.OPEN;
		InventoryMessage m = (InventoryMessage)s.readMessage().getSubject();
		assertEquals(8, m.getItems().size());
	}

	@Test
	public void testChecksum() throws IOException{
		ThreadedBitcoinNetwork s = prepareWithDump("bitcoin-inv-2.dump");
		InputStream in = ClassLoader.getSystemResourceAsStream("bitcoin-inv-2.dump");
		in.skip(20);
		byte[] checksum = new byte[4];
		in.read(checksum);
		byte[] content = new byte[289];
		in.read(content);
		byte[] calc = s.calculateChecksum(content);

		assertEquals(checksum.length, calc.length);
		for(int i=0; i<calc.length; i++)
			assertEquals(checksum[i], calc[i]);
	}

	/*
	 * Tests against transaction http://blockexplorer.com/t/94QA14eKWN
     */
	@Test
	public void testReadTxMessage() throws IOException{
		ThreadedBitcoinNetwork s = prepareWithDump("bitcoin-tx-14.dump");
		s.state.currentState = SocketState.OPEN;
		Transaction m = (Transaction)s.readMessage().getSubject();
		assertEquals(1,m.getInputs().size());
		TxInput txIn = m.getInputs().get(0);
		byte[] b = txIn.getPrevious().getHash().clone();
		StringUtils.reverse(b);
		assertEquals("2936ee6a0db4e4901988503bb6e966128dd5fa01bcf08451f78a1d5b08dbbd6d", StringUtils.getHexString(b));
		assertEquals(2, m.getOutputs().size());
	}

	@Test
	public void testReadBlockMessage() throws IOException{
		ThreadedBitcoinNetwork s = prepareWithDump("bitcoin-block-3.dump");
		s.state.currentState = SocketState.OPEN;
		BlockMessage m = (BlockMessage)s.readMessage().getSubject();
		assertEquals(1, m.getVersion());

		// The dump is of block 96180, previous hash points to 96179,
		assertEquals("0000000000018998eb165333c20db25a170c2e3a468ea05a3ad672c8b678fdc2",StringUtils.getHexString(m.getPreviousHash()));
		assertEquals("ae7741a7e6cd43917e49f081dbe222106f30042687b9cc0de2d24af0950f43da",StringUtils.getHexString(m.getMerkleRoot()));
		// Nonce is in hexadecimal (1652850737 = 0x62848031)
		assertEquals("62848031", StringUtils.getHexString(m.getNonce()));
	}

	@Test
	public void testReadWriteBlockMessage() throws IOException {
		ThreadedBitcoinNetwork s = prepareWithDump("bitcoin-block-3.dump");
		s.state.currentState = SocketState.OPEN;
		BlockMessage m = (BlockMessage)s.readMessage().getSubject();
		byte[] buf = readDump("bitcoin-block-3.dump", 7266);
		byte[] output = new byte[7266];
		s.outputStream = LittleEndianOutputStream.wrap(output);
		s.sendMessage(new Event(null, EventType.BLOCK_TYPE, m));
		assertEquals(StringUtils.getHexString(buf), StringUtils.getHexString(output));
	}

	@Test
	public void testReadWriteTransaction() throws IOException {
		ThreadedBitcoinNetwork s = prepareWithDump("bitcoin-tx-14.dump");
		s.state.currentState = SocketState.OPEN;
		Transaction m = (Transaction)s.readMessage().getSubject();
		byte[] buf = readDump("bitcoin-tx-14.dump", 282);
		byte[] output = new byte[282];
		s.outputStream = LittleEndianOutputStream.wrap(output);
		s.sendMessage(new Event(null, EventType.TRANSACTION_TYPE, m));
		assertEquals(StringUtils.getHexString(buf), StringUtils.getHexString(output));
	}

	@Test
	public void testReadWriteAddrMessage() throws IOException {
		ThreadedBitcoinNetwork s = prepareWithDump("bitcoin-addr-11.dump");
		s.state.currentState = SocketState.OPEN;
		Message m = s.readMessage().getSubject();
		byte[] buf = readDump("bitcoin-addr-11.dump", 5335);
		byte[] output = new byte[5335];
		s.outputStream = LittleEndianOutputStream.wrap(output);
		s.sendMessage(new Event(null, EventType.ADDR_TYPE, m));
		assertEquals(StringUtils.getHexString(buf), StringUtils.getHexString(output));
	}

	@Test
	public void testReadWriteInventoryMessage() throws IOException {
		ThreadedBitcoinNetwork s = prepareWithDump("bitcoin-inv-2.dump");
		s.state.currentState = SocketState.OPEN;
		Message m = s.readMessage().getSubject();
		byte[] buf = readDump("bitcoin-inv-2.dump", 313);
		byte[] output = new byte[313];
		s.outputStream = LittleEndianOutputStream.wrap(output);
		s.sendMessage(new Event(null, EventType.INVENTORY_TYPE, m));
		assertEquals(StringUtils.getHexString(buf), StringUtils.getHexString(output));
	}

	@Test
	public void testReadWriteGetDataMessage() throws IOException {
		ThreadedBitcoinNetwork s = prepareWithDump("bitcoin-getdata-72.dump");
		s.state.currentState = SocketState.OPEN;
		Message m = s.readMessage().getSubject();
		byte[] buf = readDump("bitcoin-getdata-72.dump", 61);
		byte[] output = new byte[61];
		s.outputStream = LittleEndianOutputStream.wrap(output);
		s.sendMessage(new Event(null, EventType.GET_DATA_TYPE, m));
		assertEquals(buf, output);
	}

	/**
	 * Simpler helper
	 * @param expected
	 * @param actual
	 * @throws IOException
	 */
	protected void assertEquals(byte[] expected, byte[] actual) throws IOException {
		if(expected.length != actual.length){
			LOG.error("Buffer lengths do not match!");
			return;
		}
		for(int i=0; i<expected.length; i++){
			if(expected[i] != actual[i]){
				LOG.info(StringUtils.getHexString(expected));
				LOG.info(StringUtils.getHexString(actual));
				fail("Buffers do not match!");
			}
		}
	}
}
