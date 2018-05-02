package com.gzzm.ods.flow;

import java.util.Date;

/**
 * 将OdFlowInstanceLink转化为可以在前台显示的数据
 *
 * @author camel
 * @date 13-12-1
 * @see OdFlowInstanceLink
 */
public class OdLinkItem
{
    private Long instanceId;

    private String title;

    private Integer userId;

    private String userName;

    private Date time;

    public OdLinkItem()
    {
    }

    public OdLinkItem(OdFlowInstanceLink link)
    {
        instanceId = link.getLinkInstanceId();
        title = link.getLinkInstance().getDocument().getTitle();
        userId = link.getUserId();
        if (userId != null)
            userName = link.getUser().getUserName();
        time = link.getLinkTime();
    }

    public Long getInstanceId()
    {
        return instanceId;
    }

    public void setInstanceId(Long instanceId)
    {
        this.instanceId = instanceId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public Date getTime()
    {
        return time;
    }

    public void setTime(Date time)
    {
        this.time = time;
    }
}
