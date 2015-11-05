package org.binson.lowlevel;

import java.io.IOException;
import java.io.Writer;

/**
 * Creates JSON string output.
 * Note the special handling of double values that are inf, or NaN.
 * Also note the handling of byte arrays; they have no corresponding type
 * in JSON, so they are written as a string of hex digits.
 * Pretty printing is currently not supported.
 * 
 * @author Frans Lundberg
 */
public class JsonOutput implements Output {
    private Writer writer;
    private boolean pretty;
    private int indentLevel = 0;
    private String indentString;
    private String extraIndentString;
    
    public JsonOutput(Writer writer) {
        if (writer == null) {
            throw new IllegalArgumentException("writer == null not allowed");
        }
        
        this.pretty = false;
        this.writer = writer;
    }
    
    public static JsonOutput createForPrettyOutput(Writer writer, int indentSize, int extraIndentSize) {
        JsonOutput j = new JsonOutput(writer);
        j.pretty = true;
        
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < indentSize; i++) {
            b.append(" ");
        }
        j.indentString = b.toString();
        
        b = new StringBuilder();
        for (int i = 0; i < extraIndentSize; i++) {
            b.append(" ");
        }
        j.extraIndentString = b.toString();
        
        return j;
    }

    public void writeBegin() throws IOException {
        writer.write('{');
        
        if (pretty) {
            indentLevel++;
        }
    }

    public void writeEnd() throws IOException {
        if (pretty) {
            writer.write("\n");
            indentLevel--;
            indent();
        }
        
        writer.write('}');
        
        if (pretty && indentLevel == 0) {
            writer.write("\n");
        }
    }

    private void indent() throws IOException {
        writer.write(extraIndentString);
        
        for (int i = 0; i < indentLevel; i++) {
            writer.write(indentString);
        }
    }

    public void writeBeginArray() throws IOException {
        writer.write('[');
        
        if (pretty) {
            writer.write("\n");
            indentLevel++;
            indent();
        }
    }

    public void writeEndArray() throws IOException {
        if (pretty) {
            writer.write("\n");
            indentLevel--;
            indent();
        }
        
        writer.write(']');
    }

    public void writeBoolean(boolean value) throws IOException {
        writer.write(value ? "true" : "false");
    }

    public void writeInteger(long value) throws IOException {
        writer.write(Long.toString(value));
    }

    /**
     * Writes a double value to JSON.
     * 
     * @throws IllegalArgumentException 
     *      If 'value' is infinite (Double.isInfinite(value)) or
     *      not-a-number (Double.isNaN(value)).
     */
    public void writeDouble(double value) throws IOException {
        if (Double.isInfinite(value) || Double.isNaN(value)) {
            throw new IllegalArgumentException("Double value " + value 
                    + " cannot be expressed as a JSON number.");
        }
        
        writer.write(Double.toString(value));
    }
    
    public void writeName(String name) throws IOException {
        if (pretty) {
            writer.write("\n");
            indent();
        }
        
        writeString(name);
    }
    
    public void writeArrayValueSeparator() throws IOException {
        writer.write(",");
        
        if (pretty) {
            writer.write("\n");
            indent();
        }
    }

    public void writeString(String string) throws IOException {
        writer.write('"');
        writer.write(escape(string));
        writer.write('"');
    }

    public void writeBytes(byte[] value) throws IOException {
        writeString(bytesToHex("0x", value));
    }
    
    public void writeNameValueSeparator() throws IOException {
        writer.write(':');
        if (pretty) {
            writer.write(" ");
        }
    }

    public void writePairSeparator() throws IOException {
        writer.write(", ");
    }

    public void flush() throws IOException {
        writer.flush();
    }
    
    private final static char[] HEX_ARRAY = "0123456789abcdef".toCharArray();
    
    static String bytesToHex(String prefix, byte[] bytes) {
        // Inspired by http://stackoverflow.com/questions/9655181/convert-from-byte-array-to-hex-string-in-java
        
        final int prefixLength = prefix.length();
        char[] chars = new char[prefixLength + bytes.length * 2];
        prefix.getChars(0, prefixLength, chars, 0);
        
        for (int i = 0; i < bytes.length; i++) {
            int offset = prefixLength + i*2;          
            int value = bytes[i] & 0xFF;
            chars[offset + 0] = HEX_ARRAY[value >>> 4];    // 4 most significant bits
            chars[offset + 1] = HEX_ARRAY[value & 0x0f];    // 4 least significant bits
        }
        return new String(chars);
    }
    
    static String escape(String string) {
        StringBuilder b = new StringBuilder();
        int length = string.length();
        
        for (int i = 0; i < length; i++) {
            appendChar(b, string.charAt(i));
        }
        return b.toString();
    }
    
    private static void appendChar(StringBuilder b, char c) {
        final char esc = '\\';
        
        switch (c) {
        case '"': 
        case '\\':
        case '/':
            b.append(esc);
            b.append(c);
            break;
            
        case '\b':
            b.append(esc);
            b.append('b');
            break;
            
        case '\f':
            b.append(esc);
            b.append('f');
            break;
            
        case '\n':
            b.append(esc);
            b.append('n');
            break;
            
        case '\r':
            b.append(esc);
            b.append('r');
            break;
            
        case '\t':
            b.append(esc);
            b.append('t');
            break;
            
        default:
            b.append(c);
            break;
        }
    }
}
