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
        Binson o1 = new Binson();
        Binson o2 = new Binson();
        assertTrue(o1.equals(o2));
        assertTrue(o2.equals(o1));
    }
    
    @Test
    public void testEquals2() {
        Binson o1 = new Binson().put("k1", 12).put("k2", -1234);
        Binson o2 = new Binson().put("k1", 12).put("k2", -1234);
        assertTrue(o1.equals(o2));
        assertTrue(o2.equals(o1));
    }
    
    @Test
    public void testEquals3() {
        Binson o1 = new Binson().put("k1", 12).put("k2", -1234);
        Binson o2 = new Binson().put("k1", 12).put("k2", -5555);
        assertFalse(o1.equals(o2));
        assertFalse(o2.equals(o1));
    }
    
    @Test
    public void testEquals4() {
        Binson o1 = new Binson().put("k", "one");
        Binson o2 = new Binson().put("k", "one").put("k2", "two");
        assertFalse(o1.equals(o2));
        assertFalse(o2.equals(o1));
    }
    
    @Test
    public void testEquals5() {
        Binson o1 = new Binson().put("k", "one");
        Binson o2 = new Binson().put("k", "two").put("k", "one");
        assertTrue(o1.equals(o2));
        assertTrue(o2.equals(o1));
    }
}
