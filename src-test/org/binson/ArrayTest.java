package org.binson;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests storing a Binson array in a BinsonObject.
 * 
 * @author Frans Lundberg
 */
public class ArrayTest {
    private BinsonObject obj;
    
    public ArrayTest() {
        BinsonArray array = new BinsonArray().add(123);
        obj = new BinsonObject().put("a", array);
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
    
    @Test(expected=FormatException.class)
    public void testNonExistant() {
        obj.getArray("b");
    }
}
