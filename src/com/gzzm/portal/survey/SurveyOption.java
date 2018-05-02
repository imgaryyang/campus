package com.gzzm.portal.survey;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

/**
 * 调查问题可选答案表:调查里每个问题的可选答案
 *
 * @author wxj
 * @date 2011-6-16
 */
@Entity(table = "PLSURVEYOPTION", keys = "optionId")
public class SurveyOption
{
    /**
     * 答案的id
     */
    @Generatable(length = 8)
    private Integer optionId;

    /**
     * 答案所属题目表
     */
    @Index
    @ColumnDescription(nullable = false)
    private Integer questionId;

    @Cascade
    @NotSerialized
    private SurveyQuestion question;

    /**
     * 选项内容
     */
    @Require
    @Unique(with = "questionId")
    @ColumnDescription(type = "varchar(400)")
    private String content;

    /**
     * 图片
     */
    private byte[] picture;

    /**
     * 图片链接的URL
     */
    @ColumnDescription(type = "varchar(250)")
    private String url;

    /**
     * 备注
     */
    @ColumnDescription(type = "varchar(400)")
    private String remark;

    /**
     * 顺序ID，用于决定答案显示的顺序
     */
    @ColumnDescription(type = "number(5)")
    private Integer orderId;

    public SurveyOption()
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

    public Integer getQuestionId()
    {
        return questionId;
    }

    public void setQuestionId(Integer questionId)
    {
        this.questionId = questionId;
    }

    public SurveyQuestion getQuestion()
    {
        return question;
    }

    public void setQuestion(SurveyQuestion question)
    {
        this.question = question;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public byte[] getPicture()
    {
        return picture;
    }

    public void setPicture(byte[] picture)
    {
        this.picture = picture;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
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

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof SurveyOption))
            return false;

        SurveyOption that = (SurveyOption) o;

        return optionId.equals(that.optionId);

    }

    @Override
    public int hashCode()
    {
        return optionId.hashCode();
    }

    @Override
    public String toString()
    {
        return content;
    }

    public SurveyOption cloneOption()
    {
        SurveyOption option = new SurveyOption();

        option.setContent(getContent());
        option.setPicture(getPicture());
        option.setUrl(getUrl());
        option.setRemark(getRemark());
        option.setOrderId(getOrderId());

        return option;
    }
}
