package org.binson.lowlevel;

/**
 * Low-level constants; see the Binson specification.
 * 
 * @author Frans Lundberg
 */
public class Constants {
    // Frans 2015-10-04: Before today constants for TRUE/FALSE 
    // were mixed up; not following BINSON-SPEC-1. Changed to 
    // follow spec: "true = %x44, false = %x45".
    
    public static final int BEGIN       = 0x40;    // '@'
    public static final int END         = 0x41;    // 'A'
    public static final int BEGIN_ARRAY = 0x42;    
    public static final int END_ARRAY   = 0x43;
    
    public static final int BOOLEAN     = 0x44;
    public static final int TRUE        = 0x44;
    public static final int FALSE       = 0x45;
    public static final int DOUBLE      = 0x46;
    
    public static final int INTEGER     = 0x10;
    public static final int INTEGER1    = 0x10;
    public static final int INTEGER2    = 0x11;
    public static final int INTEGER4    = 0x12;
    public static final int INTEGER8    = 0x13;
    
    public static final int STRING      = 0x14;
    public static final int STRING1     = 0x14;
    public static final int STRING2     = 0x15;
    public static final int STRING4     = 0x16;
    
    public static final int BYTES       = 0x18;
    public static final int BYTES1      = 0x18;
    public static final int BYTES2      = 0x19;
    public static final int BYTES4      = 0x1a;
    
    /** Last two bits are for int length (1, 2, 4 or 8 bytes). */
    public static final int INT_LENGTH_MASK = 0x03;
    public static final int ONE_BYTE        = 0x00;
    public static final int TWO_BYTES       = 0x01;
    public static final int FOUR_BYTES      = 0x02;
    public static final int EIGHT_BYTES     = 0x03;
    
    public static final long TWO_TO_7  = 128;
    public static final long TWO_TO_15 = 32768;
    public static final long TWO_TO_31 = 2147483648L;
    
    public static final int BOOLEAN_CLASSNAME_LENGTH = 17;
    public static final int LONG_CLASSNAME_LENGTH = 14;
    public static final int DOUBLE_CLASSNAME_LENGTH = 16;
    public static final int STRING_CLASSNAME_LENGTH = 16;
    public static final int BYTES_CLASSNAME_LENGTH = 2;
    public static final int BINSON_OBJECT_CLASSNAME_LENGTH = 17;
    public static final int BINSON_ARRAY_CLASSNAME_LENGTH = 22;
}
