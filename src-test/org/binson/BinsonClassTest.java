package org.binson;

import org.junit.Assert;
import org.junit.Test;

public class BinsonClassTest {

    @Test
    public void testToString() {
        Assert.assertTrue(new Binson().put("m", "Hello").toString().contains("Hello"));
    }
    
    @Test
    public void testPutValue() {
        Binson b = new Binson().putValue("k1", Boolean.TRUE);
        Assert.assertEquals(true, b.getBoolean("k1"));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testPutNullValue() {
        new Binson().putValue(null, "hello");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testGetWithNullName() {
        String fieldName = null;
        new Binson().getBoolean(fieldName);
    }
    
    @Test
    public void testEquals() {
        Binson b1 = new Binson().put("k", "v1");
        Binson b2 = new Binson().put("k", "v1");
        Binson b3 = new Binson().put("k", "v2");
        Assert.assertEquals(true, b1.equals(b2));
        Assert.assertEquals(true, b2.equals(b1));
        Assert.assertEquals(false, b1.equals(b3));
        Assert.assertEquals(false, b3.equals(b1));
    }
    
    @Test
    public void testHashCode() {
        Binson b1 = new Binson().put("k", "v1");
        Binson b2 = new Binson().put("k", "v1");
        Binson b3 = new Binson().put("k", "v2");
        Assert.assertEquals(b1.hashCode(), b2.hashCode());
        Assert.assertNotEquals(b1.hashCode(), b3.hashCode());
    }
   
    @Test
    public void testContainsKey() {
        Assert.assertTrue(new Binson().put("k1", "v1").containsKey("k1"));
        Assert.assertFalse(new Binson().put("k1", "v1").containsKey("k2"));
    }
    
    @Test
    public void testRemove() {
        Binson b = new Binson().put("k1", "v1").put("k2", "v2");
        b.remove("k1");
        Assert.assertEquals(false, b.hasString("k1"));
        Assert.assertEquals(true, b.hasString("k2"));
    }
}
