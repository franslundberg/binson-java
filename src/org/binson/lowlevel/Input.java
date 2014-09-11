package org.binson.lowlevel;

import java.io.IOException;

public interface Input {

    public boolean getBoolean();

    public long getInteger();

    public double getDouble();

    public String getString();

    public byte[] getBytes();

    /**
     * Reads next token and returns the token type.
     * The returned value is one of (from Constants):
     * BEGIN, END, BEGIN_ARRAY, END_ARRAY, 
     * BOOLEAN, INTEGER, DOUBLE, STRING, BYTES.
     */
    public int next() throws IOException;
}
