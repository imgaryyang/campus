package com.gzzm.portal.survey;

import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.sql.*;
import java.util.List;

/**
 * @author steq
 * @date 2011-6-16
 */
public abstract class SurveyDao extends GeneralDao {
    public SurveyDao() {
    }

    public Survey getSurvey(Integer surveyId) throws Exception {
        return load(Survey.class, surveyId);
    }

    @OQL("select t from SurveyType t where t.stationId=:1 order by orderId")
    public abstract List<SurveyType> getSurveyTypes(Integer stationId) throws Exception;

    /**
     * 发布调查
     *
     * @param surveyIds 发布的调查id
     * @throws Exception 数据库异常
     */
    @OQLUpdate("update Survey set state=1 where surveyId in :1")
    public abstract void publish(Integer[] surveyIds) throws Exception;

    /**
     * 取消发布调查
     *
     * @param surveyIds 取消发布的调查id
     * @throws Exception 数据库异常
     */
    @OQLUpdate("update Survey set state=0 where surveyId in :1")
    public abstract void unPublish(Integer[] surveyIds) throws Exception;

    /**
     * @param surveyId
     * @return
     * @throws Exception
     */
    @OQL("select t from SurveyQuestion t where t.surveyId=:1 order by orderId")
    public abstract List<SurveyQuestion> getSurveyQuestions(Integer surveyId) throws Exception;

    /**
     * 查询问题的可选答案列表
     *
     * @param questionId
     * @return
     * @throws Exception
     */
    @OQL("select t from com.gzzm.portal.survey.SurveyOption t where t.questionId=:1 order by t.orderId")
    public abstract List<SurveyOption> getQuestionOptions(Integer questionId) throws Exception;

    /**
     * 统计一个选项的回答数
     * @param optionId
     * @return
     * @throws Exception
     */
    @OQL("select count(t) from SurveyAnswer s , s.options t where t.optionId=:1")
    public abstract Integer countAnswerOption(Integer optionId) throws Exception;

    /**
     * 统计一个问题的回答数
     * @param questionId
     * @return
     * @throws Exception
     */
    @OQL("select distinct count(select count(s.options) from SurveyAnswer s where exists temp in s.items :" +
            "    temp.questionId=:1)+count(select count(s1.options) from SurveyAnswer s1 where exists temp1 in s1.options :" +
            "    temp1.questionId=:1) from SurveyAnswer h")
    public abstract Integer countQuestion(Integer questionId) throws Exception;

    /**
     * 统计一个问卷调查的问卷回收数
     * @param surveyId
     * @return
     * @throws Exception
     */
    @OQL("select  count(t) from SurveyAnswer t where t.surveyId=:1")
    public abstract Integer countAnswer(Integer surveyId) throws Exception;

    /**
     * 统计其他选项
     * @param questionId
     * @return
     * @throws Exception
     */
    @OQL("select count(*) from SurveyAnswerItem t where t.questionId=:1")
    public abstract Integer countOtherOption(Integer questionId) throws Exception;

    /**
     * 查询某个问题填写的其他回答列表
     * @param questionId
     * @return
     * @throws Exception
     */
    @OQL("select t from SurveyAnswerItem t where t.questionId=:1 and t.answer.userName like ?2 and t.answer.answerTime>=?3 and t.answer.answerTime<?4 order by t.answer.answerTime desc")
    public abstract List<SurveyAnswerItem> queryOtherAnswers(Integer questionId, String userName, Date startTime,Date endTime) throws Exception;

}
