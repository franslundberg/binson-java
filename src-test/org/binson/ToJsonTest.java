package org.binson;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests BinsonObject.toJson().
 * 
 * @author Frans Lundberg
 */
public class ToJsonTest {
    
    @Test
    public void testBytes1() {
        Binson obj = new Binson().put("bytes", new byte[]{0, 1, 16*2+9, (byte)255});
        String expected = "{\"bytes\":\"0x000129ff\"}";
        assertEquals(expected, obj.toJson());
    }
    
    @Test
    public void testBytes2() {
        Binson obj = new Binson().put("bytes", new byte[]{10});
        String expected = "{\"bytes\":\"0x0a\"}";
        assertEquals(expected, obj.toJson());
    }
    
    @Test
    public void testBytes3() {
        Binson obj = new Binson().put("bytes", new byte[]{});
        String expected = "{\"bytes\":\"0x\"}";
        assertEquals(expected, obj.toJson());
    }
    
    @Test
    public void testArray() {
        String expected = "{\"a\":[1,2,3]}";
        Binson obj = new Binson().put("a", new BinsonArray().add(1).add(2).add(3));
        assertEquals(expected, obj.toJson());
    }
}
