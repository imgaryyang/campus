package com.gzzm.oa.vote;

import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.DateUtils;
import net.cyan.commons.validate.annotation.*;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * 投票信息实体
 *
 * @author db
 * @date 11-12-1
 */
@Entity(table = "OAVOTE", keys = "voteId")
public class Vote
{
    /**
     * 主键
     */
    @Generatable(length = 7)
    private Integer voteId;

    /**
     * 标题
     */
    @Require
    @ColumnDescription(type = "varchar(250)")
    private String title;

    private Integer typeId;

    private VoteType type;

    /**
     * 说明
     */
    @MaxLen(500)
    @ColumnDescription(type = "varchar(1000)")
    private String intro;

    /**
     * 所属部门
     * 关联部门实体
     */
    @Require
    @Index
    @ColumnDescription(type = "number(7)")
    private Integer deptId;

    @NotSerialized
    private Dept dept;

    /**
     * 创建人
     * 关系用户实体
     */
    @Require
    @ColumnDescription(type = "number(9)")
    private Integer creator;

    @NotSerialized
    @ToOne("CREATOR")
    private User user;

    /**
     * 创建时间
     * 创建对象时自动赋为当前时间
     */
    private java.util.Date createTime;

    /**
     * 开始时间
     */
    @Require
    private java.sql.Date startTime;

    /**
     * 结束时间
     */
    @LargerThan("startTime")
    private java.sql.Date endTime;

    /**
     * 警告时间
     */
    private java.sql.Date alarmTime;

    /**
     * 是否有效
     * false表示无效
     * true表示有效
     */
    @Require
    @ColumnDescription(defaultValue = "1", nullable = false)
    private Boolean valid;

    /**
     * 匿名
     * true表示匿名
     * false表示不是匿名
     */
    @Require
    private Boolean anonymous;

    @OneToMany
    private List<VoteScope> voteScopes;

    @NotSerialized
    @OneToMany
    @OrderBy(column = "ORDERID")
    private List<VoteProblem> problems;

    @NotSerialized
    @OneToMany
    @OrderBy(column = "ORDERID")
    private List<VoteProblemGroup> groups;

    /**
     * 投票范围类型
     */
    @ColumnDescription(defaultValue = "1", nullable = false)
    private VoteScopeType scopeType;

    private VotePeriod period;

    /**
     * 限时，单位为分钟
     */
    @ColumnDescription(type = "number(5)")
    private Integer timeLimit;

    public Vote()
    {
    }

    public Integer getVoteId()
    {
        return voteId;
    }

    public void setVoteId(Integer voteId)
    {
        this.voteId = voteId;
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

    public VoteType getType()
    {
        return type;
    }

    public void setType(VoteType type)
    {
        this.type = type;
    }

    public String getIntro()
    {
        return intro;
    }

    public void setIntro(String intro)
    {
        this.intro = intro;
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

    public Integer getCreator()
    {
        return creator;
    }

    public void setCreator(Integer creator)
    {
        this.creator = creator;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public java.sql.Date getStartTime()
    {
        return startTime;
    }

    public void setStartTime(java.sql.Date startTime)
    {
        this.startTime = startTime;
    }

    public java.sql.Date getEndTime()
    {
        return endTime;
    }

    public void setEndTime(java.sql.Date endTime)
    {
        this.endTime = endTime;
    }

    public java.sql.Date getAlarmTime()
    {
        return alarmTime;
    }

    public void setAlarmTime(java.sql.Date alarmTime)
    {
        this.alarmTime = alarmTime;
    }

    public Boolean isValid()
    {
        return valid;
    }

    public void setValid(Boolean valid)
    {
        this.valid = valid;
    }

    public Boolean isAnonymous()
    {
        return anonymous;
    }

    public void setAnonymous(Boolean anonymous)
    {
        this.anonymous = anonymous;
    }

    public List<VoteScope> getVoteScopes()
    {
        return voteScopes;
    }

    public void setVoteScopes(List<VoteScope> voteScopes)
    {
        this.voteScopes = voteScopes;
    }

    public List<VoteProblem> getProblems()
    {
        return problems;
    }

    public void setProblems(List<VoteProblem> problems)
    {
        this.problems = problems;
    }

    public List<VoteProblemGroup> getGroups()
    {
        return groups;
    }

    public void setGroups(List<VoteProblemGroup> groups)
    {
        this.groups = groups;
    }

    public boolean isEnd()
    {
        return endTime != null && DateUtils.addDate(endTime, 1).getTime() < System.currentTimeMillis();
    }

    public VoteScopeType getScopeType()
    {
        return scopeType;
    }

    public void setScopeType(VoteScopeType scopeType)
    {
        this.scopeType = scopeType;
    }

    public VotePeriod getPeriod()
    {
        return period;
    }

    public void setPeriod(VotePeriod period)
    {
        this.period = period;
    }

    public Integer getTimeLimit()
    {
        return timeLimit;
    }

    public void setTimeLimit(Integer timeLimit)
    {
        this.timeLimit = timeLimit;
    }

    @Override
    public String toString()
    {
        return title;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        Vote vote = (Vote) o;

        return !(voteId != null ? !voteId.equals(vote.voteId) : vote.voteId != null);

    }

    @Override
    public int hashCode()
    {
        return voteId != null ? voteId.hashCode() : 0;
    }
}