package com.gzzm.oa.vote;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

import java.util.List;

/**
 * 投票问题分组，将一个投票中的问题分成几组
 *
 * @author camel
 * @date 12-3-30
 */
@Entity(table = "OAVOTEPROBLEMGROUP", keys = "groupId")
public class VoteProblemGroup
{
    @Generatable(length = 7)
    private Integer groupId;

    @Require
    @Unique(with = "voteId")
    @ColumnDescription(type = "varchar(250)")
    private String groupName;

    private Integer voteId;

    @NotSerialized
    private Vote vote;

    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    @OrderBy(column = "ORDERID")
    @OneToMany
    @NotSerialized
    private List<VoteProblem> problems;

    public VoteProblemGroup()
    {
    }

    public Integer getGroupId()
    {
        return groupId;
    }

    public void setGroupId(Integer groupId)
    {
        this.groupId = groupId;
    }

    public String getGroupName()
    {
        return groupName;
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

    public Integer getVoteId()
    {
        return voteId;
    }

    public void setVoteId(Integer voteId)
    {
        this.voteId = voteId;
    }

    public Vote getVote()
    {
        return vote;
    }

    public void setVote(Vote vote)
    {
        this.vote = vote;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    public List<VoteProblem> getProblems()
    {
        return problems;
    }

    public void setProblems(List<VoteProblem> problems)
    {
        this.problems = problems;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof VoteProblemGroup))
            return false;

        VoteProblemGroup that = (VoteProblemGroup) o;

        return groupId.equals(that.groupId);
    }

    @Override
    public int hashCode()
    {
        return groupId.hashCode();
    }

    @Override
    public String toString()
    {
        return groupName;
    }

    public VoteProblemGroup cloneGroup()
    {
        VoteProblemGroup c = new VoteProblemGroup();
        c.setGroupName(getGroupName());
        c.setOrderId(getOrderId());

        return c;
    }
}
