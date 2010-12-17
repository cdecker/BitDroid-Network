package net.bitdroid.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.bitdroid.network.wire.LittleEndianInputStream;

public class BitcoinClientSocket implements Runnable {

	static final byte[] magic = new byte[]{(byte)0xf9,(byte)0xbe,(byte)0xb4,(byte)0xd9};
	static final int magicInt = -642466055;
	protected InputStream inputStream;
	protected OutputStream outputStream;
	protected boolean dirty = false;
	protected boolean checksumAvailable = false;
	protected List<BitcoinEventListener> eventListeners = new LinkedList<BitcoinEventListener>();

	/**
	 * Constructor wrapping the actual socket.
	 */
	public BitcoinClientSocket(){

	}

	protected Message readMessage() throws IOException{
		// Read magic
		byte buf[] = new byte[4];
		inputStream.read(buf);
		if(!Arrays.equals(buf, magic))
			throw new IOException("Stream is out of sync. Probably the other client is missbehaving?");
		buf = new byte[12];
		inputStream.read(buf);
		String command = (new String(buf)).trim();

		// Poor mans read unsigned int :-)
		buf = new byte[4];
		inputStream.read(buf);
		int size = (int)((buf[3] & 0xFF) << 24 | (buf[2] & 0xFF) << 16 | 
				(buf[1] & 0xFF) << 8 | (buf[0] & 0xFF));

		// Now read the buffer and wrap it into a LittleEndianInputStream
		final byte b[] = new byte[size];
		inputStream.read(b);
		LittleEndianInputStream leis = LittleEndianInputStream.wrap(b);
		
		if(checksumAvailable){
			byte[] checksum = new byte[4];
			inputStream.read(checksum);
			// TODO add switch to check the checksum.
		}

		// Boilerplate to select the right message to initialize.
		Message message;
		if("version".equalsIgnoreCase(command))
			message = new VersionMessage(this);
		else // Default case "verack"
			message = new VerackMessage(this);

		message.setSize(size);
		// And now each message knows how to read its format:
		message.read(leis);
		return message;

	}

	public void run() {
		try{
			while(!dirty && isConnected()){
				// Read the message
				Message mess = readMessage();
				// React to the messages
				// Dispatch to listeners
				for(BitcoinEventListener listener : eventListeners)
					listener.eventReceived(mess);
			}
		}catch(IOException ioe){
			dirty = true;
		}finally{
			// TODO disconnect
		}
	}

	void setChecksumRequired(boolean required){
		this.checksumAvailable = required;
	}
	
	public boolean isConnected(){
		return true;
	}
}
