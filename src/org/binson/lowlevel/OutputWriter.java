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
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    
    public static void writeToOutput(Binson obj, Output output) throws IOException {
        String[] keys = obj.keySet().toArray(EMPTY_STRING_ARRAY);
        Arrays.sort(keys, BinsonFieldNameComparator.INSTANCE);
        
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
