package com.gzzm.oa.activite;

import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.*;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * 日常活动基本信息表
 *
 * @author lfx
 * @date 11-9-29
 */
@Entity(table = "OAACTIVITE", keys = "activiteId")
public class Activite
{
    /**
     * 主键
     */
    @Generatable(length = 8)
    private Integer activiteId;

    /**
     * 活动标题
     */
    @Require
    @ColumnDescription(type = "varchar(250)")
    private String title;

    /**
     * 活动类型 活动类型，关联活动类型表OAACTIVITETYPE
     */
    @Require
    private Integer typeId;

    @NotSerialized
    private ActiviteType type;

    /**
     * 活动内容
     */
    private char[] activiteContent;

    /**
     * 活动开始时间
     */
    @Require
    private Date startTime;

    /**
     * 动结束时间
     */
    @LargerThan("startTime")
    @Require
    private Date endTime;

    /**
     * 活动地点
     */
    @Require
    private String activitePlace;

    /**
     * 报名截止时间
     */
    @Require
    @LessEqual("startTime")
    private Date applyEndTime;

    /**
     * 活动预算经费
     */
    @ColumnDescription(type = "number(9,2)")
    private Double amount;

    /**
     * 活动实际经费
     */
    @ColumnDescription(type = "number(9,2)")
    private Double actualAmount;

    /**
     * 状态
     */
    private ActiviteState state;

    /**
     * 活动发起部门
     */
    private Integer deptId;

    /**
     * 关联部门表
     */
    @NotSerialized
    private Dept dept;

    /**
     * 活动创建人
     */
    private Integer creator;

    /**
     * 关联用户表
     */
    @ToOne("CREATOR")
    @NotSerialized
    private User user;

    /**
     * 活动创建时间
     */
    private Date createTime;

    /**
     * 是否短信通知
     */
    private Boolean notify;

    /**
     * 所有活动人员（包含所有状态）
     */
    @NotSerialized
    @OneToMany
    private List<ActiviteMember> memberLists;

    @ColumnDescription(nullable = false, defaultValue = "0")
    private Boolean publicity;

    /**
         * 关联年度活动预算表
         */
    private Integer activiteBudgetId;

    /**
     * 关联年度活动预算表
     */
    @NotSerialized
    private ActiviteBudget activiteBudget;

    /**
     * 活动情况小结
     */
    @ColumnDescription(type = "varchar(4000)")
    private String summary;

    public Activite()
    {
    }

    public Integer getActiviteId()
    {
        return activiteId;
    }

    public void setActiviteId(Integer activiteId)
    {
        this.activiteId = activiteId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Integer getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Integer typeId)
    {
        this.typeId = typeId;
    }

    public char[] getActiviteContent()
    {
        return activiteContent;
    }

    public void setActiviteContent(char[] activiteContent)
    {
        this.activiteContent = activiteContent;
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

    public String getActivitePlace()
    {
        return activitePlace;
    }

    public void setActivitePlace(String activitePlace)
    {
        this.activitePlace = activitePlace;
    }

    public Date getApplyEndTime()
    {
        return applyEndTime;
    }

    public void setApplyEndTime(Date applyEndTime)
    {
        this.applyEndTime = applyEndTime;
    }

    public Double getAmount()
    {
        return amount;
    }

    public void setAmount(Double amount)
    {
        this.amount = amount;
    }

    public ActiviteState getState()
    {
        return state;
    }

    public void setState(ActiviteState state)
    {
        this.state = state;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Integer getCreator()
    {
        return creator;
    }

    public void setCreator(Integer creator)
    {
        this.creator = creator;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public Boolean isNotify()
    {
        return notify;
    }

    public void setNotify(Boolean notify)
    {
        this.notify = notify;
    }

    public ActiviteType getType()
    {
        return type;
    }

    public void setType(ActiviteType type)
    {
        this.type = type;
    }

    public Dept getDept()
    {
        return dept;
    }

    public void setDept(Dept dept)
    {
        this.dept = dept;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public List<ActiviteMember> getMemberLists()
    {
        return memberLists;
    }

    public void setMemberLists(List<ActiviteMember> memberLists)
    {
        this.memberLists = memberLists;
    }

    public Boolean isPublicity()
    {
        return publicity;
    }

    public void setPublicity(Boolean publicity)
    {
        this.publicity = publicity;
    }

    public Integer getActiviteBudgetId()
    {
        return activiteBudgetId;
    }

    public void setActiviteBudgetId(Integer activiteBudgetId)
    {
        this.activiteBudgetId = activiteBudgetId;
    }

    public ActiviteBudget getActiviteBudget()
    {
        return activiteBudget;
    }

    public void setActiviteBudget(ActiviteBudget activiteBudget)
    {
        this.activiteBudget = activiteBudget;
    }

    public Double getActualAmount()
    {
        return actualAmount;
    }

    public void setActualAmount(Double actualAmount)
    {
        this.actualAmount = actualAmount;
    }

    public String getSummary()
    {
        return summary;
    }

    public void setSummary(String summary)
    {
        this.summary = summary;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof Activite))
            return false;

        Activite activite = (Activite) o;

        return activiteId.equals(activite.activiteId);
    }

    @Override
    public int hashCode()
    {
        return activiteId.hashCode();
    }

    @Override
    public String toString()
    {
        return title;
    }
}
