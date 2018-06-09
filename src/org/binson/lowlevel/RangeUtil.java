package org.binson.lowlevel;

import static org.binson.lowlevel.Constants.*;

/**
 * A few static functions for integer range checking.
 * 
 * @author Frans Lundberg
 */
public class RangeUtil {
    
    public final static boolean isInOneByteRange(long value) {
        return value >= -TWO_TO_7 && value < TWO_TO_7;
    }
    
    public final static boolean isInTwoByteRange(long value) {
        return value >= -TWO_TO_15 && value < TWO_TO_15;
    }
    
    public final static boolean isInFourByteRange(long value) {
        return value >= -TWO_TO_31 && value < TWO_TO_31;
    }
}
