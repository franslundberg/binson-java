package org.binson;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests storing a string in a BinsonObject.
 * 
 * @author Frans Lundberg
 */
public class StringTest {
    private Binson obj = new Binson().put("s", "hello");
    
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
    
    @Test(expected=BinsonFormatException.class)
    public void testNonExistant() {
        obj.getString("s2");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testNullString() {
        String value = null;
        obj.put("string", value);
    }
}
