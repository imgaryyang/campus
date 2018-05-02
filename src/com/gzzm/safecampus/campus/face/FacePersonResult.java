package com.gzzm.safecampus.campus.face;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

/**
 * 人脸识别结果结果
 * @author zy
 * @date 2018/4/13 14:01
 */
@Entity(table = "SCFACEPERSONRESULT" , keys = "resultId")
public class FacePersonResult
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
     * 人脸宽度
     */
    @ColumnDescription(type = "number(8,2)")
    private double faceWidth;

    /**
     * 人脸高度
     */
    @ColumnDescription(type = "number(8,2)")
    private double faceHeight;

    /**
     * 置信度
     */
    @ColumnDescription(type = "number(6,2)")
    private double confidence;

    public FacePersonResult()
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

    public double getFaceWidth()
    {
        return faceWidth;
    }

    public void setFaceWidth(double faceWidth)
    {
        this.faceWidth = faceWidth;
    }

    public double getFaceHeight()
    {
        return faceHeight;
    }

    public void setFaceHeight(double faceHeight)
    {
        this.faceHeight = faceHeight;
    }

    public double getConfidence()
    {
        return confidence;
    }

    public void setConfidence(double confidence)
    {
        this.confidence = confidence;
    }
}
