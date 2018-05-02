package com.gzzm.oa.vote;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

/**
 * 投票选项实体
 *
 * @author db
 * @date 11-12-1
 */
@Entity(table = "OAVOTEOPTION", keys = "optionId")
public class VoteOption
{
    /**
     * 主键
     */
    @Generatable(length = 8)
    private Integer optionId;

    /**
     * 选项名
     */
    @Require
    @Unique(with = "problemId")
    @ColumnDescription(type = "varchar(250)")
    private String optionName;

    /**
     * 投票问题ID
     * 关联投票问题实体
     */
    @Require
    @Index
    @ColumnDescription(type = "number(9)")
    private Integer problemId;

    @NotSerialized
    private VoteProblem voteProblem;

    @ColumnDescription(type = "varchar(1000)")
    private String remark;

    /**
     * 允许填意见
     */
    @ColumnDescription(defaultValue = "0")
    private Boolean opinion;

    /**
     * 得分
     */
    @ColumnDescription(type = "number(3)")
    private Integer score;

    /**
     * 排序
     */
    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    public VoteOption()
    {
    }

    public Integer getOptionId()
    {
        return optionId;
    }

    public void setOptionId(Integer optionId)
    {
        this.optionId = optionId;
    }

    public String getOptionName()
    {
        return optionName;
    }

    public void setOptionName(String optionName)
    {
        this.optionName = optionName;
    }

    public Integer getProblemId()
    {
        return problemId;
    }

    public void setProblemId(Integer problemId)
    {
        this.problemId = problemId;
    }

    public VoteProblem getVoteProblem()
    {
        return voteProblem;
    }

    public void setVoteProblem(VoteProblem voteProblem)
    {
        this.voteProblem = voteProblem;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    public Boolean isOpinion()
    {
        return opinion;
    }

    public void setOpinion(Boolean opinion)
    {
        this.opinion = opinion;
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
        if (this == o) return true;
        if (!(o instanceof VoteOption)) return false;

        VoteOption that = (VoteOption) o;

        return !(optionId != null ? !optionId.equals(that.optionId) : that.optionId != null);

    }

    @Override
    public int hashCode()
    {
        return optionId != null ? optionId.hashCode() : 0;
    }

    @Override
    public String toString()
    {
        // 返回问题名
        // 如果此方法返回的话，会现排序视图显示的问题名是以包名全径开头的名的错误
        return optionName;
    }
}
