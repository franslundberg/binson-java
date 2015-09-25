package org.binson;

import org.binson.lowlevel.Constants;
import org.junit.Test;
import static org.junit.Assert.*;

public class ClassNameLengthTest {
    
    @Test
    public void booleanClass() {
        assertEquals(Constants.BOOLEAN_CLASSNAME_LENGTH, Boolean.class.getName().length());
    }
    
    @Test
    public void longClass() {
        assertEquals(Constants.LONG_CLASSNAME_LENGTH, Long.class.getName().length());
    }
    
    @Test
    public void doubleClass() {
        assertEquals(Constants.DOUBLE_CLASSNAME_LENGTH, Double.class.getName().length());
    }
    
    @Test
    public void stringClass() {
        assertEquals(Constants.STRING_CLASSNAME_LENGTH, String.class.getName().length());
    }
    
    @Test
    public void bytesClass() {
        assertEquals(Constants.BYTES_CLASSNAME_LENGTH, byte[].class.getName().length());
    }
    
    @Test
    public void binsonObjectClass() {
        assertEquals(Constants.BINSON_OBJECT_CLASSNAME_LENGTH, Binson.class.getName().length());
    }
    
    @Test
    public void binsonArrayClass() {
        assertEquals(Constants.BINSON_ARRAY_CLASSNAME_LENGTH, BinsonArray.class.getName().length());
    }
}

