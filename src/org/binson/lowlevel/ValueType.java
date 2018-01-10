package org.binson.lowlevel;

import org.binson.Binson;
import org.binson.BinsonArray;

public enum ValueType {
    BOOLEAN,
    INTEGER,
    DOUBLE,
    STRING,
    BYTES,
    ARRAY,
    OBJECT;
    
    /**
     * @throws IllegalArgumentException
     *          If value is null or not a valid Java object type.
     */
    public static ValueType fromObject(Object value) {
        if (value == null) {
            throw new IllegalArgumentException("value == null not allowed");
        }
        
        int classNameLength = value.getClass().getName().length();
        
        switch (classNameLength) {
        case Constants.BOOLEAN_CLASSNAME_LENGTH:    
            // java.lang.Boolean, org.binson.Binson -- same length
            if (value instanceof Boolean) {
                return BOOLEAN;
            }
            
            if (value instanceof Binson) {
                return OBJECT;
            }
            
            break;
        
        case Constants.LONG_CLASSNAME_LENGTH:
            if (value instanceof Long) {
                return INTEGER;
            }
            break;
        
        case Constants.DOUBLE_CLASSNAME_LENGTH:
            // Double, String - same length
            if (value instanceof Double) {
                return DOUBLE;
            } else if (value instanceof String) {
                return STRING;
            }
            break;
            
        case Constants.BYTES_CLASSNAME_LENGTH:
            if (value instanceof byte[]) {
                return BYTES;
            }
            break;
                        
        default:
            if (value instanceof Binson) {
                return OBJECT;
            } else if (value instanceof BinsonArray) {
                return ARRAY;
            }
            
            break;
        }
        
        throw new IllegalArgumentException("not a Binson value, " + value.toString());
    }
}
