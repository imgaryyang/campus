package com.gzzm.portal.eval;

import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * @author sjy
 * @date 2018/2/27
 */
@Entity(table = "PLEVALQUESTION", keys = "questionId")
public class EvalQuestion
{
    @Generatable(length = 6)
    private Integer questionId;

    private Integer typeId;

    @ToOne("TYPEID")
    private EvalType type;

    @ColumnDescription(type = "varchar(500)")
    private String question;

    @ColumnDescription(type = "varchar(6)")
    private Integer orderId;

    @OneToMany
    @OrderBy(column = "orderId")
    private List<EvalAnswer> answers;

    public Integer getQuestionId()
    {
        return questionId;
    }

    public void setQuestionId(Integer questionId)
    {
        this.questionId = questionId;
    }

    public Integer getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Integer typeId)
    {
        this.typeId = typeId;
    }

    public EvalType getType()
    {
        return type;
    }

    public void setType(EvalType type)
    {
        this.type = type;
    }

    public String getQuestion()
    {
        return question;
    }

    public void setQuestion(String question)
    {
        this.question = question;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    public List<EvalAnswer> getAnswers()
    {
        return answers;
    }

    public void setAnswers(List<EvalAnswer> answers)
    {
        this.answers = answers;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof EvalQuestion))
        {
            return false;
        }

        EvalQuestion that = (EvalQuestion) o;

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
        return question;
    }
}
