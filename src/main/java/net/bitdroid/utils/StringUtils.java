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
package net.bitdroid.utils;

import java.io.UnsupportedEncodingException;

/**
 * @author cdecker
 *
 */

public class StringUtils {

	static final byte[] HEX_CHAR_TABLE = {
		(byte)'0', (byte)'1', (byte)'2', (byte)'3',
		(byte)'4', (byte)'5', (byte)'6', (byte)'7',
		(byte)'8', (byte)'9', (byte)'a', (byte)'b',
		(byte)'c', (byte)'d', (byte)'e', (byte)'f'
	};    

	public static String getHexString(byte[] raw) 
	throws UnsupportedEncodingException 
	{
		byte[] hex = new byte[2 * raw.length];
		int index = 0;

		for (byte b : raw) {
			int v = b & 0xFF;
			hex[index++] = HEX_CHAR_TABLE[v >>> 4];
			hex[index++] = HEX_CHAR_TABLE[v & 0xF];
		}
		return new String(hex, "ASCII");
	}

	public static void reverse(byte[] b) {
		int left  = 0;          // index of leftmost element
		int right = b.length-1; // index of rightmost element

		while (left < right) {
			// exchange the left and right elements
			byte temp = b[left]; 
			b[left]  = b[right]; 
			b[right] = temp;

			// move the bounds toward the center
			left++;
			right--;
		}
	}

}

