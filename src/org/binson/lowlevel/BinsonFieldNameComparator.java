package org.binson.lowlevel;

import java.nio.charset.StandardCharsets;
import java.util.Comparator;

/**
 * Compares strings according to lexicographical ordering based on their
 * representation as UTF-8 bytes. Used to order Binson fields when serializing.
 */
public class BinsonFieldNameComparator implements Comparator<String> {
    /**
     * The instance of the class, one instance is enough since if can be freely shared 
     * between threads. It has no state.
     */
    public static final BinsonFieldNameComparator INSTANCE = new BinsonFieldNameComparator();
    
    /**
     * Use the INSTANCE instance.
     */
    private BinsonFieldNameComparator() {}
    
    /**
     * Compares its two arguments for order. Returns a negative integer, zero, or a 
     * positive integer as the first argument is less than, equal to, or greater than the second.
     */
    public int compare(String s1, String s2) {
        return ByteArrayComparator.compareArrays(s1.getBytes(StandardCharsets.UTF_8), s2.getBytes(StandardCharsets.UTF_8));
    }
}
