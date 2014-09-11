package org.binson;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests BinsonObject.toBinsonString(), that is conversion from a BinsonObject
 * to a Binson string.
 *
 * @author Frans Lundberg
 */
public class ToBinsonStringTest {
    @Test
    public void testEmpty() {
        BinsonObject obj = new BinsonObject();
        String binsonString = obj.toBinsonString();
        assertEquals("{}", binsonString);
    }
    
    @Test
    public void testOneField() {
        BinsonObject obj = new BinsonObject().put("k", "v");
        String binsonString = obj.toBinsonString();
        assertEquals("{k=\"v\"}", binsonString);
    }
    
    @Test
    public void testEscapedKey1() {
        BinsonObject obj = new BinsonObject().put("a b", "v");
        String binsonString = obj.toBinsonString();
        assertEquals("{\"a b\"=\"v\"}", binsonString);
    }
    
    @Test
    public void testEscapedKey2() {
        BinsonObject obj = new BinsonObject().put("a\"b", "v");
        String binsonString = obj.toBinsonString();
        String expected = "{\"a\\\"b\"=\"v\"}";
        assertEquals(expected, binsonString);
    }
    
    @Test
    public void testEscapedKey3() {
        BinsonObject obj = new BinsonObject().put("a\tb", "v");
        String binsonString = obj.toBinsonString();
        assertEquals("{\"a\tb\"=\"v\"}", binsonString);
    }
    
    @Test
    public void testTwoFields() {
        BinsonObject obj = new BinsonObject().put("a", 1).put("b", 2);
        String binsonString = obj.toBinsonString();
        assertEquals("{a=1, b=2}", binsonString);
    }
}
