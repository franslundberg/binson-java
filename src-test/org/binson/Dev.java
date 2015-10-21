package org.binson;

// TODO In README.md, describe how to build (mostly a reference to build.xml really).

// TODO refactor, use TextReader in JsonParser too.

// TODO use StringFormatException in JsonParser too.

// TODO refactor, parseNumber() in JsonParser and BinsonString parser are nearly identical 
// code - refactor to common code.

// TODO build and publish zip (javadoc, jar).

/**
 * For Binson developer.
 */
public class Dev {
    public static void main(String[] args) {
        Binson obj = new Binson();
        obj.put("myInt", 12);
        obj.put("height", 1.78);
        obj.put("bigDouble", 1.2e9);
        obj.put("string", "string-value\n fnutts:\"\"");
        obj.put("bytes", new byte[]{3, 3*16+10, (byte) 255});
        
        Binson inner = new Binson().put("a", 1);
        obj.put("inner", inner);
        
        BinsonArray array = new BinsonArray().add(1).add(2);
        obj.put("array", array);
        
        System.out.println(obj.toJson() + "\n");
        System.out.println(obj.toBinsonString() + "\n");
    }
}
