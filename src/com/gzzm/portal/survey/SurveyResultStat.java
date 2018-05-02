package com.gzzm.portal.survey;

import net.cyan.activex.OfficeUtils;
import net.cyan.arachne.RequestContext;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.IOUtils;
import net.cyan.commons.util.InputFile;
import net.cyan.commons.util.Inputable;
import net.cyan.nest.annotation.Inject;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.List;

/**
 * 一个问卷调查的统计
 * @author sjy
 * @date 2015-9-8
 */
@Service()
public class SurveyResultStat {

    private static final DecimalFormat RATIOFORMAT = new DecimalFormat("#.##%");

    @Inject
    private SurveyDao dao;

    private Integer surveyId;

    @NotSerialized
    private Survey survey;

    /**
     * 参与记录数
     */
    @NotSerialized
    private Integer recordCount;

    /**
     * 问题列表
     */
    @NotSerialized
    private List<SurveyQuestion> questions;

    @Service(url = "/portal/survey/SurveyResultStat")
    public String showResult() throws Exception{
        recordCount=dao.countAnswer(surveyId);
        survey=dao.load(Survey.class,surveyId);
        questions = dao.getSurveyQuestions(surveyId);
        return "/portal/survey/surveyresult.ptl";
    }
    @Service()
    public InputFile exportResult(Integer surveyId) throws Exception{
        HttpServletRequest request = RequestContext.getContext().getRequest();
        InputStream streamByUrl = IOUtils.getStreamByUrl("http://"+request.getLocalAddr()+":"+request.getLocalPort()+"/portal/survey/SurveyResultStat?surveyId=" + surveyId);
        String surveyName = dao.load(Survey.class, surveyId).getSurveyName();
        Inputable inputable = OfficeUtils.htmlToWord(new InputFile(streamByUrl,surveyName).getInputable());
        return new InputFile(inputable,surveyName+".doc");
    }


    public List<SurveyOption> getQuestionOptions(Integer questionId) throws Exception{
        return dao.getQuestionOptions(questionId);
    }

    public Integer countAnswerOption(Integer optionId) throws Exception{
        return dao.countAnswerOption(optionId);
    }

    public Integer countOtherOption(Integer questionId) throws Exception{
        return dao.countOtherOption(questionId);
    }
//计算普通选项的百分比
    public String optionPercent(SurveyOption option) throws Exception{
        Integer integer = dao.countQuestion(option.getQuestionId());
        if(integer!=0){
            return RATIOFORMAT.format(countAnswerOption(option.getOptionId()).floatValue() / integer.floatValue());
        }
        return "0%";
    }
    //计算其他选项的百分比
    public String otheroptionPercent(Integer questionId) throws Exception{
        Integer integer = dao.countQuestion(questionId);
        if(integer!=0){
            float v = countAnswerOption(questionId).floatValue();
            String format = RATIOFORMAT.format(countOtherOption(questionId).floatValue() / integer.floatValue());
            return RATIOFORMAT.format(countOtherOption(questionId).floatValue() / integer.floatValue());
        }
        return "0%";
    }
    public Integer getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Integer surveyId) {
        this.surveyId = surveyId;
    }

    public Survey getSurvey() {
        return survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    public Integer getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(Integer recordCount) {
        this.recordCount = recordCount;
    }

    public List<SurveyQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<SurveyQuestion> questions) {
        this.questions = questions;
    }
}
