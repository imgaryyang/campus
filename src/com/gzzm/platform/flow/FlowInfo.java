package com.gzzm.platform.flow;

import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 流程信息表，每条流程的每个版本一条记录
 *
 * @author camel
 * @date 2010-9-14
 */
@Entity(table = "PFFLOWINFO", keys = "flowId")
public class FlowInfo
{
    /**
     * 流程ID，主键，每个版本的FLOWID都不一样
     */
    @Generatable(length = 7)
    private Integer flowId;

    /**
     * 流程名称
     */
    @Require
    @ColumnDescription(type = "varchar(250)")
    private String flowName;

    /**
     * 忽略版本号的流程ID，当IEFLOWID相同时表示这是同一个流程的多个版本
     */
    @Generatable(length = 7)
    private Integer ieFlowId;

    /**
     * 流程类型，按流程的用途对流程进行分类，其值为一个预定义的字符串，如send，receive等，具体的业务功能模块可定义自己的流程类型
     */
    @Require
    @ColumnDescription(type = "varchar(50)", nullable = false)
    private FlowType type;


    /**
     * 流程版本号，从1开始，发布之后才产生版本号，没发布之前版本号为空
     */
    @ColumnDescription(type = "number(4)")
    private Integer version;

    /**
     * 流程定义
     */
    @NotSerialized
    private char[] flow;

    /**
     * 流程设计，流程未发布之前的设计信息，用于流程的可视化设计
     */
    @NotSerialized
    private byte[] design;

    /**
     * 流程创建人
     */
    private Integer creator;

    @NotSerialized
    @ToOne("CREATOR")
    private User createUser;

    /**
     * 创建时间
     */
    private Date publishTime;

    /**
     * 是否已发布，true表示已发布，false表示还处于设计阶段，只有未发布的流程才能修改
     */
    @ColumnDescription(defaultValue = "0")
    private Boolean published;

    /**
     * 是否被使用，如果被使用则发布时创建一个新的版本
     */
    @ColumnDescription(defaultValue = "0")
    private Boolean used;

    /**
     * 所属部门的ID
     */
    @Index
    private Integer deptId;

    @NotSerialized
    private Dept dept;

    /**
     * 更新时间，每次修改时置为当前时间
     *
     * @see #beforeModify();
     */
    private Date updateTime;

    private Integer orderId;

    public FlowInfo()
    {
    }

    public Integer getFlowId()
    {
        return flowId;
    }

    public void setFlowId(Integer flowId)
    {
        this.flowId = flowId;
    }

    public String getFlowName()
    {
        return flowName;
    }

    public void setFlowName(String flowName)
    {
        this.flowName = flowName;
    }

    public Integer getIeFlowId()
    {
        return ieFlowId;
    }

    public void setIeFlowId(Integer ieFlowId)
    {
        this.ieFlowId = ieFlowId;
    }

    public FlowType getType()
    {
        return type;
    }

    public void setType(FlowType type)
    {
        this.type = type;
    }

    public char[] getFlow()
    {
        return flow;
    }

    public void setFlow(char[] flow)
    {
        this.flow = flow;
    }

    public byte[] getDesign()
    {
        return design;
    }

    public void setDesign(byte[] design)
    {
        this.design = design;
    }

    public Integer getVersion()
    {
        return version;
    }

    public void setVersion(Integer version)
    {
        this.version = version;
    }

    public Boolean isUsed()
    {
        return used;
    }

    public void setUsed(Boolean used)
    {
        this.used = used;
    }

    public Integer getCreator()
    {
        return creator;
    }

    public void setCreator(Integer creator)
    {
        this.creator = creator;
    }

    public User getCreateUser()
    {
        return createUser;
    }

    public void setCreateUser(User createUser)
    {
        this.createUser = createUser;
    }

    public Date getPublishTime()
    {
        return publishTime;
    }

    public void setPublishTime(Date publishTime)
    {
        this.publishTime = publishTime;
    }

    public Boolean isPublished()
    {
        return published;
    }

    public void setPublished(Boolean published)
    {
        this.published = published;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Dept getDept()
    {
        return dept;
    }

    public void setDept(Dept dept)
    {
        this.dept = dept;
    }

    public Date getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof FlowInfo))
            return false;

        FlowInfo flowInfo = (FlowInfo) o;

        return flowId.equals(flowInfo.flowId);
    }

    @Override
    public int hashCode()
    {
        return flowId.hashCode();
    }

    @Override
    public String toString()
    {
        return flowName;
    }

    @BeforeAdd
    @BeforeUpdate
    public void beforeModify()
    {
        setUpdateTime(new Date());
    }
}
