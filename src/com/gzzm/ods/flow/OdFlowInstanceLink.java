package com.gzzm.ods.flow;

import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 关联公文
 *
 * @author camel
 * @date 13-12-1
 */
@Entity(table = "ODFLOWINSTANCELINK", keys = {"instanceId", "linkInstanceId"})
public class OdFlowInstanceLink
{
    /**
     * 主公文的实例ID
     */
    @ColumnDescription(type = "number(12)")
    private Long instanceId;

    @NotSerialized
    private OdFlowInstance instance;

    /**
     * 被关联的公文的实例id
     */
    @ColumnDescription(type = "number(12)")
    private Long linkInstanceId;

    @NotSerialized
    @ToOne("LINKINSTANCEID")
    private OdFlowInstance linkInstance;

    /**
     * 设置公文关联的用户的ID
     */
    private Integer userId;

    /**
     * 设置公文关联的用户，关联User对象
     */
    @NotSerialized
    private User user;

    /**
     * 关联的时间
     */
    private Date linkTime;

    public OdFlowInstanceLink()
    {
    }

    public Long getInstanceId()
    {
        return instanceId;
    }

    public void setInstanceId(Long instanceId)
    {
        this.instanceId = instanceId;
    }

    public Long getLinkInstanceId()
    {
        return linkInstanceId;
    }

    public void setLinkInstanceId(Long linkInstanceId)
    {
        this.linkInstanceId = linkInstanceId;
    }

    public OdFlowInstance getInstance()
    {
        return instance;
    }

    public void setInstance(OdFlowInstance instance)
    {
        this.instance = instance;
    }

    public OdFlowInstance getLinkInstance()
    {
        return linkInstance;
    }

    public void setLinkInstance(OdFlowInstance linkInstance)
    {
        this.linkInstance = linkInstance;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public Date getLinkTime()
    {
        return linkTime;
    }

    public void setLinkTime(Date linkTime)
    {
        this.linkTime = linkTime;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof OdFlowInstanceLink))
            return false;

        OdFlowInstanceLink that = (OdFlowInstanceLink) o;

        return instanceId.equals(that.instanceId) && linkInstanceId.equals(that.linkInstanceId);
    }

    @Override
    public int hashCode()
    {
        int result = instanceId.hashCode();
        result = 31 * result + linkInstanceId.hashCode();
        return result;
    }
}
