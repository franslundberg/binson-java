package org.binson;

import org.junit.Assert;
import org.junit.Test;

public class BinsonArrayTest {

    @Test
    public void testSanity() {
        BinsonArray a = new BinsonArray().add(1234);
        Assert.assertEquals(1234, a.getInteger(0));
    }
    
    @Test(expected=FormatException.class)
    public void testThatGetIntegerThrowsExceptionWhenFieldHasUnexpectedType() {
        BinsonArray a = new BinsonArray().add(12.33);
        a.getInteger(0);
    }
    
    @Test
    public void testGetBoolean1() {
        BinsonArray a = new BinsonArray().add(true);
        Assert.assertEquals(true, a.getBoolean(0));
    }
    
    @Test
    public void testGetBoolean2() {
        BinsonArray a = new BinsonArray().add(false).add(true).add(true);
        Assert.assertEquals(false, a.getBoolean(0));
        Assert.assertEquals(true, a.getBoolean(1));
        Assert.assertEquals(true, a.getBoolean(2));
    }
    
    @Test(expected=FormatException.class)
    public void testGetBoolean3() {
        BinsonArray a = new BinsonArray().add(false).add(1234);
        Assert.assertEquals(false, a.getBoolean(0));
        Assert.assertEquals(true, a.getBoolean(1));
    }
    
    @Test
    public void testGetInteger1() {
        BinsonArray a = new BinsonArray().add("hello").add(1234);
        Assert.assertEquals(1234, a.getInteger(1));
    }
    
    @Test(expected=FormatException.class)
    public void testGetInteger2() {
        BinsonArray a = new BinsonArray().add("hello").add(1234);
        a.getInteger(0);
    }
    
    @Test
    public void testGetDouble1() {
        BinsonArray a = new BinsonArray().add(12.33);
        Assert.assertEquals(12.33, a.getDouble(0), 1e-9);
    }
    
    @Test(expected=FormatException.class)
    public void testGetDouble2() {
        BinsonArray a = new BinsonArray().add(12.33).add("hello");
        a.getDouble(1);
    }
    
    @Test
    public void testGetString1() {
        BinsonArray a = new BinsonArray().add("hello");
        Assert.assertEquals("hello", a.getString(0));
    }
    
    @Test(expected=FormatException.class)
    public void testGetString2() {
        BinsonArray a = new BinsonArray().add(1234);
        a.getString(0);
    }
    
    @Test
    public void testGetBytes1() {
        BinsonArray a = new BinsonArray().add("hello").add(new byte[]{1, 2, 3});
        Assert.assertArrayEquals(new byte[]{1, 2, 3}, a.getBytes(1));
    }
    
    @Test(expected=FormatException.class)
    public void testGetBytes2() {
        BinsonArray a = new BinsonArray().add("hello").add(new byte[]{1, 2, 3});
        a.getBytes(0);
    }
    
    @Test
    public void testGetArray1() {
        BinsonArray a = new BinsonArray()
                .add("hello")
                .add(new BinsonArray().add(1).add(2));
        Assert.assertEquals("hello", a.getString(0));
        BinsonArray inner = a.getArray(1);
        Assert.assertEquals(1, inner.getInteger(0));
        Assert.assertEquals(2, inner.getInteger(1));
    }
    
    @Test(expected=FormatException.class)
    public void testGetArray2() {
        BinsonArray a = new BinsonArray()
                .add("hello")
                .add(new BinsonArray().add(1).add(2));
        a.getArray(0);
    }
    
    @Test
    public void testGetObject1() {
        BinsonArray a = new BinsonArray()
                .add("hello")
                .add(new Binson().put("one", 11).put("two", 22));
        Binson inner = a.getObject(1);
        Assert.assertEquals(11, inner.getInteger("one"));
        Assert.assertEquals(22, inner.getInteger("two"));
    }
    
    @Test(expected=FormatException.class)
    public void testGetObject2() {
        BinsonArray a = new BinsonArray()
                .add("hello")
                .add(new Binson().put("one", 11).put("two", 22));
        a.getObject(0);
    }
}
