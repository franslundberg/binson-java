package org.binson;

import org.junit.Assert;
import org.junit.Test;

public class CopyTest {

    @Test
    public void test1() {
        Binson obj1 = new Binson().put("a", 1);
        Binson obj2 = obj1.copy();
        obj1.put("a", 2);
        
        Assert.assertEquals(2, obj1.getInteger("a"));
        Assert.assertEquals(1, obj2.getInteger("a"));
    }
}
