package org.binson.lowlevel;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.binson.BinsonArray;
import org.binson.BinsonObject;

/**
 * Outputs a Binson object to an Output implementation.
 * 
 * @author Frans Lundberg
 */
public class OutputWriter {
    public static void mapToOutput(Map<String, Object> map, Output output) throws IOException {
        String[] keys = map.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        boolean isFirstPair = true;
        
        output.writeBegin();
        
        for (String name : keys) {
            Object value = map.get(name);
            assert value != null;
            
            if (isFirstPair) {
                isFirstPair = false;
            } else {
                output.writePairSeparator();
            }
            
            output.writeName(name);
            output.writeNameValueSeparator();
            writeValue(output, value);
        }
        
        output.writeEnd();
        output.flush();
    }
    
    private static void writeValue(Output output, Object value) throws IOException {
        int classNameLength = value.getClass().getName().length();
        
        switch (classNameLength) {
        case Constants.BOOLEAN_CLASSNAME_LENGTH:
            if (value instanceof Boolean) {
                output.writeBoolean((Boolean) value);
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
            
        case Constants.BINSON_ARRAY_CLASSNAME_LENGTH:
            if (value instanceof BinsonArray) {
                BinsonArray array = (BinsonArray) value;
                output.writeBeginArray();
                boolean isFirst = true;
                        
                for (Object element : array) {
                    if (isFirst) {
                        isFirst = false;
                    } else {
                        output.writeArrayValueSeparator();
                    }
                    writeValue(output, element);
                }
                output.writeEndArray();
            }
            break;
            
        case Constants.BINSON_OBJECT_CLASSNAME_LENGTH:
            if (value instanceof BinsonObject) {
                BinsonObject object = (BinsonObject) value;
                mapToOutput(object, output);
            }
            break;
            
        default:
            // Empty. Elements of unsupported types are silently ignored.
            break;
        }
    }
}
