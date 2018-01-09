package org.binson.lowlevel;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.binson.BinsonArray;
import org.binson.Binson;

// A. Remove BinsonString support.

/**
 * Parser that parses a Binson string to a Binson object.
 * 
 * @author Frans Lundberg
 */
public class BinsonStringParser {
    private final TextReader r;
    
    private BinsonStringParser(Reader reader) {
        if (reader == null) {
            throw new IllegalArgumentException("reader == null is not allowed");
        }
        this.r = new TextReader(reader);
    }
    
    /**
     * Parses a Binson string to the destination Binson object.
     * 
     * @param reader
     *      The source for text to parse.
     * @param destination
     *      The destination Binson object to add the data to.
     * @throws IOException  
     *      For IO problem.
     * @throws StringBinsonFormatException
     *      When the text read does not follow the expected format.
     */
    public static void parse(Reader reader, Binson destination) throws IOException {
        BinsonStringParser p = new BinsonStringParser(reader);
        p.parseObject(destination);
    }
    
    private void parseObject(Map<String, Object> destination) throws IOException {
        char c = r.nextNonWhite();
        
        if (c != '{') {
            throw new StringBinsonFormatException("Bad start, expected {.", r);
        }
        
        while (true) {
            c = r.nextNonWhite();
            if (c == '}') {
                break;
            } else {
                r.pushBack(c);
                parsePair(destination);
            }
        }
    }
    
    private void parsePair(Map<String, Object> destination) throws IOException {
        String name = parseName();
        char c = r.nextNonWhite();
        if (c != ':') {
            throw new StringBinsonFormatException("Expected ':' after name '" + name + "'.", r);
        }
        
        Object value = parseValue();
        
        // TODO consider: how to handle existing value - not allowed for Binson string, but
        // but there could be existing values in destination already....
        
        destination.put(name, value);
    }
    
    private String parseName() throws IOException {
        char first = r.nextNonWhite();
        
        if (first == '\"') {
            return parseQuotedName();
        } else {
            return parseNonQuotedName(first);
        }    
    }
    
    private Object parseValue() throws IOException {
        char first;
        Object result;
        first = r.nextNonWhite();
        
        switch (first) {
        case 't':
            parseWord("true");
            result = new Boolean(true);
            break;
        case 'f':
            parseWord("false");
            result = new Boolean(false);
            break;
        case '"':
            result = parseString();
            break;
        case '{':
            Map<String, Object> obj = newMap();
            r.pushBack('{');
            parseObject(obj);
            result = obj;
            break;
        case '[':
            List<Object> array = newList();
            parseArray(array);
            result = array;
            break;
        // TODO inf, -inf, nan ...
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
            throw new StringBinsonFormatException("Bad char when parsing value, " + first + ".", r);
        }
        
        return result;
    }
    
    private String parseQuotedName() throws IOException {
        // First " already consumed.
        // This method consumes chars until ".
        
        StringBuilder b = new StringBuilder();
        while (true) {
            char c = r.next();
            if (c == '"') {
                break;
            }
            b.append(c);
        }
        
        return b.toString();
    }
    
    private String parseNonQuotedName(char first) throws IOException {
        // Consumes chars until ':' or whitespace.
        
        StringBuilder b = new StringBuilder();
        b.append(first);
        while (true) {
            char c = r.next();
            if (c == ':' || TextReader.isWhitespace(c)) {
                r.pushBack(c);
                break;
            }
            b.append(c);
        }
        
        return b.toString();
    }

    /**
     * The first [ has been consumed when this method is called.
     */
    private final void parseArray(List<Object> array) throws IOException {
        while (true) {
            Object value = parseValueOrEndOfArray();
            if (value == null) {
                return;
            } else {
                array.add(value);
            }    
        }
    }
    
    private final Object parseValueOrEndOfArray() throws IOException {
        char c = r.nextNonWhite();
        if (c == ']') {
            return null;
        } else {
            r.pushBack(c);
            return parseValue();
        }
    }
    
    /** 
     * Starts one character 'late' in word.
     */
    private void parseWord(String word) throws IOException {
        int length = word.length();
        for (int i = 1; i < length; i++) {
            char c = r.next();
            if (c != word.charAt(i)) {
                throw new StringBinsonFormatException("Bad char when expecting '" + word + "', got " + c + ".", r);
            }
        }
    }
    

    // ======== Methods suitable to override ========
    
    /**
     * Creates a new empty List. Override this method to be able to parse to any 
     * List implementation.
     * 
     * @return The new list instance.
     */
    public List<Object> newList() {
        return new BinsonArray();
    }
    
    /**
     * Creates a new empty Map. Override this method to be able to parse
     * to any Map implementation.
     * 
     * @return The new map instance.
     */
    public Map<String, Object> newMap() {
        return new Binson(new HashMap<String, Object>());
    }
    
    
    // ======== parseString() with helpers ========

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
            throw new StringBinsonFormatException("Bad character ('" + c + "') after backslash escape.", r);
        }
        
        return res;
    }
    
    private final char parseUEscape() throws IOException {
        char[] chars = new char[4];
        for (int i = 0; i < 4; i++) {
            char c = r.next();
            
            if (!isHexChar(c)) {
                throw new StringBinsonFormatException("Expected a hex char in \\u escape, got " 
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
    
    
    // ======== parseNumber with helpers ========
    
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
                d1 = Double.valueOf(s.toString());
            } catch (NumberFormatException e) {
                throw new StringBinsonFormatException("Could not parse floating point number " +
                        "to Double, string: '" + s.toString() + "'.", r);
            }
            return d1;
            
        case 'e':
        case 'E':
            parseExp(s);
            Double d2;
            try {
                d2 = Double.valueOf(s.toString());
            } catch (NumberFormatException e) {
                throw new StringBinsonFormatException("Could not parse floating point number with exp " +
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
                throw new StringBinsonFormatException("Could not parse integer to Long, string: '" + string + "'.", r);
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
            throw new StringBinsonFormatException("Expected digit after decimal sign, got " 
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
            throw new StringBinsonFormatException("Expected one of -+0123456789, got " + c + ".", r);
        }
        
        if (!gotDigitAfterE) {
            c = r.next();
            if (c >= '0' && c <= '9') {
                s.append(c);
                gotDigitAfterE = true;
            } else {
                throw new StringBinsonFormatException("Expected digit, got " + c + ".", r);
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
}
