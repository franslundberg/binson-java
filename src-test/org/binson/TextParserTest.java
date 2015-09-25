package org.binson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;

import org.binson.lowlevel.JsonNull;
import org.binson.lowlevel.StringFormatException;
import org.binson.lowlevel.TextParser;
import org.binson.lowlevel.TextReader;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Frans Lundberg
 */
public class TextParserTest {
    private Binson obj;
    
    private void obj(String s) {
        StringReader reader = new StringReader(s);
        
        try {
            obj = TextParser.parse(reader);
        } catch (IOException e) {
            throw new Error("never happens");
        }
    }
    
    /** Creates JSON text of single member named 'a'. */
    private void a(String s) {
        obj("{\"a\": " + s + "}");
    }
    
    /** Like a(), but with Binson features allowed (special doubles, bytes). */
    private void aBinson(String s) {
        String s2 = "{\"a\": " + s + "}";
        StringReader reader = new StringReader(s2);
        TextReader textReader = new TextReader(reader);
        obj = new Binson();
        
        TextParser parser = new TextParser(textReader, obj) {
            @Override
            public boolean allowSpecialDoubles() {
                return true;
            }
            
            @Override
            public boolean allowNull() {
                return false;
            }
            
            public boolean allowBytes() {
                return true;
            }
        };
        
        try {
            parser.object();
        } catch (IOException e) {
            throw new Error("never happens");
        }
    }
    
    @Test
    public void testEmpty() {
        obj("{}");
        assertEquals(0, obj.size());
    }
    
    @Test
    public void testOneField() {
        a("true");
        assertEquals(true, obj.getBoolean("a"));
    }
    
    @Test(expected=StringFormatException.class)
    public void testTruE() {
        obj("{\"a\":truE}");
        assertEquals(true, obj.getBoolean("a"));
    }

    public void testFalse() {
        obj("{\"a\":false}");
        assertEquals(false, obj.getBoolean("a"));
    }
    
    @Test(expected=StringFormatException.class)
    public void testFalsE() {
        obj("{\"a\":falsE}");
    }
    
    @Test
    public void testNull() {
        obj("{\"a\":null}");
        assertEquals(JsonNull.NULL, obj.get("a"));
    }
    
    @Test(expected=StringFormatException.class)
    public void testNuLL() {
        obj("{\"a\":nuLL}");
    }
    
    @Test
    public void testTwoFields() {
        obj("{\"a\":true, \"b\":false}");
        assertEquals(true, obj.getBoolean("a"));
        assertEquals(false, obj.getBoolean("b"));
    }
    
    @Test
    public void testNestedObject() {
        obj("{\"a\":{\"b\":true}}");
        assertEquals(true, obj.getObject("a").getBoolean("b"));
    }
    
    @Test
    public void testDeeplyNested() {
        obj("{\"a\": {\"b\": {\"c\": {\"d\":true} } } }");
        assertEquals(true, obj.getObject("a").getObject("b").getObject("c").getBoolean("d"));
    }
    
    @Test
    public void testString() {
        obj("{\"a\":\"string\"}");
        assertEquals("string", obj.getString("a"));
    }
    
    @Test
    public void testEscapeTab() {
        obj("{\"a\":\"s\t\"}");
        assertEquals("s\t", obj.getString("a"));
    }
    
    @Test
    public void testEscapeQuotationMark() {
        obj("{\"a\": \"s\\\"\"}");   // string: s"
        assertEquals("s\"", obj.getString("a"));
    }
    
    @Test
    public void testEscapeBackSlash() {
        String s = "{\"a\": \"a\\\\z\"}";
        obj(s);
        assertEquals("a\\z", obj.getString("a"));
    }
    
    @Test
    public void testEscapeForwardSlash() {
        obj("{\"a\": \"s/\"}");
        assertEquals("s/", obj.getString("a"));
    }
    
    @Test
    public void testEscapeWhiteSpace() {
        obj("{\"a\": \"-\b-\f-\n-\r-\t-\"}");
        assertEquals("-\b-\f-\n-\r-\t-", obj.getString("a"));
    }
    
    @Test
    public void testUEscape1() {
        obj("{\"a\": \"-\u0041-\"}");   
        assertEquals("-A-", obj.getString("a"));
    }
    
    @Test
    public void testUEscape2() {
        // Capital Sigma, Σ, U+03A3     
        obj("{\"a\": \"-\u03A3-\"}");   
        assertEquals("-Σ-", obj.getString("a"));
    }
    
    @Test
    public void testUEscape3() {
        // Capital Sigma, Σ, U+03A3, lower-case.
        obj("{\"a\": \"-\u03a3-\"}");   
        assertEquals("-Σ-", obj.getString("a"));
    }
    
    @Test
    public void testSigmaCharacter() {
        // Capital Sigma, Σ, U+03A3. Non-ascii character in string.
        obj("{\"a\": \"-Σ-\"}");   
        assertEquals("-Σ-", obj.getString("a"));
    }
    
    @Test
    public void testArray1() {
        obj("{\"a\": []}");
        assertTrue(obj.hasArray("a"));
        assertEquals(0, obj.getArray("a").size());
    }
    
    @Test
    public void testArray2() {
        obj("{\"a\": [true,false]}");
        assertEquals(true, obj.getArray("a").getBoolean(0));
        assertEquals(false, obj.getArray("a").getBoolean(1));
    }
    
    @Test
    public void testArrayNested() {
        obj("{  \"a\": [true, [\"nested\"]]  }");
        assertEquals(true, obj.getArray("a").getBoolean(0));
        assertEquals("nested", obj.getArray("a").getArray(1).get(0));
    }
    
    @Test
    public void testInt1() {
        a("1");
        assertEquals(1, obj.getInteger("a"));
    }
    
    @Test
    public void testIntBig() {
        a("1222333444555");
        assertEquals(1222333444555L, obj.getInteger("a"));
    }
    
    @Test
    public void testInt0() {
        a("0");
        assertEquals(0, obj.getInteger("a"));
    }
    
    @Test(expected=StringFormatException.class)
    public void testIntPlus() {
        // This is not valid.
        a("+120");
        assertEquals(1, obj.getInteger("a"));
    }
    
    @Test
    public void testIntNeg() {
        a("-123");
        assertEquals(-123, obj.getInteger("a"));
    }
    
    @Test(expected=StringFormatException.class)
    public void testIntBad1() {
        a("-1222333444555666L");
    }
    
    @Test(expected=StringFormatException.class)
    public void testIntBad2() {
        a("-1222_333_444555666");
    }
    
    @Test
    public void testIntLargest() {
        a("9223372036854775807");
        assertEquals(9223372036854775807L, obj.getInteger("a"));
    }
    
    @Test
    public void testIntSmallest() {
        a("-9223372036854775808");
        assertEquals(-9223372036854775808L, obj.getInteger("a"));
    }
    
    @Test(expected=StringFormatException.class)
    public void testIntTooLarge() {
        a("9223372036854775808");
    }
    
    @Test(expected=StringFormatException.class)
    public void testIntTooSmall() {
        a("-9223372036854775809");
    }
    
    @Test
    public void testFrac1() {
        a("0.0");
        assertTrue(0.0 == obj.getDouble("a"));
    }
    
    @Test
    public void testFracNeg() {
        a("-1.23");
        assertTrue(-1.23 == obj.getDouble("a"));
    }
    
    @Test(expected=StringFormatException.class)
    public void testFracPlus() {
        a("+1.23");     // not valid
        assertTrue(+1.23 == obj.getDouble("a"));
    }
    
    @Test
    public void testFracExp1() {
        a("20.12E-9");
        assertTrue(20.12e-9 == obj.getDouble("a"));
    }
    
    @Test
    public void testFracExp2() {
        a("0.00012e+19");
        assertTrue(0.00012e+19 == obj.getDouble("a"));
    }
    
    @Test(expected=StringFormatException.class)
    public void testFracBad1() {
        a("01.23");
    }
    
    @Test(expected=StringFormatException.class)
    public void testFracBad2() {
        a(".12");
    }
     
    @Test
    public void testFracExp3() {
        a("1e2");
        assertTrue(100.0 == obj.getDouble("a"));
    }
    
    @Test
    public void testFracExp4() {
        a("11234e212");
        assertTrue(11234e212 == obj.getDouble("a"));
    }
    
    @Test(expected=StringFormatException.class)
    public void testFracTooLarge() {
        a("1.2e+400");    
    }
    
    @Test(expected=StringFormatException.class)
    public void testFracTooSmall() {
        a("-1.2e+400");    
    }
    
    @Test
    public void testSpecialNaN1() {
        aBinson("NaN");
        double d = obj.getDouble("a");
        assertTrue(Double.isNaN(d));
    }
    
    @Test(expected=StringFormatException.class)
    public void testSpecialNaN2() {
        a("NaN");
    }
    
    @Test
    public void testSpecialInf() {
        aBinson("inf");
        double d = obj.getDouble("a");
        assertTrue(d == Double.POSITIVE_INFINITY);
    }
    
    @Test
    public void testSpecialNegInf() {
        aBinson("-inf");
        double d = obj.getDouble("a");
        assertTrue(d == Double.NEGATIVE_INFINITY);
    }
    
    @Test(expected=StringFormatException.class)
    public void testSpecialInf2() {
        a("inf");
    }
    
    @Test(expected=StringFormatException.class)
    public void testSpecialInf3() {
        a("-inf");
    }
    
    @Test
    public void bytesEmpty() {
        a("x");
        Assert.assertArrayEquals(new byte[0], obj.getBytes("a"));
    }
    
    @Test
    public void bytesOne() {
        a("x00");
        Assert.assertArrayEquals(new byte[]{0}, obj.getBytes("a"));
    }
    
    @Test
    public void bytesLonger() {
        a("x000102030405060708090a0b0c0d0e0f10");
        
        byte[] arr = new byte[17];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (byte) i;
        }
        
        Assert.assertArrayEquals(arr, obj.getBytes("a"));
    }
    
    @Test(expected=StringFormatException.class)
    public void bytesOddHexCount() {
        a("x012");
    }
    
    @Test
    public void bytesMixedCase() {
        a("xCafeBabE");
        byte[] arr = new byte[] {(byte) 0xca, (byte) 0xfe, (byte) 0xba, (byte) 0xbe};
        Assert.assertArrayEquals(arr, obj.getBytes("a"));
    }
}
