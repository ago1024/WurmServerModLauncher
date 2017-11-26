package org.gotti.wurmunlimited.modcomm.intra;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * ByteBuffer helper
 */
public class BBHelper {

	/**
	 * put an utf-8 string (byte count + bytes)
	 * @param buffer ByteBuffer
	 * @param string String
	 */
	public static void putUtf8String(ByteBuffer buffer, String string) {
		byte[] charData = string.getBytes(StandardCharsets.UTF_8);
		buffer.putInt(charData.length);
		buffer.put(charData);
	}

	/**
	 * read an utf-8 string (byte count + bytes)
	 * @param buffer Byte buffer
	 * @return string
	 */
	public static String getUtf8String(ByteBuffer buffer) {
		int lenght = buffer.getInt();
		byte[] data = new byte[lenght];
		buffer.get(data);
		return new String(data, StandardCharsets.UTF_8);
	}

}
