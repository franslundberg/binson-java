package org.binson;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test with nested Binson object.
 */
public class NestedTest {
    
    @Test
    public void testEmptyL1() {
    	// Test compared to manually created Binson from spec.
    	Binson b = new Binson();
    	byte[] expected = new byte[]{0x40, 0x41};
    	Assert.assertArrayEquals(expected, b.toBytes());
    }
    
    @Test
    public void testEmptyL2() {
    	// Test compared to manually created Binson from spec.
    	// Two levels of empty Binson objects.
    	Binson b = new Binson().put("", new Binson());
    	byte[] expected = new byte[]{0x40, 0x14, 0x00, 0x40, 0x41, 0x41};
    	Assert.assertArrayEquals(expected, b.toBytes());
    }
    
    @Test
    public void testEmptyL3() {
    	// Test compared to manually created Binson from spec.
    	// Three levels of empty Binson objects.
    	Binson b = new Binson()
            .put("", new Binson()
                .put("", new Binson()));
    	byte[] expected = new byte[]{0x40, 0x14, 0x00, 0x40, 0x14, 0x00, 0x40, 0x41, 0x41, 0x41};
    	Assert.assertArrayEquals(expected, b.toBytes());
    }
    
    @Test
    public void testEmptyL4() {
    	// Test compared to manually created Binson from spec.
    	// Four levels of empty Binson objects.
    	Binson b = new Binson().put("",
    	    new Binson().put("",
    	        new Binson().put("",
    	            new Binson()
    	        )
    	    )
    	);
    	
    	byte[] expected = new byte[]{0x40, 0x14, 0x00, 0x40, 0x14, 0x00, 0x40, 0x14, 0x00, 0x40, 0x41, 0x41, 0x41, 0x41};
    	Assert.assertArrayEquals(expected, b.toBytes());
    }
}
