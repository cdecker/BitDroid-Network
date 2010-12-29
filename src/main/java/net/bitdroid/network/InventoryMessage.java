package net.bitdroid.network;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import net.bitdroid.network.wire.LittleEndianInputStream;
import net.bitdroid.network.wire.LittleEndianOutputStream;

public class InventoryMessage extends Message {
	enum InventoryType {
		ERROR(0), MSG_TX(1), MSG_BLOCK(2);
		private int code;

		private InventoryType(int c) {
			code = c;
		}

		public int getCode() {
			return code;
		}
	};
	
	private List<InventoryItem> items = new LinkedList<InventoryMessage.InventoryItem>();


	public InventoryMessage(BitcoinClientSocket clientSocket) {
		super(clientSocket);
	}

	@Override
	void read(LittleEndianInputStream in) throws IOException {
		int count = in.readInt();
		for(int i=0; i<count; i++){
			byte[] buffer = new byte[32];
			in.read(buffer);
			//items.add(new InventoryItem(in.read(), buffer));
		}
	}

	@Override
	void toWire(LittleEndianOutputStream leos) throws IOException {
	}
	public class InventoryItem {
		private byte[] hash;
		/**
		 * @return the hash
		 */
		public final byte[] getHash() {
			return hash;
		}
		/**
		 * @return the type
		 */
		public final InventoryType getType() {
			return type;
		}
		private InventoryType type;
		public InventoryItem(InventoryType type, byte hash[]){
			this.hash = hash;
			this.type = type;
		}
	}
	@Override
	public String getCommand() {
		return "inv";
	}
}
