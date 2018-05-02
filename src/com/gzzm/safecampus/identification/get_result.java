package com.gzzm.safecampus.identification;

import java.util.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/*
  Example interface
 */
public class get_result {
    // must not change
    public List<ACTION_RESULT> results; // list of results of corresponding person

    // the function takes the image as Base64 format and assign the corresponding private data field
    public native int PredictActionFromImage(String uOriginId, String pFrame, int uFrameSize);
    // not implemented
    public native int PredictActionFromVideo(String uOriginId, String pFrame, int uFrameSize);
    public static void main(String[] args) {
            System.loadLibrary("LOAD_DATA");
        get_result test = new get_result();
        StringBuilder contentBuffer = new StringBuilder();
        String filePath = "test.txt";
        String test_string = "";
        // must initialize
        test.results = new Vector<ACTION_RESULT>();

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

        test.PredictActionFromImage("1", test_string, 0);
        //System.out.println(test.results.size());
        for ( int i = 0; i < test.results.size(); ++ i ) {
            System.out.println(test.results.get(i).getX());
            System.out.println(test.results.get(i).getY());
            System.out.println(test.results.get(i).getWidth());
            System.out.println(test.results.get(i).getHeight());
            System.out.println(test.results.get(i).getPersonId());
            for ( int j = 0; j < test.results.get(i).getVecActionItem().size(); ++ j ) {
                System.out.println(test.results.get(i).getVecActionItem().get(j).getActionId());
                System.out.println(test.results.get(i).getVecActionItem().get(j).getConfidence());

            }
        }

    }
}
