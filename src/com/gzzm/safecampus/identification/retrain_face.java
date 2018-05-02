package com.gzzm.safecampus.identification;

// please don't change this definition, if there were any problems, consult author
public class retrain_face {
  
    // must not change
   // public List<ACTION_RESULT> results; // list of results of corresponding person
   // public List<ADDFACE_RESULT_ITEM> results;
 
    // the function takes the image as Base64 format and assign the corresponding private data field
    public native String retrain(String sError);

    public static void main(String[] args) {
        System.loadLibrary("LOAD_DATA");
        retrain_face test = new retrain_face();
        StringBuilder contentBuffer = new StringBuilder();

        // String filePath = "test.txt";
        // String test_string = "";

        // try {
        //     List<String> lines = Files.readAllLines(Paths.get(filePath),
        //                                             Charset.defaultCharset());
        //     for (String line : lines) {
        //         test_string = test_string + line;
        //     }
        // }
        //  catch (Exception e) {
        //     e.printStackTrace();
        // }
        String Error = "s";
        String sError = "s";
        //System.out.println(Error);
        Error = test.retrain(sError);
        System.out.println(Error);

    }
}
