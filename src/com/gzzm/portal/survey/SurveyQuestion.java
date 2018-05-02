package com.gzzm.portal.survey;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.*;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * 调查里每个问题的基本信息
 *
 * @author wxj
 * @date 2011-6-16
 */
@Entity(table = "PLSURVEYQUESTION", keys = "questionId")
public class SurveyQuestion
{
    /**
     * 题目主键
     */
    @Generatable(length = 8)
    private Integer questionId;

    /**
     * 所属调查表id
     */
    @ColumnDescription(nullable = false)
    @Index
    private Integer surveyId;

    @Cascade
    @NotSerialized
    private Survey survey;

    /**
     * 问题的标题
     */
    @Require
    @Unique(with = "surveyId")
    @ColumnDescription(type = "varchar(500)")
    private String questionName;

    /**
     * 数据类型
     */
    @Require
    private QuestionDataType dataType;

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
     * 是否必填:0为不必填,1为必填
     */
    @Require
    @ColumnDescription(type = "number(1)", nullable = false, defaultValue = "0")
    private Boolean required;

    /**
     * 是否允许输入其他内容
     */
    private Boolean other;

    /**
     * 链接目标
     */
    private String url;

    /**
     * 顺序ID，用于决定题目显示的顺序
     */
    @ColumnDescription(type = "number(5)")
    private Integer orderId;

    /**
     * 从调查问题表到调查问题可选答案表的一对多关系
     */
    @NotSerialized
    @OneToMany
    @OrderBy(column = "ORDERID")
    private List<SurveyOption> options;

    public SurveyQuestion()
    {
    }

    public Integer getQuestionId()
    {
        return questionId;
    }

    public void setQuestionId(Integer questionId)
    {
        this.questionId = questionId;
    }

    public Integer getSurveyId()
    {
        return surveyId;
    }

    public void setSurveyId(Integer surveyId)
    {
        this.surveyId = surveyId;
    }

    public Survey getSurvey()
    {
        return survey;
    }

    public void setSurvey(Survey survey)
    {
        this.survey = survey;
    }

    public QuestionDataType getDataType()
    {
        return dataType;
    }

    public void setDataType(QuestionDataType dataType)
    {
        this.dataType = dataType;
    }

    public Boolean getRequired()
    {
        return required;
    }

    public void setRequired(Boolean required)
    {
        this.required = required;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    public List<SurveyOption> getOptions()
    {
        return options;
    }

    public void setOptions(List<SurveyOption> options)
    {
        this.options = options;
    }

    public String getQuestionName()
    {
        return questionName;
    }

    public void setQuestionName(String questionName)
    {
        this.questionName = questionName;
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

    public Boolean isOther()
    {
        return other;
    }

    public void setOther(Boolean other)
    {
        this.other = other;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;

        if (!(o instanceof SurveyQuestion))
            return false;

        SurveyQuestion that = (SurveyQuestion) o;

        return questionId.equals(that.questionId);
    }

    @Override
    public int hashCode()
    {
        return questionId.hashCode();
    }

    @Override
    public String toString()
    {
        return questionName;
    }

    public SurveyQuestion cloneQuestion()
    {
        SurveyQuestion question = new SurveyQuestion();

        question.setQuestionName(questionName);
        question.setDataType(dataType);
        question.setMinCount(minCount);
        question.setMaxCount(maxCount);
        question.setMinval(minval);
        question.setMaxval(maxval);
        question.setRequired(required);
        question.setOrderId(orderId);

        List<SurveyOption> options = new ArrayList<SurveyOption>();

        for (SurveyOption option : getOptions())
        {
            options.add(option.cloneOption());
        }

        question.setOptions(options);

        return question;
    }
}
