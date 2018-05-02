package com.gzzm.portal.survey;

import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 每个回答对应每个问题的答案
 *
 * @author wxj
 * @date 2011-6-16
 */
@Entity(table = "PLSURVEYANSWERITEM", keys = {"answerId", "questionId"})
@Indexes({@Index(columns = {"ANSWERID", "QUESTIONID", "NUMBERVALUE"})})
public class SurveyAnswerItem
{
    /**
     * 参与调查id
     */
    @ColumnDescription(nullable = false)
    private Long answerId;

    private SurveyAnswer answer;

    /**
     * 所属题目id
     */
    @ColumnDescription(nullable = false)
    private Integer questionId;

    private SurveyQuestion question;

    /**
     * 字符串内容
     */
    @ColumnDescription(type = "varchar(4000)")
    private String charValue;

    /**
     * 数值内容:使用整数和数字型答案，使用数字保存，方便统计和查询
     */
    @ColumnDescription(type = "number(12,3)")
    private Double numberValue;

    /**
     * 日期型内容
     */
    private Date dateValue;

    public SurveyAnswerItem()
    {
    }

    public Long getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
    }

    public SurveyAnswer getAnswer()
    {
        return answer;
    }

    public void setAnswer(SurveyAnswer answer)
    {
        this.answer = answer;
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

    public String getCharValue()
    {
        return charValue;
    }

    public void setCharValue(String charValue)
    {
        this.charValue = charValue;
    }

    public Double getNumberValue()
    {
        return numberValue;
    }

    public void setNumberValue(Double numberValue)
    {
        this.numberValue = numberValue;
    }

    public Date getDateValue()
    {
        return dateValue;
    }

    public void setDateValue(Date dateValue)
    {
        this.dateValue = dateValue;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof SurveyAnswerItem))
            return false;

        SurveyAnswerItem that = (SurveyAnswerItem) o;

        return answerId.equals(that.answerId) && questionId.equals(that.questionId);

    }

    @Override
    public int hashCode()
    {
        int result = answerId.hashCode();
        result = 31 * result + questionId.hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return super.toString();
    }
}
