package org.binson;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests BinsonObject.fromJson().
 *
 * @author Frans Lundberg
 */
public class FromJsonTest {
    @Test
    public void empty() {
        Binson obj = Binson.fromJson("{}");
        assertEquals(0, obj.size());
    }
    
    @Test
    public void oneString() {
        Binson obj = Binson.fromJson("{\"a\": \"hi!\"}");
        assertEquals(1, obj.size());
        assertEquals("hi!", obj.getString("a"));
    }
    
    @Test
    public void oneInteger() {
        Binson obj = Binson.fromJson("{\"a\": 1234}");
        assertEquals(1, obj.size());
        assertEquals(1234, obj.getInteger("a"));
    }
    
    @Test
    public void nested() {
        String json = "{\"a\":{\"b\":12}}";
        Binson obj = Binson.fromJson(json);
        Binson nested = obj.getObject("a");
        
        assertEquals(1, nested.size());
        assertEquals(12, nested.getInteger("b"));
    }
    
    @Test
    public void testUEscapeOmega() {
        String s = "{\"omega\": \"\u03a9\"}";
        Binson obj = Binson.fromJson(s);
        String omega = obj.getString("omega");
        assertEquals(1, omega.length());
        assertEquals('\u03a9', omega.charAt(0));
    }
    
    @Test
    public void testScientificFractions() {
        String s = "{\"a\": [0.0e-12, 0.00123e3, 23.2e+3, 1e-9]}";
        Binson obj = Binson.fromJson(s);
        BinsonArray arr = obj.getArray("a");
        assertEquals("array size", 4, arr.size());
    }
    
    @Test
    public void testSimpleFraction() {
        Binson obj = Binson.fromJson("{\"a\": 1.2}");
        assertEquals(1.2, obj.getDouble("a"), 0.0001);
    }
    
    @Test
    public void testFractions() {
        String s = "{\"a\":[0.0, 0.00123, 23.6666, 123434534.3456789]}";
        Binson obj = Binson.fromJson(s);
        BinsonArray arr = obj.getArray("a");
        assertEquals("array size", 4, arr.size());
        assertEquals(new Double(23.6666), arr.get(2));
    }
    
    // TODO add to test for JsonParseException column and line count.
}
