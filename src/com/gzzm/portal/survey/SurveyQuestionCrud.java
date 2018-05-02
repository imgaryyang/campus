package com.gzzm.portal.survey;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.annotation.Like;
import net.cyan.nest.annotation.Inject;

/**
 * @author wxj
 * @date 2011-6-16
 */
@Service(url = "/portal/survey/question")
public class SurveyQuestionCrud extends DeptOwnedSubCrud<SurveyQuestion, Integer>
{
    @Inject
    private SurveyDao dao;

    /**
     * 调查信息di
     */
    private Integer surveyId;

    @Like
    private String questionName;

    public SurveyQuestionCrud()
    {
        setLog(true);
    }

    public Integer getSurveyId()
    {
        return surveyId;
    }

    public void setSurveyId(Integer surveyId)
    {
        this.surveyId = surveyId;
    }

    public String getQuestionName()
    {
        return questionName;
    }

    public void setQuestionName(String questionName)
    {
        this.questionName = questionName;
    }

    @Override
    protected String getTopOwnerField()
    {
        return "survey.station";
    }

    @Override
    public int getOrderFieldLength()
    {
        return 5;
    }

    @Override
    public void initEntity(SurveyQuestion entity) throws Exception
    {
        super.initEntity(entity);

        entity.setSurveyId(surveyId);
    }


    @Override
    @Forward(page = "/portal/survey/question.ptl")
    public String show(Integer key, String forward) throws Exception
    {
        return super.show(key, forward);
    }

    @Override
    @Forward(page = "/portal/survey/question.ptl")
    public String add(String forward) throws Exception
    {
        return super.add(forward);
    }

    @Override
    @Forward(page = "/portal/survey/question.ptl")
    public String duplicate(Integer key, String forward) throws Exception
    {
        super.duplicate(key, forward);

        SurveyQuestion entity = getEntity();

        entity.setSurveyId(getSurveyId());
        entity.setOrderId(getOrderValue(true));

        dao.add(entity);

        setNew$(false);

        return null;
    }

    @Override
    public SurveyQuestion clone(SurveyQuestion entity) throws Exception
    {
        return entity.cloneQuestion();
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        Survey survey = dao.getSurvey(getSurveyId());

        view.setTitle("调查问题管理-" + survey.getSurveyName());

        view.addColumn("标题", "questionName");
        view.addColumn("数据类型", "dataType");
        view.addColumn("是否必填", "required");
        view.addColumn("链接目标", "url");

        view.defaultInit();

        view.addButton(Buttons.sort());

        return view;
    }
}
