package org.binson;

import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Test;

public class FieldOrderTest {	
	@Test
	public void testAlex1() {
		// Reported 2016-01-28 by Alexander Reshniuk
		
		byte[] b1 = {(byte)0xf3, (byte)0x91, (byte)0xb0, (byte)0xa7};  // UTF16: DB07 DC27
		byte[] b2 = {(byte)0xef, (byte)0xad, (byte)0xab, (byte)0xe8, (byte)0xb0, (byte)0xa6}; // UTF16: FB6B 8C26 
		String s1 = new String(b1, StandardCharsets.UTF_8);		
		String s2 = new String(b2, StandardCharsets.UTF_8);
		
		byte[] expected = {0x40, 0x14, 
				0x06, (byte)0xef, (byte)0xad, (byte)0xab, (byte)0xe8, (byte)0xb0, (byte)0xa6, 
				0x40, 0x41, 0x14,
				0x04, (byte)0xf3, (byte)0x91, (byte)0xb0, (byte)0xa7, 
				0x40, 0x41, 0x41};
		
		Binson binson = new Binson().put(s1, new Binson()).put(s2, new Binson());
		
		Assert.assertArrayEquals(expected, binson.toBytes());
	}
	
	@Test
	public void testAlex2() {
		// Reported 2016-01-29 by Alexander Reshniuk
		
		byte[] b1 = { (byte)0xec, (byte)0xbe, (byte)0xb6, (byte)0xe9, (byte)0xb0, (byte)0x8a };
		byte[] b2 = { (byte)0xec, (byte)0x8c, (byte)0xb7, (byte)0xe9, (byte)0xb4, (byte)0x88 };
		String s1 = new String(b1, StandardCharsets.UTF_8);		
		String s2 = new String(b2, StandardCharsets.UTF_8);
		
		byte[] expected = {
				0x40, 0x14, 0x06, 
				(byte)0xec, (byte)0x8c, (byte)0xb7, (byte)0xe9, (byte)0xb4, (byte)0x88,
				0x40, 0x41, 0x14, 0x06, 
				(byte)0xec, (byte)0xbe, (byte)0xb6, (byte)0xe9, (byte)0xb0, (byte)0x8a, 
				0x40, 0x41, 0x41
		};
		
		Binson binson = new Binson().put(s1, new Binson()).put(s2, new Binson());
		
		Assert.assertArrayEquals(expected, binson.toBytes());
	}
}
