package com.gzzm.safecampus.identification;

import java.util.*;

// please don't change this definition, if there were any problems, consult author
public class ACTION_RESULT {

    private int _x;
    private int _y;
    private int _width;
    private int _height;
    private String _personId;
    // should be initialize due to jni restriction
    private List<ACTION_RESULT_ITEM> _vecActionItem = new Vector<ACTION_RESULT_ITEM>();

    public int getX() {
        return _x;
    }

    public int getY() {
        return _y;
    }

    public int getWidth() {
        return _width;
    }

    public int getHeight() {
        return _height;
    }

    public String getPersonId() {
        return _personId;
    }

    public List<ACTION_RESULT_ITEM> getVecActionItem() {
        return _vecActionItem;
    }

    public void setX(int x) {
        _x = x;
    }

    public void setY(int y) {
        _y = y;
    }

    public void setWidth(int width) {
        _width = width;
    }

    public void setHeight(int height) {
        _height = height;
    }

    public void setPersonId(String personId) {
        _personId = personId;
    }

    public void setVecActionItem(List<ACTION_RESULT_ITEM> vecActionItem) {
        _vecActionItem = vecActionItem;
    }

}
