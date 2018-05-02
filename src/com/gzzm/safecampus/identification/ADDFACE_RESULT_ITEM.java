package com.gzzm.safecampus.identification;

// please don't change this definition, if there were any problems, consult author
public class ADDFACE_RESULT_ITEM {
    private int _ImageID;
    private String _sError;

    public void setImageID(int ImageID) {
        _ImageID = ImageID;
    }

    public void setsError(String sError) {
        _sError = sError;
    }

    public int getImageID() {
        return _ImageID;
    }

    public String getsError() {
        return _sError;
    }
}
