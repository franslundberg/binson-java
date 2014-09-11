package org.binson;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

/**
 * Tests storing a bytes (byte[]) in a BinsonObject.
 * 
 * @author Frans Lundberg
 */
public class BytesTest {
    private BinsonObject obj = new BinsonObject().put("a", new byte[1]);

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
    
    @Test(expected=FormatException.class)
    public void testGetNonExistant() {
        obj.getBytes("b");
    }
}
