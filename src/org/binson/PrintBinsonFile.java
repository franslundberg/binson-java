package org.binson;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static java.lang.System.exit;

/**
 * Created by simonj on 2018-06-15.
 */
public class PrintBinsonFile {

    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Please provide a file as argument.");
            exit(-1);
        }

        String inputFile = args[0];
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(inputFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            exit(-1);
        }

        try {
            Binson b = Binson.fromBytes(inputStream);
            System.out.println(b.toJson());
        } catch (IOException e) {
            e.printStackTrace();
            exit(-1);
        } catch (BinsonFormatException e) {
            e.printStackTrace();
            exit(-1);
        }

        exit(0);

    }

}
