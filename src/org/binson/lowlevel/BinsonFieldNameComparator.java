package org.binson.lowlevel;

import java.nio.charset.StandardCharsets;
import java.util.Comparator;

/**
 * Compares strings according to lexicographical ordering based on their
 * representation as UTF-8 bytes. Used to order Binson fields when serializing.
 */
public class BinsonFieldNameComparator implements Comparator<String> {
    public int compare(String s1, String s2) {
        return ByteArrayComparator.compareArrays(s1.getBytes(StandardCharsets.UTF_8), s2.getBytes(StandardCharsets.UTF_8));
    }
}
