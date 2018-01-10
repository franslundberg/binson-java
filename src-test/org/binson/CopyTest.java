package org.binson;

import org.junit.Assert;
import org.junit.Test;

public class CopyTest {

    @Test
    public void testCopyObject() {
        Binson obj1 = new Binson().put("a", 1);
        Binson obj2 = obj1.copy();
        obj1.put("a", 2);
        
        Assert.assertEquals(2, obj1.getInteger("a"));
        Assert.assertEquals(1, obj2.getInteger("a"));
    }
    
    @Test
    public void testCopyArray() {
        BinsonArray arr1 = new BinsonArray().add("string1").add(123);
        BinsonArray arr2 = arr1.copy();
        
        Assert.assertEquals(arr1.getString(0), arr2.getString(0));
        Assert.assertEquals(arr1.getInteger(1), arr2.getInteger(1));
        Assert.assertEquals(2, arr1.size());
        Assert.assertEquals(2, arr2.size());
        Assert.assertEquals("string1", arr2.getString(0));
    }
}
