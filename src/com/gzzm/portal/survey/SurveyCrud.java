package com.gzzm.portal.survey;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.login.Authoritys;
import com.gzzm.portal.commons.StationOwnedCrud;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.*;

/**
 * @author steq
 * @date 2011-6-16
 */
@Service(url = "/portal/survey/survey")
public class SurveyCrud extends StationOwnedCrud<Survey, Integer>
{
    @Inject
    private SurveyDao dao;

    @Like
    private String surveyName;

    @Lower(column = "createdTime")
    private Date time_start;

    @Upper(column = "createdTime")
    private Date time_end;

    private Integer typeId;

    public SurveyCrud()
    {
        setLog(true);
        addOrderBy("createdTime", OrderType.desc);
    }

    public String getSurveyName()
    {
        return surveyName;
    }

    public void setSurveyName(String surveyName)
    {
        this.surveyName = surveyName;
    }

    public Date getTime_start()
    {
        return time_start;
    }

    public void setTime_start(Date time_start)
    {
        this.time_start = time_start;
    }

    public Date getTime_end()
    {
        return time_end;
    }

    public void setTime_end(Date time_end)
    {
        this.time_end = time_end;
    }

    public Integer getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Integer typeId)
    {
        this.typeId = typeId;
    }

    @NotSerialized
    @Select(field = {"typeId", "entity.typeId"})
    public List<SurveyType> getTypes() throws Exception
    {
        Integer stationId = null;

        if (getEntity() != null)
            stationId = getEntity().getStationId();

        if (stationId == null)
            return Collections.emptyList();

        return dao.getSurveyTypes(stationId);
    }

    @Override
    public String getOrderField()
    {
        return null;
    }

    /**
     * 设置一些默认生成值
     * 创建人、状态、创建时间
     *
     * @return 保存成功
     * @throws Exception 数据库异常
     */
    @Override
    public boolean beforeInsert() throws Exception
    {
        getEntity().setCreator(userOnlineInfo.getUserId());
        getEntity().setState(SurveyState.NOPUBLISHED);
        getEntity().setCreatedTime(new java.util.Date());

        return true;
    }

    /**
     * 发布
     * 使状态由0变成1
     *
     * @throws Exception 数据库异常
     */
    @Service(method = HttpMethod.post)
    public void publish() throws Exception
    {
        checkeKeys();

        dao.publish(getKeys());
    }

    /**
     * 取消发布
     * 使状态从1变成0
     *
     * @throws Exception 数据库异常
     */
    @Service(method = HttpMethod.post)
    public void unPublish() throws Exception
    {
        checkeKeys();

        dao.unPublish(getKeys());
    }

    @Override
    @Service
    public String duplicate(Integer key, String forward) throws Exception
    {
        String result = super.duplicate(key, forward);

        Survey entity = getEntity();

        entity.setCreatedTime(new java.util.Date());
        entity.setCreator(userOnlineInfo.getUserId());
        entity.setState(SurveyState.NOPUBLISHED);

        dao.add(entity);

        setNew$(false);

        return result;
    }

    @Override
    public Survey clone(Survey entity) throws Exception
    {
        return entity.cloneSurvey();
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addComponent("调查标题", "surveyName");

        if (!isOneStationOnly())
            view.addComponent("所属网站", "stationId").setProperty("onchange", "stationChange()");

        view.addComponent("创建时间", "time_start", "time_end");

        view.addColumn("标题", "surveyName");
        view.addColumn("创建人", "user.userName").setWidth("70");
        view.addColumn("创建时间", "createdTime").setWidth("125");
        view.addColumn("类型", "type.typeName").setWidth("125");

        if (!isOneStationOnly())
            view.addColumn("所属网站", "station.stationName").setWidth("140");

        view.addColumn("状态", "state").setWidth("50");

        if (CrudAuths.isModifiable())
            view.addColumn("问题列表", new CButton("问题列表", "showQuestions(${surveyId})")).setWidth("70");

        if (Authoritys.hasAuth("result"))
        {
            view.addColumn("调查结果", new CButton("查看结果", "showResult(${surveyId})")).setWidth("70");
            view.addColumn("回复列表", new CButton("查看列表", "showAnswers(${surveyId})")).setWidth("70");
        }

        view.defaultInit();

        if (CrudAuths.isModifiable())
        {
            view.addButton("发布", "publish()");
            view.addButton("取消发布", "unPublish()");
        }

        view.importJs("/portal/survey/survey.js");

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("标题", "surveyName");

        if (!isOneStationOnly())
        {
            view.addComponent("所属网站", "stationId").setProperty("text",
                    getEntity().getStation() == null ? "" : getEntity().getStation().getStationName())
                    .setProperty("onchange", "stationChange();");
        }

        view.addComponent("类型", "typeId");
        view.addComponent("开始时间", "startTime");
        view.addComponent("结束时间", "endTime");
        view.addComponent("限制次数", "limitCount");
        view.addComponent("限制时间", "limitTime").setProperty("datatype", "minute");

        view.addComponent("说明", new CTextArea("remark"));

        view.importJs("/portal/survey/survey.js");

        view.addDefaultButtons();

        return view;
    }
}