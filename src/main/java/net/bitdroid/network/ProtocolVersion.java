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

/**
 * @author cdecker
 *
 */
public class ProtocolVersion {
	private static boolean testnet = false;
	public static String cryptoProvider = "BC";
	public static final int VERSION = 31700; 
	public static final String CLIENT_NAME = "BitDroid 0.1";

	public static boolean isTestnet(){
		return testnet;
	}
	
	public static void setTestnet(boolean _testnet){
		testnet = _testnet;
	}
	
	private static byte[] testnetMagic = new byte[]{(byte) 0xFA,(byte) 0xBF,(byte) 0xB5,(byte) 0xDA};
	private static byte[] productionMagic = new byte[]{(byte) 0xF9,(byte) 0xBe,(byte) 0xB4,(byte) 0xD9};
	
	/**
	 * Returns the magic bytes (header bytes) for the current protocol version.
	 * 
	 * @return 4 magic byte
	 */
	public static byte[] getMagic(){
		return testnet?testnetMagic:productionMagic;
	}
	
	/**
	 * Returns the version byte of addresses for this protocol version.
	 * 
	 * @return version byte
	 */
	public static byte getAddressVersion(){
		return testnet?(byte)111:(byte)0;
	}
}
