package org.binson;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests BinsonObject.equals() / hashCode().
 *
 * @author Frans Lundberg
 */
public class EqualsTest {
    
    @Test
    public void testEquals1() {
        BinsonObject o1 = new BinsonObject();
        BinsonObject o2 = new BinsonObject();
        assertTrue(o1.equals(o2));
        assertTrue(o2.equals(o1));
    }
    
    @Test
    public void testEquals2() {
        BinsonObject o1 = new BinsonObject().put("k1", 12).put("k2", -1234);
        BinsonObject o2 = new BinsonObject().put("k1", 12).put("k2", -1234);
        assertTrue(o1.equals(o2));
        assertTrue(o2.equals(o1));
    }
    
    @Test
    public void testEquals3() {
        BinsonObject o1 = new BinsonObject().put("k1", 12).put("k2", -1234);
        BinsonObject o2 = new BinsonObject().put("k1", 12).put("k2", -5555);
        assertFalse(o1.equals(o2));
        assertFalse(o2.equals(o1));
    }
    
    @Test
    public void testEquals4() {
        BinsonObject o1 = new BinsonObject().put("k", "one");
        BinsonObject o2 = new BinsonObject().put("k", "one").put("k2", "two");
        assertFalse(o1.equals(o2));
        assertFalse(o2.equals(o1));
    }
    
    @Test
    public void testEquals5() {
        BinsonObject o1 = new BinsonObject().put("k", "one");
        BinsonObject o2 = new BinsonObject().put("k", "two").put("k", "one");
        assertTrue(o1.equals(o2));
        assertTrue(o2.equals(o1));
    }
}
