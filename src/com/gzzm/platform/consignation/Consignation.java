package com.gzzm.platform.consignation;

import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.*;
import net.cyan.commons.validate.annotation.*;
import net.cyan.nest.annotation.Inject;
import net.cyan.thunwind.annotation.*;

import java.sql.Date;
import java.util.List;

/**
 * 委托信息
 *
 * @author camel
 * @date 2010-5-27
 */
@Entity(table = "PFCONSIGNATION", keys = "consignationId")
public class Consignation
{
    @Inject
    private static Provider<List<ConsignationModule>> modulesProvider;

    /**
     * 委托ID，主键
     */
    @Generatable(length = 9)
    private Integer consignationId;

    /**
     * 委托人的ID
     */
    @Index
    private Integer consigner;

    /**
     * 受委托人的ID
     */
    @Require
    @Index
    private Integer consignee;

    /**
     * 委托人，关联User对象
     */
    @NotSerialized
    @ToOne("CONSIGNER")
    private User consignerUser;

    /**
     * 被委托人，关联User对象
     */
    @NotSerialized
    @ToOne("CONSIGNEE")
    @Require
    private User consigneeUser;

    /**
     * 开始时间
     */
    @Require
    private Date startTime;

    /**
     * 结束时间
     */
    @Require
    @LargerEqual("startTime")
    private Date endTime;

    /**
     * 委托状态
     */
    @ColumnDescription(nullable = false, defaultValue = "1")
    private ConsignationState state;

    /**
     * 说明
     */
    private String remark;

    @Require
    @ColumnDescription(type = "varchar(50)")
    @ValueCollection(table = "PFCONSIGNATIONMODULE", valueColumn = "MODULE")
    private List<String> modules;

    public Consignation()
    {
    }

    public Integer getConsignationId()
    {
        return consignationId;
    }

    public void setConsignationId(Integer consignationId)
    {
        this.consignationId = consignationId;
    }

    public Integer getConsigner()
    {
        return consigner;
    }

    public void setConsigner(Integer consigner)
    {
        this.consigner = consigner;
    }

    public Integer getConsignee()
    {
        return consignee;
    }

    public void setConsignee(Integer consignee)
    {
        this.consignee = consignee;
    }

    public User getConsignerUser()
    {
        return consignerUser;
    }

    public void setConsignerUser(User consignerUser)
    {
        this.consignerUser = consignerUser;
    }

    public User getConsigneeUser()
    {
        return consigneeUser;
    }

    public void setConsigneeUser(User consigneeUser)
    {
        this.consigneeUser = consigneeUser;
    }

    public Date getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public Date getEndTime()
    {
        return endTime;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    public ConsignationState getState()
    {
        return state;
    }

    public void setState(ConsignationState state)
    {
        this.state = state;
    }

    @NotSerialized
    public ConsignationState getShowState()
    {
        if (state == ConsignationState.available)
        {
            if (isEnd())
            {
                //如果当前时间已经过了结束时间，显示已结束
                return ConsignationState.end;
            }
            else if (!isStarted())
            {
                //如果当前时间还未开始，显示未开始
                return ConsignationState.notStarted;
            }
        }

        return state;
    }

    @NotSerialized
    public boolean isEnd()
    {
        return endTime != null && DateUtils.addDate(endTime, 1).getTime() < System.currentTimeMillis();
    }

    @NotSerialized
    public boolean isStarted()
    {
        return startTime != null && startTime.getTime() <= System.currentTimeMillis();
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public List<String> getModules()
    {
        return modules;
    }

    public void setModules(List<String> modules)
    {
        this.modules = modules;
    }

    @NotSerialized
    public String getModuleNames()
    {
        List<String> modules = getModules();
        if (modules == null)
            return "";

        List<ConsignationModule> consignationModules = modulesProvider.get();
        StringBuilder buffer = new StringBuilder();

        for (String module : modules)
        {
            for (ConsignationModule consignationModule : consignationModules)
            {
                if (consignationModule.getType().equals(module))
                {
                    StringUtils.concat(buffer, consignationModule.getName(), ",");
                    break;
                }
            }
        }

        return buffer.toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof Consignation))
            return false;

        Consignation that = (Consignation) o;

        return consignationId.equals(that.consignationId);
    }

    @Override
    public int hashCode()
    {
        return consignationId.hashCode();
    }
}
