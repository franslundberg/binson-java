package org.binson.lowlevel;

import org.binson.BinsonFormatException;

/**
 * Thrown to indicate that a maximum size limitation of the
 * parser has been exceeded when trying to parse a Binson object.
 * 
 * @author Frans Lundberg
 */
public class MaxSizeException extends BinsonFormatException {
    private static final long serialVersionUID = 1L;

    public MaxSizeException(String message) {
        super(message);
    }
}
