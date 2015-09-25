package org.binson;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests for storing a double in a BinsonObject.
 * 
 * @author Frans Lundberg
 */
public class DoubleTest {
    private Binson obj = new Binson().put("a", 1.2);

    @Test
    public void testGet() {
        assertEquals(1.2, obj.getDouble("a"), 0.00001);
    }
    
    @Test
    public void testHas() {
        assertFalse(obj.hasDouble("b"));
        assertFalse(obj.hasBoolean("a"));
        assertFalse(obj.hasString("a"));
        assertTrue(obj.hasDouble("a"));
    }
    
    @Test(expected=FormatException.class)
    public void testNonExistant() {
        obj.getDouble("b");
    }
}
