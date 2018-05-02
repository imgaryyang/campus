package com.gzzm.safecampus.identification;

import java.util.List;

/**
 * 返回识别结果
 * @author zy
 * @date 2018/3/29 14:36
 */
public class ResultIde
{
    private int imageX;

    private int imageY;

    private int faceWidth;

    private int faceHeight;

    private String actionPersonId;

    private List<ResultItem> resultItems;

    public ResultIde()
    {
    }

    public int getImageX()
    {
        return imageX;
    }

    public void setImageX(int imageX)
    {
        this.imageX = imageX;
    }

    public int getImageY()
    {
        return imageY;
    }

    public void setImageY(int imageY)
    {
        this.imageY = imageY;
    }

    public int getFaceWidth()
    {
        return faceWidth;
    }

    public void setFaceWidth(int faceWidth)
    {
        this.faceWidth = faceWidth;
    }

    public int getFaceHeight()
    {
        return faceHeight;
    }

    public void setFaceHeight(int faceHeight)
    {
        this.faceHeight = faceHeight;
    }

    public String getActionPersonId()
    {
        return actionPersonId;
    }

    public void setActionPersonId(String actionPersonId)
    {
        this.actionPersonId = actionPersonId;
    }

    public List<ResultItem> getResultItems()
    {
        return resultItems;
    }

    public void setResultItems(List<ResultItem> resultItems)
    {
        this.resultItems = resultItems;
    }
}
