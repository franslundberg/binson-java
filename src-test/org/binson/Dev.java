package org.binson;

// TODO refactor, use TextReader in JsonParser too.

// TODO use StringFormatException in JsonParser too.

// TODO refactor, parseNumber() in JsonParser and BinsonString parser are nearly identical 
// code - refactor to common code.

/**
 * For Binson developer.
 */
public class Dev {
    
    strictfp public static boolean isDenormalized(float val) {
        if (val == 0) {
            return false;
        }
        
        // Java 6+ needed.
        /*
        if ((val > -Float.MIN_NORMAL) && (val < Float.MIN_NORMAL)) {
            return true;
        }
        */
        
        return false;
    }

    public static void main(String[] args) {
        
        
        
        BinsonObject obj = new BinsonObject();
        obj.put("myInt", 12);
        obj.put("height", 1.78);
        obj.put("bigDouble", 1.2e9);
        obj.put("string", "string-value\n fnutts:\"\"");
        obj.put("bytes", new byte[]{3, 3*16+10, (byte) 255});
        
        BinsonObject inner = new BinsonObject().put("a", 1);
        obj.put("inner", inner);
        
        BinsonArray array = new BinsonArray().add(1).add(2);
        obj.put("array", array);
        
        System.out.println(obj.toJson() + "\n");
        System.out.println(obj.toBinsonString() + "\n");
    }
}
