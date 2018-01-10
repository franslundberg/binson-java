package org.binson.lowlevel;

import java.io.IOException;
import java.util.Arrays;
import org.binson.BinsonArray;
import org.binson.Binson;

/**
 * Writes (serializes) a Binson object to an Output implementation.
 * 
 * @author Frans Lundberg
 */
public class OutputWriter {
    private static final BinsonFieldNameComparator BINSON_STRING_COMPARATOR = new BinsonFieldNameComparator();
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    
    public static void writeToOutput(Binson obj, Output output) throws IOException {
        String[] keys = obj.keySet().toArray(EMPTY_STRING_ARRAY);
        Arrays.sort(keys, BINSON_STRING_COMPARATOR);
        
        boolean isFirstPair = true;
        
        output.writeBegin();
        
        for (String name : keys) {
            Object value = obj.getValue(name);
            ValueType type = ValueType.fromObject(value);
            
            if (isFirstPair) {
                isFirstPair = false;
            } else {
                output.writePairSeparator();
            }
            
            output.writeName(name);
            output.writeNameValueSeparator();
            writeValue(output, type, value);
        }
        
        output.writeEnd();
        output.flush();
    }
    
    private static void writeValue(Output output, ValueType type, Object value) throws IOException {
        switch (type) {
        case BOOLEAN:
            output.writeBoolean((Boolean) value);
            break;
            
        case INTEGER:
            output.writeInteger((Long) value);
            break;
            
        case DOUBLE:
            output.writeDouble((Double) value);
            break;
            
        case STRING:
            output.writeString((String) value);
            break;
            
        case BYTES:
            output.writeBytes((byte[]) value);
            break;
            
        case ARRAY:
            BinsonArray array = (BinsonArray) value;
            writeArray(array, output);
            break;
            
        case OBJECT:
            writeToOutput((Binson) value, output);
            break;
            
        default:
            throw new Error("never happens, " + type.toString());
        }
        
        /*
        int classNameLength = value.getClass().getName().length();
        
        switch (classNameLength) {
        case Constants.BOOLEAN_CLASSNAME_LENGTH:
            if (value instanceof Boolean) {
                output.writeBoolean((Boolean) value);
            }
            
            if (value instanceof Binson) {
                Binson object = (Binson) value;
                writeToOutput(object, output);
                break;
            }
            
            break;
        
        case Constants.LONG_CLASSNAME_LENGTH:
            if (value instanceof Long) {
                output.writeInteger((Long) value);
            }
            break;
        
        case Constants.DOUBLE_CLASSNAME_LENGTH:    // Double, String - same length
            if (value instanceof Double) {
                output.writeDouble((Double) value);
            } else if (value instanceof String) {
                output.writeString((String) value);
            }
            break;
            
        case Constants.BYTES_CLASSNAME_LENGTH:
            if (value instanceof byte[]) {
                output.writeBytes((byte[]) value);
            }
            break;
                        
        default:
            if (value instanceof Binson) {
                Binson object = (Binson) value;
                writeToOutput(object, output);
                break;
            } else if (value instanceof BinsonArray) {
                BinsonArray array = (BinsonArray) value;
                output.writeBeginArray();
                boolean isFirst = true;
                
                for (int i = 0; i < array.size(); i++) {
                    Object element = array.getElement(i);
                    if (isFirst) {
                        isFirst = false;
                    } else {
                        output.writeArrayValueSeparator();
                    }
                    writeValue(output, element);
                }
                output.writeEndArray();
            } else {
                // Note, this code does not support ignoring other value types.
            }
            
            break;
        }
        */
    }

    private static void writeArray(BinsonArray array, Output output) throws IOException {
        output.writeBeginArray();
        boolean isFirst = true;
        
        for (int i = 0; i < array.size(); i++) {
            Object element = array.getElement(i);
            if (isFirst) {
                isFirst = false;
            } else {
                output.writeArrayValueSeparator();
            }
            writeValue(output, ValueType.fromObject(element), element);
        }
        output.writeEndArray();
    }
}
