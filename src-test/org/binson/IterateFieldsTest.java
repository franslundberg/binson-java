package org.binson;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the Binson.fieldNames() method.
 */
public class IterateFieldsTest {

    @Test
    public void testSanity() {
        Binson b = new Binson().put("a", "a-value");
        List<String> strings = b.fieldNames();
        Assert.assertEquals(1, strings.size());
        Assert.assertEquals("a", strings.get(0));
        Assert.assertEquals("a-value", b.getString(strings.get(0)));
    }
	
    @Test
    public void testZeroFields() {
        Binson b = new Binson();
        Assert.assertEquals(0, b.fieldNames().size());
    }
    
    @Test
    public void testMultiple() {
        Binson b = new Binson()
            .put("b", "b-value")
            .put("c", "c-value")
            .put("a", "a-value");
        List<String> strings = b.fieldNames();
        Assert.assertEquals(3, strings.size());
        Assert.assertEquals("a", strings.get(0));
        Assert.assertEquals("b", strings.get(1));
        Assert.assertEquals("c", strings.get(2));
    }
}
