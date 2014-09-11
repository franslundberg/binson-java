package org.binson;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests for storing a boolean value in a BinsonObject.
 * 
 * @author Frans Lundberg
 */
public class BooleanTest {
    private BinsonObject obj = new BinsonObject().put("b", true);
    
    @Test
    public void testGet() {
        assertEquals(true, obj.getBoolean("b"));
    }

    @Test
    public void testHas() {
        assertTrue(obj.hasBoolean("b"));
        assertFalse(obj.hasBoolean("a"));
    }
    
    @Test(expected=FormatException.class)
    public void testNonExistant() {
        BinsonObject obj = new BinsonObject().put("b", false);
        obj.getBoolean("a");
    }
}
