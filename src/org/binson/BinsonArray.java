package org.binson;

import java.util.ArrayList;

/**
 * <p>A Binson array is a list of heterogeneous values.</p>
 * 
 * <p>The getX() methods gets a value of the type X. If the expected value does not exist, 
 * a FormatException is thrown. To check whether a field of a particular type exists, 
 * use the hasX() methods.</p>
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
    
    public String toString() {
        return new Binson().put("array", this).toString();
    }
    
    /**
     * Returns a copy of this object that shares no data with the 
     * original object.
     */
    public BinsonArray copy() {
        Binson b1 = new Binson().put("a", this);
        Binson b2 = b1.copy();
        return b2.getArray("a");
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
        if (!(obj instanceof Boolean)) {
            throw new FormatException("No boolean in Binson array at index " + index + ".");
        }
        return ((Boolean) obj).booleanValue();
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
        if (!(obj instanceof Long)) {
            throw new FormatException("No integer in Binson array at index " + index + ".");
        }
        return ((Long) obj).longValue();
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
        if (!(obj instanceof Double)) {
            throw new FormatException("No Double in Binson array at index " + index + ".");
        }
        return ((Double) obj).doubleValue();
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
        if (!(obj instanceof String)) {
            throw new FormatException("No String in Binson array at index " + index + ".");
        }
        return (String) obj;
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
        if (!(obj instanceof byte[])) {
            throw new FormatException("No bytes element in Binson array at index " + index + ".");
        }
        return (byte[]) obj;
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
        if (!(obj instanceof BinsonArray)) {
            throw new FormatException("No BinsonArray in Binson array at index " + index + ".");
        }
        return (BinsonArray) obj;
    }
        
    // object
    
    public BinsonArray add(Binson value) {
        if (value == null) {
            throw new IllegalArgumentException("value == null not allowed");
        }
        super.add(value);
        return this;
    }

    public boolean isObject(int index) {
        Object obj = get(index);
        return obj instanceof Binson;
    }
    
    public Binson getObject(int index) {
        Object obj = get(index);
        if (!(obj instanceof Binson)) {
            throw new FormatException("No Binson object in Binson array at index " + index + ".");
        }
        return (Binson) obj;
    }
}
