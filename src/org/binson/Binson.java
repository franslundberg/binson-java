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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.binson.lowlevel.BinsonOutput;
import org.binson.lowlevel.JsonOutput;
import org.binson.lowlevel.JsonParser;
import org.binson.lowlevel.OutputWriter;
import org.binson.lowlevel.ValueType;
import org.binson.lowlevel.BinsonParser;

/**
 * <p>A Binson object is a map with support for typed access to its members.
 * See binson.org for more information about Binson.</p>
 * 
 * <p>A Binson object is can be created by calling the default constructor followed by
 * put methods to add fields. Example:</p>
 * 
 * <pre> Binson obj = new Binson().put(&quot;name&quot;, &quot;Frans&quot;).put(&quot;height&quot;, 178);
 * </pre>
 * 
 * <p>The getX() methods gets a value of the type X. If the expected value does not exist, 
 * a FormatException is thrown. To check whether a field of a particular type exists, 
 * use the hasX() methods.</p>
 * 
 * <pre> obj.getString(&quot;name&quot;);       // returns &quot;Frans&quot;
 * obj.getInteger(&quot;height&quot;);    // returns 178 
 * obj.hasString(&quot;name&quot;);       // returns true
 * obj.hasInteger(&quot;height&quot;);    // returns true
 * </pre>
 * 
 * <p>To serialize a Binson object to bytes and parse it from bytes, 
 * see the methods <code>toBytes()</code> and <code>fromBytes()</code>.</p>
 * 
 * <p>This class contains full support for generating and parsing 
 * JSON (http://json.org/) objects. See the methods <code>fromJson()</code> 
 * and <code>toJson()</code>.</p>
 * 
 * <p>The table below shows how Binson types are stored as Java objects internally.</p>
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
 * object           Binson
 * </pre>
 * 
 * @author Frans Lundberg
 */
public class Binson {
    private final Map<String, Object> map;

    /**
     * Creates an empty Binson object backed by a newly created HashMap.
     */
    public Binson() {
        map = new HashMap<String, Object>();
    }
    
    /**
     * Adds a field to this Binson object. Note, prefer to use the type-specific
     * put() methods instead. Internal object representation of Binson types
     * may change in future versions.
     * 
     * @param name
     *          The name of the field.
     * @param value
     *          The value of the field.
     * @throws IllegalArgumentException
     *          If name or value is null. If value is of an unsupported Java type.
     */
    public Binson putValue(String name, Object value) {
        ValueType.fromObject(value);
        if (name == null) {
            throw new IllegalArgumentException("name == null not allowed");
        }
        
        map.put(name, value);
        return this;
    }
    
    public Object getValue(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name == null not allowed");
        }
        
        return map.get(name);
    }
    
    /**
     * Validates this object against the provided Binson schema.
     * The schema is a Binson object that follows the specification
     * BINSON-SCHEMA (see binson.org).
     * hce
     * @param schema  Binson schema.
     * @throws BinsonFormatException  If the validation is not successful.
     */
    public void validate(Binson schema) {
        for (String fieldName : schema.keySet()) {
            if (fieldName.endsWith("-info")) {
                continue;
            }
            
            Object schemaValue = schema.map.get(fieldName);
            Object thisValue = this.map.get(fieldName);
            
            String infoName = fieldName + "-info";
            Binson info = null;
            if (schema.hasObject(infoName)) {
                info = schema.getObject(infoName);
            }
            
            validateValue(fieldName, schemaValue, info, thisValue);
        }
    }
    
    /**
     * @throws BinsonFormatException
     */
    private void validateValue(String fieldName, Object schemaValue, Binson info, Object thisValue) {
        boolean optional = false;
        
        if (info != null) {
            if (info.hasBoolean("optional")) {
                optional = info.getBoolean("optional");
            }
        }
        
        if (optional && thisValue == null) {
            return;
        }
        
        if (thisValue == null) {
            throw new BinsonFormatException("missing mandatory field '" + fieldName + "'");
        }
        
        if (!schemaValue.getClass().equals(thisValue.getClass())) {
            throw new BinsonFormatException("bad field type of field '" 
                    + fieldName + "' expected " + schemaValue.getClass().getSimpleName()
                    + ", got " + thisValue.getClass().getSimpleName());
        }
        
        if (schemaValue instanceof BinsonArray) {
            BinsonArray schemaArray = (BinsonArray) schemaValue;
            BinsonArray thisArray = (BinsonArray) thisValue;
            
            if (schemaArray.size() == 1) {
                Object schemaArrayValue = schemaArray.getElement(0);
                
                for (int i = 0; i < thisArray.size(); i++) {
                    Object thisArrayValue = thisArray.getElement(i);
                    
                    if (thisArrayValue instanceof Binson && schemaArrayValue instanceof Binson) {
                        ((Binson) thisArrayValue).validate((Binson) schemaArrayValue);
                    } else {
                        validateValue(fieldName, schemaArrayValue, null, thisArrayValue);
                    }
                }
            }
        }
        
        if (schemaValue instanceof Binson) {
            ((Binson) thisValue).validate((Binson) schemaValue);
        }
    }
    
    public String toString() {
        return toJson();
    }
    
    /**
     * Returns a copy of this object that shares no data with the original object.
     */
    public Binson copy() {
        return Binson.fromBytes(this.toBytes());
    }
    
    /**
     * Returns a set of the names of the fields. 
     * Naming borrowed from java.util.Map.
     */
    public Set<String> keySet() {
        return map.keySet();
    }
    
    /**
     * Returns true if the Binson object has a field with the given name.
     * Naming borrowed from java.util.Map.
     */
    public boolean containsKey(String name) {
        return map.containsKey(name);
    }
    
    /**
     * Removes a given field if it exists.
     */
    public void remove(String name) {
        map.remove(name);
    }
    
    
    // ======== putX, hasX, getX methods ========
    
    // boolean
    
    public Binson put(String name, boolean value) {
        map.put(name, (Boolean) value);
        return this;
    }
    
    public boolean hasBoolean(String name) {
        checkName(name);
        Object object = map.get(name);
        return object != null && object instanceof Boolean;
    }
    
    public boolean getBoolean(String name) {
        checkName(name);
        Object object = map.get(name);
        if (object == null || !(object instanceof Boolean)) {
            throw new BinsonFormatException("No boolean named '" + name + "'.");
        }
        
        return ((Boolean)object).booleanValue();
    }
    
    // long
    
    public Binson put(String name, long value) {
        map.put(name, (Long) value);
        return this;
    }
    
    public boolean hasInteger(String name) {
        checkName(name);
        Object object = map.get(name);
        return object != null && object instanceof Long;
    }
    
    public long getInteger(String name) {
        checkName(name);
        
        Object object = map.get(name);
        if (object == null || !(object instanceof Long)) {
            throw new BinsonFormatException("No integer named '" + name + "'.");
        }
        return ((Long)object).longValue();
    }
    
    // double
    
    public Binson put(String name, double value) {
        map.put(name, (Double) value);
        return this;
    }
    
    public boolean hasDouble(String name) {
        checkName(name);
        Object object = map.get(name);
        return object != null && object instanceof Double;
    }
    
    public double getDouble(String name) {
        checkName(name);
        Object object = map.get(name);
        if (object == null || !(object instanceof Double)) {
            throw new BinsonFormatException("No double named '" + name + "'.");
        }
        return ((Double)object).doubleValue();
    }
    
    // string
    
    public Binson put(String name, String value) {
        if (value == null) {
            throw new IllegalArgumentException("value == null");
        }
        map.put(name, value);
        return this;
    }
    
    public boolean hasString(String name) {
        checkName(name);
        Object object = map.get(name);
        return object != null && object instanceof String;
    }
    
    public String getString(String name) {
        checkName(name);
        Object object = map.get(name);
        if (object == null || !(object instanceof String)) {
            throw new BinsonFormatException("No string named '" + name + "'.");
        }
        return (String) object;
    }
    
    // bytes
    
    public Binson put(String name, byte[] value) {
        if (value == null) {
            throw new IllegalArgumentException("value == null");
        }
        map.put(name, value);
        return this;
    }
    
    public boolean hasBytes(String name) {
        checkName(name);
        Object object = map.get(name);
        return object != null && object instanceof byte[];
    }
    
    public byte[] getBytes(String name) {
        checkName(name);
        Object object = map.get(name);
        if (object == null || !(object instanceof byte[])) {
            throw new BinsonFormatException("No bytes field named '" + name + "'.");
        }
        return (byte[]) object;
    }
    
    // array
    
    public Binson put(String name, BinsonArray value) {
        if (value == null) {
            throw new IllegalArgumentException("value == null");
        }
        map.put(name, value);
        return this;
    }
    
    public boolean hasArray(String name) {
        checkName(name);
        Object object = map.get(name);
        return object != null && object instanceof BinsonArray;
    }
    
    public BinsonArray getArray(String name) {
        checkName(name);
        Object object = map.get(name);
        if (object == null || !(object instanceof BinsonArray)) {
            throw new BinsonFormatException("No array named '" + name + "'.");
        }
        return (BinsonArray) object;
    }
    
    // object
    
    public Binson put(String name, Binson value) {
        if (value == null) {
            throw new IllegalArgumentException("value == null");
        }
        map.put(name, value);
        return this;
    }
    
    public boolean hasObject(String name) {
        checkName(name);
        Object object = map.get(name);
        return object != null && object instanceof Binson;
    }
    
    public Binson getObject(String name) {
        checkName(name);
        Object object = map.get(name);
        if (object == null || !(object instanceof Binson)) {
            throw new BinsonFormatException("No Binson object name '" + name + "'.");
        }
        return (Binson) object;
    }
    
    
    // ======== Conversion, bytes ========
    
    /**
     * Writes this Binson object as bytes to the provided OutputStream.
     * 
     * @param out  Output stream to write the bytes to.
     * @throws IOException  IO problems.
     */
    public void toBytes(OutputStream out) throws IOException {
        OutputWriter.writeToOutput(this, new BinsonOutput(out));    
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
     * Parses the bytes in an InputStream to a Binson object.
     * 
     * @param in  
     *      Input bytes to be parsed.
     * @return
     *      The parsed Binson object.
     * @throws IOException
     *      If there is an error reading the stream.
     * @throws BinsonFormatException
     *      If the bytes in the stream is not valid Binson bytes.
     */
    public static Binson fromBytes(InputStream in) throws IOException {
        if (in == null) {
            throw new IllegalArgumentException("in == null not allowed");
        }
        
        return BinsonParser.parse(in);
    }
    
    /**
     * Parses Binson bytes to a Binson object.
     * 
     * @param bytes  Bytes to convert to a Binson object.
     * @throws BinsonFormatException If the bytes are not valid Binson bytes.
     * @return The Binson object.
     */
    public static Binson fromBytes(byte[] bytes) {
        return fromBytes(bytes, 0);
    }
    
    public static Binson fromBytes(byte[] bytes, int offset) {
        ByteArrayInputStream bin = new ByteArrayInputStream(bytes, offset, bytes.length - offset);
        Binson result;
        
        try {
            result = fromBytes(bin);
            bin.close();
        } catch (EOFException e1) {
            throw new BinsonFormatException("end-of-file reached before end of object.");
        } catch (IOException e2) {
            throw new Error("never happens", e2);
        }
        
        return result;
    }
    
    
    // ======== Conversion, JSON ========
    
    public void toJson(Writer writer) throws IOException {
        OutputWriter.writeToOutput(this, new JsonOutput(writer));
    }
    
    public void toPrettyJson(Writer writer, int indentSize, int extraIndentSize) throws IOException {
        OutputWriter.writeToOutput(this, 
                JsonOutput.createForPrettyOutput(writer, indentSize, extraIndentSize));
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
    
    public String toPrettyJson() {
        StringWriter writer = new StringWriter();
        try {
            toPrettyJson(writer, 2, 0);
        } catch (IOException e) {
            throw new Error("never happens", e);
        }
        return writer.toString();
    }
    
    public static Binson fromJson(Reader reader) throws IOException {
        return fromJson(reader, false);
    }

    public static Binson fromJson(Reader reader, boolean enableHex) throws IOException {
        Binson result = new Binson();
        JsonParser.parse(reader, result, enableHex);
        return result;
    }
    
    public static Binson fromJson(String string) {
        StringReader reader = new StringReader(string);
        try {
            return fromJson(reader, false);
        } catch (IOException e) {
            throw new Error("never happens", e);
        }
    }

    public static Binson fromJson(String string, boolean enableHex) {
        StringReader reader = new StringReader(string);
        try {
            return fromJson(reader, enableHex);
        } catch (IOException e) {
            throw new Error("never happens", e);
        }
    }

    public int size() {
        return map.size();
    }

    /**
     * Note, Binson objects do not support storing general Java objects
     * as values. This method throws an exception if 'value' is not of
     * a supported type. Use the putX() methods (where X is the type)
     * instead, if possible.
     * 
     * @throws IllegalArgumentException
     *          If value is null or of an unsupported Java class.
     */
    public void putElement(String key, Object value) {
        ValueType.fromObject(value);
        map.put(key, value);
    }
    
    @Override
    public boolean equals(Object thatObject) {
        if (thatObject == null) {
            return false;
        }
        
        if (!(thatObject instanceof Binson)) {
            return false;
        }
        
        Binson that = (Binson) thatObject;
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
