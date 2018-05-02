package com.gzzm.safecampus.identification.common;

import java.util.List;

/**
 * 发送数据
 * @author zy
 * @date 2018/4/11 9:46
 */
public class SendData
{
    /**
     * 业务类型
     */
    private String typeCode;

    /**
     * 学校编号
     */
    private String schoolCode;

    /**
     * 人员编号
     */
    private String personId;

    /**
     * base64图片
     */
    private String base64Pic;

    /**
     * 删除人脸图片
     */
    private List<Integer> delImageIds;

    public SendData()
    {
    }

    public SendData(String schoolCode, String base64Pic)
    {
        this.schoolCode=schoolCode;
        this.base64Pic=base64Pic;
    }

    public SendData(String schoolCode,String personId,List<Integer> delImageIds)
    {
        this.schoolCode=schoolCode;
        this.personId=personId;
        this.delImageIds=delImageIds;
    }

    public SendData(String schoolCode,String personId,String base64Pic)
    {
        this.schoolCode=schoolCode;
        this.personId=personId;
        this.base64Pic=base64Pic;
    }

    public String getTypeCode()
    {
        return typeCode;
    }

    public void setTypeCode(String typeCode)
    {
        this.typeCode = typeCode;
    }

    public String getSchoolCode()
    {
        return schoolCode;
    }

    public void setSchoolCode(String schoolCode)
    {
        this.schoolCode = schoolCode;
    }

    public String getPersonId()
    {
        return personId;
    }

    public void setPersonId(String personId)
    {
        this.personId = personId;
    }

    public String getBase64Pic()
    {
        return base64Pic;
    }

    public void setBase64Pic(String base64Pic)
    {
        this.base64Pic = base64Pic;
    }

    public List<Integer> getDelImageIds()
    {
        return delImageIds;
    }

    public void setDelImageIds(List<Integer> delImageIds)
    {
        this.delImageIds = delImageIds;
    }
}
