package org.binson.lowlevel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.binson.Binson;
import org.binson.BinsonFormatException;
import org.junit.Assert;
import org.junit.Test;

public class BinsonParserTest {

    @Test(expected=MaxSizeException.class)
    public void testTooBigObject() throws BinsonFormatException, IOException {
        Binson b = new Binson().put("a", new byte[100*1000]);
        byte[] bytes = b.toBytes();
        ByteArrayInputStream in1 = new ByteArrayInputStream(bytes);
        BinsonParser parser1 = new BinsonParser(in1);
        
        ByteArrayInputStream in2 = new ByteArrayInputStream(bytes);
        BinsonParser parser2 = new BinsonParser(in2);
        
        Binson b1 = parser1.parse();
        Assert.assertArrayEquals(bytes, b1.toBytes());
        
        parser2.setMaxSize(4000);
        Binson b2 = parser2.parse();      // throws MaxSizeException
        Assert.assertArrayEquals(bytes, b2.toBytes());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testBadArgumentToSetMaxSize() throws BinsonFormatException, IOException {
        byte[] bytes = new Binson().put("a", new byte[100*1000]).toBytes();
        ByteArrayInputStream in1 = new ByteArrayInputStream(bytes);
        BinsonParser parser = new BinsonParser(in1);
        parser.setMaxSize(-4000);    // throws IllegalArgumentException
    }
}
