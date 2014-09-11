package org.binson;

import static org.junit.Assert.*;

import org.binson.lowlevel.Constants;
import org.junit.Test;

/**
 * Tests for integers stored in four bytes (value between -2^31 and 2^31-1).
 *
 * @author Frans Lundberg
 */
public class Int32Test {
    
    /** Byte size of minimal binson object with one integer field. */
    private static int INT32_BYTE_SIZE = 1 + (1+1+1) + (1+4) + 1;

    @Test
    public void byteSize1() {
        BinsonObject obj = new BinsonObject().put("i", 111222333);
        assertEquals(INT32_BYTE_SIZE, obj.toBytes().length);
    }
    
    @Test
    public void byteSize2() {
        BinsonObject obj = new BinsonObject().put("i", -Constants.TWO_TO_31);
        assertEquals(INT32_BYTE_SIZE, obj.toBytes().length);
    }
    
    @Test
    public void byteSize3() {
        BinsonObject obj = new BinsonObject().put("i", -Constants.TWO_TO_31 - 1);
        assertTrue(obj.toBytes().length > INT32_BYTE_SIZE);
    }
    
    @Test
    public void int1() {
        BinsonObject obj = new BinsonObject().put("i", 111222333);
        byte[] bytes = obj.toBytes();
        BinsonObject obj2 = BinsonObject.fromBytes(bytes);
        
        assertEquals(111222333, obj2.getInteger("i"));
    }
    
    @Test
    public void boundaryValues() {
        BinsonObject obj = new BinsonObject();
        obj.put("i0", -Constants.TWO_TO_31);
        obj.put("i3", Constants.TWO_TO_31-1);
        BinsonObject obj2 = BinsonObject.fromBytes(obj.toBytes());
        
        assertEquals(-Constants.TWO_TO_31, obj2.getInteger("i0"));
        assertEquals(Constants.TWO_TO_31-1, obj2.getInteger("i3"));
    }
}
