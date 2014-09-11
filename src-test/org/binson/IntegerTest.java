package org.binson;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests for storing an integer in a BinsonObject.
 * 
 * @author Frans Lundberg
 */
public class IntegerTest {
    private BinsonObject obj = new BinsonObject().put("a", 1234);
    
    @Test
    public void testGet() {
        assertEquals(1234, obj.getInteger("a"));
    }
    
    @Test
    public void testHas() {
        assertTrue(obj.hasInteger("a"));
        assertFalse(obj.hasInteger("b"));
        assertFalse(obj.hasBoolean("a"));
    }
    
    @Test(expected=FormatException.class)
    public void testNonExistant1() {
        new BinsonObject().getInteger("b");
    }
    
    @Test(expected=FormatException.class)
    public void testNonExistant2() {
        new BinsonObject().put("bbb", 1).getInteger("b");
    }
}
