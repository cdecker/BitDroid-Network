package net.bitdroid.network;

import java.io.IOException;
import java.net.InetAddress;


/**
 * Default driver that automatically responds to certain messages to keep the
 * connection alive.
 * 
 * @author cdecker
 *
 */
public class BitcoinClientDriver implements BitcoinEventListener {

	private BitcoinClientSocket socket;
	
	public BitcoinClientDriver(BitcoinClientSocket socket){
		this.socket = socket;
	}
	
	public void eventReceived(Message message) {
		if(message instanceof VersionMessage){
			// If we got the message out here in userland the protocol version is supported
			// Create a verack and send it back
			VersionMessage hisVersion = (VersionMessage)message;
			VerackMessage verack = new VerackMessage(socket);
			VersionMessage version = new VersionMessage(socket);
			version.setClientVersion("BitDroid 0.1");
			version.setProtocolVersion(31700);
			version.setMyAddress(hisVersion.getYourAddress());
			version.setYourAddress(hisVersion.getMyAddress());
			version.setTimestamp(System.currentTimeMillis());
			try {
				socket.sendMessage(version);
				socket.sendMessage(verack);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void messageSent(Message message) {}

}
