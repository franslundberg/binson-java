package org.binson.lowlevel;

import java.io.IOException;

/**
 * Interface for serialization; shared between JsonOutput and BinsonOutput.
 * 
 * @author Frans Lundberg
 */
public interface Output {
    public void writeBegin() throws IOException;

    public void writeEnd() throws IOException;

    public void writeBeginArray() throws IOException;

    public void writeEndArray() throws IOException;

    public void writeBoolean(boolean value) throws IOException;

    public void writeInteger(long value) throws IOException;

    public void writeDouble(double value) throws IOException;

    public void writeString(String string) throws IOException;

    public void writeBytes(byte[] value) throws IOException;

    public void writeName(String name) throws IOException;
    
    public void writeArrayValueSeparator() throws IOException;
    
    public void writeNameValueSeparator() throws IOException;

    public void writePairSeparator() throws IOException;
    
    public void flush() throws IOException;
}
