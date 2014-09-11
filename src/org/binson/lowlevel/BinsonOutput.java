package org.binson.lowlevel;

import java.io.IOException;
import java.io.OutputStream;

import static org.binson.lowlevel.Constants.*;

/**
 * This class wraps an OutputStream and provides low-level Binson output functionality.
 * 
 * @author Frans Lundberg
 */
public final class BinsonOutput implements Output {
    private OutputStream out;
    
    /**
     * Creates a new instance by wrapping the provided OutputStream.
     */
    public BinsonOutput(OutputStream out) {
        this.out = out;
    }
    
    public void writeBegin() throws IOException {
        out.write(Constants.BEGIN);
    }

    public void writeEnd() throws IOException {
        out.write(Constants.END);
    }
    
    public void writeBeginArray() throws IOException {
        out.write(Constants.BEGIN_ARRAY);
    }

    public void writeEndArray() throws IOException {
        out.write(Constants.END_ARRAY);
    }
    
    public void writeBoolean(boolean value) throws IOException {
        out.write(value == true ? Constants.TRUE : Constants.FALSE);
    }
    
    public void writeInteger(long value) throws IOException {
        writeIntegerOrLength(Constants.INTEGER, value);
    }
    
    public void writeDouble(double value) throws IOException {
        byte[] bytes = new byte[9];
        bytes[0] = Constants.DOUBLE;
        Bytes.doubleToBytesLE(value, bytes, 1);
        out.write(bytes);
    }
    
    public void writeString(String string) throws IOException {
        byte[] bytes = Bytes.stringToUtf8(string);
        writeIntegerOrLength(Constants.STRING, bytes.length);
        out.write(bytes);
    }
    
    public void writeBytes(byte[] value) throws IOException {
        writeIntegerOrLength(Constants.BYTES, value.length);
        out.write(value);
    }
    
    public void writeName(String name) throws IOException {
        writeString(name);
    }
    
    public void writeArrayValueSeparator() throws IOException {
        // empty
    }

    public void writeNameValueSeparator() {
        // empty
    }

    public void writePairSeparator() {
        // empty
    }
    
    public void flush() throws IOException {
        out.flush();
    }
    
    private void writeIntegerOrLength(int baseType, long value) throws IOException {
        int type;
        byte[] buffer;
        
        if (value >= -TWO_TO_7 && value < TWO_TO_7) {
            type = baseType | ONE_BYTE;
            buffer = new byte[1];
            buffer[0] = (byte) value;
        } else if (value >= -TWO_TO_15 && value < TWO_TO_15) {
            type = baseType | TWO_BYTES;
            buffer = new byte[2];
            Bytes.shortToBytesLE((int) value, buffer, 0);
        } else if (value >= -TWO_TO_31 && value < TWO_TO_31) {
            type = baseType | FOUR_BYTES;
            buffer = new byte[4];
            Bytes.intToBytesLE((int) value, buffer, 0);
        } else {
            type = baseType | EIGHT_BYTES;
            buffer = new byte[8];
            Bytes.longToBytesLE(value, buffer, 0);
        }
        
        out.write(type);
        out.write(buffer);
    }
}
