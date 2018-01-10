package org.binson;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

/**
 * Tests storing a bytes (byte[]) in a Binson object.
 * 
 * @author Frans Lundberg
 */
public class BytesTest {
    private Binson obj = new Binson().put("a", new byte[1]);

    @Test
    public void testGet() {
        byte[] bytes = obj.getBytes("a");
        assertTrue(Arrays.equals(new byte[1], bytes));
    }
    
    @Test
    public void testHas() {
        assertTrue(obj.hasBytes("a"));
        assertFalse(obj.hasString("a"));
        assertFalse(obj.hasBytes("aaa"));
    }
    
    @Test(expected=BinsonFormatException.class)
    public void testGetNonExistant() {
        obj.getBytes("b");
    }
    
    @Test
    public void test128() {
        byte[] arr1 = new byte[128];
        arr1[5] = 123;
        Binson b = new Binson().put("bytes", arr1);
        byte[] serialized = b.toBytes();
        byte[] arr2 = Binson.fromBytes(serialized).getBytes("bytes");
        assertArrayEquals(arr1, arr2);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testPutNull() {
        byte[] value = null;
        new Binson().put("bytes", (byte[]) value);
    }
}
