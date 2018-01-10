package org.binson.lowlevel;

import java.io.IOException;
import java.io.Reader;
import org.binson.BinsonArray;
import org.binson.Binson;

/**
 * This parser implements a straight-forward, "manual" (no parser generation) 
 * approach that follows the specification at json.org closely.
 * 
 * @author Frans Lundberg
 */
public class JsonParser {
    private final TextReader r;
    private boolean enableHex = false;   
            // set to true to parse 0x... strings to byte arrays instead of strings.
    
    private JsonParser(Reader reader) {
        if (reader == null) {
            throw new IllegalArgumentException("reader == null is not allowed");
        }
        this.r = new TextReader(reader);
    }

    public static void parse(Reader reader, Binson destination) throws IOException {
        parse(reader, destination, false);
    }
    
    public static void parse(Reader reader, Binson destination, boolean enableHex) throws IOException {
        JsonParser p = new JsonParser(reader);
        p.enableHex = enableHex;
        p.parseJsonObject(destination);
    }
    
    private void parseJsonObject(Binson destination) throws IOException {
        char c = r.nextNonWhite();
        
        if (c != '{') {
            throw new JsonParseException("Bad start, expected {.", r);
        }
        
        parseObject(destination);
    }
    
    /**
     * The first { has been read already when this is called.
     * 
     * @param obj
     *          The destination where to put the result.
     */
    private final void parseObject(Binson obj) throws IOException {
        String name = parseStringOrEndOfObject();
        if (name == null) {
            return;
        }
        
        labelA: while (true) {
            // Name already parsed, now parse: colon value.
            char c = this.r.nextNonWhite();
            if (c != ':') {
                throw new JsonParseException("Expected ':', got " + c + ".", r);
            }
            
            Object value = parseValue(-1);
            obj.putElement(name, value);
            
            // After value, we can have a comma, or }
            c = r.nextNonWhite();
            switch (c) {
            case ',':
                c = r.nextNonWhite();
                if (c != '"') {
                    throw new JsonParseException("Expected '\"', got " + c + ".", r);
                }
                name = parseString();
                break;
            case '}':
                break labelA;
            default:
                throw new JsonParseException("Bad char after name-value pair, '" + c + "'.", r);
            }
        }
    }
    
    /**
     * The first [ has been read already when this method is called.
     */
    private final void parseArray(BinsonArray array) throws IOException {
        Object value = parseValueOrEndOfArray();
        if (value == null) {
            return;
        } else {
            array.addElementNoChecks(value);
        }
        
        labelA: while (true) {
            // here, we can have a comma, or ]
            char c = this.r.nextNonWhite();
            switch (c) {
            case ',':
                array.addElementNoChecks(parseValue(-1));
                break;
            case ']':
                break labelA;
            default:
                throw new JsonParseException("Bad char after array value, '" + c + "'.", r);
            }
        }
    }
    
    private final Object parseValueOrEndOfArray() throws IOException {
        char c = r.nextNonWhite();
        if (c == ']') {
            return null;
        } else {
            return parseValue(c);
        }
    }
    
    /**
     * @param firstChar First char to use, or -1 to let this method get the next one.
     */
    private Object parseValue(int firstChar) throws IOException {
        char first;
        Object result;
        
        if (firstChar == -1) {
            first = r.nextNonWhite();
        } else {
            first = (char) firstChar;
        }
        
        switch (first) {
        case 't':
            parseWord("true");
            result = new Boolean(true);
            break;
        case 'f':
            parseWord("false");
            result = new Boolean(false);
            break;
        case 'n':
            parseWord("null");
            result = JsonNull.NULL;
            break;
        case '"':
            if (enableHex) {
                result = parseStringOrBytes();
            } else {
                result = parseString();
            }
            break;
        case '{':
            Binson obj = newMap();
            parseObject(obj);
            result = obj;
            break;
        case '[':
            BinsonArray array = newBinsonArray();
            parseArray(array);
            result = array;
            break;
        case '-':
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
            result = parseNumber(first);
            break;
        default:
            throw new JsonParseException("Bad char when parsing value, " + first + ".", r);
        }
        
        return result;
    }
    
    /** 
     * Starts one character 'late' in word.
     */
    private void parseWord(String word) throws IOException {
        int length = word.length();
        for (int i = 1; i < length; i++) {
            char c = r.next();
            if (c != word.charAt(i)) {
                throw new JsonParseException("Bad char when expecting '" + word + "', got " + c + ".", r);
            }
        }
    }
    
    /**
     * Returns a Long or a Double. We need to one char look-ahead
     * ("peek") to determine that the number has ended.
     */
    private final Object parseNumber(char first) throws IOException {
        JsonNumberParser np = new JsonNumberParser();
        r.pushBack(first);
        return np.number(r);
    }
    
    /**
     * Parses a String, or returns null if the next non-white char is '}'.
     */
    private final String parseStringOrEndOfObject() throws IOException {
        char c = r.nextNonWhite();
        if (c == '}') {
            return null;
        } else if (c == '"') {
            return parseString();
        } else {
            throw new JsonParseException("Expected string start '\"'.", r);
        }
    }
    
    /** First quotation mark should have been consumed already. */
    private final String parseString() throws IOException {
        StringBuilder b = new StringBuilder();
        while (true) {
            char c = r.next();
            if (c == '"') {
                break;
            } else if (c == '\\') {
                b.append(parseEscapedChar());
            } else {
                b.append(c);
            }
        }

        return b.toString();
    }
    
    /** First quotation mark should have been consumed already. */
    private final Object parseStringOrBytes() throws IOException {
        StringBuilder b = new StringBuilder();
        while (true) {
            char c = r.next();
            if (c == '"') {
                break;
            } else if (c == '\\') {
                b.append(parseEscapedChar());
            } else {
                b.append(c);
            }
        }
   
        String string = b.toString();
        Object result = string;
        
        if (string.startsWith("0x")) {
            String hexString = string.substring(2);
            try {
                byte[] bytes = Hex.toBytes(hexString);
                result = bytes;
            } catch (IllegalArgumentException e) {
                // do nothing, handle the string as a string
            }
        }
        
        return result;
    }
    
    private final char parseEscapedChar() throws IOException {
        char c = r.next();
        char res;
        
        switch(c) {
        case '"':
        case '\\':
        case '/':
        case 'b':
        case 'f':
        case 'n':
        case 'r':
        case 't':
            res = c;
            break;
        case 'u':
            res = parseUEscape();
            break;
        default:
            throw new JsonParseException("Bad character after back-slash escape.", r);
        }
        
        return res;
    }
    
    private final char parseUEscape() throws IOException {
        char[] chars = new char[4];
        for (int i = 0; i < 4; i++) {
            char c = r.next();
            
            if (!isHexChar(c)) {
                throw new JsonParseException("Expected a hex char in \\u escape, got " 
                        + c + ".", r);
            }
            chars[i] = c;
        }
        
        int value;
        try {
            value = Integer.parseInt(new String(chars), 16);
        } catch (NumberFormatException e) {
            throw new Error("Should never happen, " + new String(chars));
        }
        return (char) value;
    }
    
    private boolean isHexChar(char c) {
        boolean isHex = false;
        switch (c) {
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
        case 'a':
        case 'b':
        case 'c':
        case 'd':
        case 'e':
        case 'f':
            isHex = true;
        }
        return isHex;
    }
    
    /**
     * Creates a new empty List. Override to be able to parse to any 
     * subclass of BinsonArray.
     * 
     * @return New empty list for Binson data.
     */
    public BinsonArray newBinsonArray() {
        return new BinsonArray();
    }
    
    /**
     * Creates a new empty Map. Override this method to be able to parse
     * to any Binson subclass.
     * 
     * @return A new map. The implementation in this class returns a new Binson object.
     */
    public Binson newMap() {
        return new Binson();
    }
}
