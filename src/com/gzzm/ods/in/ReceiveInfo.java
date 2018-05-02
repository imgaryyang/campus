package com.gzzm.ods.in;

/**
 * 公文收文信息，包含一个收到的公文的信息，提供给第三方的收发文接口中使用
 *
 * @author camel
 * @date 2016/12/3
 */
public class ReceiveInfo extends DocumentInfo
{
    private Long receiveId;

    private String deptId;

    private String type;

    private String url;

    public ReceiveInfo()
    {
    }

    public Long getReceiveId()
    {
        return receiveId;
    }

    public void setReceiveId(Long receiveId)
    {
        this.receiveId = receiveId;
    }

    public String getDeptId()
    {
        return deptId;
    }

    public void setDeptId(String deptId)
    {
        this.deptId = deptId;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }
}
