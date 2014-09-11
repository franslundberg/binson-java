package org.binson.lowlevel;

import java.io.IOException;
import java.io.Writer;

/**
 * Outputs a Binson string.
 * 
 * @author Frans Lundberg
 */
public class BinsonStringOutput implements Output {
    private Writer writer;
    
    public BinsonStringOutput(Writer writer) {
        if (writer == null) {
            throw new IllegalArgumentException("writer == null not allowed");
        }
        this.writer = writer;
    }
    
    public void writeBegin() throws IOException {
        writer.write('{');
    }

    public void writeEnd() throws IOException {
        writer.write('}');
    }

    public void writeBeginArray() throws IOException {
        writer.write('[');
    }

    public void writeEndArray() throws IOException {
        writer.write(']');
    }

    public void writeBoolean(boolean value) throws IOException {
        writer.write(value ? "true" : "false");
    }

    public void writeInteger(long value) throws IOException {
        writer.write(Long.toString(value));
    }

    public void writeDouble(double value) throws IOException {
        // Java uses "NaN", "Infinity", "-Infinity", 1.1E-6, 3.3E6.
        // See Double.valueOf(String) and Double.toString(double).
        // http://docs.oracle.com/javase/1.5.0/docs/api/index.html?java/lang/Double.html
        
        writer.write(Double.toString(value));
    }

    public void writeString(String string) throws IOException {
        writer.write('"');
        writer.write(JsonOutput.escape(string));
        writer.write('"');
    }

    public void writeBytes(byte[] value) throws IOException {
        writer.write(JsonOutput.bytesToHex("0x", value));
    }
    
    public void writeName(String name) throws IOException {
        if (needsQuotationMarks(name)) {
            writer.write(createQuotedName(name));
        } else {
            writer.write(name);
        }
    }
    
    public void writeArrayValueSeparator() throws IOException {
        writer.write(',');
    }

    public void writeNameValueSeparator() throws IOException {
        writer.write('=');
    }

    public void writePairSeparator() throws IOException {
        writer.write(", ");
    }

    public void flush() throws IOException {
        writer.flush();
    }
    
    private boolean needsQuotationMarks(String name) {
        boolean result = false;
        int length = name.length();
        for (int i = 0; i < length; i++) {
            char c = name.charAt(i);
            if (charNeedsQuoationMarks(c)) {
                result = true;
                break;
            }
        }
        return result;
    }
    
    private boolean charNeedsQuoationMarks(int c) {
        // These are the allowed white-spaces (' ', \n, \r, \t) plus '"' which also
        // needs escaping, of course.
        
        return c == ' ' || c == '"' || c == '\t' || c == '\n' || c == '\r';
    }
    
    private String createQuotedName(String name) {
        StringBuilder b = new StringBuilder();
        b.append("\"");
        
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            switch (c) {
            case '"':
                b.append('\\');
                b.append('"');
                break;
            default:
                b.append(c);
                break;
            }
        }
        
        b.append("\"");
        return b.toString();
    }
}
