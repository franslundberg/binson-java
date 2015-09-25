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
        Binson obj = new Binson()
                .put("name", "Natalia")
                .put("born", 2011);
        
        Binson schema = new Binson()
                .put("name", "Maria")
                .put("born", 1980);
        
        obj.validate(schema);
    }
    
    @Test(expected=FormatException.class)
    public void testMissingMandatoryField() {
        Binson obj = new Binson()
                .put("name", "Natalia");
        
        Binson schema = new Binson()
                .put("name", "Maria")
                .put("born", 1980);
        
        obj.validate(schema);
    }
    
    @Test
    public void testMissingOptionalField() {
        Binson obj = new Binson()
                .put("name", "Natalia");
        
        Binson schema = new Binson()
                .put("name", "Maria")
                .put("name-info", new Binson())
                .put("born", 1980)
                .put("born-info", new Binson()
                        .put("comment", "The year of birth")
                        .put("optional", true));
        
        obj.validate(schema);
    }
    
    @Test(expected=FormatException.class)
    public void testInvalidType() {
        Binson schema = new Binson()
                .put("name", "Maria")
                .put("born", 1980);
        
        Binson obj = new Binson()
                .put("name", "Natalia")
                .put("born", "1980-03-30");
        
        obj.validate(schema);
    }
    
    public void testAdditionalField() {
        Binson schema = new Binson()
                .put("name", "Maria")
                .put("born", 1980);
        
        Binson obj = new Binson()
                .put("name", "Lars")
                .put("born", 2011)
                .put("nickName", "Lasse");
        
        obj.validate(schema);
    }
    
    @Test
    public void testNested1() {
        Binson schema = new Binson()
                .put("companyName", "Example Name")
                .put("boss", new Binson()
                        .put("name", "Anton Antonsson")
                        .put("born", 1980))
                .put("employees", 10);
        
        Binson obj = new Binson()
                .put("companyName", "Code Exchange AB")
                .put("boss", new Binson()
                        .put("name", "Natalia")
                        .put("born", 2011))
                .put("employees", 20);
        
        obj.validate(schema);
    }
    
    @Test(expected=FormatException.class)
    public void testNested2() {
        Binson schema = new Binson()
                .put("companyName", "Example Name")
                .put("boss", new Binson()
                        .put("name", "Anton Antonsson")
                        .put("born", 1980));
        
        Binson obj = new Binson()
                .put("companyName", "Code Exchange AB")
                .put("boss", new Binson()
                        .put("name", "Natalia"));
        
        obj.validate(schema);
    }
    
    @Test
    public void testArraySanity() {
        Binson schema = new Binson()
                .put("permissions", new BinsonArray()
                        .add("permission-example"));
        
        Binson obj = new Binson()
                .put("permissions", new BinsonArray()
                        .add("read").add("append"));
        
        obj.validate(schema);
    }
    
    @Test
    public void testEmptySchemaArray() {
        // Empty schema array, anything allowed in array.
        
        Binson schema = new Binson()
                .put("permissions", new BinsonArray());
        
        Binson obj = new Binson()
                .put("permissions", new BinsonArray()
                        .add("read").add(1234));
        
        obj.validate(schema);
    }
    
    @Test
    public void testArraySchema1() {
        // Array with schema for each array item.
        
        Binson schema = new Binson()
                .put("permissions", new BinsonArray()
                        .add(new Binson()
                                .put("name", "read")
                                .put("id", new byte[]{12, 12, 12, 12}))
                        );
        
        Binson perm1 = new Binson()
                .put("name", "read")
                .put("id", new byte[]{12, 12, 12, 12});
        
        Binson perm2 = new Binson()
                .put("name", "write")
                .put("id", new byte[]{12, 12, 12, 12});
        
        Binson obj = new Binson()
                .put("permissions", new BinsonArray()
                        .add(perm1).add(perm2));
        
        obj.validate(schema);
    }
    
    @Test(expected=FormatException.class)
    public void testArraySchema2() {
        // Array with schema for each array item. One array item fails to validate.
        
        Binson schema = new Binson()
                .put("permissions", new BinsonArray()
                        .add(new Binson()
                                .put("name", "read")
                                .put("id", new byte[]{12, 12, 12, 12}))
                        );
        
        Binson perm1 = new Binson()
                .put("name", "read")
                .put("id", new byte[]{12, 12, 12, 12});
        
        Binson perm2 = new Binson()
                .put("name", "write")
                .put("MYID", new byte[]{12, 12, 12, 12});
        
        Binson obj = new Binson()
                .put("permissions", new BinsonArray()
                        .add(perm1).add(perm2));
        
        obj.validate(schema);
    }
}
