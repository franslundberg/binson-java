package org.binson.lowlevel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.binson.BinsonArray;
import org.binson.BinsonObject;

// TODO replace JsonParser and BinsonStringParser with this class.

/**
 * Parser that can parse both JSON objects and Binson text.
 * 
 * @author Frans Lundberg
 */
public class TextParser {
    // See RFC 7159, naming taken from there.
    // A new instance of this class is used for each object that is parsed.
    
    private final TextReader r;
    private final Map<String, Object> dest;
    private final NumberParser numberParser;
    
    public TextParser(TextReader textReader, Map<String, Object> dest) {
        this.r = textReader;
        this.dest = dest;
        this.numberParser = new NumberParser(allowSpecialDoubles());
    }
    
    public static BinsonObject parse(Reader reader) throws IOException {
        BinsonObject dest = new BinsonObject();
        TextReader textReader = new TextReader(reader);
        TextParser textParser = new TextParser(textReader, dest);
        textParser.object();
        return dest;
    }
    
    // ======== Override for non-JSON parser ========
    
    public boolean allowNull() {
        return true;
    }
    
    public boolean allowSpecialDoubles() {
        return false;
    }
    
    public boolean allowBytes() {
        return false;
    }
    
    // ======== Override for custom Map / List implementations ========
    
    /**
     * Creates a new empty List. Override this method to be able to parse to any 
     * List implementation.
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
    
    // ========
    
    
    public void object() throws IOException {
        beginObject();
        members();
    }
    
    private void beginObject() throws IOException {
        char c = r.nextNonWhite();
        if (c != '{') {
            throw new StringFormatException("Expected '{', got '" + c + "'.", r);
        }
    }
    
    /**
     * Consumes *member and ending }.
     */
    private void members() throws IOException {
        char c;
        
        c = r.nextNonWhite();
        if (c == '}') {
            return;    // empty object {}
        }
        
        r.pushBack(c);
        member();
        
        while (true) {
            c = r.nextNonWhite();
            if (c == '}') {
                return;
            } 
            
            if (c != ',') {
                throw new StringFormatException("Expected } or comma, got '" + c + "'.", r);
            }
            
            member();
        }
    }
    
    private void member() throws IOException {
        String name = string();
        nameSeparator();
        Object value = value();
        dest.put(name, value);
    }
    
    /**
     * Separator between name and value of a member.
     */
    private void nameSeparator() throws IOException {
        char c = r.nextNonWhite();
        if (c != ':') {
            throw new StringFormatException("Expected ':', got '" + c + "'.", r);
        }
    }
    
    /**
     * The first [ has been consumed already.
     */
    private final void array(List<Object> dest) throws IOException {
        char c = r.nextNonWhite();
        
        if (c == ']') {
            return;
        } else {
            r.pushBack(c);
        }
        
        while (true) {
            dest.add(value());
            
            c = r.nextNonWhite();
            if (c == ']') {
                return;
            } 
            
            if (c != ',') {
                throw new StringFormatException("Expected ',' or ']', got '" + c + "'.", r);
            }
        }
    }
    
    private Object value() throws IOException {
        char c = r.nextNonWhite();
        Object value;
        
        switch (c) {
        case 'f':
            parseWord("false");
            value = new Boolean(false);
            break;
        case 't':
            parseWord("true");
            value = new Boolean(true);
            break;
        case 'n':
            if (!allowNull()) {
                throw new StringFormatException("Cannot parse value.", r);
            }
            parseWord("null");
            value = JsonNull.NULL;
            break;
        case '{':
            r.pushBack('{');
            Map<String, Object> map = newMap();
            TextParser parser = new TextParser(r, map);
            parser.object();
            value = map;
            break;
        case '[':
            List<Object> list = newList();
            array(list);
            value = list;
            break;
        case '"':
            r.pushBack(c);
            value = string();
            break;
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
        case '-':
        case 'i':    // inf
        case 'N':    // NaN
            r.pushBack(c);
            value = numberParser.number(r);
            break;
        case '+':
            throw new StringFormatException("Cannot parse, numbers cannot start with '+'.", r);
        case 'x':
            value = x();
            break;
        default:
            throw new StringFormatException("Cannot parse value.", r);
        }
        
        return value;
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
    
    
    // ======== string() with helpers ========
    
    private String string() throws IOException {
        char c = r.nextNonWhite();
        if (c != '"') {
            throw new StringFormatException("Expected '\"', got '" + c + "'.", r);
        }
        
        return chars();
    }
    
    /**
     * Consumes: [*char quotation-mark].
     */
    private String chars() throws IOException {
        StringBuffer b = new StringBuffer();
        
        while (true) {
            char c = r.next();
            if (c == '"') {
                break;
            } else if (c == '\\') {
                c = escape();
            }
            
            b.append(c);
        }
        
        return b.toString();
    }
    
    /**
     * Handles a character escape starting with a backslash.
     * The backslash has been consumed already.
     */
    private char escape() throws IOException {
        char result;
        char c = r.next();
        
        switch (c) {
        case '"':
        case '\\':
        case '/':
            result = c;
            break;
        case 'b':
            result = '\b';
            break;
        case 'f':
            result = '\f';
            break;
        case 'n':
            result = '\n';
            break;
        case 'r':
            result = '\r';
            break;
        case 't':
            result = '\t';
            break;
        case 'u':
            result = uEscape();
        default:
            throw new StringFormatException("Illegal string escape char: '" + c + "'.", r);
        }
        
        return result;
    }
    
    private final char uEscape() throws IOException {
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
        //
        // This should be case-insensitive for JSON. 
        // See http://tools.ietf.org/html/rfc5234 (ABNF) "HEXDIG" rule. 
        //
        
        boolean isHex;
        
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
        case 'A':
        case 'B':
        case 'C':
        case 'D':
        case 'E':
        case 'F':
            isHex = true;
            break;
        default:
            isHex = false;
            break;
        }
        
        return isHex;
    }
    
    
    // ======== Handle bytes (xHHHHHH...) ========
    
    private byte[] x() throws IOException {
        // Zero or more pairs of hex chars (case-insensitive).
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        while (true) {
            char c1 = r.next();
            if (isHexChar(c1)) {
                char c2 = r.next();
                if (isHexChar(c2)) {
                    out.write(hexPairToByte(c1, c2));
                } else {
                    throw new StringFormatException("Could not parse bytes, the number "
                            + "of hex characters must be even.", r);
                }
            } else {
                r.pushBack(c1);
                break;
            }
        }
        
        return out.toByteArray();
    }
    
    private byte hexPairToByte(char c1, char c2) {
        return (byte) (hexCharToInt(c1) * 16 + hexCharToInt(c2));
    }
    
    private static int hexCharToInt(char c) {
        int result = -1;
        
        switch (c) {
        case 'A':
            c = 'a';
            break;
        case 'B':
            c = 'b';
            break;
        case 'C':
            c = 'c';
            break;
        case 'D':
            c = 'd';
            break;
        case 'E':
            c = 'e';
            break;
        case 'F':
            c = 'f';
            break;
        }
        
        if ((c >= '0') && (c <= '9')) {
            result = c - '0';
        } else if (c >= 'a' && c <= 'f') {
            result = 10 + c - 'a';
        } else {
            throw new IllegalArgumentException("not a hex char: '" + c + "'.");
        }
        
        return result;
    }
}
