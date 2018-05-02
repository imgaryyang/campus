package com.gzzm.portal.survey;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.components.CButton;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;

/**
 * @author steq
 * @date 2011-7-5
 */
@Service(url = "/portal/survey/answer")
public class SurveyAnswerQuery extends DeptOwnedSubQuery<SurveyAnswer, Long>
{
    @Inject
    private SurveyDao dao;

    @Like
    private String name;

    private Integer surveyId;

    @Lower(column = "answerTime")
    private Date time_start;

    @Upper(column = "answerTime")
    private Date time_end;

    @Like
    private String userName;

    @NotSerialized
    private Survey survey;

    public SurveyAnswerQuery()
    {
        addOrderBy("answerTime", OrderType.desc);
    }

    public Integer getSurveyId()
    {
        return surveyId;
    }

    public void setSurveyId(Integer surveyId)
    {
        this.surveyId = surveyId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Date getTime_end()
    {
        return time_end;
    }

    public void setTime_end(Date time_end)
    {
        this.time_end = time_end;
    }

    public Date getTime_start()
    {
        return time_start;
    }

    public void setTime_start(Date time_start)
    {
        this.time_start = time_start;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public Survey getSurvey() throws Exception
    {
        if (survey == null)
        {
            SurveyAnswer entity = getEntity();
            if (entity != null)
                survey = entity.getSurvey();
            else
                survey = dao.getSurvey(surveyId);
        }

        return survey;
    }

    @Override
    protected String getTopOwnerField()
    {
        return "survey.station";
    }

    @Override
    @Forward(page = "/portal/survey/answer.ptl")
    public String show(Long key, String forward) throws Exception
    {
        return super.show(key, forward);
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(false);
        view.setTitle("调查回复列表-" + dao.getSurvey(surveyId).getSurveyName());

        view.addComponent("姓名", "userName");
        view.addComponent("查询时间", "time_start", "time_end");

        view.addColumn("姓名", "userName");
        view.addColumn("性别", "sex");
        view.addColumn("年龄", "age");
        view.addColumn("电话", "phone");
        view.addColumn("地址", "address").setWidth("150");
        view.addColumn("职业", "occupation");
        view.addColumn("IP", "ip");
        view.addColumn("填写时间", "answerTime");

        view.addColumn("查看内容", new CButton("查看", "showContent(${answerId})"));

        view.addButton(Buttons.query());

        view.importJs("/portal/survey/answer.js");
        return view;
    }
}


