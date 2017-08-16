package org.binson;

import static org.junit.Assert.*;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for storing a double in a BinsonObject.
 * 
 * @author Frans Lundberg
 */
public class DoubleTest {
    private Binson obj = new Binson().put("a", 1.2);

    @Test
    public void testGet() {
        assertEquals(1.2, obj.getDouble("a"), 0.00001);
    }
    
    @Test
    public void test123() {
        byte[] bytes = new Binson().put("d", 1.23).toBytes();
        //System.out.println(Hex.create(bytes));
        // 4014016446ae47e17a14aef33f41
        
        Binson obj = Binson.fromBytes(bytes);
        Assert.assertTrue(1.23 == obj.getDouble("d"));
    }
    
    @Test
    public void testHas() {
        assertFalse(obj.hasDouble("b"));
        assertFalse(obj.hasBoolean("a"));
        assertFalse(obj.hasString("a"));
        assertTrue(obj.hasDouble("a"));
    }
    
    @Test(expected=FormatException.class)
    public void testNonExistant() {
        obj.getDouble("b");
    }
    
    @Test
    public void testPi() {
        byte[] bytes = new Binson().put("pi", Math.PI).toBytes();
        //System.out.println(Hex.create(bytes));
        // 401402706946182d4454fb21094041
        
        Binson obj = Binson.fromBytes(bytes);
        Assert.assertTrue(Math.PI == obj.getDouble("pi"));
    }
    
    @Test
    public void testNaN() {
        byte[] bytes = new Binson().put("nan", Double.NaN).toBytes();
        //System.out.println(Hex.create(bytes));
        // 4014036e616e46000000000000f87f41
        
        Binson obj = Binson.fromBytes(bytes);
        Assert.assertTrue(Double.isNaN(obj.getDouble("nan")));
    }
    
    @Test
    public void testInf() {
        byte[] bytes = new Binson().put("inf", Double.POSITIVE_INFINITY).toBytes();
        
        //System.out.println(Hex.create(bytes));
        // 401403696e6646000000000000f07f41
        
        Binson obj = Binson.fromBytes(bytes);
        Assert.assertTrue(Double.POSITIVE_INFINITY == obj.getDouble("inf"));
    }
}
