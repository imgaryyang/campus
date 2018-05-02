package com.gzzm.oa.activite;

import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.*;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.HrefCell;
import net.cyan.crud.view.components.CButton;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;

/**
 * 活动查询列表
 *
 * @author fwj
 * @date 11-9-30
 */
@Service(url = "/oa/activite/query")
public class ActiviteQuery extends DeptOwnedQuery<Activite, Integer>
{
    @UserId
    private Integer userId;

    @Inject
    private ActiviteService service;

    /**
     * 活动名称
     */
    @Like
    private String title;

    @Lower(column = "endTime")
    private Date timeStart;

    @Upper(column = "startTime")
    private Date timeEnd;

    public ActiviteQuery()
    {
        addOrderBy(new OrderBy("createTime", OrderType.desc));
    }

    public Integer getUserId()
    {
        return userId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Date getTimeStart()
    {
        return timeStart;
    }

    public void setTimeStart(Date timeStart)
    {
        this.timeStart = timeStart;
    }

    public Date getTimeEnd()
    {
        return timeEnd;
    }

    public void setTimeEnd(Date timeEnd)
    {
        this.timeEnd = timeEnd;
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        return "state=1 and not exists m in memberLists : m.userId=:userId and sysdate()<=applyEndTime and publicity=1";
    }

    @Override
    @Forward(page = "/oa/activite/activiteinfo.ptl")
    public String show(Integer key, String forward) throws Exception
    {
        return super.show(key, forward);
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(false);

        view.addComponent("活动标题", "title");
        view.addComponent("时间", "timeStart", "timeEnd");
        view.addButton(Buttons.query());

        view.addColumn("活动标题", new HrefCell("title").setAction(Actions.show(null)));
        view.addColumn("活动地点", "activitePlace");
        view.addColumn("活动类型", "type.typeName");
        view.addColumn("发起人", "user.userName");
        view.addColumn("发起部门", "dept.deptName");
        view.addColumn("开始时间", "startTime");
        view.addColumn("结束时间", "endTime");

        view.addColumn("报名", new CButton("报名", "apply(${activiteId})"));

        view.importJs("/oa/activite/query.js");
        return view;
    }

    @NotSerialized
    public boolean isApplyable() throws Exception
    {
        return isApplyable(getEntity());
    }

    /**
     * 此活动是否允许报名
     *
     * @param activite 要判断的活动
     * @return 允许报名返回true，不允许返回false
     * @throws Exception 数据库查询数据错误
     */
    public boolean isApplyable(Activite activite) throws Exception
    {
        return activite.getState() == ActiviteState.issued &&
                activite.getApplyEndTime().getTime() <= System.currentTimeMillis() &&
                service.getDao().getActiviteMember(activite.getActiviteId(), userId) == null;
    }

    /**
     * 申请参加活动
     *
     * @param activiteId 活动ID
     * @throws Exception 数据库异常
     */
    @Service
    @ObjectResult
    public void apply(Integer activiteId) throws Exception
    {
        service.apply(activiteId, userId);
    }
}
