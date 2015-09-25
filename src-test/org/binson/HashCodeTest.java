package org.binson;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests BinsonObject.hashCode().
 *
 * @author Frans Lundberg
 */
public class HashCodeTest {
    
    @Test
    public void test1() {
        Binson o1 = new Binson();
        Binson o2 = new Binson();
        assertEquals(o1.hashCode(), o2.hashCode());
    }

    @Test
    public void test2() {
        Binson o1 = new Binson().put("k1", "v2");
        Binson o2 = new Binson().put("k2", "v1");
        assertTrue(o1.hashCode() != o2.hashCode());
    }
    
    @Test
    public void test3() {
        Binson o1 = new Binson().put("k1", "v1").put("k2", 1);
        Binson o2 = new Binson().put("k1", "v1").put("k2", 1);
        assertTrue(o1.hashCode() == o2.hashCode());
    }
}
