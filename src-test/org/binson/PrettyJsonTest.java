package org.binson;

import org.junit.Test;

/**
 * Trying to test Json prettiness. Check manually, prettiness is
 * hard to define :-).
 * 
 * @author Frans Lundberg
 */
public class PrettyJsonTest {
    
    @Test
    public void testSanity() {
        Binson b = new Binson()
                .put("comment", "The first comment")
                .put("name", "myThing")
                .put("nested", new Binson()
                        .put("k1", "v1"))
                .put("type", 12);
        
        @SuppressWarnings("unused")
        String pretty = b.toPrettyJson();
        //System.out.print(pretty);
    }
    
    @Test
    public void testArrays() {
        Binson b = new Binson()
                .put("name", "myThing")
                .put("nested", new BinsonArray()
                        .add("v0").add("v1"))
                .put("type", 12)
                .put("z", new BinsonArray()
                        .add("val0")
                        .add(new BinsonArray()
                                .add("n2")
                                .add("n3")));
        @SuppressWarnings("unused")
        String pretty = b.toPrettyJson();
        //System.out.print(pretty);
    }
}
