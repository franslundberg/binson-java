package org.binson;

import static org.junit.Assert.*;

import org.binson.lowlevel.Hex;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for storing integers.
 * 
 * @author Frans Lundberg
 */
public class IntegerTest {
    private Binson obj = new Binson().put("a", 1234);
    
    @Test
    public void testGet() {
        assertEquals(1234, obj.getInteger("a"));
    }
    
    @Test
    public void testHas() {
        assertTrue(obj.hasInteger("a"));
        assertFalse(obj.hasInteger("b"));
        assertFalse(obj.hasBoolean("a"));
    }
    
    @Test(expected=BinsonFormatException.class)
    public void testNonExistant1() {
        new Binson().getInteger("b");
    }
    
    @Test(expected=BinsonFormatException.class)
    public void testNonExistant2() {
        new Binson().put("bbb", 1).getInteger("b");
    }
    
    @Test
    public void checkThatInt8CannotBeStoredAsInt16() {
        // 
        // We hand-create a byte array where an int of value 3 is incorrectly stored in
        // 16 bits instead of in 8 bits. We check that a BinsonFormatException is thrown
        // as expected.
        //
        // Relevant examples of correct Binson data:
        // new Binson().put("a", 3);         {"a":3} =       40140161100341
        // new Binson().put("a", 3 + 256);   b2: {"a":259} = 4014016111030141
        //
        
        Binson.fromBytes(Hex.toBytes("40140161100341"));
        Binson.fromBytes(Hex.toBytes("4014016111030141"));
        
        Exception ex = null;
        try {
            Binson.fromBytes(Hex.toBytes("4014016111030041"));
        } catch (BinsonFormatException e) {
            ex = e;
        }
        
        Assert.assertNotNull("invalid input parsed without exception", ex);
        Assert.assertEquals(ex.getClass(), BinsonFormatException.class);
    }
    
    @Test
    public void checkThatInt16CannotBeStoredAsInt32() {
        // 
        // We hand-create a byte array where an int of value 3+256 is incorrectly stored in
        // 4 bytes instead of in 2 bytes. We check that a BinsonFormatException is thrown
        // as expected.
        //
        // Relevant examples of correct Binson data:
        // b1: {"a":259}       4014016111030141          3 + 256
        // b2: {"a":65795}     40140161120301010041      3 + 256 + 256*256
        // incorrect:          40140161120301000041      3 + 256
        
        Binson.fromBytes(Hex.toBytes("4014016111030141"));
        Binson.fromBytes(Hex.toBytes("40140161120301010041"));
        
        Exception ex = null;
        try {
            Binson.fromBytes(Hex.toBytes("40140161120301000041"));
        } catch (BinsonFormatException e) {
            ex = e;
        }
        
        Assert.assertNotNull("invalid input parsed without exception", ex);
        Assert.assertEquals(ex.getClass(), BinsonFormatException.class);
    }
    
    @Test
    public void checkThatInt32CannotBeStoredAsInt64() {
        // 
        // We hand-create a byte array where an int of value 3+256+256*256 is incorrectly stored in
        // 8 bytes instead of in 4 bytes. We check that a BinsonFormatException is thrown
        // as expected.
        //
        // Relevant examples of correct Binson data:
        //
        // b1: {"a":65795}       40140161120301010041              3 + 256 + 256*256
        // b2: {"a":4295033091}  4014016113030101000100000041      3 + 256 + 256*256 + 256L*256*256*256
        // incorrect:            4014016113030101000000000041      3 + 256 + 256*256
        
        Binson.fromBytes(Hex.toBytes("40140161120301010041"));
        Binson.fromBytes(Hex.toBytes("4014016113030101000100000041"));
        
        Exception ex = null;
        try {
            Binson.fromBytes(Hex.toBytes("4014016113030101000000000041"));
        } catch (BinsonFormatException e) {
            ex = e;
        }
        
        Assert.assertNotNull("invalid input parsed without exception", ex);
        Assert.assertEquals(ex.getClass(), BinsonFormatException.class);
    }
    
    public static void main(String[] args) {
        
        Binson b1 = new Binson().put("a", 3 + 256 + 256*256);
        Binson b2 = new Binson().put("a", 3 + 256 + 256*256 + 256L*256*256*256);
        
        System.out.println("b1: " + b1.toJson() + "\n" + Hex.create(b1.toBytes()));
        System.out.println("b2: " + b2.toJson() + "\n" + Hex.create(b2.toBytes()));
    }
}
