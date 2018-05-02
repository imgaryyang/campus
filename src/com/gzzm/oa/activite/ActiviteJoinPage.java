package com.gzzm.oa.activite;

import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.transaction.Transactional;
import net.cyan.crud.*;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.HrefCell;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;

/**
 * 当前用户参加活动信息
 *
 * @author fwj
 * @date 11-9-30
 */
@Service(url = "/oa/activite/join")
public class ActiviteJoinPage extends BaseQueryCrud<ActiviteMember, Integer>
{
    @Inject
    private ActiviteService service;

    @UserId
    private Integer userId;

    @Like("activite.title")
    private String title;

    @Lower(column = "activite.endTime")
    private Date timeStart;

    @Upper(column = "activite.startTime")
    private Date timeEnd;

    /**
     * 状态
     */
    private MemberState state;

    /**
     * 活动状态做查询条件
     */
    @In("activite.state")
    private ActiviteState[] activiteStates;

    public ActiviteJoinPage()
    {
        addOrderBy(new OrderBy("applyTime", OrderType.desc));
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

    public MemberState getState()
    {
        return state;
    }

    public void setState(MemberState state)
    {
        this.state = state;
    }

    public ActiviteState[] getActiviteStates()
    {
        return activiteStates;
    }

    public void setActiviteStates(ActiviteState[] activiteStates)
    {
        this.activiteStates = activiteStates;
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        String s = "userId=:userId and activite.state<>0";

        if (state == MemberState.invite)
        {
            s += "and sysdate()<=activite.applyEndTime";
        }

        return s;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(false);

        view.addComponent("活动标题", "title");
        view.addComponent("时间", "timeStart", "timeEnd");
        view.addComponent("状态", "state");
        view.addButton(Buttons.query());

        view.addColumn("活动标题", new HrefCell("activite.title").setAction("showActivite(${activiteId})"));
        view.addColumn("活动地点", "activite.activitePlace");
        view.addColumn("发起人", "activite.user.userName");
        view.addColumn("发起部门", "activite.dept.deptName");
        view.addColumn("开始时间", "activite.startTime");
        view.addColumn("申请/邀请时间", "applyTime");
        view.addColumn("活动状态", "activite.state");
        view.addColumn("状态", "state");

        view.addColumn("是否参加", new ConditionComponent().add("activite.state.name()=='issued'&&state.name()=='invite'",
                new CUnion(new CButton("参加", "participate(${memberId})"),
                        new CButton("不参加", "noParticipate(${memberId})")))
                .add("activite.state.name()=='issued'&&state.name()=='participation'",
                        new CButton("不参加", "noParticipate(${memberId})"))
                .add("activite.state.name()=='issued'&&state.name()=='nonparticipation'",
                        new CButton("参加", "participate(${memberId})")));

        view.importJs("/oa/activite/join.js");
        return view;
    }

    /**
     * 参加
     *
     * @param memberId 成员ID
     * @throws Exception 数据库跟新操作异常
     */
    @Service
    @Transactional
    public void participate(Integer memberId) throws Exception
    {
        ActiviteMember member = service.getDao().getActiviteMember(memberId);
        member.setState(MemberState.participation);
        member.setMemberId(memberId);
        service.getDao().update(member);

        service.updateSchedule(member.getActivite());
        service.sendMessage(member);
    }

    /**
     * 不参加
     *
     * @param memberId 成员ID
     * @throws Exception 数据库跟新操作异常
     */
    @Service
    @Transactional
    public void noParticipate(Integer memberId) throws Exception
    {
        ActiviteMember member = service.getDao().getActiviteMember(memberId);
        member.setState(MemberState.nonparticipation);
        member.setMemberId(memberId);
        service.getDao().update(member);

        service.updateSchedule(member.getActivite());
        service.sendMessage(member);
    }
}
