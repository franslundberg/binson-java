package org.binson;

import org.junit.Test;

/**
 * Tests Binson object validation (BinsonObject.validate()).
 * 
 * @author Frans Lundberg
 */
public class ValidateTest {

    @Test
    public void testSanity() {
        BinsonObject obj = new BinsonObject()
                .put("name", "Natalia")
                .put("born", 2011);
        
        BinsonObject schema = new BinsonObject()
                .put("name", "Maria")
                .put("born", 1980);
        
        obj.validate(schema);
    }
    
    @Test(expected=FormatException.class)
    public void testMissingMandatoryField() {
        BinsonObject obj = new BinsonObject()
                .put("name", "Natalia");
        
        BinsonObject schema = new BinsonObject()
                .put("name", "Maria")
                .put("born", 1980);
        
        obj.validate(schema);
    }
    
    @Test
    public void testMissingOptionalField() {
        BinsonObject obj = new BinsonObject()
                .put("name", "Natalia");
        
        BinsonObject schema = new BinsonObject()
                .put("name", "Maria")
                .put("name-info", new BinsonObject())
                .put("born", 1980)
                .put("born-info", new BinsonObject()
                        .put("comment", "The year of birth")
                        .put("optional", true));
        
        obj.validate(schema);
    }
    
    @Test(expected=FormatException.class)
    public void testInvalidType() {
        BinsonObject schema = new BinsonObject()
                .put("name", "Maria")
                .put("born", 1980);
        
        BinsonObject obj = new BinsonObject()
                .put("name", "Natalia")
                .put("born", "1980-03-30");
        
        obj.validate(schema);
    }
    
    public void testAdditionalField() {
        BinsonObject schema = new BinsonObject()
                .put("name", "Maria")
                .put("born", 1980);
        
        BinsonObject obj = new BinsonObject()
                .put("name", "Lars")
                .put("born", 2011)
                .put("nickName", "Lasse");
        
        obj.validate(schema);
    }
    
    @Test
    public void testNested1() {
        BinsonObject schema = new BinsonObject()
                .put("companyName", "Example Name")
                .put("boss", new BinsonObject()
                        .put("name", "Anton Antonsson")
                        .put("born", 1980))
                .put("employees", 10);
        
        BinsonObject obj = new BinsonObject()
                .put("companyName", "Code Exchange AB")
                .put("boss", new BinsonObject()
                        .put("name", "Natalia")
                        .put("born", 2011))
                .put("employees", 20);
        
        obj.validate(schema);
    }
    
    @Test(expected=FormatException.class)
    public void testNested2() {
        BinsonObject schema = new BinsonObject()
                .put("companyName", "Example Name")
                .put("boss", new BinsonObject()
                        .put("name", "Anton Antonsson")
                        .put("born", 1980));
        
        BinsonObject obj = new BinsonObject()
                .put("companyName", "Code Exchange AB")
                .put("boss", new BinsonObject()
                        .put("name", "Natalia"));
        
        obj.validate(schema);
    }
}
