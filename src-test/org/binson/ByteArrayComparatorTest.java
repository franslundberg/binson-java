package org.binson;

import org.binson.lowlevel.ByteArrayComparator;
import org.junit.Assert;
import org.junit.Test;

public class ByteArrayComparatorTest {
	@Test
	public void test1() {
		byte[] a1 = new byte[]{1, 2, 2};
		byte[] a2 = new byte[]{1, 2, 1};
		
		Assert.assertTrue(ByteArrayComparator.compareArrays(a1, a2) > 0);
	}
	
	@Test
	public void test2() {
		byte[] a1 = new byte[]{};
		byte[] a2 = new byte[]{1};
		
		Assert.assertTrue(ByteArrayComparator.compareArrays(a1, a2) < 0);
	}
	
	@Test
	public void test3() {
		byte[] a1 = new byte[]{1, 2, 3};
		byte[] a2 = new byte[]{1, 2, 3};
		
		Assert.assertTrue(ByteArrayComparator.compareArrays(a1, a2) == 0);
	}
	
	@Test
	public void test4() {
		byte[] a1 = new byte[]{(byte)128};
		byte[] a2 = new byte[]{(byte)130};
		Assert.assertTrue(ByteArrayComparator.compareArrays(a1, a2) < 0);
	}
	
	@Test
	public void test5() {
		byte[] a1 = new byte[]{1, 2, 3};
		byte[] a2 = new byte[]{1, 2, 3, 4};
		
		Assert.assertTrue(ByteArrayComparator.compareArrays(a1, a2) < 0);
	}
}
