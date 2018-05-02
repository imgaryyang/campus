package com.gzzm.portal.survey;

import net.cyan.arachne.annotation.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author sjy
 * @date 2015-9-8
 */
@Service()
public class SurveyResultPage {
    @Inject
    private SurveyDao dao;

    private SurveyAnswer answer;
    private Long answerId;

    @Service(url = "/portal/survey/SurveyResultPage")
    public String showAnswerResult() throws Exception {
        answer=dao.load(SurveyAnswer.class,answerId);
        return "/portal/survey/answerresult.ptl";
    }

    public String isChecked(Integer optionId) throws Exception{
        List<SurveyOption> options = answer.getOptions();
        if(options!=null&&options.size()>0)
        for(SurveyOption option:options){
            if(option.getOptionId().equals(optionId))
                return "checked";
        }
        return "";
    }
    public Object getOtherText(Integer questionId) throws Exception{
        List<SurveyAnswerItem> items = answer.getItems();
        if(items!=null&&items.size()>0){
            for(SurveyAnswerItem item:items){
                if(item.getQuestionId().equals(questionId)){
                    return item.getCharValue()!=null?item.getCharValue():(item.getNumberValue()!=null?item.getNumberValue():(item.getDateValue()));
                }
            }
        }
        return "";
    }

    public Long getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
    }

    public SurveyAnswer getAnswer() {
        return answer;
    }

    public void setAnswer(SurveyAnswer answer) {
        this.answer = answer;
    }
}
