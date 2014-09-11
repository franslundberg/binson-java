package org.binson;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests for integers stored in eight bytes (value between -2^63 and 2^63-1).
 *
 * @author Frans Lundberg
 */
public class Int64Test {
    
    /** Byte size of minimal binson object with one integer field. */
    private static int INT64_BYTE_SIZE = 1 + (1+1+1) + (1+8) + 1;

    @Test
    public void byteSize1() {
        BinsonObject obj = new BinsonObject().put("i", 111222333444L);
        assertEquals(INT64_BYTE_SIZE, obj.toBytes().length);
    }
    
    @Test
    public void byteSize2() {
        BinsonObject obj = new BinsonObject().put("i", Long.MIN_VALUE);
        assertEquals(INT64_BYTE_SIZE, obj.toBytes().length);
    }
    
    @Test
    public void int1() {
        BinsonObject obj = new BinsonObject().put("i", 111222333444L);
        byte[] bytes = obj.toBytes();
        BinsonObject obj2 = BinsonObject.fromBytes(bytes);
        
        assertEquals(111222333444L, obj2.getInteger("i"));
    }
    
    @Test
    public void boundaryValues() {
        BinsonObject obj = new BinsonObject();
        obj.put("i0", Long.MIN_VALUE);
        obj.put("i1", Long.MAX_VALUE);
        BinsonObject obj2 = BinsonObject.fromBytes(obj.toBytes());
        
        assertEquals(Long.MIN_VALUE, obj2.getInteger("i0"));
        assertEquals(Long.MAX_VALUE, obj2.getInteger("i1"));
    }
}
