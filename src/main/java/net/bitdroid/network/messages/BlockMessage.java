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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

import net.bitdroid.network.wire.LittleEndianInputStream;
import net.bitdroid.network.wire.LittleEndianOutputStream;
import net.bitdroid.utils.StringUtils;

/**
 * @author cdecker
 *
 */
public class BlockMessage extends Message {
	public EventType getType(){
		return EventType.BLOCK_TYPE;
	}
	private long version = 1;
	private long timestamp;
	private long target = 1;
	private byte[] previousHash = new byte[32];
	private byte[] merkleRoot = new byte[32];
	private byte[] nonce = new byte[4];
	private List<Transaction> transactions = new LinkedList<Transaction>();

	/* (non-Javadoc)
	 * @see net.bitdroid.network.Message#getCommand()
	 */
	@Override
	public String getCommand() {
		return "block";
	}

	/* (non-Javadoc)
	 * @see net.bitdroid.network.Message#read(net.bitdroid.network.wire.LittleEndianInputStream)
	 */
	@Override
	public void read(LittleEndianInputStream in) throws IOException {
		version = in.readUnsignedInt();
		if(version != 1)
			throw new IOException("Block version not supported.");
		in.read(previousHash);
		StringUtils.reverse(previousHash);
		in.read(merkleRoot);
		StringUtils.reverse(merkleRoot);
		timestamp = in.readUnsignedInt();
		target = in.readUnsignedInt();
		in.read(nonce);
		StringUtils.reverse(nonce);
		long transactionCount = in.readVariableSize();
		for(int i=0; i<transactionCount; i++){
			Transaction t = new Transaction();
			t.read(in);
			transactions.add(t);
		}
	}

	/* (non-Javadoc)
	 * @see net.bitdroid.network.Message#toWire(net.bitdroid.network.wire.LittleEndianOutputStream)
	 */
	@Override
	public void toWire(LittleEndianOutputStream leos) throws IOException {
		// Re reverse those byte arrays:
		byte _previousHash[] = previousHash.clone();
		StringUtils.reverse(_previousHash);
		byte _nonce[] = nonce.clone();
		StringUtils.reverse(_nonce);
		byte _merkleRoot[] = merkleRoot.clone();
		StringUtils.reverse(_merkleRoot);
		leos.writeUnsignedInt(version);
		leos.write(_previousHash);
		leos.write(_merkleRoot);
		leos.writeUnsignedInt(timestamp);
		leos.writeUnsignedInt(target);
		leos.write(_nonce);
		leos.writeVariableSize(transactions.size());
		for(Transaction t : transactions)
			t.toWire(leos);
	}

	public byte[] getHash() throws NoSuchAlgorithmException{
		byte b[] = new byte[80];
		LittleEndianOutputStream leos = LittleEndianOutputStream.wrap(b);
		try {
			// Re reverse those byte arrays:
			byte _previousHash[] = previousHash.clone();
			StringUtils.reverse(_previousHash);
			byte _nonce[] = nonce.clone();
			StringUtils.reverse(_nonce);
			byte _merkleRoot[] = merkleRoot.clone();
			StringUtils.reverse(_merkleRoot);
			leos.writeUnsignedInt(version);
			leos.write(_previousHash);
			leos.write(_merkleRoot);
			leos.writeUnsignedInt(timestamp);
			leos.writeUnsignedInt(target);
			leos.write(_nonce);
		} catch (IOException e) {
			// Should never happen since we know exactly what's going in there
			e.printStackTrace();
		}
		MessageDigest hasher = MessageDigest.getInstance("SHA-256");
		byte h[] = hasher.digest(b);
		hasher.reset();
		h = hasher.digest(h);
		StringUtils.reverse(h);
		return h;
	}

	/**
	 * @return the version
	 */
	public long getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(long version) {
		this.version = version;
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the target
	 */
	public long getTarget() {
		return target;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(long target) {
		this.target = target;
	}

	/**
	 * @return the previousHash
	 */
	public byte[] getPreviousHash() {
		return previousHash;
	}

	/**
	 * @param previousHash the previousHash to set
	 */
	public void setPreviousHash(byte[] previousHash) {
		this.previousHash = previousHash;
	}

	/**
	 * @return the merkleRoot
	 */
	public byte[] getMerkleRoot() {
		return merkleRoot;
	}

	/**
	 * @param merkleRoot the merkleRoot to set
	 */
	public void setMerkleRoot(byte[] merkleRoot) {
		this.merkleRoot = merkleRoot;
	}

	/**
	 * @return the nonce
	 */
	public byte[] getNonce() {
		return nonce;
	}

	/**
	 * @param nonce the nonce to set
	 */
	public void setNonce(byte[] nonce) {
		this.nonce = nonce;
	}

	/**
	 * @return the transactions
	 */
	public List<Transaction> getTransactions() {
		return transactions;
	}
	
	/* (non-Javadoc)
	 * @see net.bitdroid.network.Event#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Block[hash=");
		try {
			sb.append(StringUtils.getHexString(this.getHash()));
		} catch (Exception e) {
		}
		sb.append("]");
		return sb.toString();
	}
}
