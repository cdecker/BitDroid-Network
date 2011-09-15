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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
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
public class Transaction extends Message {
	public EventType getType(){
		return EventType.TRANSACTION_TYPE;
	}

	private int version;
	private int locktime;
	private List<TxInput> inputs = new LinkedList<TxInput>();
	private List<TxOutput> outputs = new LinkedList<TxOutput>();

	/* (non-Javadoc)
	 * @see net.bitdroid.network.Message#getCommand()
	 */
	@Override
	public String getCommand() {
		return "tx";
	}

	/* (non-Javadoc)
	 * @see net.bitdroid.network.Message#read(net.bitdroid.network.wire.LittleEndianInputStream)
	 */
	@Override
	public void read(LittleEndianInputStream in) throws IOException {
		version = in.readInt();

		// Read inputs
		long inputCount = in.readVariableSize();
		for(int i=0; i<inputCount; i++){
			TxOutputPoint o = new TxOutputPoint();
			byte[] b = new byte[32];
			in.read(b);
			o.setHash(b);
			o.setIndex(in.readInt());
			TxInput txIn = new TxInput();
			txIn.setPrevious(o);
			long sigLength = in.readVariableSize();
			byte sig[] = new byte[(int) sigLength];
			in.read(sig);
			txIn.setSignature(sig);
			txIn.setSequence(in.readUnsignedInt());
			inputs.add(txIn);
		}

		long outputCount = in.readVariableSize();
		for(int i=0; i<outputCount; i++){
			TxOutput txOut = new TxOutput();
			txOut.setValue(in.readUnsignedLong());
			long scriptLength = in.readVariableSize();
			byte script[] = new byte[(int)scriptLength];
			in.read(script);
			txOut.setScript(script);
			outputs.add(txOut);
		}
		locktime = in.readInt();
	}

	/* (non-Javadoc)
	 * @see net.bitdroid.network.Message#toWire(net.bitdroid.network.wire.LittleEndianOutputStream)
	 */
	@Override
	public void toWire(LittleEndianOutputStream leos) throws IOException {
		leos.writeInt(version);

		// Write inputs
		leos.writeVariableSize(inputs.size());
		for(TxInput txIn : inputs){
			leos.write(txIn.getPrevious().getHash());
			leos.writeInt(txIn.getPrevious().getIndex());
			leos.writeVariableSize(txIn.getSignature().length);
			leos.write(txIn.getSignature());
			leos.writeUnsignedInt(txIn.getSequence());
		}

		leos.writeVariableSize(outputs.size());
		for(TxOutput o : outputs){
			leos.writeUnsignedLong(o.getValue());
			leos.writeVariableSize(o.getScript().length);
			leos.write(o.getScript());
		}
		leos.writeInt(locktime);
	}

	public class TxOutput {
		private BigInteger value;
		private byte[] script;
		/**
		 * @return the value
		 */
		public BigInteger getValue() {
			return value;
		}
		/**
		 * @param value the value to set
		 */
		public void setValue(BigInteger value) {
			this.value = value;
		}
		/**
		 * @return the script
		 */
		public byte[] getScript() {
			return script;
		}
		/**
		 * @param script2 the script to set
		 */
		public void setScript(byte[] script2) {
			this.script = script2;
		}
	}

	public class TxInput {
		private TxOutputPoint previous;
		private byte[] signature;
		private long sequence;
		/**
		 * @return the previous
		 */
		public TxOutputPoint getPrevious() {
			return previous;
		}
		/**
		 * @param previous the previous to set
		 */
		public void setPrevious(TxOutputPoint previous) {
			this.previous = previous;
		}
		/**
		 * @return the signature
		 */
		public byte[] getSignature() {
			return signature;
		}
		/**
		 * @param sig the signature to set
		 */
		public void setSignature(byte[] sig) {
			this.signature = sig;
		}
		/**
		 * @return the sequence
		 */
		public long getSequence() {
			return sequence;
		}
		/**
		 * @param sequence the sequence to set
		 */
		public void setSequence(long sequence) {
			this.sequence = sequence;
		}
	}

	/**
	 * Reference to a previous output point. Mainly used to show where the
	 * coins came from in the first place and claim ownership.
	 * @author cdecker
	 *
	 */
	public class TxOutputPoint {
		private byte[] hash;
		private int index;
		/**
		 * @return the hash
		 */
		public byte[] getHash() {
			return hash;
		}
		/**
		 * @param hash the hash to set
		 */
		public void setHash(byte[] hash) {
			this.hash = hash;
		}
		/**
		 * @return the index
		 */
		public int getIndex() {
			return index;
		}
		/**
		 * @param index the index to set
		 */
		public void setIndex(int index) {
			this.index = index;
		}
	}

	/**
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * @return the locktime
	 */
	public int getLocktime() {
		return locktime;
	}

	/**
	 * @param locktime the locktime to set
	 */
	public void setLocktime(int locktime) {
		this.locktime = locktime;
	}

	/**
	 * @return the inputs
	 */
	public List<TxInput> getInputs() {
		return inputs;
	}

	/**
	 * @param inputs the inputs to set
	 */
	public void setInputs(List<TxInput> inputs) {
		this.inputs = inputs;
	}

	/**
	 * @return the outputs
	 */
	public List<TxOutput> getOutputs() {
		return outputs;
	}

	/**
	 * @param outputs the outputs to set
	 */
	public void setOutputs(List<TxOutput> outputs) {
		this.outputs = outputs;
	}
	
	/**
	 * @return
	 * @throws NoSuchAlgorithmException 
	 */
	public byte[] getHash() throws NoSuchAlgorithmException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		LittleEndianOutputStream leos = new LittleEndianOutputStream(baos);
		try {
			this.toWire(leos);
		} catch (IOException e) {
			e.printStackTrace();
		}
		MessageDigest hasher = MessageDigest.getInstance("SHA-256");
		byte h[] = hasher.digest(baos.toByteArray());
		hasher.reset();
		h = hasher.digest(h);
		StringUtils.reverse(h);
		return h;
	}
	
	/* (non-Javadoc)
	 * @see net.bitdroid.network.Event#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Transaction[hash=");
		try {
			sb.append(StringUtils.getHexString(this.getHash()));
		} catch (Exception e) {
		}
		sb.append("]");
		return sb.toString();
	}
}
