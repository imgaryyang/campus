package com.gzzm.oa.vote;

import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 投票范围实体
 *
 * @author db
 * @date 11-12-1
 */
@Entity(table = "OAVOTESCOPE", keys = {"voteId", "type", "objectId"})
public class VoteScope
{
    /**
     * 主键1
     * 关联投票表
     */
    @Index
    private Integer voteId;

    @Cascade
    @NotSerialized
    private Vote vote;

    /**
     * 类型
     * 0表示部门、1表示用户
     */
    @Require
    private VoteScopeType type;

    /**
     * 主键2
     * 当type为0时：表示部门ID，此部门下的所有用户均可参与投票
     * 当type为1时：表示用户ID，此ID对应的用户可以参与投票
     */
    private Integer objectId;

    @NotSerialized
    @ToOne("OBJECTID")
    private User user;

    @NotSerialized
    @ToOne("OBJECTID")
    private Dept dept;

    @ComputeColumn("min(select r.voteTime from VoteRecord r where " +
            "r.voteId=this.voteId and (r.deptId=this.objectId and this.type=0 " +
            "or r.userId=this.objectId and this.type=1) and r.state=1)")
    private Date voteTime;

    @ComputeColumn("(case when type=0 then dept.deptName else user.userName end)")
    private String objectName;

    @ColumnDescription(type = "number(3)", defaultValue = "1", nullable = false)
    private Integer voteCount;

    public VoteScope()
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

    public VoteScopeType getType()
    {
        return type;
    }

    public void setType(VoteScopeType type)
    {
        this.type = type;
    }

    public Integer getObjectId()
    {
        return objectId;
    }

    public void setObjectId(Integer objectId)
    {
        this.objectId = objectId;
    }

    public Vote getVote()
    {
        return vote;
    }

    public void setVote(Vote vote)
    {
        this.vote = vote;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public Dept getDept()
    {
        return dept;
    }

    public void setDept(Dept dept)
    {
        this.dept = dept;
    }

    public Date getVoteTime()
    {
        return voteTime;
    }

    public void setVoteTime(Date voteTime)
    {
        this.voteTime = voteTime;
    }

    public String getObjectName()
    {
        return objectName;
    }

    public void setObjectName(String objectName)
    {
        this.objectName = objectName;
    }

    public Integer getVoteCount()
    {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount)
    {
        this.voteCount = voteCount;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof VoteScope))
            return false;

        VoteScope voteScope = (VoteScope) o;

        return objectId.equals(voteScope.objectId) && type == voteScope.type && voteId.equals(voteScope.voteId);
    }

    @Override
    public int hashCode()
    {
        int result = voteId.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + objectId.hashCode();
        return result;
    }
}
