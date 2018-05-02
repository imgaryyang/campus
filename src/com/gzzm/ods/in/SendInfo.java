package com.gzzm.ods.in;

/**
 * @author camel
 * @date 2016/12/3
 */
public class SendInfo extends DocumentInfo
{
    private String deptId;

    private String contentId;

    private String[] receiveDeptIds;

    public SendInfo()
    {
    }

    public String getDeptId()
    {
        return deptId;
    }

    public void setDeptId(String deptId)
    {
        this.deptId = deptId;
    }

    public String getContentId()
    {
        return contentId;
    }

    public void setContentId(String contentId)
    {
        this.contentId = contentId;
    }

    public String[] getReceiveDeptIds()
    {
        return receiveDeptIds;
    }

    public void setReceiveDeptIds(String[] receiveDeptIds)
    {
        this.receiveDeptIds = receiveDeptIds;
    }
}
