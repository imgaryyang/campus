package com.gzzm.oa.activite;

import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.*;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.HrefCell;
import net.cyan.crud.view.components.CButton;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.List;


/**
 * @author lfx
 * @date 11-9-29
 */
@Service(url = "/oa/activite/crud")
public class ActiviteCrud extends DeptOwnedNormalCrud<Activite, Integer>
{
    @Inject
    private ActiviteService service;

    @UserId
    private Integer userId;

    @Like
    private String title;

    /**
     * 用状态做查询条件
     */
    private ActiviteState state;

    @Lower(column = "endTime")
    private Date timeStart;

    @Upper(column = "startTime")
    private Date timeEnd;

    /**
     * 用活动类型做查询条件
     */
    private Integer typeId;

    /**
     * 活动类型列表
     */
    @NotSerialized
    private List<ActiviteType> activiteTypes;

    /**
     * 预算年份列表
     */
    @NotSerialized
    private List<ActiviteBudget> activiteBudgets;

    /**
     * 创建时邀请参加的用户ID
     */
    private List<Integer> inviteUserIds;

    public ActiviteCrud()
    {
        addOrderBy(new OrderBy("startTime", OrderType.desc));
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public ActiviteState getState()
    {
        return state;
    }

    public void setState(ActiviteState state)
    {
        this.state = state;
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

    public Integer getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Integer typeId)
    {
        this.typeId = typeId;
    }

    public List<Integer> getInviteUserIds()
    {
        return inviteUserIds;
    }

    public void setInviteUserIds(List<Integer> inviteUserIds)
    {
        this.inviteUserIds = inviteUserIds;
    }

    @Select(field = {"entity.typeId", "typeId"})
    public List<ActiviteType> getActiviteTypes() throws Exception
    {
        if (activiteTypes == null)
            activiteTypes = service.getDao().getActiviteTypes();
        return activiteTypes;
    }

    @Select(field = {"entity.activiteBudgetId", "activiteBudgetId"})
    public List<ActiviteBudget> getActiviteBudgets() throws Exception
    {
        //从这里过滤部门，在dao里面加多个where deptId
        if (activiteBudgets == null)
            activiteBudgets = service.getDao().getActiviteBudgets(getDefaultDeptId());

        return activiteBudgets;
    }

    @Override
    public String getOrderField()
    {
        return null;
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        setDefaultDeptId();
    }

    @Override
    public void initEntity(Activite entity) throws Exception
    {
        super.initEntity(entity);

        getEntity().setState(ActiviteState.created);
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        getEntity().setCreator(userId);
        getEntity().setCreateTime(new Date(new java.util.Date().getTime()));
        return super.beforeInsert();
    }

    @Override
    public void afterInsert() throws Exception
    {
        super.afterInsert();

        if (inviteUserIds != null)
        {
            Integer activiteId = getEntity().getActiviteId();
            int index = 0;
            for (Integer userId : inviteUserIds)
            {
                ActiviteMember member = new ActiviteMember();
                member.setActiviteId(activiteId);
                member.setApplyTime(new java.util.Date());
                member.setState(userId.equals(this.userId) ? MemberState.participation : MemberState.invite);
                member.setOrderId(index++);
                member.setUserId(userId);

                service.getDao().add(member);
            }
        }
    }

    @Override
    public boolean beforeUpdate() throws Exception
    {
        super.beforeUpdate();

        if (getEntity().isPublicity() == null)
            getEntity().setPublicity(false);

        if (getEntity().isNotify() == null)
            getEntity().setNotify(false);

        return true;
    }

    @Override
    public void afterSave() throws Exception
    {
        super.afterSave();

        service.updateSchedule(service.getActivite(getEntity().getActiviteId()));
    }

    @Override
    @Forward(page = "/oa/activite/activite.ptl")
    public String add(String forward) throws Exception
    {
        //保存发起部门
        setDefaultDeptId();

        return super.add(forward);
    }

    @Override
    @Forward(page = "/oa/activite/activite.ptl")
    public String show(Integer key, String forward) throws Exception
    {
        return super.show(key, forward);
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = getAuthDeptIds() == null || getAuthDeptIds().size() > 1 ?
                new ComplexTableView(new AuthDeptDisplay(), "deptId", true) : new PageTableView(true);

        view.addComponent("活动标题", "title");
        view.addComponent("时间", "timeStart", "timeEnd");
        view.addComponent("类型", "typeId");
        view.addComponent("状态", "state");

        view.addColumn("活动标题", new HrefCell("title").setAction(Actions.show(null)));
        view.addColumn("活动地点", "activitePlace");
        view.addColumn("开始时间", "startTime");
        view.addColumn("结束时间", "endTime");
        view.addColumn("报名截止时间", "applyEndTime");
        view.addColumn("状态", "state");
        view.addColumn("发起人", "user.userName");
        view.addColumn("参与人员管理", new CButton("参与人员管理", "showMembers(${activiteId})"));

        view.addButton(Buttons.query());
        view.addButton(Buttons.delete());

        view.importJs("/oa/activite/activite.js");

        return view;
    }

    /**
     * 发布
     *
     * @param activiteId 要发布的活动id
     * @throws Exception 数据库插入日常活动或更新日常活动异常
     */
    @Service(method = HttpMethod.post)
    @ObjectResult
    public void issue(Integer activiteId) throws Exception
    {
        service.issue(activiteId);
    }

    /**
     * 活动结束
     *
     * @param activiteId 活动ID
     * @throws Exception 数据库设置活动结束状态异常
     */
    @Service(url = "/oa/activite/activieclose/{$0}")
    public String endPtl(Integer activiteId) throws Exception
    {
        setEntity(service.getDao().load(Activite.class, activiteId));
        return "/oa/activite/activiteend.ptl";
    }

    /**
     * 活动结束
     *
     * @throws Exception 数据库设置活动结束状态异常
     */
    @Service(method = HttpMethod.post)
    @ObjectResult
    public String end1() throws Exception
    {
        try
        {
            getEntity().setState(ActiviteState.end);
            service.getDao().save(getEntity());
        }
        catch (Exception e)
        {

            e.printStackTrace();
            return "fail";

        }
        return "ok";
    }

    /**
     * 活动结束
     *
     * @param activiteId 活动ID
     * @throws Exception 数据库设置活动结束状态异常
     */
    @Service(method = HttpMethod.post)
    @ObjectResult
    public void end(Integer activiteId) throws Exception
    {
        service.end(activiteId);
    }

    /**
     * 取消活动
     *
     * @param activiteId 要取消的活动的ID
     * @throws Exception 数据库更新活动取消状态异常
     */
    @Service(method = HttpMethod.post)
    @ObjectResult
    public void cancel(Integer activiteId) throws Exception
    {
        Activite activite = new Activite();
        activite.setActiviteId(activiteId);
        activite.setState(ActiviteState.canceled);

        service.getDao().update(activite);

        service.removeSchedule(activiteId);
    }

    @Override
    public void afterDelete(Integer key, boolean exists) throws Exception
    {
        super.afterDelete(key, exists);

        service.removeSchedule(key);
    }

    @Override
    public void afterDeleteAll() throws Exception
    {
        super.afterDeleteAll();

        if (getKeys() != null)
        {
            for (Integer key : getKeys())
                service.removeSchedule(key);
        }
    }
}
