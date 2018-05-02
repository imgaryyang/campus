package com.gzzm.safecampus.identification;

import java.util.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;


// please don't change this definition, if there were any problems, consult author
public class ADDFACE_RESULT{

    // must not change
   // public List<ACTION_RESULT> results; // list of results of corresponding person
    public List<ADDFACE_RESULT_ITEM> results = new Vector<ADDFACE_RESULT_ITEM>();

    // the function takes the image as Base64 format and assign the corresponding private data field
    public native int AddFaceFromImage(String uOriginId, String uPersonId, String pFrame);

    public static void main(String[] args) {
        System.loadLibrary("LOAD_DATA");
        ADDFACE_RESULT test = new ADDFACE_RESULT();
	StringBuilder contentBuffer = new StringBuilder();
       // test.AddFaceFromImage("1","1", "hello", 0,"s");
       // System.out.println("In Java, int is " + test._x);

        String filePath = "wym.txt";
        String test_string = "";

        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath),
                                                    Charset.defaultCharset());
            for (String line : lines) {
                test_string = test_string + line;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        String error = "";
        test.AddFaceFromImage("1","wym", test_string);
        //System.out.println(test.results.getImageID());
        //System.out.println(test.results.getsError());
        for ( int i = 0; i < test.results.size(); ++ i ) {
            System.out.println(test.results.get(i).getImageID());
            System.out.println(test.results.get(i).getsError());
        }
        // System.out.println(ImageID);
       // System.out.println(Error);
    }
}
