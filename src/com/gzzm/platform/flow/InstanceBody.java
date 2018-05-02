package com.gzzm.platform.flow;

import com.gzzm.platform.form.FormBody;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.Entity;

/**
 * 流程实例和表单关联表，有些流程实例能关联多个表单
 *
 * @author camel
 * @date 2014/6/4
 */
@Entity(table = "PFINSTANCEBODY", keys = {"instanceId", "bodyId"})
public class InstanceBody
{
    private Long instanceId;

    @NotSerialized
    private SystemFlowInstance instance;

    private Long bodyId;

    @NotSerialized
    private FormBody body;

    private String title;

    public InstanceBody()
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

    public Long getBodyId()
    {
        return bodyId;
    }

    public void setBodyId(Long bodyId)
    {
        this.bodyId = bodyId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public SystemFlowInstance getInstance()
    {
        return instance;
    }

    public void setInstance(SystemFlowInstance instance)
    {
        this.instance = instance;
    }

    public FormBody getBody()
    {
        return body;
    }

    public void setBody(FormBody body)
    {
        this.body = body;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof InstanceBody))
            return false;

        InstanceBody that = (InstanceBody) o;

        return bodyId.equals(that.bodyId) && instanceId.equals(that.instanceId);
    }

    @Override
    public int hashCode()
    {
        int result = instanceId.hashCode();
        result = 31 * result + bodyId.hashCode();
        return result;
    }
}
