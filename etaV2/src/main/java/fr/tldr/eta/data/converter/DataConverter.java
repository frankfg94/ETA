package fr.tldr.eta.data.converter;



public class DataConverter {
	
	
	private static final char[] hexTable = "0123456789ABCDEF".toCharArray();
	
	/**
	 * <p>
	 * Convert an array of bytes into a string.
	 *
	 * @param data an arty of bytes
	 * @return A string containing a the <tt>data</tt> split in base 16.
	 * @throws IllegalArgumentException if <tt>data</tt> is null.
	 */

	public static String printHexBinary( byte[] data) {
		StringBuilder stringBuilder = new StringBuilder(data.length * 2);
		for (byte b : data) {
			stringBuilder.append(hexTable[(b >> 4) & 0xF]);
			stringBuilder.append(hexTable[(b & 0xF)]);
		}


		return stringBuilder.toString();
	}
}
