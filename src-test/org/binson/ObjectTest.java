package org.binson;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests storing a BinsonObject in a BinsonObject.
 * 
 * @author Frans Lundberg
 */
public class ObjectTest {
    private BinsonObject obj;
    
    public ObjectTest() {
        BinsonObject inner = new BinsonObject();
        obj = new BinsonObject().put("a", inner);
    }

    @Test
    public void testGet() {
        BinsonObject expected = new BinsonObject();
        assertEquals(expected, obj.getObject("a"));
    }
    
    @Test
    public void testHas() {
        assertTrue(obj.hasObject("a"));
        assertFalse(obj.hasObject("b"));
        assertFalse(obj.hasString("a"));
    }
    
    @Test(expected=FormatException.class)
    public void testNonExistant() {
        obj.getObject("b");
    }
}
