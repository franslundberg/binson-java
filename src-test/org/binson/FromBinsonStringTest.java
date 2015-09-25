package org.binson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.binson.lowlevel.StringFormatException;
import org.junit.Test;

public class FromBinsonStringTest {
    @Test
    public void testEmpty() {
        String s = "{}";
        Binson obj = Binson.fromBinsonString(s);
        assertEquals(0, obj.size());
        assertEquals("{}", obj.toBinsonString());
    }
    
    @Test
    public void testOneField() {
        String s = "{a:true}";
        Binson obj = Binson.fromBinsonString(s);
        assertEquals(1, obj.size());
        assertEquals(true, obj.getBoolean("a"));
    }
    
    @Test
    public void testQuotedName() {
        String s = "{\"k1\":\"v1\" \" k 2  \":\"v2\"}";
        Binson obj = Binson.fromBinsonString(s);
        assertEquals("v1", obj.getString("k1"));
        assertEquals("v2", obj.getString(" k 2  "));
    }
    
    @Test
    public void testTwoFields() {
        String s = "{a:true b:false}";
        Binson obj = Binson.fromBinsonString(s);
        assertEquals(true, obj.getBoolean("a"));
        assertEquals(false, obj.getBoolean("b"));
    }
    
    @Test
    public void testTwoFieldsWithExtraWhitespace() {
        String s = "{ \n a : true  \t\n\r b : false\n}\n\r";
        Binson obj = Binson.fromBinsonString(s);
        assertEquals(true, obj.getBoolean("a"));
        assertEquals(false, obj.getBoolean("b"));
    }
    
    @Test
    public void testString() {
        String s = "{name:\"Ove\"}";
        Binson obj = Binson.fromBinsonString(s);
        assertEquals("Ove", obj.getString("name"));
    }
    
    @Test
    public void testMaxInteger() {
        String s = "{a:9223372036854775807}";    // 2^63-1
        Binson obj = Binson.fromBinsonString(s);
        assertEquals(9223372036854775807L, obj.getInteger("a"));
    }
    
    @Test(expected=StringFormatException.class)
    public void testToBigInteger() {
        String s = "{a:9223372036854775808}";    // 2^63
        Binson.fromBinsonString(s);
    }
    
    @Test
    public void testFraction1() {
        String s = "{a:1.2}";
        assertTrue(1.2 == Binson.fromBinsonString(s).getDouble("a"));
    }
    
    @Test
    public void testFraction2() {
        String s = "{a:-0.00033}";
        assertTrue(-0.00033 == obj(s).getDouble("a"));
    }
    
    @Test
    public void testE1() {
        String s = "{a:1e9}";
        assertTrue(1e9 == obj(s).getDouble("a"));
    }
    
    @Test
    public void testE2() {
        String s = "{a:0.23E-9}";
        assertTrue(2.3E-10 == obj(s).getDouble("a"));
    }
    
    @Test
    public void testE3() {
        String s = "{a:-0.23e123}";
        assertTrue(-0.23e123 == obj(s).getDouble("a"));
    }
    
    @Test
    public void testInnerObject1() {
        String s = "{a:{b:2}}";
        assertEquals(2, obj(s).getObject("a").getInteger("b"));
    }
    
    @Test
    public void testInnerObject2() {
        Binson o1 = obj("{a:{b:{c:{d:8 e:9}}}}");
        Binson o2 = o1.getObject("a").getObject("b").getObject("c");
        assertEquals(8, o2.getInteger("d"));
        assertEquals(9, o2.getInteger("e"));
    }
    
    @Test
    public void testEmptyArray() {
        Binson obj = obj("{a:[]}");
        BinsonArray arr = obj.getArray("a");
        assertEquals(0, arr.size());
    }
    
    @Test
    public void testArray1() {
        Binson obj = obj("{a:[-7]}");
        BinsonArray arr = obj.getArray("a");
        assertEquals(1, arr.size());
        assertEquals(-7, arr.getInteger(0));
    }
    
    @Test
    public void testArray2() {
        Binson obj = obj("{a : [1 2.3 \"hi!\"]}");
        BinsonArray arr = obj.getArray("a");
        assertEquals(3, arr.size());
        assertEquals(1, arr.getInteger(0));
        assertTrue(2.3 == arr.getDouble(1));
        assertEquals("hi!", arr.getString(2));
    }
    
    @Test
    public void testNestedArray() {
        Binson obj = obj("{ a : [9 [2 \"w\"]] }");
        BinsonArray a = obj.getArray("a");
        assertEquals(9, a.getInteger(0));
        assertEquals(2, a.getArray(1).getInteger(0));
        assertEquals("w", a.getArray(1).getString(1));
    }
    
    // TODO add a test for line, column of BinsonStringParseException.
    
    // ======== Helpers ========
    
    private Binson obj(String s) {
        return Binson.fromBinsonString(s);
    }
}
