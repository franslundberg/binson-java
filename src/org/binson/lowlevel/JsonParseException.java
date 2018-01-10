package org.binson.lowlevel;

import org.binson.BinsonFormatException;

/**
 * Thrown to indicate that a JSON string being parsed
 * is badly formatted.
 * 
 * @author Frans Lundberg
 */
public class JsonParseException extends BinsonFormatException {
    private static final long serialVersionUID = 1L;
    private final int line;
    private final int column;
    
    public JsonParseException(String message, TextReader textReader) {
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
