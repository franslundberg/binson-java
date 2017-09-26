package org.binson;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests for storing a boolean value in a Binson object.
 * 
 * @author Frans Lundberg
 */
public class BooleanTest {
    private Binson obj = new Binson().put("b", true);
    
    @Test
    public void testGet() {
        assertEquals(true, obj.getBoolean("b"));
    }

    @Test
    public void testHas() {
        assertTrue(obj.hasBoolean("b"));
        assertFalse(obj.hasBoolean("a"));
    }
    
    @Test(expected=BinsonFormatException.class)
    public void testNonExistant() {
        Binson obj = new Binson().put("b", false);
        obj.getBoolean("a");
    }
}
