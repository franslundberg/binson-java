package org.binson.lowlevel;

import java.io.IOException;

/**
 * Parses a JSON number. Complex enough for a separate class.
 * Note, JSON does not support special doubles, such as "inf" and "NaN". 
 *
 * @author Frans Lundberg
 */
public class JsonNumberParser {
    private TextReader r;
    
    public JsonNumberParser() {
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
        char first = r.next();
        boolean hasFrac = false;
        boolean hasExp = false;
        StringBuffer b = new StringBuffer();
        
        parseInt(first, b);
        hasFrac = frac(b);
        hasExp = exp(b);
        
        String numberString = b.toString();
        
        if (hasFrac || hasExp) {
            return createDouble(numberString);
        } else {
            return createLong(numberString);
        }
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
            throw new JsonParseException("Integer out of range: '"
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
            throw new JsonParseException("Floating point number out of range: '" 
                    + numberString + "'.", r);
        }
        
        if (doubleValue.isInfinite() || doubleValue.isNaN()) {
            throw new JsonParseException("Floating point number out of range: '" 
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
            throw new JsonParseException("Could not parse number.", r);
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
            throw new JsonParseException("Bad number character: '" + c + "'.", r);
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
}
