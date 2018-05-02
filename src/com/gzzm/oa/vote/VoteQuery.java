package com.gzzm.oa.vote;

import com.gzzm.platform.annotation.AuthDeptIds;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.*;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.*;

/**
 * 查看投票Crud
 *
 * @author db
 * @date 11-12-12
 */
@Service(url = "/oa/vote/VoteQuery")
public class VoteQuery extends BaseQueryCrud<Vote, Integer>
{
    @Inject
    private VoteDao dao;

    @Like
    private String title;

    @Lower(column = "endTime")
    private Date timeStart;

    @Upper(column = "startTime")
    private Date timeEnd;

    /**
     * 自动注入用户的相关信息
     */
    @Inject
    private UserOnlineInfo userOnlineInfo;

    @AuthDeptIds
    private Collection<Integer> authDeptIds;

    /**
     * 0为全部，1为进行中，2为已结束
     */
    private int state;

    /**
     * 0为全部，1为未超时，2为已超时
     */
    private int timeState;

    /**
     * 投票状态，0为全部，1为未投票，2未已投票
     */
    private int voteState;

    private Integer typeId;

    private VoteType type;

    /**
     * 是否为当前信息上报
     */
    private boolean current;

    private VoteRecord record;

    public VoteQuery()
    {
        addOrderBy("startTime", OrderType.desc);
        addOrderBy("createTime", OrderType.desc);
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Date getTimeEnd()
    {
        return timeEnd;
    }

    public void setTimeEnd(Date timeEnd)
    {
        this.timeEnd = timeEnd;
    }

    public Date getTimeStart()
    {
        return timeStart;
    }

    public void setTimeStart(Date timeStart)
    {
        this.timeStart = timeStart;
    }

    public int getState()
    {
        return state;
    }

    public void setState(int state)
    {
        this.state = state;
    }

    public int getTimeState()
    {
        return timeState;
    }

    public void setTimeState(int timeState)
    {
        this.timeState = timeState;
    }

    public int getVoteState()
    {
        return voteState;
    }

    public void setVoteState(int voteState)
    {
        this.voteState = voteState;
    }

    public Integer getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Integer typeId)
    {
        this.typeId = typeId;
    }

    public boolean isCurrent()
    {
        return current;
    }

    public void setCurrent(boolean current)
    {
        this.current = current;
    }

    protected VoteType getType() throws Exception
    {
        if (type == null && typeId != null)
            type = dao.getVoteType(typeId);

        return type;
    }

    @NotSerialized
    public String getActionName() throws Exception
    {
        VoteType type = getType();

        if (type == null)
            return "投票";

        return type.getActionName();
    }

    @NotCondition
    @NotSerialized
    public Collection<Integer> getDeptIds()
    {
        return userOnlineInfo.getParentDeptIds();
    }

    @NotSerialized
    public Integer getUserId()
    {
        return userOnlineInfo.getUserId();
    }

    @NotSerialized
    public Collection<Integer> getAuthDeptIds()
    {
        if (authDeptIds == null)
            authDeptIds = Collections.singleton(userOnlineInfo.getDeptId());
        return authDeptIds;
    }

    public boolean isAlarm(Vote vote) throws Exception
    {
        if (vote.getAlarmTime() == null ||
                DateUtils.addDate(vote.getAlarmTime(), 1).getTime() > System.currentTimeMillis())
            return false;

        VoteRecord record = getRecord(vote);

        return record == null || record.getState() == VoteRecordState.uncommitted ||
                DateUtils.addDate(vote.getAlarmTime(), 1).getTime() < record.getVoteTime().getTime();
    }

    public boolean isCommitted(Vote vote) throws Exception
    {
        VoteRecord record = getRecord(vote);

        return record != null && record.getState() == VoteRecordState.committed;
    }

    protected VoteScope getVoteScope(Vote vote)
    {
        if (vote.getScopeType() == VoteScopeType.DEPT)
        {
            for (VoteScope scope : vote.getVoteScopes())
            {
                if (authDeptIds == null && userOnlineInfo.getDeptId().equals(scope.getObjectId()) ||
                        authDeptIds != null && authDeptIds.contains(scope.getObjectId()))
                {
                    return scope;
                }
            }
        }
        else if (vote.getScopeType() == VoteScopeType.USER)
        {
            for (VoteScope scope : vote.getVoteScopes())
            {
                if (scope.getType() == VoteScopeType.USER && userOnlineInfo.getUserId().equals(scope.getObjectId()))
                    return scope;
            }

            List<Integer> parentDeptIds = userOnlineInfo.getParentDeptIds();

            for (VoteScope scope : vote.getVoteScopes())
            {
                if (scope.getType() == VoteScopeType.DEPT && parentDeptIds.contains(scope.getObjectId()))
                    return scope;
            }
        }
        else if (vote.getScopeType() == VoteScopeType.ALL)
        {
            for (VoteScope scope : vote.getVoteScopes())
            {
                if (scope.getType() == VoteScopeType.USER && userOnlineInfo.getUserId().equals(scope.getObjectId()))
                    return scope;
            }

            for (VoteScope scope : vote.getVoteScopes())
            {
                if (authDeptIds == null && userOnlineInfo.getDeptId().equals(scope.getObjectId()) ||
                        authDeptIds != null && authDeptIds.contains(scope.getObjectId()))
                {
                    return scope;
                }
            }
        }

        return null;
    }

    public VoteRecord getRecord(Vote vote) throws Exception
    {
        if (record != null && record.getVoteId().equals(vote.getVoteId()))
            return record;


        VoteScopeType scopeType = vote.getScopeType();
        if (scopeType == VoteScopeType.ALL)
        {
            VoteScope scope = getVoteScope(vote);
            scopeType = scope == null ? VoteScopeType.USER : scope.getType();
        }

        if (scopeType == VoteScopeType.DEPT)
        {
            for (VoteScope scope : vote.getVoteScopes())
            {
                if (authDeptIds == null && userOnlineInfo.getDeptId().equals(scope.getObjectId()) ||
                        authDeptIds != null && authDeptIds.contains(scope.getObjectId()))
                {
                    record = dao.getVoteRecordByDeptId(vote.getVoteId(), scope.getObjectId(), null);
                    break;
                }
            }
        }
        else
        {
            record = dao.getVoteRecordByUserId(vote.getVoteId(), userOnlineInfo.getUserId(), null);
        }

        return record;
    }

    @Override
    protected void beforeQuery() throws Exception
    {
        super.beforeQuery();

        if (current)
        {
            state = 1;
            voteState = 1;
        }
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(false);

        // 添加查询条件
        view.addComponent("标题", "title");

        if (!current)
        {
            view.addComponent("状态", new CCombox("state", new Object[]{
                    new KeyValue<String>("0", "全部"),
                    new KeyValue<String>("1", "进行中"),
                    new KeyValue<String>("2", "已结束"),
            }).setNullable(false));
        }

        view.addComponent(getActionName() + "状态", new CCombox("voteState", new Object[]{
                new KeyValue<String>("0", "全部"),
                new KeyValue<String>("1", "未" + getActionName()),
                new KeyValue<String>("2", "已" + getActionName()),
        }).setNullable(false));

        view.addComponent("时间", "timeStart", "timeEnd");


        // 添加显示列

        //超时图标
        view.addColumn("", new ConditionComponent().add("crud$.isAlarm(this)", new CImage(Buttons.getIcon("alarm"))))
                .setWidth("30").setLocked(true);

        view.addColumn("标题", new HrefCell("title").setAction("forwardVote(${voteId})"));
        view.addColumn("说明", "intro").setWidth("300").setWrap(true);
        view.addColumn("状态", "end?'已结束':'进行中'").setWidth("50");
        view.addColumn(getActionName() + "状态", "crud$.isCommitted(this)?'已" +
                getActionName() + "':'未" + getActionName() + "'").setWidth("70").setAlign(Align.center);
        view.addColumn("开始时间", "startTime").setWidth("80");
        view.addColumn("要求" + getActionName() + "时间", "alarmTime").setWidth("100");
        view.addColumn("截止时间", "endTime").setWidth("80");

        // 添加查询按钮
        view.addButton(Buttons.query());

        //导入JS
        view.importJs("/oa/vote/vote.js");

        return view;
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        StringBuilder buffer = new StringBuilder("valid=1 and sysdate()>=startTime and (scopeType=1 and " +
                "(exists vs in voteScopes : (vs.type=0 and vs.objectId in :deptIds or vs.type=1 and " +
                "vs.objectId=:userId)) or scopeType=0 and (exists vs in voteScopes : (vs.type=0 and " +
                "vs.objectId in :authDeptIds)) or scopeType=2 and (exists vs in voteScopes :" +
                " (vs.type=0 and vs.objectId in :authDeptIds or vs.objectId=:userId)))");

        if (state == 1)
        {
            buffer.append(" and addDay(endTime,1)>sysdate()");
        }
        else if (state == 2)
        {
            buffer.append(" and addDay(endTime,1)<=sysdate()");
        }

        if (voteState > 0)
        {
            buffer.append(" and (select r from VoteRecord r where r.voteId=vote.voteId and (r.userId=:userId" +
                    " and (exists vs in voteScopes : (vs.type=0 and vs.objectId in :deptIds or vs.type=1 and vs.objectId=:userId))" +
                    " or (scopeType=0 and exists vs in voteScopes : (vs.type=0 and vs.objectId in :authDeptIds and r.deptId=vs.objectId))) and r.state=1)");

            if (voteState == 1)
            {
                buffer.append(" is empty");
            }
            else if (voteState == 2)
            {
                buffer.append(" is not empty");
            }
        }

        return buffer.toString();
    }
}
