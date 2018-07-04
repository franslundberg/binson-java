package org.binson.lowlevel;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import org.binson.BinsonArray;
import org.binson.Binson;
import org.binson.BinsonFormatException;
import static org.binson.lowlevel.Constants.*;

/**
 * An instance of this class can parse binary binson data to a Java Binson object.
 * 
 * @author Frans Lundberg
 */
public class BinsonParser {
    private final InputStream in;
    private int maxSize = 40*1000000;
    private int maxFieldCount = 1000;
    private long streamOffset = 0;
    
    /**
     * Creates a new parser that takes data from
     * the given input stream. "public" since 2.1.
     */
    public BinsonParser(InputStream in) throws BinsonFormatException, IOException {
        if (in == null) throw new IllegalArgumentException("in == null not allowed");
        this.in = in;
    }
    
    /**
     * Sets the maximum byte size of the Binson object to parse.
     * Default is 40e6 bytes (following recommendations in BINSON-SPEC-1).
     * If an attempt is made to parse a larger binson object,
     * a BinsonFormatException will be thrown.
     * 
     * This method must be called before parse() is called.
     */
    public void setMaxSize(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("bad maxSize, " + maxSize);
        }
        this.maxSize = maxSize;
    }
    
    public int getMaxSize() {
    	return maxSize;
    }
    
    /**
     * Sets the maximum number of field on a Binson object that the parser
     * should accept. The default value is 1000. Note, BINSON-SPEC-1 recommends
     * to not use more than 100 fields in a Binson object. The default behavior of
     * the parser is to allow 10 times more.
     * 
     * This method must be called before parse() is called.
     */
    public void setMaxFieldCount(int maxFieldCount) {
        if (maxFieldCount <= 0) {
            throw new IllegalArgumentException("bad maxFieldCount, " + maxFieldCount);
        }
        this.maxFieldCount = maxFieldCount;
    }
    
    public int getMaxFieldCount() {
    	return maxFieldCount;
    }
    
    /**
     * Parses a Binson object from the input stream.
     * 
     * @throws BinsonFormatException
     * @throws IOException
     * @since 2.1
     */
    public Binson parse() throws IOException {
        return parseObject();
    }
    
    /**
     * Parses bytes from the the provided input stream and returns a Binson object.
     * 
     * @param in  The InputStream to read from.
     * @return The newly created Binson object.
     * @throws IOException If an IOException is thrown from the underlying InputStream.
     * @throws BinsonFormatException If the bytes does not follow the Binson spec (BINSON-SPEC-1).
     */
    public static Binson parse(InputStream in) throws IOException {
        return new BinsonParser(in).parseObject();
    }
    
    private Binson parseObject() throws IOException {
        int type = readOne();
        if (type != BEGIN) {
            throw new BinsonFormatException("Expected BEGIN, got " + type + ".");
        }
        
        return parseFields();
    }

    private Binson parseFields() throws IOException {
        Binson object = new Binson();
        int type;
        String currentFieldName = null;
        int fieldCount = 0;
        
        outer: while (true) {
            type = readOne();
            
            switch (type) {
            case STRING1:
            case STRING2:
            case STRING4:
            	fieldCount++;
            	if (fieldCount > this.maxFieldCount) {
            		throw new MaxSizeException("The Binson object being parsed has more fields " +
            				"than the maxFieldCount setting of the parser (" +
            				this.maxFieldCount + ").");
            	}
                currentFieldName = parseField(currentFieldName, type, object);
                break;
            case END:
                break outer;
            default:
                throw new BinsonFormatException("Expected string/end, got " + type + ".");
            }
        }
        
        return object;
    }
    
    /**
     * Parses a field and returns the field name.
     */
    private String parseField(String currentFieldName, int type, Binson dest) throws IOException {
        String name = parseString(type);
        
        if (currentFieldName != null) {
            if (BinsonFieldNameComparator.INSTANCE.compare(currentFieldName, name) >= 0) {
                throw new BinsonFormatException("bad field order, " + currentFieldName + ", " 
                		+ name);
            }
        }
        
        Object value = parseValue(false);
        dest.putElement(name, value);
        
        return name;
    }
    
    private Object parseValue(boolean inArray) throws IOException {
        Object result;
        int type = readOne();
        
        switch (type) {
        case BEGIN:
            result = parseFields();
            break;
        case BEGIN_ARRAY:
            result = parseArray();
            break;
        case END_ARRAY:
            if (inArray) {
                result = null;
            } else {
                throw new BinsonFormatException("Unexpected type: " + type + ".");
            }
            break;
            
        case FALSE:
            result = Boolean.FALSE;
            break;
            
        case TRUE:
            result = Boolean.TRUE;
            break;
            
        case DOUBLE:
            result = parseDouble();
            break;
            
        case INTEGER1:
        case INTEGER2:
        case INTEGER4:
        case INTEGER8:
            result = readInteger(type);
            break;
            
        case STRING1:
        case STRING2:
        case STRING4:
            result = parseString(type);
            break;
            
        case BYTES1:
        case BYTES2:
        case BYTES4:
            result = parseBytes(type);
            break;
            
        default:
            throw new BinsonFormatException("Unexpected type: " + type + ".");
        }
        
        return result;
    }
    
    private BinsonArray parseArray() throws IOException {
        BinsonArray array = new BinsonArray();
        
        while (true) {
            Object value = parseValue(true);
            if (value == null) {
                break;
            }
            
            array.addElementNoChecks(value);
        }
        
        return array;
    }
    
    private String parseString(int type) throws IOException {
        int stringLen = (int) readInteger(type);
        if (stringLen < 0) throw new BinsonFormatException("Bad stringLen, " + stringLen + ".");
        byte[] stringBytes = read(stringLen);
        return Bytes.utf8ToString(stringBytes);
    }

    private byte[] parseBytes(int type) throws IOException {
        int bytesLen = (int) readInteger(type);
        if (bytesLen < 0) throw new BinsonFormatException("Bad bytesLen, " + bytesLen + ".");
        byte[] bytes = read(bytesLen);
        return bytes;
    }
    
    private double parseDouble() throws IOException {
        byte[] b8 = read(8);
        return Bytes.bytesToDoubleLE(b8, 0);
    }
    
    private long readInteger(int type) throws IOException {
        int intType = type & Constants.INT_LENGTH_MASK;
        long integer;
        
        switch (intType) {
        case Constants.ONE_BYTE:
            int oneByte = readOne();
            if (oneByte > 127) {
                oneByte -= 256;
            }
            integer = oneByte;
            break;
            
        case Constants.TWO_BYTES:
            byte[] b2 = read(2);
            integer = Bytes.bytesToShortLE(b2, 0);
            if (RangeUtil.isInOneByteRange(integer)) {
                throw new BinsonFormatException("integer value " + integer 
                		+ " should be stored in one byte, not two");
            }
            break;
            
        case Constants.FOUR_BYTES:
            byte[] b4 = read(4);
            integer = Bytes.bytesToIntLE(b4, 0);
            if (RangeUtil.isInTwoByteRange(integer)) {
                throw new BinsonFormatException("integer value " + integer 
                		+ " should be stored in less than 4 bytes");
            }
            break;
            
        case Constants.EIGHT_BYTES:
            byte[] b8 = read(8);
            integer = Bytes.bytesToLongLE(b8, 0);
            if (RangeUtil.isInFourByteRange(integer)) {
                throw new BinsonFormatException("integer value " + integer 
                		+ " should be stored in less than 8 bytes");
            }
            break;
            
        default:
            throw new Error("never happens, intType: " + intType);
        }
        
        return integer;
    }
    
    /**
     * Reads one byte from the input stream.
     * 
     * @return An integer with the byte value in the range 0..255. 
     * @throws IOException
     */
    private int readOne() throws IOException {
        byte[] buffer = read(1);
        return Bytes.unsigned(buffer[0]);
    }
    
    /**
     * Allocates a buffer (the result), reads data to it from the
     * input stream and returns the result.
     * 
     * @throws IOException 
     * @throws BinsonFormatException
     *          If the maximum Binson object size has been exceeded.
     */
    private byte[] read(int length) throws IOException {
        return readFully(in, length);
    }
    
    /**
     * Blocks until 'length' bytes have been read to 'dest'.
     * 
     * @throws MaxSizeException
     */
    private byte[] readFully(InputStream in, int length) 
            throws IOException {
        // Copied from https://github.com/franslundberg/java-cut/blob/master/src/cut/Io.java
        // 2018-01-08.
        
        streamOffset += length;
        if (streamOffset >= maxSize) {
            throw new MaxSizeException("Binson object being parsed exceeds max "
                    + "byte size (" + maxSize + "),");
        }
        
        byte[] dest = new byte[length];
        int offset = 0;
        int len = length;
        
        while (true) {
            int count = in.read(dest, offset, len);
            if (count == -1) {
                throw new EOFException("EOF reached");
            }
            
            offset += count;
            len -= count;
            if (len == 0) {
                break;
            }
        }
        
        return dest;
    }
}
