package com.gzzm.oa.vote;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.*;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * 投票问题实体
 *
 * @author db
 * @date 11-12-1
 */
@Entity(table = "OAVOTEPROBLEM", keys = "problemId")
public class VoteProblem
{
    /**
     * 主键
     */
    @Generatable(length = 7)
    private Integer problemId;

    /**
     * 问题名
     */
    @Require
    @ColumnDescription(type = "varchar(250)")
    private String problemName;

    /**
     * 投票信息ID
     * 关联投票信息表
     */
    @Require
    @Index
    @ColumnDescription(type = "number(7)")
    private Integer voteId;

    @NotSerialized
    private Vote vote;

    @Require
    private ProblemDataType dataType;

    /**
     * 最少选择多少个选项
     */
    @MinVal("0")
    @Require
    private Integer minCount;

    /**
     * 最多选择多少个选项
     */
    @MinVal("1")
    @LargerEqual("minCount")
    @Require
    private Integer maxCount;

    /**
     * 最大值
     */
    private String maxval;

    /**
     * 最小值
     */
    private String minval;

    /**
     * 是否允许输入其他内容
     */
    private Boolean other;

    /**
     * 是否必选（填）
     * 0表示必选（填）、1表示可选（填）
     */
    @Require
    private Boolean required;

    /**
     * 排序
     */
    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    /**
     * 关联投票选项实体
     * 方便通过投票问题查询投票选项
     */
    @NotSerialized
    @OneToMany
    @OrderBy(column = "ORDERID")
    private List<VoteOption> optionList;

    private Integer groupId;

    @NotSerialized
    private VoteProblemGroup problemGroup;

    private OptionOrderType orderType;

    public VoteProblem()
    {
    }

    public Integer getProblemId()
    {
        return problemId;
    }

    public void setProblemId(Integer problemId)
    {
        this.problemId = problemId;
    }

    public String getProblemName()
    {
        return problemName;
    }

    public void setProblemName(String problemName)
    {
        this.problemName = problemName;
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

    public ProblemDataType getDataType()
    {
        return dataType;
    }

    public void setDataType(ProblemDataType dataType)
    {
        this.dataType = dataType;
    }

    public Integer getMinCount()
    {
        return minCount;
    }

    public void setMinCount(Integer minCount)
    {
        this.minCount = minCount;
    }

    public Integer getMaxCount()
    {
        return maxCount;
    }

    public void setMaxCount(Integer maxCount)
    {
        this.maxCount = maxCount;
    }

    public String getMaxval()
    {
        return maxval;
    }

    public void setMaxval(String maxval)
    {
        this.maxval = maxval;
    }

    public String getMinval()
    {
        return minval;
    }

    public void setMinval(String minval)
    {
        this.minval = minval;
    }

    public Boolean isRequired()
    {
        return required;
    }

    public void setRequired(Boolean required)
    {
        this.required = required;
    }

    public Boolean getOther()
    {
        return other;
    }

    public void setOther(Boolean other)
    {
        this.other = other;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    public List<VoteOption> getOptionList()
    {
        return optionList;
    }

    public void setOptionList(List<VoteOption> optionList)
    {
        this.optionList = optionList;
    }

    public Integer getGroupId()
    {
        return groupId;
    }

    public void setGroupId(Integer groupId)
    {
        this.groupId = groupId;
    }

    public VoteProblemGroup getProblemGroup()
    {
        return problemGroup;
    }

    public void setProblemGroup(VoteProblemGroup problemGroup)
    {
        this.problemGroup = problemGroup;
    }

    public OptionOrderType getOrderType()
    {
        return orderType;
    }

    public void setOrderType(OptionOrderType orderType)
    {
        this.orderType = orderType;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof VoteProblem)) return false;

        VoteProblem that = (VoteProblem) o;

        return !(problemId != null ? !problemId.equals(that.problemId) : that.problemId != null);
    }

    @Override
    public int hashCode()
    {
        return problemId != null ? problemId.hashCode() : 0;
    }

    @Override
    public String toString()
    {
        // 返回问题名
        // 如果不返回投票问题我的话，会现排序视图显示的问题名是以包名全径开头的名的错误
        return problemName;
    }

    public VoteProblem cloneProblem()
    {
        VoteProblem c = new VoteProblem();
        c.setProblemName(problemName);
        c.setDataType(dataType);
        c.setMinCount(minCount);
        c.setMaxCount(maxCount);
        c.setMinval(minval);
        c.setMaxval(maxval);
        c.setOther(other);
        c.setRequired(required);
        c.setOrderType(orderType);

        List<VoteOption> optionList = new ArrayList<VoteOption>(getOptionList().size());
        for (VoteOption option : getOptionList())
        {
            VoteOption option1 = new VoteOption();
            option1.setOptionName(option.getOptionName());
            option1.setRemark(option.getRemark());
            option1.setOpinion(option.isOpinion());
            option1.setScore(option.getScore());

            optionList.add(option1);
        }

        c.setOptionList(optionList);

        return c;
    }
}
