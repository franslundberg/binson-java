package org.binson;

/**
 * Thrown when the format of the input data is invalid.
 *
 * @author Frans Lundberg
 */
public class FormatException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public FormatException(String message) {
        super(message);
    }
}
