package com.gzzm.safecampus.identification;

// please don't change this definition, if there were any problems, consult author
public class PREFACE_RESULT_ITEM {
    private String _uPersonId;
    private int _uConfidence;

    public void setPersonId(String id) {
        _uPersonId = id;
    }

    public void setConfidence(int con) {
        _uConfidence = con;
    }

    public String getPersonId() {
        return _uPersonId;
    }

    public int getConfidence() {
        return _uConfidence;
    }
}
