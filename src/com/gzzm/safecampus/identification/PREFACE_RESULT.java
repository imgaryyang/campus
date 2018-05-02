package com.gzzm.safecampus.identification;

import java.util.*;


// please don't change this definition, if there were any problems, consult author
public class PREFACE_RESULT {

    private int _x;
    private int _y;
    private int _width;
    private int _height;
    private List<PREFACE_RESULT_ITEM> _vecPreFaceItem = new Vector<PREFACE_RESULT_ITEM>();

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

    public List<PREFACE_RESULT_ITEM> getPreFaceItem() {
        return _vecPreFaceItem;
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

    public void setVecPreFaceItem(List<PREFACE_RESULT_ITEM> vecPreFaceItem) {
        _vecPreFaceItem = vecPreFaceItem;
    }
}
