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
        Binson obj = new Binson().put("i", 111222333444L);
        assertEquals(INT64_BYTE_SIZE, obj.toBytes().length);
    }
    
    @Test
    public void byteSize2() {
        Binson obj = new Binson().put("i", Long.MIN_VALUE);
        assertEquals(INT64_BYTE_SIZE, obj.toBytes().length);
    }
    
    @Test
    public void int1() {
        Binson obj = new Binson().put("i", 111222333444L);
        byte[] bytes = obj.toBytes();
        Binson obj2 = Binson.fromBytes(bytes);
        
        assertEquals(111222333444L, obj2.getInteger("i"));
    }
    
    @Test
    public void boundaryValues() {
        Binson obj = new Binson();
        obj.put("i0", Long.MIN_VALUE);
        obj.put("i1", Long.MAX_VALUE);
        Binson obj2 = Binson.fromBytes(obj.toBytes());
        
        assertEquals(Long.MIN_VALUE, obj2.getInteger("i0"));
        assertEquals(Long.MAX_VALUE, obj2.getInteger("i1"));
    }
}
