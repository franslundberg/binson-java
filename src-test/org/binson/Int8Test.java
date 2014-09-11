package org.binson;

import static org.junit.Assert.*;

import org.binson.lowlevel.Constants;
import org.junit.Test;

/**
 * Tests for integers stored in one single byte (value between -128 and 127).
 *
 * @author Frans Lundberg
 */
public class Int8Test {
    
    /** Byte size of minimal binson object with one integer field. */
    private static int INT8_BYTE_SIZE = 1 + (1+1+1) + (1+1) + 1;

    @Test
    public void byteSize1() {
        BinsonObject obj = new BinsonObject().put("i", 127);
        assertEquals(INT8_BYTE_SIZE, obj.toBytes().length);
    }
    
    @Test
    public void byteSize2() {
        BinsonObject obj = new BinsonObject().put("i", -128);
        assertEquals(INT8_BYTE_SIZE, obj.toBytes().length);
    }
    
    @Test
    public void int1() {
        BinsonObject obj = new BinsonObject().put("i", 123);
        byte[] bytes = obj.toBytes();
        
        byte[] expected = new byte[]{
                Constants.BEGIN,
                Constants.STRING1,
                1,
                (byte) 'i',
                Constants.INTEGER1,
                123,
                Constants.END
        };
        
        assertArrayEquals(expected, bytes);
    }
    
    @Test
    public void int1b() {
        BinsonObject obj = new BinsonObject().put("i", 123);
        BinsonObject obj2 = BinsonObject.fromBytes(obj.toBytes());
        
        assertEquals(1, obj2.size());
        assertEquals(123, obj2.getInteger("i"));
    }
    
    
    @Test
    public void int2() {
        BinsonObject obj = new BinsonObject().put("i0", -123).put("i1", 0).put("i2", 100);
        BinsonObject obj2 = BinsonObject.fromBytes(obj.toBytes());
        
        assertEquals(-123, obj2.getInteger("i0"));
        assertEquals(0, obj2.getInteger("i1"));
        assertEquals(100, obj2.getInteger("i2"));
    }
    
    @Test
    public void int3() {
        BinsonObject obj = new BinsonObject().put("i0", -123).put("i1", 0).put("i2", 100);
        BinsonObject obj2 = BinsonObject.fromBytes(obj.toBytes());
        
        assertArrayEquals(obj.toBytes(), obj2.toBytes());
    }
    
    @Test
    public void boundaryValues() {
        BinsonObject obj = new BinsonObject();
        obj.put("i0", -128).put("i1", -1).put("i2", 0).put("i3", 127);
        BinsonObject obj2 = BinsonObject.fromBytes(obj.toBytes());
        
        assertEquals(-128, obj2.getInteger("i0"));
        assertEquals(-1, obj2.getInteger("i1"));
        assertEquals(0, obj2.getInteger("i2"));
        assertEquals(127, obj2.getInteger("i3"));
    }
}
