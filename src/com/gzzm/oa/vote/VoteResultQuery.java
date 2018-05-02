package com.gzzm.oa.vote;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.KeyValue;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;

/**
 * 投票结果Crud
 *
 * @author db
 * @date 11-12-12
 */
@Service(url = "/oa/vote/VoteResultQuery")
public class VoteResultQuery extends DeptOwnedQuery<Vote, Integer>
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
     * 0为全部，1为进行中，2为已结束
     */
    private int state;

    private Integer typeId;

    private VoteType type;

    public VoteResultQuery()
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

    public int getState()
    {
        return state;
    }

    public void setState(int state)
    {
        this.state = state;
    }

    public Integer getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Integer typeId)
    {
        this.typeId = typeId;
    }

    protected VoteType getType() throws Exception
    {
        if (type == null && typeId != null)
            type = dao.getVoteType(typeId);

        return type;
    }

    public String getActionName() throws Exception
    {
        VoteType type = getType();

        if (type == null)
            return "投票";

        return type.getActionName();
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = getAuthDeptIds() == null || getAuthDeptIds().size() > 1 ?
                new ComplexTableView(new AuthDeptDisplay(), "deptId", false) : new PageTableView(false);

        // 添加查询条件
        view.addComponent("标题", "title");
        view.addComponent("状态", new CCombox("state", new Object[]{
                new KeyValue<String>("0", "全部"),
                new KeyValue<String>("1", "进行中"),
                new KeyValue<String>("2", "已结束"),
        }).setNullable(false));
        view.addComponent("时间", "timeStart", "timeEnd");


        // 添加显示列
        view.addColumn("标题", "title");
        view.addColumn("创建人", "user.userName").setWidth("70");
        view.addColumn("状态", "end?'已结束':'进行中'").setWidth("50");
        view.addColumn("开始时间", "startTime").setWidth("80");
        view.addColumn("结束时间", "endTime").setWidth("80");
        view.addColumn("查看结果", new CHref("查看结果").
                setAction("showRecordItems(${voteId},${anonymous.toString()})")).setWidth("60");
        view.addColumn(getActionName() + "统计", new CHref("统计结果").setAction("showResult(${voteId})")).setWidth("60");

        // 添加查询按钮
        view.defaultInit();

        // 引入JS文件
        view.importJs("/oa/vote/vote.js");

        return view;
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        StringBuilder buffer = new StringBuilder("valid=1 and sysdate()>=startTime");

        if (state == 1)
        {
            buffer.append(" and addDay(endTime,1)>sysdate()");
        }
        else if (state == 2)
        {
            buffer.append(" and addDay(endTime,1)<=sysdate()");
        }

        return buffer.toString();
    }
}
