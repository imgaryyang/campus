package com.gzzm.safecampus.campus.face;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

/**
 * 行为识别结果
 * @author zy
 * @date 2018/4/24 15:20
 */
@Entity(table = "SCACTIONPERSONRESULT" , keys = "resultId")
public class ActionPersonResult
{
    /**
     * 主键
     */
    @ColumnDescription(type = "varchar2(36)")
    private String resultId;

    /**
     * 所属图片
     */
    @ColumnDescription(type = "varchar2(36)")
    private String imageId;

    @ToOne("IMAGEID")
    @NotSerialized
    private FaceImageLib faceImageLib;

    /**
     * 识别结果人脸
     */
    @ColumnDescription(type = "varchar2(36)")
    private String faceId;

    @ToOne("FACEID")
    @NotSerialized
    private FaceLibrary faceLibrary;

    /**
     * 图片x坐标
     */
    @ColumnDescription(type = "number(8,2)")
    private double imageX;

    /**
     * 图片y坐标
     */
    @ColumnDescription(type = "number(8,2)")
    private double imageY;

    /**
     * 行为宽度
     */
    @ColumnDescription(type = "number(8,2)")
    private double actionWidth;

    /**
     * 行为高度
     */
    @ColumnDescription(type = "number(8,2)")
    private double actionHeight;

    /**
     * 置信度 多个,隔开
     */
    @ColumnDescription(type = "varchar2(50)")
    private String confidence;

    /**
     * 行为 多个,隔开
     */
    @ColumnDescription(type = "varchar2(50)")
    private String actionId;

    public ActionPersonResult()
    {
    }

    public String getResultId()
    {
        return resultId;
    }

    public void setResultId(String resultId)
    {
        this.resultId = resultId;
    }

    public String getImageId()
    {
        return imageId;
    }

    public void setImageId(String imageId)
    {
        this.imageId = imageId;
    }

    public FaceImageLib getFaceImageLib()
    {
        return faceImageLib;
    }

    public void setFaceImageLib(FaceImageLib faceImageLib)
    {
        this.faceImageLib = faceImageLib;
    }

    public String getFaceId()
    {
        return faceId;
    }

    public void setFaceId(String faceId)
    {
        this.faceId = faceId;
    }

    public FaceLibrary getFaceLibrary()
    {
        return faceLibrary;
    }

    public void setFaceLibrary(FaceLibrary faceLibrary)
    {
        this.faceLibrary = faceLibrary;
    }

    public double getImageX()
    {
        return imageX;
    }

    public void setImageX(double imageX)
    {
        this.imageX = imageX;
    }

    public double getImageY()
    {
        return imageY;
    }

    public void setImageY(double imageY)
    {
        this.imageY = imageY;
    }

    public double getActionWidth()
    {
        return actionWidth;
    }

    public void setActionWidth(double actionWidth)
    {
        this.actionWidth = actionWidth;
    }

    public double getActionHeight()
    {
        return actionHeight;
    }

    public void setActionHeight(double actionHeight)
    {
        this.actionHeight = actionHeight;
    }

    public String getConfidence()
    {
        return confidence;
    }

    public void setConfidence(String confidence)
    {
        this.confidence = confidence;
    }

    public String getActionId()
    {
        return actionId;
    }

    public void setActionId(String actionId)
    {
        this.actionId = actionId;
    }
}
