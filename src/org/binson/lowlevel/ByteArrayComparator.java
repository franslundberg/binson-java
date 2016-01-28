package org.binson.lowlevel;

import java.util.Comparator;

/**
 * Compares two byte arrays in lexicographical order.
 */
public class ByteArrayComparator implements Comparator<byte[]> {
    public final int compare(byte[] arr1, byte[] arr2) {
        return ByteArrayComparator.compareArrays(arr1, arr2); 
    }
    
    public static final int compareArrays(byte[] arr1, byte[] arr2) {
        final boolean firstShorter = arr1.length < arr2.length;
        final int minLength = firstShorter ? arr1.length : arr2.length;
        int i;
        
        for (i = 0; i < minLength; i++) {
            int diff = (arr1[i] & 0xff) - (arr2[i] & 0xff);
            if (diff != 0) {
                return diff;
            }
        }
        
        return arr1.length == arr2.length ? 0 : (firstShorter ? -1 : +1);
    }
}
