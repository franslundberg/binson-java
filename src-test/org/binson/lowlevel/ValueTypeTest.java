package org.binson.lowlevel;

import org.binson.Binson;
import org.binson.BinsonArray;
import org.junit.Assert;
import org.junit.Test;

public class ValueTypeTest {

    @Test
    public void testBoolean() {
        Assert.assertEquals(ValueType.BOOLEAN, ValueType.fromObject(new Boolean(true)));
        Assert.assertEquals(ValueType.BOOLEAN, ValueType.fromObject(new Boolean(false)));
        Assert.assertNotEquals(ValueType.BOOLEAN, ValueType.fromObject("Hello"));
    }
    
    @Test
    public void testArray() {
        Assert.assertEquals(ValueType.ARRAY, ValueType.fromObject(new BinsonArray()));
        Assert.assertNotEquals(ValueType.ARRAY, ValueType.fromObject("Hello"));
        Assert.assertNotEquals(ValueType.ARRAY, ValueType.fromObject(new Binson()));
    }
}
