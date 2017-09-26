package org.binson.lowlevel;

import org.binson.BinsonFormatException;

/**
 * Thrown to indicate that a string (JSON, BinsonString for example)
 * is badly formatted.
 * 
 * @author Frans Lundberg
 */
public class StringBinsonFormatException extends BinsonFormatException {
    private static final long serialVersionUID = 1L;
    private final int line;
    private final int column;
    
    public StringBinsonFormatException(String message, TextReader textReader) {
        super(message + " Location " + textReader.getLine() + ":" + textReader.getColumn() + ".");
        line = textReader.getLine();
        column = textReader.getColumn();
    }
    
    public int getLine() {
        return line;
    }
    
    public int getColumn() {
        return column;
    }
}
