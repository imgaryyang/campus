package com.gzzm.oa.leaveword;

import com.gzzm.platform.annotation.AuthDeptIds;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.Null;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author hsy
 * @date 12-3-27
 */
@Service(url = "/oa/leaveword/query")
public class LeaveWordCrud extends BaseNormalCrud<LeaveWord, Integer>
{
    @Inject
    private LeaveWordDao dao;

    /**
     * 时间段查询开始
     */
    @Lower(column = "leaveTime")
    private java.sql.Date leaveTime_start;
    /**
     * 时间段查询结束
     */
    @Upper(column = "leaveTime")
    private java.sql.Date leaveTime_end;

    private LeaveWordQueryType type;

    /**
     * 获取当前用户信息
     */
    @Inject
    private UserOnlineInfo userOnlineInfo;

    /**
     * 获取当前部门的id
     */
    @AuthDeptIds
    private Collection<Integer> authDeptIds;

    /**
     * 用户名
     */
    @Like("user.userName")
    private String userName;

    private LeaveWordState[] state;

    public LeaveWordCrud()
    {
        addOrderBy("leaveTime", OrderType.desc);
        addOrderBy("createTime", OrderType.desc);
    }

    public java.sql.Date getLeaveTime_start()
    {
        return leaveTime_start;
    }

    public void setLeaveTime_start(java.sql.Date leaveTime_start)
    {
        this.leaveTime_start = leaveTime_start;
    }

    public Date getLeaveTime_end()
    {
        return leaveTime_end;
    }

    public void setLeaveTime_end(java.sql.Date leaveTime_end)
    {
        this.leaveTime_end = leaveTime_end;
    }

    public LeaveWordQueryType getType()
    {
        return type;
    }

    public void setType(LeaveWordQueryType type)
    {
        this.type = type;
    }

    @NotCondition
    @NotSerialized
    public Integer getUserId()
    {
        return userOnlineInfo.getUserId();
    }

    public Collection<Integer> getAuthDeptIds()
    {
        return authDeptIds;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public LeaveWordState[] getState()
    {
        return state;
    }

    public void setState(LeaveWordState[] state)
    {
        this.state = state;
    }

    @Override
    public void initEntity(LeaveWord entity) throws Exception
    {
        super.initEntity(entity);
    }

    public boolean beforeInsert() throws Exception
    {
        if (authDeptIds == null || authDeptIds.isEmpty())
            getEntity().setDeptId(userOnlineInfo.getDeptId());
        else
            getEntity().setDeptId(authDeptIds.iterator().next());

        getEntity().setCreateTime(new java.util.Date());
        getEntity().setUserId(getUserId());
        getEntity().setState(LeaveWordState.draft);

        return true;
    }

    @Override
    @Forward(page = "/oa/leaveword/leaveword.ptl")
    public String add(String forward) throws Exception
    {
        return super.add(forward);
    }

    @Service
    @Transactional
    public boolean commit(Integer leaveWordId) throws Exception
    {
        dao.lockLeaveWord(leaveWordId);

        LeaveWord leaveWord = dao.getLeaveWord(leaveWordId);

        if (leaveWord.getState() == null || leaveWord.getState() == LeaveWordState.draft)
        {
            leaveWord.setLeaveWordId(leaveWordId);
            leaveWord.setState(LeaveWordState.noAccepted);
            leaveWord.setLeaveTime(new Date());

            dao.update(leaveWord);

            return true;
        }
        else
        {
            return false;
        }
    }

    @Service
    @Transactional
    public boolean withdraw(Integer leaveWordId) throws Exception
    {
        dao.lockLeaveWord(leaveWordId);

        LeaveWord leaveWord = dao.getLeaveWord(leaveWordId);

        if (leaveWord.getState() == LeaveWordState.noAccepted)
        {
            leaveWord.setLeaveWordId(leaveWordId);
            leaveWord.setState(LeaveWordState.draft);
            leaveWord.setLeaveTime(Null.Date);

            dao.update(leaveWord);

            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    @Forwards({
            @Forward(name = "show", page = "/oa/leaveword/leaveword_show.ptl"),
            @Forward(name = "update", page = "/oa/leaveword/leaveword.ptl")})
    public String show(Integer key, String forward) throws Exception
    {
        super.show(key, forward);

        if (type == LeaveWordQueryType.allLeaveWord || getEntity().getState() != LeaveWordState.draft)
            return "show";
        else
            return "update";
    }

    @Override
    @Forward(page = "/oa/leaveword/leaveword.ptl")
    public String duplicate(Integer key, String forward) throws Exception
    {
        return super.duplicate(key, forward);
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        if (type == LeaveWordQueryType.allLeaveWord && authDeptIds != null)
            return "deptId in ?authDeptIds and state>0";
        else if (type == LeaveWordQueryType.myLeaveWord)
            return "userId=:userId";
        return null;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(type == LeaveWordQueryType.myLeaveWord);

        view.addComponent("留言时间", "leaveTime_start", "leaveTime_end");
        view.addComponent("标题", "title");

        if (type == LeaveWordQueryType.allLeaveWord)
        {
            view.addButton(Buttons.query());
            view.addColumn("留言人", "anonymous!=null&&anonymous?'匿名':user.userName");
            view.addColumn("标题", "title").setAutoExpand(true);
            view.addColumn("提交时间", "leaveTime");
            view.addColumn("状态", "state");

            if (containsState(LeaveWordState.noAccepted))
            {
                view.addColumn("接收", new ConditionComponent().add("state.name()=='noAccepted'",
                        new CButton("接收", "accept(${leaveWordId})")));
            }

            view.makeEditable("", "查看");
        }
        else
        {
            view.addColumn("标题", "title");
            view.addColumn("填写时间", "createTime");
            view.addColumn("提交时间", "leaveTime");
            view.addColumn("状态", "state");

            view.addColumn("提交/撤回", new ConditionComponent().add("state==null||state.name()=='draft'",
                    new CButton("提交", "commit(${leaveWordId})"))
                    .add("state.name()=='noAccepted'", new CButton("撤回", "withdraw(${leaveWordId})")));

            view.defaultInit(false);
        }

        view.importJs("/oa/leaveword/leaveword.js");

        view.addButton(Buttons.export("xls"));
        return view;
    }

    public boolean containsState(LeaveWordState state)
    {
        if (this.state == null)
            return true;

        for (LeaveWordState state1 : this.state)
        {
            if (state1 == state)
                return true;
        }

        return false;
    }
}
