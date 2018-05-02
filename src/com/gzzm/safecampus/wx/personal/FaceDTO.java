package com.gzzm.safecampus.wx.personal;

import net.cyan.crud.annotation.In;

/**
 * @author yiuman
 * @date 2018/4/10
 */
public class FaceDTO {

    private Integer faceType;

    private String base64;

    public Integer getFaceType() {
        return faceType;
    }

    public void setFaceType(Integer faceType) {
        this.faceType = faceType;
    }

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    @Override
    public String toString() {
        return "FaceDTO{" +
                "faceType=" + faceType +
                ", base64='" + base64 + '\'' +
                '}';
    }
}
