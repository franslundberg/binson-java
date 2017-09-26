package org.binson;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests storing a Binson array in a Binson object.
 * 
 * @author Frans Lundberg
 */
public class ArrayTest {
    private Binson obj;
    
    public ArrayTest() {
        BinsonArray array = new BinsonArray().add(123);
        obj = new Binson().put("a", array);
    }
    
    @Test
    public void testGet() {
        BinsonArray expected = new BinsonArray().add(123);
        assertEquals(expected, obj.getArray("a"));
    }
    
    @Test
    public void testHas() {
        assertTrue(obj.hasArray("a"));
        assertFalse(obj.hasArray("b"));
        assertFalse(obj.hasBoolean("a"));
    }
    
    @Test(expected=BinsonFormatException.class)
    public void testNonExistant() {
        obj.getArray("b");
    }
}
