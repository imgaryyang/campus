package com.gzzm.portal.eval;

import net.cyan.commons.util.*;

import java.util.*;

/**
 * @author sjy
 * @date 2018/2/28
 */
public class EvalStatResultBean
{
    private String title;

    private Long allTotalEval;

    private Integer totalEval;

    private Float totalScore;

    private ListMap<EvalOption, Long> optionResult;

    private List<QuestionStatBean> questionResult;

    public ListMap<EvalOption, Long> getOptionResult()
    {
        return optionResult;
    }

    public void setOptionResult(ListMap<EvalOption, Long> optionResult)
    {
        this.optionResult = optionResult;
    }

    public Integer getTotalEval()
    {
        return totalEval;
    }

    public void setTotalEval(Integer totalEval)
    {
        this.totalEval = totalEval;
    }

    public Float getTotalScore()
    {
        return totalScore;
    }

    public void setTotalScore(Float totalScore)
    {
        this.totalScore = totalScore;
    }

    public List<QuestionStatBean> getQuestionResult()
    {
        return questionResult;
    }

    public void setQuestionResult(List<QuestionStatBean> questionResult)
    {
        this.questionResult = questionResult;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Long getAllTotalEval()
    {
        return allTotalEval;
    }

    public void setAllTotalEval(Long allTotalEval)
    {
        this.allTotalEval = allTotalEval;
    }
}
