package org.binson;

import static org.junit.Assert.*;

import org.binson.lowlevel.Constants;
import org.junit.Test;

/**
 * General tests for the Binson object structure; arrays, nested objects and mixed types.
 *
 * @author Frans Lundberg
 */
public class BinsonObjectTest {

    @Test
    public void testPackageDocExample1() {
        Binson obj = new Binson().put("a", 123).put("s", "Hello world!");
        byte[] bytes = obj.toBytes();
        Binson obj2 = Binson.fromBytes(bytes);
        assert obj2.equals(obj);
        assertEquals(obj2, obj);
    }
    
    @Test
    public void putAndGetInteger() {
        Binson obj = new Binson();
        obj.put("a", 1);
        assertEquals(1, obj.getInteger("a"));
    }
    
    @Test
    public void putAndGetDouble() {
        Binson obj = new Binson();
        obj.put("a", 1.23);
        assertEquals(1.23, obj.getDouble("a"), 1e-6);
    }

    
    @Test
    public void putAndGetString() {
        Binson obj = new Binson();
        obj.put("key", "value");
        assertEquals("value", obj.getString("key"));
    }
    
    @Test
    public void checkBytesOfEmptyObject() {
        byte[] bytes = new Binson().toBytes();
        assertArrayEquals(new byte[]{Constants.BEGIN, Constants.END}, bytes);
    }
    
    @Test
    public void emptyObjectToBytesAndBack() {
        Binson obj = new Binson();
        byte[] bytes = obj.toBytes();
        Binson obj2 = Binson.fromBytes(bytes);
        
        assertArrayEquals(bytes, obj2.toBytes());
        assertEquals(0, obj2.size());
    }
    
    @Test
    public void oneString() {
        Binson obj = new Binson();
        obj.put("name", "value");
        byte[] bytes = obj.toBytes();
        Binson obj2 = Binson.fromBytes(bytes);
        
        assertEquals("value", obj2.getString("name"));
        assertEquals(1, obj2.size());
    }
    
    @Test
    public void twoStrings() {
        Binson obj = new Binson().put("s1", "v1").put("s2", "v2");
        byte[] bytes = obj.toBytes();
        Binson obj2 = Binson.fromBytes(bytes);
        
        assertEquals("v1", obj2.getString("s1"));
        assertEquals("v2", obj2.getString("s2"));
        assertEquals(2, obj2.size());
    }
    
    @Test
    public void allPrimitiveTypes() {
        byte[] bytes = new byte[]{1, 2, 3};
        
        Binson obj = new Binson();
        obj.put("integer", 12);
        obj.put("double", 123.45);
        obj.put("string", "string-value");
        obj.put("bytes", bytes);
        
        Binson obj2 = Binson.fromBytes(obj.toBytes());
        assertEquals(4, obj2.size());
        assertEquals(12, obj2.getInteger("integer"));
        assertEquals(123.45, obj2.getDouble("double"), 0.00001);
        assertEquals("string-value", obj2.getString("string"));
        assertArrayEquals(bytes, obj2.getBytes("bytes"));
    }
    
    @Test
    public void nestedObject() {
        // obj = {"nested": {"a": 123}}
        Binson obj = new Binson().put("nested", new Binson().put("a", 123));
        Binson obj2 = Binson.fromBytes(obj.toBytes());
        Binson nested2 = obj2.getObject("nested");
        
        assertEquals(1, obj2.size());
        assertEquals(1, nested2.size());
        assertEquals(123, nested2.getInteger("a"));
        assertArrayEquals(obj.toBytes(), obj2.toBytes());
    }
    
    @Test
    public void deeplyNestedObject() {
        // obj = {n1: {n2, {n3, {n4, "value"}}}}
        
        Binson obj = new Binson()
            .put("n1", new Binson()
                .put("n2", new Binson()
                    .put("n3", new Binson()
                        .put("n4", "value")
                    )
                )
            );
        
        assertEquals("value", obj.getObject("n1").getObject("n2").getObject("n3").getString("n4"));
    }

    @Test
    public void array() {
        // obj = {"a": [123]}
        
        Binson obj = new Binson().put("a", new BinsonArray().add(123));
        Binson obj2 = Binson.fromBytes(obj.toBytes());
        
        assertEquals(1, obj2.size());
        assertEquals(123, obj2.getArray("a").getInteger(0));
        assertEquals(1, obj2.getArray("a").size());
    }
    
    @Test
    public void deeplyNestedArray() {
        // obj = {a: [[[3]]]}
        Binson obj = new Binson();
        obj.put("a",
            new BinsonArray().add(
                new BinsonArray().add(
                    new BinsonArray().add(3)
                )
            )
        );
    }
    
    @Test
    public void arrayWithAllTypes() {
        // obj = {a: [true, 1, 1.23, "s", <bytes>, [2], {a:3}]}
        BinsonArray arr = new BinsonArray();
        Binson obj = new Binson().put("a", arr);
        arr.add(true)
            .add(1)
            .add(1.23)
            .add("s")
            .add(new byte[1])
            .add(new BinsonArray().add(2))
            .add(new Binson().put("a", 3));
        Binson obj2 = Binson.fromBytes(obj.toBytes());
        BinsonArray arr2 = obj2.getArray("a");
        
        assertEquals(7, arr2.size());
        assertEquals(true, arr2.getBoolean(0));
        assertEquals(1, arr2.getInteger(1));
        assertEquals(1.23, arr2.getDouble(2), 1e-6);
        assertEquals("s", arr2.getString(3));
        assertArrayEquals(new byte[1], arr2.getBytes(4));
        assertEquals(2, arr2.getArray(5).getInteger(0));
        assertEquals(3, arr2.getObject(6).getInteger("a"));
    }
}
