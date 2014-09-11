package org.binson;

import java.util.ArrayList;

/**
 * A Binson array, an ArrayList of heterogenous values.
 * See BinsonObject for how Binson types are mapped to Java classes.
 * 
 * @author Frans Lundberg
 */
public class BinsonArray extends ArrayList<Object> {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an empty Binson array.
     */
    public BinsonArray() {
        super();
    }
    
    // boolean
    
    public BinsonArray add(boolean value) {
        super.add((Boolean) value);
        return this;
    }
    
    public boolean isBoolean(int index) {
        Object obj = get(index);
        return obj instanceof Boolean;
    }
    
    public boolean getBoolean(int index) {
        Object obj = get(index);
        return obj instanceof Boolean ? ((Boolean) obj) : false;
    }
    
    // long
    
    public BinsonArray add(long value) {
        super.add((Long) value);
        return this;
    }

    public boolean isInteger(int index) {
        Object obj = get(index);
        return obj instanceof Long;
    }
    
    public long getInteger(int index) {
        Object obj = get(index);
        return obj instanceof Long ? ((Long) obj).longValue() : 0;
    }
    
    // double
    
    public BinsonArray add(double value) {
        super.add((Double) value);
        return this;
    }
    
    public boolean isDouble(int index) {
        Object obj = get(index);
        return obj instanceof Double;
    }
    
    public double getDouble(int index) {
        Object obj = get(index);
        return obj instanceof Double ? ((Double) obj).doubleValue() : 0.0;
    }
    
    // string
    
    public BinsonArray add(String value) {
        if (value == null) {
            throw new IllegalArgumentException("value == null not allowed");
        }
        super.add(value);
        return this;
    }
    
    public boolean isString(int index) {
        Object obj = get(index);
        return obj instanceof String;
    }
    
    public String getString(int index) {
        Object obj = get(index);
        return obj instanceof String ? (String) obj : null;
    }
        
    // bytes
    
    public BinsonArray add(byte[] value) {
        if (value == null) {
            throw new IllegalArgumentException("value == null not allowed");
        }
        super.add(value);
        return this;
    }
    
    public boolean isBytes(int index) {
        Object obj = get(index);
        return obj instanceof byte[];
    }
    
    public byte[] getBytes(int index) {
        Object obj = get(index);
        return obj instanceof byte[] ? (byte[]) obj : null;
    }
    
    // array
    
    public BinsonArray add(BinsonArray value) {
        if (value == null) {
            throw new IllegalArgumentException("value == null not allowed");
        }
        super.add(value);
        return this;
    }
    
    public boolean isArray(int index) {
        Object obj = get(index);
        return obj instanceof BinsonArray;
    }
    
    public BinsonArray getArray(int index) {
        Object obj = get(index);
        return obj instanceof BinsonArray ? (BinsonArray) obj : null;
    }
        
    // object
    
    public BinsonArray add(BinsonObject value) {
        if (value == null) {
            throw new IllegalArgumentException("value == null not allowed");
        }
        super.add(value);
        return this;
    }

    public boolean isObject(int index) {
        Object obj = get(index);
        return obj instanceof BinsonObject;
    }
    
    public BinsonObject getObject(int index) {
        Object obj = get(index);
        return obj instanceof BinsonObject ? (BinsonObject) obj : null;
    }
}
