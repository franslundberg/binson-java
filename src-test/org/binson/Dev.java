package org.binson;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

// TODO A. Remove support for "Binson strings", not much used, not documented much.

// TODO In README.md, describe how to build (mostly a reference to build.xml really).

// TODO refactor, use TextReader in JsonParser too.

// TODO use StringFormatException in JsonParser too.

// TODO refactor, parseNumber() in JsonParser and BinsonString parser are nearly identical 
// code - refactor to common code.

/**
 * For Binson developer.
 */
public class Dev {
    public static void main(String[] args) {
    	byte[] bbb = {0x40, 0x14, 0x04, (byte)0xf3, (byte)0x91, (byte)0xb0, (byte)0xa7, 0x40, 0x41, 0x14, 0x06, (byte)0xef, (byte)0xad, (byte)0xab, (byte)0xe8, (byte)0xb0, (byte)0xa6, 0x40, 0x41, 0x41};
		
		byte[] b1 = {(byte)0xf3, (byte)0x91, (byte)0xb0, (byte)0xa7};
		byte[] b2 = {(byte)0xef, (byte)0xad, (byte)0xab, (byte)0xe8, (byte)0xb0, (byte)0xa6};
		
		String s1 = new String(b1, StandardCharsets.UTF_8);		
		String s2 = new String(b2, StandardCharsets.UTF_8);
		System.out.println("s1.compareTo(s2) is: " + s1.compareTo(s2) +  "(negative == less)");
		
		
		Binson obj = Binson.fromBytes(bbb);
		byte[] bytes = obj.toBytes();
		Arrays.equals(bbb, bytes);
		
		System.out.println(obj+"\n");
		
		System.out.println("Len: "+bytes.length);
		
		for (int j=0; j<bytes.length; j++) {
			  System.out.format("\\x%02x", bytes[j]);
		}
		System.out.println();
    }
}
