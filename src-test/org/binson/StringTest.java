package org.binson;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests storing a string in a BinsonObject.
 * 
 * @author Frans Lundberg
 */
public class StringTest {
    private BinsonObject obj = new BinsonObject().put("s", "hello");
    
    @Test
    public void testGet() {
        assertEquals("hello", obj.getString("s"));
    }
    
    @Test
    public void testHas() {
        assertTrue(obj.hasString("s"));
        assertFalse(obj.hasString("s2"));
        assertFalse(obj.hasInteger("s"));
    }
    
    @Test(expected=FormatException.class)
    public void testNonExistant() {
        obj.getString("s2");
    }
}
