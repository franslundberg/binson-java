package org.binson;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests that output works for subclasses of Binson and BinsonArray.
 * 
 * @author Frans Lundberg
 */
public class SubclassTest {

    @Test
    public void testBinsonSub() {
        BinsonSub sub = new BinsonSub();
        sub.put("field", "value");
        
        Binson b1 = new Binson();
        b1.put("a", sub);
        
        Binson b2 = new Binson();
        b2.put("a", new Binson().put("field", "value"));
        
        Assert.assertArrayEquals(b2.toBytes(), b1.toBytes());
    }
    
    @Test
    public void testBinsonArraySub() {
        BinsonArraySub sub = new BinsonArraySub();
        sub.add("first");
        
        Binson b1 = new Binson();
        b1.put("a", sub);
        
        Binson b2 = new Binson();
        b2.put("a", new BinsonArray().add("first"));
        
        Assert.assertArrayEquals(b2.toBytes(), b1.toBytes());
    }
    
    public static class BinsonSub extends Binson {}
    
    @SuppressWarnings("serial")
    public static class BinsonArraySub extends BinsonArray {}
}
