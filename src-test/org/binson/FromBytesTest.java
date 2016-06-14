package org.binson;

import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

public class FromBytesTest {
    private Binson obj = new Binson().put("aa", "bb");
    
    @Test
    public void testFromBytesWithOffset() {
        byte[] bytes1 = obj.toBytes();
        byte[] bytes2 = new byte[bytes1.length + 4];
        System.arraycopy(bytes1, 0, bytes2, 3, bytes1.length);
        
        Binson obj2 = Binson.fromBytes(bytes2, 3);
        Assert.assertTrue(Arrays.equals(bytes1, obj2.toBytes()));
    }
}
