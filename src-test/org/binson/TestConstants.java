package org.binson;

import org.binson.lowlevel.Constants;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests that constants follow BINSON-SPEC-1.
 * 
 * @author Frans Lundberg
 */
public class TestConstants {
    @Test
    public void testBeginEnd() {
        Assert.assertEquals(0x40, Constants.BEGIN);
        Assert.assertEquals(0x41, Constants.END);
        Assert.assertEquals(0x42, Constants.BEGIN_ARRAY);
        Assert.assertEquals(0x43, Constants.END_ARRAY);
    }
    
    @Test
    public void testBoolean() {
        Assert.assertEquals(0x44, Constants.TRUE);
        Assert.assertEquals(0x45, Constants.FALSE);
    }
    
    @Test
    public void testTypeConstants() {
        Assert.assertEquals(0x46, Constants.DOUBLE);
        
        Assert.assertEquals(0x10, Constants.INTEGER1);
        Assert.assertEquals(0x11, Constants.INTEGER2);
        Assert.assertEquals(0x12, Constants.INTEGER4);
        Assert.assertEquals(0x13, Constants.INTEGER8);
        
        Assert.assertEquals(0x14, Constants.STRING1);
        Assert.assertEquals(0x15, Constants.STRING2);
        Assert.assertEquals(0x16, Constants.STRING4);
        
        Assert.assertEquals(0x18, Constants.BYTES1);
        Assert.assertEquals(0x19, Constants.BYTES2);
        Assert.assertEquals(0x1a, Constants.BYTES4);
    }
}
