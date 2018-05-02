package com.gzzm.safecampus.identification;

import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;

/*
  Example interface
 */
public class face_result {

    // must not change
    public List<PREFACE_RESULT> results = new Vector<PREFACE_RESULT>();

    // the function takes the image as Base64 format and assign the corresponding private data field
    public native int PredictFaceFromImage(String uOriginId, String pFrame, int uFrameSize);
    // not implemented
    public native int PredictFaceFromVideo(String uOriginId, String pFrame, int uFrameSize);
    public static void main(String[] args) {
        System.loadLibrary("LOAD_DATA");
        face_result test = new face_result();
        StringBuilder contentBuffer = new StringBuilder();
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


        test.PredictFaceFromImage("1", test_string, 0);

        for ( int i = 0; i < test.results.size(); ++ i ) {
            System.out.println(test.results.get(i).getX());
            System.out.println(test.results.get(i).getY());
            System.out.println(test.results.get(i).getWidth());
            System.out.println(test.results.get(i).getHeight());
            System.out.println(test.results.get(i).getPreFaceItem());
            for ( int j = 0; j < test.results.get(i).getPreFaceItem().size(); ++ j ) {
                System.out.println(test.results.get(i).getPreFaceItem().get(j).getPersonId());
                System.out.println(test.results.get(i).getPreFaceItem().get(j).getConfidence());

            }
        }

    }
}
