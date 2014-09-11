package org.binson.lowlevel;

/**
 * Represents the ugly null value of JSON.
 * 
 * @author Frans Lundberg
 */
public class JsonNull {
    
    /**
     * A constant representing the JSON null value.
     */
    public static final JsonNull NULL = new JsonNull();
    
    private JsonNull() {
    }
}
