package org.binson.lowlevel;

import java.io.IOException;
import java.io.Reader;

/**
 * Wraps a Reader with additional features useful when parsing.
 * An object of this class has a pushBack feature and keeps track of the current
 * row and column of the text being parsed.
 * 
 * @author Frans Lundberg
 */
public class TextReader {
    private final Reader reader;
    private int line = 1;           // one-based line index
    private int column = 1;         // one-based column index
    private int pushedBack = -1;    // char currently "pushed back" or -1 if none.
    
    public TextReader(Reader reader) {
        this.reader = reader;
    }

    public final int getLine() {
        return line;
    }

    public final int getColumn() {
        return column;
    }
    
    /**
     * Returns next character to process. Updates line/column/char indexes and handles the push back
     * feature (an internal implementation feature).
     * 
     * @return The next character to process.
     * @throws IOException
     *      If there is a problem reading the char from the underlying reader.
     * @throws JsonParseException
     *      IF the text read does not follow the expected format.
     */
    public final char next() throws IOException {
        int c;

        if (pushedBack != -1) {
            c = pushedBack;
            pushedBack = -1;
        } else {
            c = reader.read();
            
            if (c == '\n') {
                line++;
                column = 1;
            } else {
                column++;
            }
        }

        if (c == -1) {
            throw new JsonParseException("Unexpectedly reached end of stream.", this);
        }

        return (char) c;
    }
    
    public static boolean isWhitespace(int c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\r';
    }
    
    public final char nextNonWhite() throws IOException {
        while (true) {
            char c = next();
            if (!isWhitespace(c)) {
                return c;
            }
        }
    }
    
    public final void pushBack(char c) {
        if (this.pushedBack != -1) {
            // Should never happen, consider it a bug.
            throw new Error("Tried to push back char when one char was already pushed back.");
        }
        this.pushedBack = c;
    }
}
