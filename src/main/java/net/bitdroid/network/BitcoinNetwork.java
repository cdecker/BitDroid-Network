package net.bitdroid.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public class BitcoinNetwork implements Runnable {

	private final Selector selector; 
	private final ServerSocketChannel server; 

	public BitcoinNetwork(int port) throws IOException {
	      server = ServerSocketChannel.open();
	      selector = Selector.open(); 
	      server.configureBlocking(false);
	      server.socket().bind(new InetSocketAddress(InetAddress.getLocalHost(), port));
	      SelectionKey sk = server.register(selector, SelectionKey.OP_ACCEPT);
	}
	/**
	 * 
	 */
	public void run() {
		while(!Thread.interrupted())
			try {
				selector.select();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

}
