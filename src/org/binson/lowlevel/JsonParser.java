package org.binson.lowlevel;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.binson.BinsonArray;
import org.binson.BinsonObject;

/**
 * This parser implements a straight-forward, "manual" (no parser generation) 
 * approach that follows the specification at json.org closely.
 * 
 * @author Frans Lundberg
 */
public class JsonParser {
    private final TextReader r;
    
    private JsonParser(Reader reader) {
        if (reader == null) {
            throw new IllegalArgumentException("reader == null is not allowed");
        }
        this.r = new TextReader(reader);
    }

    public static void parse(Reader reader, BinsonObject destination) throws IOException {
        JsonParser p = new JsonParser(reader);
        p.parseJsonObject(destination);
    }
    
    private void parseJsonObject(Map<String, Object> destination) throws IOException {
        char c = r.nextNonWhite();
        
        if (c != '{') {
            throw new StringFormatException("Bad start, expected {.", r);
        }
        
        parseObject(destination);
    }
    
    /**
     * The first { has been read already when this is called.
     */
    private final void parseObject(Map<String, Object> obj) throws IOException {
        
        String name = parseStringOrEndOfObject();
        if (name == null) {
            return;
        }
        
        labelA: while (true) {
            // Name already parsed, now parse: colon value.
            char c = this.r.nextNonWhite();
            if (c != ':') {
                throw new StringFormatException("Expected ':', got " + c + ".", r);
            }
            
            Object value = parseValue(-1);
            obj.put(name, value);
            
            // After value, we can have a comma, or }
            c = r.nextNonWhite();
            switch (c) {
            case ',':
                c = r.nextNonWhite();
                if (c != '"') {
                    throw new StringFormatException("Expected '\"', got " + c + ".", r);
                }
                name = parseString();
                break;
            case '}':
                break labelA;
            default:
                throw new StringFormatException("Bad char after name-value pair, '" + c + "'.", r);
            }
        }
    }
    
    /**
     * The first [ has been read already when this method is called.
     */
    private final void parseArray(List<Object> array) throws IOException {
        Object value = parseValueOrEndOfArray();
        if (value == null) {
            return;
        } else {
            array.add(value);
        }
        
        labelA: while (true) {
            // here, we can have a comma, or ]
            char c = this.r.nextNonWhite();
            switch (c) {
            case ',':
                array.add(parseValue(-1));
                break;
            case ']':
                break labelA;
            default:
                throw new StringFormatException("Bad char after array value, '" + c + "'.", r);
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
        // TODO consider refactoring: pushBack instead of firstChar.
        
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
            result = parseString();
            break;
        case '{':
            Map<String, Object> obj = newMap();
            parseObject(obj);
            result = obj;
            break;
        case '[':
            List<Object> array = newList();
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
            throw new StringFormatException("Bad char when parsing value, " + first + ".", r);
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
                throw new StringFormatException("Bad char when expecting '" + word + "', got " + c + ".", r);
            }
        }
    }
    
    /**
     * Returns a Long or a Double. We need to look-ahead one char ("peek") to determine that
     * the number has ended.
     */
    private final Object parseNumber(char first) throws IOException {
        StringBuilder s = new StringBuilder();
        parseInt(first, s);

        char c = r.next();
        switch (c) {
        case '.':
            parseFrac(s);
            char c2 = r.next();
            if (c2 == 'e' || c2 == 'E') {
                parseExp(s);
            } else {
                r.pushBack(c2);
            }
            Double d1;
            try {
                d1 = Double.parseDouble(s.toString());
            } catch (NumberFormatException e) {
                throw new StringFormatException("Could not parse floating point number " +
                        "to Double, string: '" + s.toString() + "'.", r);
            }
            return d1;
            
        case 'e':
        case 'E':
            parseExp(s);
            Double d2;
            try {
                d2 = Double.parseDouble(s.toString());
            } catch (NumberFormatException e) {
                throw new StringFormatException("Could not parse floating point number with exp " +
                		"to Double, string: '" + s.toString() + "'.", r);
            }
            return d2;
        
        default:
            // Integer finished, return Long
            r.pushBack(c);
            Long l1;
            String string = s.toString();
            try {
                l1 = Long.parseLong(string);
            } catch (NumberFormatException e) {
                throw new StringFormatException("Could not parse integer to Java Long, string: '" + string + "'.", r);
            }
            return l1;
        }
    }
    
    private final void parseInt(char first, StringBuilder s) throws IOException {
        if (first == '-') {
            s.append('-');
            first = r.next();
        }
        parseNonNegInt(first, s);
    }
    
    private final void parseNonNegInt(char first, StringBuilder s) throws IOException {
        if (first == '0') {
            s.append('0');
            return;
        }
        
        s.append(first);
        
        while (true) {
            char c = r.next();
            if (c >= '0' && c <= '9') {
                s.append(c);
            } else {
                r.pushBack(c);
                break;
            }
        }
    }
    
    private final void parseFrac(StringBuilder s) throws IOException {
        s.append('.');
        
        char c = r.next();
        if (c >= '0' && c <= '9') {
            s.append(c);
        } else {
            throw new StringFormatException("Expected digit after decimal sign, got " 
                    + c + ".", r);
        }
        
        while (true) {
            c = r.next();
            if (c >= '0' && c <= '9') {
                s.append(c);
            } else {
                r.pushBack(c);
                break;
            }
        }
    }
    
    /**
     * First char (e/E) already consumed.
     */
    private final void parseExp(StringBuilder s) throws IOException {
        boolean gotDigitAfterE = false;
        s.append('e');   // we can always use lower-case
        char c = r.next();
        if (c == '-') {
            s.append('-');
        } else if (c == '+') {
            // do nothing
        } else if (c >= '0' && c <= '9') {
            s.append(c);
            gotDigitAfterE = true;
        } else {
            throw new StringFormatException("Expected one of -+0123456789, got " + c + ".", r);
        }
        
        if (!gotDigitAfterE) {
            c = r.next();
            if (c >= '0' && c <= '9') {
                s.append(c);
                gotDigitAfterE = true;
            } else {
                throw new StringFormatException("Expected digit, got " + c + ".", r);
            }
        }
        
        // Zero or more digits
        while (true) {
            c = r.next();
            if (c >= '0' && c <= '9') {
                s.append(c);
            } else {
                r.pushBack(c);
                break;
            }
        }
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
            throw new StringFormatException("Expected string start '\"'.", r);
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
            throw new StringFormatException("Bad character after back-slash escape.", r);
        }
        
        return res;
    }
    
    private final char parseUEscape() throws IOException {
        char[] chars = new char[4];
        for (int i = 0; i < 4; i++) {
            char c = r.next();
            
            if (!isHexChar(c)) {
                throw new StringFormatException("Expected a hex char in \\u escape, got " 
                        + c + ".", r);
            }
            chars[i] = c;
        }
        
        int value;
        try {
            value = Integer.parseInt("0x" + new String(chars));
        } catch (NumberFormatException e) {
            throw new Error("Should never happen.");
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
    
    // ---- Methods suitable to override ----
    
    /**
     * Creates a new empty List. Override to be able to parse to any 
     * implementation of List.
     */
    public List<Object> newList() {
        return new BinsonArray();
    }
    
    /**
     * Creates a new empty Map. Override this method to be able to parse
     * to any Map implementation.
     */
    public Map<String, Object> newMap() {
        return new BinsonObject(new HashMap<String, Object>());
    }
}
