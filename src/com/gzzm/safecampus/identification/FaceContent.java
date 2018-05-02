package com.gzzm.safecampus.identification;

import java.util.List;

/**
 * 人脸提交内容
 * @author zy
 * @date 2018/3/26 15:45
 */
public class FaceContent
{
    /**
     * 学校编号
     */
    private String org_id;

    /**
     * 用户编号
     */
    private String person_id;

    /**
     * 上传人脸图片数组
     */
    private List<String> images;

    public FaceContent()
    {
    }

    public String getOrg_id()
    {
        return org_id;
    }

    public void setOrg_id(String org_id)
    {
        this.org_id = org_id;
    }

    public String getPerson_id()
    {
        return person_id;
    }

    public void setPerson_id(String person_id)
    {
        this.person_id = person_id;
    }

    public List<String> getImages()
    {
        return images;
    }

    public void setImages(List<String> images)
    {
        this.images = images;
    }
}
