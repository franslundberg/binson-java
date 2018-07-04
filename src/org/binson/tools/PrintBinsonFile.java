package org.binson.tools;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.binson.Binson;
import org.binson.BinsonFormatException;

/**
 * Tool (executable class) that prints binson data in a file to 
 * system out.
 * 
 * @author Simon Johansson
 */
public class PrintBinsonFile {
    
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Please provide a file as argument.");
            System.exit(-1);
        }

        String inputFile = args[0];
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(inputFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        try {
            Binson b = Binson.fromBytes(inputStream);
            System.out.println(b.toPrettyJson());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (BinsonFormatException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
