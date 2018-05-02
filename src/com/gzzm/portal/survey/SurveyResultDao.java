package com.gzzm.portal.survey;

import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * @author steq
 * @date 2011-6-16
 */
public abstract class SurveyResultDao extends GeneralDao
{
    public SurveyResultDao()
    {
    }
    @OQL("select count(t.options) from SurveyAnswer t where t.surveyId=:1 and exists t.options in t.options :optionId=:2")
    public  abstract Integer getVoteInputCount(Integer surveyId,Integer optionId);

    /**
     * 统计总共有多少人接受了一个问卷调查
     * @param surveyId
     * @return
     */
    @OQL("select count(*) from com.gzzm.portal.survey.SurveyAnswer t where t.surveyId=:1")
    public  abstract Integer countSurvey(Integer surveyId);


}
