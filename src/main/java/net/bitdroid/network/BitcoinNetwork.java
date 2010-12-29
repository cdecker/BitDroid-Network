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
