package com.gzzm.portal.survey;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.crud.view.*;
import net.cyan.nest.annotation.*;

import java.sql.Date;
import java.util.*;

/**
 * 某个问题填写了其他答案的回答列表
 *
 * @author sjy
 * @date 2016/12/9
 */
@Service(url = "/portal/survey/otherAnswer")
public class OtherAnswerListPage extends BaseListCrud<OtherAnswerBean> {

    @Inject
    private SurveyDao dao;

    private Integer questionId;

    private String userName;

    private java.sql.Date start_time;

    private java.sql.Date end_time;

    @Override
    protected Object createListView() throws Exception {
        PageTableView view = new PageTableView(false);
        view.addComponent("姓名","userName");
        view.addComponent("填写时间","start_time","end_time");
        view.addColumn("姓名", "userName").setWidth("150").setWrap(true);
        view.addColumn("填写时间", "answerTime").setWidth("150").setAlign(Align.center);
        view.addColumn("填写内容", "answerContent").setWrap(true);
        view.addButton(Buttons.query());
        return view;
    }

    @Override
    protected void loadList() throws Exception {
        if(StringUtils.isNotBlank(userName)){
            userName="%"+userName+"%";
        }else {
            userName=null;
        }
        List<SurveyAnswerItem> surveyAnswerItems = dao.queryOtherAnswers(questionId,userName,start_time,end_time);
        List<OtherAnswerBean> list = new ArrayList<OtherAnswerBean>();
        if (surveyAnswerItems != null && surveyAnswerItems.size() > 0) {
            for (SurveyAnswerItem item : surveyAnswerItems) {
                OtherAnswerBean bean = new OtherAnswerBean();
                list.add(bean);
                SurveyAnswer answer = item.getAnswer();
                bean.setAnswerTime(answer.getAnswerTime());
                bean.setUserName(StringUtils.isBlank(answer.getUserName())?"匿名用户":answer.getUserName());
                bean.setAnswerContent(item.getCharValue() != null ? item.getCharValue() : (item.getNumberValue() != null ? item.getNumberValue() : (item.getDateValue())));
            }
        }
        setList(list);
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public java.sql.Date getStart_time() {
        return start_time;
    }

    public void setStart_time(Date start_time) {
        this.start_time = start_time;
    }

    public Date getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Date end_time) {
        this.end_time = end_time;
    }
}
