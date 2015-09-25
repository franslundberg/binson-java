package org.binson;

import static org.junit.Assert.*;

import org.binson.lowlevel.Constants;
import org.junit.Test;

/**
 * Tests for integers stored in two bytes (value between -2^15 and 2^15-1).
 *
 * @author Frans Lundberg
 */
public class Int16Test {
    
    /** Byte size of minimal binson object with one integer field. */
    private static int INT16_BYTE_SIZE = 1 + (1+1+1) + (1+2) + 1;

    @Test
    public void byteSize1() {
        Binson obj = new Binson().put("i", 12345);
        assertEquals(INT16_BYTE_SIZE, obj.toBytes().length);
    }
    
    @Test
    public void byteSize2() {
        Binson obj = new Binson().put("i", -Constants.TWO_TO_15);
        assertEquals(INT16_BYTE_SIZE, obj.toBytes().length);
    }
    
    @Test
    public void byteSize3() {
        Binson obj = new Binson().put("i", -Constants.TWO_TO_15 - 1);
        assertTrue(obj.toBytes().length > INT16_BYTE_SIZE);
    }
    
    @Test
    public void int1() {
        Binson obj = new Binson().put("i", 12345);
        assertEquals(12345, obj.getInteger("i"));
        Binson obj2 = Binson.fromBytes(obj.toBytes());
        assertEquals(12345, obj2.getInteger("i"));
    }
    
    @Test
    public void boundaryValues() {
        Binson obj = new Binson();
        obj.put("i0", -Constants.TWO_TO_15).put("i1", -1).put("i2", 0)
                .put("i3", Constants.TWO_TO_15-1);
        Binson obj2 = Binson.fromBytes(obj.toBytes());
        
        assertEquals(-Constants.TWO_TO_15, obj2.getInteger("i0"));
        assertEquals(-1, obj2.getInteger("i1"));
        assertEquals(0, obj2.getInteger("i2"));
        assertEquals(Constants.TWO_TO_15-1, obj2.getInteger("i3"));
    }
}
