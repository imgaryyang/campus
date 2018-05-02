package com.gzzm.portal.eval;

import net.cyan.arachne.annotation.*;
import net.cyan.thunwind.annotation.*;

/**
 * @author sjy
 * @date 2018/2/27
 */
@Entity(table = "PLEVALANSWER", keys = "answerId")
public class EvalAnswer
{
    @Generatable(length = 6)
    private Integer answerId;

    private Integer questionId;

    @ToOne("QUESTIONID")
    @NotSerialized
    private EvalQuestion question;

    @ColumnDescription(type = "varchar(500)")
    private String answer;

    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    public Integer getAnswerId()
    {
        return answerId;
    }

    public void setAnswerId(Integer answerId)
    {
        this.answerId = answerId;
    }

    public String getAnswer()
    {
        return answer;
    }

    public void setAnswer(String answer)
    {
        this.answer = answer;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    public EvalQuestion getQuestion()
    {
        return question;
    }

    public void setQuestion(EvalQuestion question)
    {
        this.question = question;
    }

    public Integer getQuestionId()
    {
        return questionId;
    }

    public void setQuestionId(Integer questionId)
    {
        this.questionId = questionId;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof EvalAnswer))
        {
            return false;
        }

        EvalAnswer that = (EvalAnswer) o;

        return answerId.equals(that.answerId);

    }

    @Override
    public int hashCode()
    {
        return answerId.hashCode();
    }

    @Override
    public String toString()
    {
        return answer;
    }
}
