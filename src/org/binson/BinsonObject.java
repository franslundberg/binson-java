package org.binson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.binson.lowlevel.BinsonOutput;
import org.binson.lowlevel.BinsonStringOutput;
import org.binson.lowlevel.BinsonStringParser;
import org.binson.lowlevel.JsonOutput;
import org.binson.lowlevel.JsonParser;
import org.binson.lowlevel.OutputWriter;
import org.binson.lowlevel.BinsonParser;

/**
 * <p>A Binson object implemented as a Map with support for typed access to its members.</p>
 * 
 * <p>A Binson object is can be created by calling the default contructor followed by
 * put methods to add fields. Example:</p>
 * 
 * <pre> BinsonObject obj = new BinsonObject().put(&quot;name&quot;, &quot;Frans&quot;).put(&quot;height&quot;, 178);
 * </pre>
 * 
 * <p>The getX() methods gets a value of the type X. If the value does not exist, a FormatException
 * is thrown. To check whether a field of a particular type exist, use the hasX() methods.
 * 
 * <pre> obj.getString(&quot;name&quot;);       // returns &quot;Frans&quot;
 * obj.getInteger(&quot;height&quot;);    // returns 178 
 * obj.hasString(&quot;name&quot;);       // returns true
 * obj.hasInteger(&quot;height&quot;);    // returns true
 * </pre>
 * 
 * <p>To serialize a BinsonObject to bytes and parse it from bytes, 
 * see the methods <code>toBytes()</code> and <code>fromBytes()</code>.</p>
 * 
 * <p>This class contains full support for generating and parsing 
 * JSON (http://json.org/) objects. See the methods <code>fromJson()</code> 
 * and <code>toJson()</code>.</p>
 * 
 * <p>The table below shows how Binson types are stored as Java object
 * in the Map.</p>
 * 
 * <pre>
 * Binson type      Stored as
 * 
 * boolean          Boolean
 * integer          Integer
 * double           Double
 * string           String
 * bytes            byte[]
 * array            BinsonArray
 * object           BinsonObject
 * </pre>
 * 
 * @author Frans Lundberg
 */
public class BinsonObject implements Map<String, Object> {
    private final Map<String, Object> map;

    /**
     * Creates an empty BinsonObject backed by a newly created HashMap.
     */
    public BinsonObject() {
        map = new HashMap<String, Object>();
    }
    
    /**
     * Wraps an existing Map in a new BinsonObject.
     * Changes to the provided map is reflected in this object and vice versa.
     */
    public BinsonObject(Map<String, Object> map) {
        if (map == null) {
            throw new IllegalArgumentException("map == null not allowed");
        }
        this.map = map;
    }
    
    // boolean
    
    public BinsonObject put(String name, boolean value) {
        map.put(name, (Boolean) value);
        return this;
    }
    
    public boolean hasBoolean(String name) {
        checkName(name);
        Object object = get(name);
        return object != null && object instanceof Boolean;
    }
    
    public boolean getBoolean(String name) {
        checkName(name);
        Object object = get(name);
        if (object == null || !(object instanceof Boolean)) {
            throw new FormatException("No boolean named '" + name + "'.");
        }
        
        return ((Boolean)object).booleanValue();
    }
    
    // long
    
    public BinsonObject put(String name, long value) {
        map.put(name, (Long) value);
        return this;
    }
    
    public boolean hasInteger(String name) {
        checkName(name);
        Object object = get(name);
        return object != null && object instanceof Long;
    }
    
    public long getInteger(String name) {
        checkName(name);
        
        Object object = get(name);
        if (object == null || !(object instanceof Long)) {
            throw new FormatException("No integer named '" + name + "'.");
        }
        return ((Long)object).longValue();
    }
    
    // double
    
    public BinsonObject put(String name, double value) {
        map.put(name, (Double) value);
        return this;
    }
    
    public boolean hasDouble(String name) {
        checkName(name);
        Object object = get(name);
        return object != null && object instanceof Double;
    }
    
    public double getDouble(String name) {
        checkName(name);
        Object object = get(name);
        if (object == null || !(object instanceof Double)) {
            throw new FormatException("No double named '" + name + "'.");
        }
        return ((Double)object).doubleValue();
    }
    
    // string
    
    public BinsonObject put(String name, String value) {
        map.put(name, value);
        return this;
    }
    
    public boolean hasString(String name) {
        checkName(name);
        Object object = get(name);
        return object != null && object instanceof String;
    }
    
    public String getString(String name) {
        checkName(name);
        Object object = get(name);
        if (object == null || !(object instanceof String)) {
            throw new FormatException("No string named '" + name + "'.");
        }
        return (String) object;
    }
    
    // bytes
    
    public BinsonObject put(String name, byte[] value) {
        map.put(name, value);
        return this;
    }
    
    public boolean hasBytes(String name) {
        checkName(name);
        Object object = get(name);
        return object != null && object instanceof byte[];
    }
    
    public byte[] getBytes(String name) {
        checkName(name);
        Object object = get(name);
        if (object == null || !(object instanceof byte[])) {
            throw new FormatException("No bytes field named '" + name + "'.");
        }
        return (byte[]) object;
    }
    
    // array
    
    public BinsonObject put(String name, BinsonArray value) {
        map.put(name, value);
        return this;
    }
    
    public boolean hasArray(String name) {
        checkName(name);
        Object object = get(name);
        return object != null && object instanceof BinsonArray;
    }
    
    public BinsonArray getArray(String name) {
        checkName(name);
        Object object = get(name);
        if (object == null || !(object instanceof BinsonArray)) {
            throw new FormatException("No array named '" + name + "'.");
        }
        return (BinsonArray) object;
    }
    
    // object
    
    public BinsonObject put(String name, BinsonObject value) {
        map.put(name, value);
        return this;
    }
    
    public boolean hasObject(String name) {
        checkName(name);
        Object object = get(name);
        return object != null && object instanceof BinsonObject;
    }
    
    public BinsonObject getObject(String name) {
        checkName(name);
        Object object = get(name);
        if (object == null || !(object instanceof BinsonObject)) {
            throw new FormatException("No BinsonObject name '" + name + "'.");
        }
        return (BinsonObject) object;
    }
    
    
    // ======== Conversion, bytes ========
    
    /**
     * Writes this Binson object as bytes to the provided OutputStream.
     */
    public void toBytes(OutputStream out) throws IOException {
        OutputWriter.mapToOutput(this, new BinsonOutput(out));    
    }
    
    public byte[] toBytes() {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        
        try {
            toBytes(bout);
        } catch (IOException e) {
            throw new Error("never happens", e);
        }
        
        return bout.toByteArray();
    }
    
    /**
     * Parses the bytes in an InputStream to a BinsonObject.
     * 
     * @param in  
     *      Input bytes to be parsed.
     * @return
     *      The parsed BinsonObject.
     * @throws IOException
     *      If there is an error reading the stream.
     * @throws FormatException
     *      If the bytes in the stream is not valid Binson bytes.
     */
    public static BinsonObject fromBytes(InputStream in) throws IOException {
        if (in == null) {
            throw new IllegalArgumentException("in == null not allowed");
        }
        
        return BinsonParser.parse(in);
    }
    
    /**
     * Parses Binson bytes to a BinsonObject.
     * 
     * @param bytes  Bytes to convert to a Binson object.
     * @throws FormatException If the bytes are not valid Binson bytes.
     * @return The Binson object.
     */
    public static BinsonObject fromBytes(byte[] bytes) {
        ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
        BinsonObject result;
        
        try {
            result = fromBytes(bin);
            bin.close();
        } catch (EOFException e1) {
            throw new FormatException("end-of-file reached before end of object.");
        } catch (IOException e2) {
            throw new Error("never happens", e2);
        }
        
        return result;
    }

    
    // ======== Conversion, Binson string ========
    
    /**
     * Outputs the Binson object to a Binson string. No white spaces are added 
     * expected for a single space character after a comma that separates two 
     * fields.
     */
    public void toBinsonString(Writer writer) throws IOException {
        OutputWriter.mapToOutput(this, new BinsonStringOutput(writer));
    }
    
    /**
     * Converts the Binson object to a Binson string.
     * 
     * @see #toBinsonString(Writer)
     */
    public String toBinsonString() {
        StringWriter writer = new StringWriter();
        try {
            toBinsonString(writer);
        } catch (IOException e) {
            throw new Error("never happens", e);
        }
        
        return writer.toString();
    }    
    
    public static BinsonObject fromBinsonString(Reader reader) throws IOException {
        BinsonObject result = new BinsonObject();
        BinsonStringParser.parse(reader, result);
        return result;
    }
    
    public static BinsonObject fromBinsonString(String string) {
        StringReader reader = new StringReader(string);
        try {
            return fromBinsonString(reader);
        } catch (IOException e) {
            throw new Error("never happens", e);
        }
    }
    
    
    // ======== Conversion, JSON ========
    
    public void toJson(Writer writer) throws IOException {
        OutputWriter.mapToOutput(this, new JsonOutput(writer));
    }
    
    public String toJson() {
        StringWriter writer = new StringWriter();
        try {
            toJson(writer);
        } catch (IOException e) {
            throw new Error("never happens", e);
        }
        return writer.toString();
    }
    
    public static BinsonObject fromJson(Reader reader) throws IOException {
        BinsonObject result = new BinsonObject();
        JsonParser.parse(reader, result);
        return result;
    }
    
    public static BinsonObject fromJson(String string) {
        StringReader reader = new StringReader(string);
        try {
            return fromJson(reader);
        } catch (IOException e) {
            throw new Error("never happens", e);
        }
    }
    
    
    // ======== Implements Map interface by wrapper methods ========

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    public Object get(Object key) {
        return map.get(key);
    }

    public Object put(String key, Object value) {
        return map.put(key, value);
    }

    public Object remove(Object key) {
        return map.remove(key);
    }

    public void putAll(Map<? extends String, ? extends Object> t) {
        map.putAll(t);
    }

    public void clear() {
        map.clear();
    }

    public Set<String> keySet() {
        return map.keySet();
    }

    public Collection<Object> values() {
        return map.values();
    }

    public Set<java.util.Map.Entry<String, Object>> entrySet() {
        return map.entrySet();
    }
    
    @Override
    public boolean equals(Object thatObject) {
        if (thatObject == null) {
            return false;
        }
        
        if (!(thatObject instanceof BinsonObject)) {
            return false;
        }
        
        BinsonObject that = (BinsonObject) thatObject;
        
        return Arrays.equals(this.toBytes(), that.toBytes());
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.toBytes());
    }
    
    private void checkName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("null is not allowed as a name");
        }
    }
}