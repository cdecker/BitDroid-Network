package net.bitdroid.network;


/**
 * @author cdecker
 *
 */
public interface BitcoinEventListener {
	public void eventReceived(Message message);
	public void messageSent(Message message);
}
