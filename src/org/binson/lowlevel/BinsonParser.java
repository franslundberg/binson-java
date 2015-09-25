package org.binson.lowlevel;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import org.binson.BinsonArray;
import org.binson.Binson;
import org.binson.FormatException;

import static org.binson.lowlevel.Constants.*;

// TODO consider throwing subclass: BinsonFormatException instead. Compare to the other subclasses.

/**
 * The public static parse method creates a Binson object from a given BinsonInput.
 * 
 * @author Frans Lundberg
 */
public class BinsonParser {
    private final InputStream in;
    
    private BinsonParser(InputStream in) throws FormatException, IOException {
        if (in == null) throw new IllegalArgumentException("in == null not allowed");
        this.in = in;
    }
    
    /**
     * Parses bytes from the the provided input stream and returns a Binson object.
     * 
     * @throws IOException If an IOException is thrown from the underlying InputStream.
     * @throws FormatException If the bytes does not follow the Binson spec (BINSON-SPEC-1).
     */
    public static Binson parse(InputStream in) throws IOException {
        return new BinsonParser(in).parseObject();
    }
    
    private Binson parseObject() throws IOException {
        int type = readOne();
        if (type != BEGIN) {
            throw new FormatException("Expected BEGIN, got " + type + ".");
        }
        
        return parseFields();
    }

    private Binson parseFields() throws IOException {
        Binson object = new Binson();
        int type;
        
        outer: while (true) {
            type = readOne();
            
            switch (type) {
            case STRING1:
            case STRING2:
            case STRING4:
                parseField(type, object);
                break;
            case END:
                break outer;
            default:
                throw new FormatException("Expected string/end, got " + type + ".");
            }
        }
        
        return object;
    }
    
    private void parseField(int type, Binson dest) throws IOException {
        String name = parseString(type);
        Object value = parseValue(false);
        dest.put(name, value);
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
                throw new FormatException("Unexpected type: " + type + ".");
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
            throw new FormatException("Unexpected type: " + type + ".");
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
            
            array.add(value);
        }
        
        return array;
    }
    
    private String parseString(int type) throws IOException {
        int stringLen = (int) readInteger(type);
        if (stringLen < 0) throw new FormatException("Bad stringLen, " + stringLen + ".");
        byte[] stringBytes = new byte[stringLen];
        in.read(stringBytes, 0, stringBytes.length);
        return Bytes.utf8ToString(stringBytes);
    }

    private byte[] parseBytes(int type) throws IOException {
        int bytesLen = (int) readInteger(type);
        if (bytesLen < 0) throw new FormatException("Bad bytesLen, " + bytesLen + ".");
        byte[] bytes = new byte[bytesLen];
        in.read(bytes, 0, bytes.length);
        return bytes;
    }
    
    private double parseDouble() throws IOException {
        byte[] b8 = new byte[8];
        in.read(b8, 0, 8);
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
            byte[] b2 = new byte[2];
            in.read(b2, 0, 2);
            integer = Bytes.bytesToShortLE(b2, 0);
            break;
            
        case Constants.FOUR_BYTES:
            byte[] b4 = new byte[4];
            in.read(b4, 0, 4);
            integer = Bytes.bytesToIntLE(b4, 0);
            break;
            
        case Constants.EIGHT_BYTES:
            byte[] b8 = new byte[8];
            in.read(b8, 0, 8);
            integer = Bytes.bytesToLongLE(b8, 0);
            break;
            
        default:
            throw new Error("never happens, intType: " + intType);
        }
        
        return integer;
    }
    
    private int readOne() throws IOException {
        int result = in.read();
        if (result == -1) {
            throw new EOFException();
        }
        return result;
    }
}
