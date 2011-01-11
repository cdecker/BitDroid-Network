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
import java.math.BigInteger;
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
	private int version;
	private int locktime;
	private List<TxInput> inputs = new LinkedList<TxInput>();
	private List<TxOutput> outputs = new LinkedList<TxOutput>();
	/**
	 * @param clientSocket
	 */
	public Transaction(BitcoinClientSocket clientSocket) {
		super(clientSocket);
	}

	/* (non-Javadoc)
	 * @see net.bitdroid.network.Message#getCommand()
	 */
	@Override
	String getCommand() {
		return "tx";
	}

	/* (non-Javadoc)
	 * @see net.bitdroid.network.Message#read(net.bitdroid.network.wire.LittleEndianInputStream)
	 */
	@Override
	void read(LittleEndianInputStream in) throws IOException {
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
//			in.read();
			txIn.setSignature(in.readString());
			txIn.setSequence(in.readUnsignedInt());
			inputs.add(txIn);
		}
		
		long outputCount = in.readVariableSize();
		for(int i=0; i<outputCount; i++){
			TxOutput txOut = new TxOutput();
			txOut.setValue(in.readUnsignedLong());
			txOut.setScript(in.readString());
			outputs.add(txOut);
		}
		locktime = in.readInt();
	}

	/* (non-Javadoc)
	 * @see net.bitdroid.network.Message#toWire(net.bitdroid.network.wire.LittleEndianOutputStream)
	 */
	@Override
	void toWire(LittleEndianOutputStream leos) throws IOException {
		throw new RuntimeException("Not yet implemented!");
	}
	public class TxOutput {
		private BigInteger value;
		private String script;
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
		public String getScript() {
			return script;
		}
		/**
		 * @param script the script to set
		 */
		public void setScript(String script) {
			this.script = script;
		}
	}
	
	public class TxInput {
		private TxOutputPoint previous;
		private String signature;
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
		public String getSignature() {
			return signature;
		}
		/**
		 * @param signature the signature to set
		 */
		public void setSignature(String signature) {
			this.signature = signature;
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
}
