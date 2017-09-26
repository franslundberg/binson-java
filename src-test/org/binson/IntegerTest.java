package org.binson;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests for storing an integer in a BinsonObject.
 * 
 * @author Frans Lundberg
 */
public class IntegerTest {
    private Binson obj = new Binson().put("a", 1234);
    
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
    
    @Test(expected=BinsonFormatException.class)
    public void testNonExistant1() {
        new Binson().getInteger("b");
    }
    
    @Test(expected=BinsonFormatException.class)
    public void testNonExistant2() {
        new Binson().put("bbb", 1).getInteger("b");
    }
}
