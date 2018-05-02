package com.gzzm.portal.eval;

import java.util.*;

/**
 * @author sjy
 * @date 2018/3/1
 */
public class QuestionStatBean
{
    private String question;

    private Integer allAnswer;

    private List<AnswerStatBean> answerStat;

    public String getQuestion()
    {
        return question;
    }

    public void setQuestion(String question)
    {
        this.question = question;
    }

    public Integer getAllAnswer()
    {
        return allAnswer;
    }

    public void setAllAnswer(Integer allAnswer)
    {
        this.allAnswer = allAnswer;
    }

    public List<AnswerStatBean> getAnswerStat()
    {
        return answerStat;
    }

    public void setAnswerStat(List<AnswerStatBean> answerStat)
    {
        this.answerStat = answerStat;
    }
}
