package com.gzzm.oa.vote;

import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * 投票记录实体
 *
 * @author db
 * @date 11-12-1
 */
@Entity(table = "OAVOTERECORD", keys = "recordId")
@Indexes(@Index(
        columns = {"VOTEID", "USERID"}
))
public class VoteRecord
{
    /**
     * 主键
     */
    @Generatable(length = 9)
    private Integer recordId;

    /**
     * 投票人
     * 关联用户实体
     */
    @Require
    private Integer userId;

    @NotSerialized
    private User user;

    /**
     * 投票部门，仅当投票对象类型为部门时有效
     */
    private Integer deptId;

    @NotSerialized
    private Dept dept;

    /**
     * 投票时间
     */
    private Date voteTime;

    /**
     * 投票信息ID
     * 关联投票信息表
     */
    @Index
    private Integer voteId;

    @NotSerialized
    private Vote vote;

    /**
     * 选择选项
     * 关联投票记录实体和投票选项实体
     */
    @NotSerialized
    @OneToMany
    private List<VoteSelected> selecteds;

    @NotSerialized
    @OneToMany
    private List<VoteInput> voteInputList;

    @NotSerialized
    @OneToMany
    private List<VoteOptionInput> optionInputList;

    /**
     * 匿名
     * true表示匿名
     * false表示不是匿名
     * 因为vote的anonymous属性有可能被修改，加入之前为匿名投票，不能因为投票修改为不匿名后之前匿名的投票就显示名字
     * 应此需要在投票记录表记录投票时是否匿名
     */
    @Require
    private Boolean anonymous;

    @ColumnDescription(defaultValue = "1")
    private VoteRecordState state;

    private VoteScopeType type;

    @ComputeColumn("(case when nvl(type,vote.scopeType)=0 then dept.deptName else user.userName end)")
    private String objectName;

    /**
     * 消耗时间，秒钟为单位
     */
    private Integer costTime;

    @NotSerialized
    @ComputeColumn("case when state=0 then null else nvl(sum(select voteOption.score from this.selecteds),0) end")
    private Integer score;

    public VoteRecord()
    {
    }

    public Integer getRecordId()
    {
        return recordId;
    }

    public void setRecordId(Integer recordId)
    {
        this.recordId = recordId;
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

    public Date getVoteTime()
    {
        return voteTime;
    }

    public void setVoteTime(Date voteTime)
    {
        this.voteTime = voteTime;
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

    public List<VoteSelected> getSelecteds()
    {
        return selecteds;
    }

    public void setSelecteds(List<VoteSelected> selecteds)
    {
        this.selecteds = selecteds;
    }

    public List<VoteInput> getVoteInputList()
    {
        return voteInputList;
    }

    public void setVoteInputList(List<VoteInput> voteInputList)
    {
        this.voteInputList = voteInputList;
    }

    public List<VoteOptionInput> getOptionInputList()
    {
        return optionInputList;
    }

    public void setOptionInputList(List<VoteOptionInput> optionInputList)
    {
        this.optionInputList = optionInputList;
    }

    public Boolean isAnonymous()
    {
        return anonymous;
    }

    public void setAnonymous(Boolean anonymous)
    {
        this.anonymous = anonymous;
    }

    public VoteRecordState getState()
    {
        return state;
    }

    public void setState(VoteRecordState state)
    {
        this.state = state;
    }

    public VoteScopeType getType()
    {
        return type;
    }

    public void setType(VoteScopeType type)
    {
        this.type = type;
    }

    public String getObjectName()
    {
        return objectName;
    }

    public void setObjectName(String objectName)
    {
        this.objectName = objectName;
    }

    public String getVoteResult(VoteProblem problem)
    {
        StringBuilder buffer = new StringBuilder();

        List<VoteSelected> selecteds = getSelecteds();
        for (VoteOption option : problem.getOptionList())
        {
            Integer optionId = option.getOptionId();
            for (VoteSelected selected : selecteds)
            {
                if (selected.getOptionId().equals(optionId))
                {
                    if (buffer.length() > 0)
                        buffer.append(",");
                    buffer.append(option.getOptionName());
                    break;
                }
            }
        }

        Integer problemId = problem.getProblemId();
        for (VoteInput voteInput : getVoteInputList())
        {
            if (voteInput.getProblemId().equals(problemId))
            {
                if (buffer.length() > 0)
                    buffer.append(",");
                buffer.append(voteInput.getInputContent());
            }
        }

        return buffer.toString();
    }

    public Integer getCostTime()
    {
        return costTime;
    }

    public void setCostTime(Integer costTime)
    {
        this.costTime = costTime;
    }

    public Integer getScore()
    {
        return score;
    }

    public void setScore(Integer score)
    {
        this.score = score;
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

        VoteRecord that = (VoteRecord) o;

        return !(recordId != null ? !recordId.equals(that.recordId) : that.recordId != null);

    }

    @Override
    public int hashCode()
    {
        return recordId != null ? recordId.hashCode() : 0;
    }
}
