package org.binson.lowlevel;

import java.io.IOException;

/**
 * Parses a JSON/Binson number.
 * Complex enough for a separate class.
 *
 * @author Frans Lundberg
 */
public class NumberParser {
    private TextReader r;
    private final boolean allowSpecial;
    
    public NumberParser(boolean allowSpecial) {
        this.allowSpecial = allowSpecial;
    }
    
    public Object number(TextReader r) throws IOException {
        if (r == null) {
            throw new IllegalArgumentException("r == null not allowed");
        }
        this.r = r;
        
        return number();
    }
    
    /**
     * Returns a Long or a Double with the parsed value.
     */
    private Object number() throws IOException {
        // JSON plus optionally, -inf, inf and NaN.
        
        char first = r.next();
        
        Object result = handleSpecialDoubles(first);
        if (result != null) {
            return result;
        }
        
        boolean hasFrac = false;
        boolean hasExp = false;
        StringBuffer b = new StringBuffer();
        
        parseInt(first, b);
        
        hasFrac = frac(b);
        
        hasExp = exp(b);
        
        String numberString = b.toString();
        
        if (hasFrac || hasExp) {
            result = createDouble(numberString);
        } else {
            result = createLong(numberString);
        }
        
        return result;
    }

    private boolean exp(StringBuffer b) throws IOException {
        boolean hasExp = false;
        char c = r.next();
        
        if (c == 'e' || c == 'E') {
            hasExp = true;
            b.append('e');
            char c1 = r.next();
            if (c1 == '+' || c1 == '-') {
                b.append(c1);
            } else {
                r.pushBack(c1);
            }
            
            oneOrMoreDigits(b);
        } else {
            r.pushBack(c);
        }
        
        return hasExp;
    }

    private boolean frac(StringBuffer b) throws IOException {
        boolean hasFrac = false;
        char c = r.next();
        
        if (c == '.') {
            hasFrac = true;
            b.append('.');
            oneOrMoreDigits(b);
        } else {
            r.pushBack(c);
        }
        return hasFrac;
    }

    private Object createLong(String numberString) {
        Object result;
        Long longValue;
        try {
            longValue = Long.parseLong(numberString);
        } catch (NumberFormatException e) {
            throw new StringFormatException("Integer out of range: '"
                    + numberString + "'.", r);
        }
        result = longValue;
        return result;
    }

    private Double createDouble(String numberString) {
        Double doubleValue;
        
        try {
            doubleValue = Double.valueOf(numberString);
        } catch (NumberFormatException e) {
            throw new StringFormatException("Floating point number out of range: '" 
                    + numberString + "'.", r);
        }
        
        if (doubleValue.isInfinite() || doubleValue.isNaN()) {
            throw new StringFormatException("Floating point number out of range: '" 
                    + numberString + "'.", r);
        }
        return doubleValue;
    }
    
    private void oneOrMoreDigits(StringBuffer b) throws IOException {
        // ABNF: 1*DIGIT.
        
        char c = r.next();
        
        if (isDigit(c)) {
            b.append(c);
        } else {
            throw new StringFormatException("Could not parse number.", r);
        }
        
        while (true) {
            c = r.next();
            
            if (isDigit(c)) {
                b.append(c);
            } else {
                r.pushBack(c);
                break;
            }
        }
    }
    
    /**
     * Parses ABNF: [minus] int.
     */
    private void parseInt(char first, StringBuffer b) throws IOException {
        char c;
        
        if (first == '-') {
            b.append(first);
            c = r.next();
        } else {
            c = first;
        }
        
        if (c == '0') {
            // Special case, only 0 start with a zero.
            b.append(c);
            return;
        } 
        
        if (isDigit1To9(c)) {
            b.append(c);
            digits(b);
        } else {
            throw new StringFormatException("Bad number character.", r);
        }
    }
    
    /**
     * Parses ABNF: *DIGIT. That is, zero or more digits.
     */
    private void digits(StringBuffer b) throws IOException {
        while (true) {
            char c = r.next();
            if (isDigit(c)) {
                b.append(c);
            } else {
                r.pushBack(c);
                break;
            }
        }
    }
    
    private boolean isDigit1To9(char c) {
        return c >= '1' && c <= '9';
    }
    
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    /**
     * Handles -inf, inf, and NaN if settings set for that.
     * Not for JSON, only for Binson.
     */
    private Object handleSpecialDoubles(char first) throws IOException {
        Object result = null;
        
        if (allowSpecial) {
            if (first == 'i') {
                parseWord("inf");
                return new Double(Double.POSITIVE_INFINITY);
            } else if (first == 'N') {
                parseWord("NaN");
                return new Double(Double.NaN);
            } else if (first == '-') {
                char second = r.next();
                if (second == 'i') {
                    parseWord("inf");
                    return new Double(Double.NEGATIVE_INFINITY);
                } else {
                    r.pushBack(second);
                }
            }
        } else {
            if (first == 'i' || first == 'N') {
                throw new StringFormatException("Cannot parse number.", r);
            }
        }
        
        return result;
    }
    
    /** 
     * Starts one character 'late' in the word.
     */
    private void parseWord(String word) throws IOException {
        int length = word.length();
        for (int i = 1; i < length; i++) {
            char c = r.next();
            if (c != word.charAt(i)) {
                throw new StringFormatException("Bad character when expecting '" + word + "', got " + c + ".", r);
            }
        }
    }
}
