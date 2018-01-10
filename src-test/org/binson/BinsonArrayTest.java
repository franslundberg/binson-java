package org.binson;

import org.junit.Assert;
import org.junit.Test;

public class BinsonArrayTest {

    @Test
    public void testSanity() {
        BinsonArray a = new BinsonArray().add(1234);
        Assert.assertEquals(1234, a.getInteger(0));
    }
    
    @Test(expected=BinsonFormatException.class)
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
    
    @Test(expected=BinsonFormatException.class)
    public void testGetBoolean3() {
        BinsonArray a = new BinsonArray().add(false).add(1234);
        Assert.assertEquals(false, a.getBoolean(0));
        Assert.assertEquals(true, a.getBoolean(1));
    }
    
    @Test
    public void testIsBoolean() {
        BinsonArray a = new BinsonArray().add(false).add(1234);
        Assert.assertEquals(true, a.isBoolean(0));
        Assert.assertEquals(false, a.isBoolean(1));
    }
    
    @Test
    public void testGetInteger1() {
        BinsonArray a = new BinsonArray().add("hello").add(1234);
        Assert.assertEquals(1234, a.getInteger(1));
    }
    
    @Test(expected=BinsonFormatException.class)
    public void testGetInteger2() {
        BinsonArray a = new BinsonArray().add("hello").add(1234);
        a.getInteger(0);
    }
    
    @Test
    public void testIsInteger() {
        BinsonArray a = new BinsonArray().add("hello").add(1234);
        Assert.assertEquals(false, a.isInteger(0));
        Assert.assertEquals(true, a.isInteger(1));
    }
    
    @Test
    public void testGetDouble1() {
        BinsonArray a = new BinsonArray().add(12.33);
        Assert.assertEquals(12.33, a.getDouble(0), 1e-9);
    }
    
    @Test(expected=BinsonFormatException.class)
    public void testGetDouble2() {
        BinsonArray a = new BinsonArray().add(12.33).add("hello");
        a.getDouble(1);
    }
    
    @Test
    public void testIsDouble() {
        BinsonArray a = new BinsonArray().add(12.33).add("hello");
        Assert.assertEquals(true, a.isDouble(0));
        Assert.assertEquals(false, a.isDouble(1));
    }
    
    @Test
    public void testGetString1() {
        BinsonArray a = new BinsonArray().add("hello");
        Assert.assertEquals("hello", a.getString(0));
    }
    
    @Test(expected=BinsonFormatException.class)
    public void testGetString2() {
        BinsonArray a = new BinsonArray().add(1234);
        a.getString(0);
    }
    
    @Test
    public void testIsString() {
        BinsonArray a = new BinsonArray().add("myString").add(1.23);
        Assert.assertEquals(true, a.isString(0));
        Assert.assertEquals(false, a.isString(1));
    }
    
    @Test
    public void testGetBytes1() {
        BinsonArray a = new BinsonArray().add("hello").add(new byte[]{1, 2, 3});
        Assert.assertArrayEquals(new byte[]{1, 2, 3}, a.getBytes(1));
    }
    
    @Test(expected=BinsonFormatException.class)
    public void testGetBytes2() {
        BinsonArray a = new BinsonArray().add("hello").add(new byte[]{1, 2, 3});
        a.getBytes(0);
    }
    
    @Test
    public void testIsBytes() {
        BinsonArray a = new BinsonArray()
                .add("hello")
                .add(new byte[1]);
        Assert.assertEquals(false, a.isBytes(0));
        Assert.assertEquals(true, a.isBytes(1));
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
    
    @Test(expected=BinsonFormatException.class)
    public void testGetArray2() {
        BinsonArray a = new BinsonArray()
                .add("hello")
                .add(new BinsonArray().add(1).add(2));
        a.getArray(0);
    }
    
    @Test
    public void testIsArray() {
        BinsonArray a = new BinsonArray()
                .add("hello")
                .add(new BinsonArray().add(1).add(2));
        Assert.assertEquals(false, a.isArray(0));
        Assert.assertEquals(true, a.isArray(1));
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
    
    @Test(expected=BinsonFormatException.class)
    public void testGetObject2() {
        BinsonArray a = new BinsonArray()
                .add("hello")
                .add(new Binson().put("one", 11).put("two", 22));
        a.getObject(0);
    }
    
    @Test
    public void testIsObject() {
        BinsonArray a = new BinsonArray()
                .add("hello")
                .add(new Binson().put("one", 11).put("two", 22));
        Assert.assertEquals(false, a.isObject(0));
        Assert.assertEquals(true, a.isObject(1));
    }
    
    @Test
    public void testToString() {
        BinsonArray a = new BinsonArray().add("hello");
        Assert.assertTrue(a.toString().contains("hello"));
    }
    
    @Test
    public void testAddElement() {
        BinsonArray a = new BinsonArray().addElement("Hello");
        Assert.assertEquals("Hello", a.getString(0));
    }
    
    @Test
    public void testEquals1() {
        BinsonArray a1 = new BinsonArray().add("Hello");
        BinsonArray a2 = new BinsonArray().add("Hello");
        Assert.assertEquals(true, a1.equals(a2));
    }
    
    @Test
    public void testEquals2() {
        BinsonArray a1 = new BinsonArray().add("Hello");
        BinsonArray a2 = null;
        Assert.assertEquals(false, a1.equals(a2));
    }
    
    @Test
    public void testEquals3() {
        BinsonArray a1 = new BinsonArray().add("Hello");
        Object a2 = new Object();
        Assert.assertEquals(false, a1.equals(a2));
    }
    
    @Test
    public void testHashCode1() {
        BinsonArray a1 = new BinsonArray().add("Hello");
        BinsonArray a2 = new BinsonArray().add("Hello");
        Assert.assertEquals(a1.hashCode(), a2.hashCode());
    }
    
    @Test
    public void testHashCode2() {
        BinsonArray a1 = new BinsonArray().add("Hello1");
        BinsonArray a2 = new BinsonArray().add("Hello2");
        Assert.assertNotEquals(a1.hashCode(), a2.hashCode());
    }
}
