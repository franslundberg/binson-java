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
        BinsonObject o1 = new BinsonObject();
        BinsonObject o2 = new BinsonObject();
        assertEquals(o1.hashCode(), o2.hashCode());
    }

    @Test
    public void test2() {
        BinsonObject o1 = new BinsonObject().put("k1", "v2");
        BinsonObject o2 = new BinsonObject().put("k2", "v1");
        assertTrue(o1.hashCode() != o2.hashCode());
    }
    
    @Test
    public void test3() {
        BinsonObject o1 = new BinsonObject().put("k1", "v1").put("k2", 1);
        BinsonObject o2 = new BinsonObject().put("k1", "v1").put("k2", 1);
        assertTrue(o1.hashCode() == o2.hashCode());
    }
}
