package com.gzzm.safecampus.identification;

// please don't change this definition, if there were any problems, consult author
public class ACTION_RESULT_ITEM {
    private int _uActionId;
    private int _uConfidence;

    public void setActionId(int id) {
        _uActionId = id;
    }

    public void setConfidence(int con) {
        _uConfidence = con;
    }

    public int getActionId() {
        return _uActionId;
    }

    public int getConfidence() {
        return _uConfidence;
    }
}
