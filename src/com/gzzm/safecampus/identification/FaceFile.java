package com.gzzm.safecampus.identification;

import net.cyan.commons.util.InputFile;

import java.util.Date;

/**
 * 客户端上传人脸文件
 * @author zy
 * @date 2018/3/26 15:05
 */
public class FaceFile
{
    private String uuid;

    private InputFile imageFile;

    private Date updateTime;

    public FaceFile()
    {
    }

    public String getUuid()
    {
        return uuid;
    }

    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }

    public InputFile getImageFile()
    {
        return imageFile;
    }

    public void setImageFile(InputFile imageFile)
    {
        this.imageFile = imageFile;
    }

    public Date getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }
}
