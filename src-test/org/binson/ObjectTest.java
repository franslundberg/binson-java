package org.binson;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests storing a BinsonObject in a BinsonObject.
 * 
 * @author Frans Lundberg
 */
public class ObjectTest {
    private Binson obj;
    
    public ObjectTest() {
        Binson inner = new Binson();
        obj = new Binson().put("a", inner);
    }

    @Test
    public void testGet() {
        Binson expected = new Binson();
        assertEquals(expected, obj.getObject("a"));
    }
    
    @Test
    public void testHas() {
        assertTrue(obj.hasObject("a"));
        assertFalse(obj.hasObject("b"));
        assertFalse(obj.hasString("a"));
    }
    
    @Test(expected=BinsonFormatException.class)
    public void testNonExistant() {
        obj.getObject("b");
    }
}
