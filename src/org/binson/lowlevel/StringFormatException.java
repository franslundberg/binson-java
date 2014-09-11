package org.binson.lowlevel;

import org.binson.FormatException;

public class StringFormatException extends FormatException {
    private static final long serialVersionUID = 1L;
    private final int line;
    private final int column;
    
    public StringFormatException(String message, TextReader textReader) {
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
