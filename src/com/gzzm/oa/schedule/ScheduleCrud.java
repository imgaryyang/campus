package com.gzzm.oa.schedule;

import com.gzzm.platform.annotation.*;
import com.gzzm.platform.commons.GlobalConfig;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.exporters.ExportParameters;
import net.cyan.crud.view.*;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.*;

/**
 * 日程列表视图
 *
 * @author czf
 * @date 2010-3-10
 */
@Service(url = "/oa/schedule/list")
public class ScheduleCrud extends BaseNormalCrud<Schedule, Integer>
{

    @Inject
    private GlobalConfig globalConfig;

    @Inject
    private ScheduleDao dao;

    @UserId
    private Integer userId;

    @NotCondition
    private Integer deptId;

    /*
     * 日程标志，个人日程或部门日程
     */
    @NotCondition
    private ScheduleTag tag = ScheduleTag.user;

    @Like
    private String title;

    @Lower(column = "startTime")
    private Date time_start;

    @Upper(column = "startTime")
    private Date time_end;

    /**
     * 用优先级做查询条件
     */
    private Priority priority;

    /**
     * 用类型做查询条件
     */
    private Type type;

    @NotCondition
    private ScheduleState state;

    @AuthDeptIds
    private Collection<Integer> authDeptIds;

    private boolean readOnly;

    private String page;

    public ScheduleCrud()
    {
        setLog(true);
        addOrderBy("startTime", OrderType.desc);
    }

    @NotSerialized
    public Integer getUserId()
    {
        return userId;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public ScheduleTag getTag()
    {
        return tag;
    }

    public void setTag(ScheduleTag tag)
    {
        this.tag = tag;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
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

    public ScheduleState getState()
    {
        return state;
    }

    public void setState(ScheduleState state)
    {
        this.state = state;
    }

    public Priority getPriority()
    {
        return priority;
    }

    public void setPriority(Priority priority)
    {
        this.priority = priority;
    }

    public Type getType()
    {
        return type;
    }

    public void setType(Type type)
    {
        this.type = type;
    }

    public boolean isReadOnly()
    {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly)
    {
        this.readOnly = readOnly;
    }

    public String getPage()
    {
        return page;
    }

    public void setPage(String page)
    {
        this.page = page;
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        String condition;
        if (tag == ScheduleTag.user)
            condition =
                    "creator=:userId and tag=0 or :userId in (select userId from participants) and tag=1 and state<>3";
        else
            condition = "deptId=:deptId and tag=1";

        if (state != null)
        {
            switch (state)
            {
                case notStarted:
                    condition = "(" + condition + ") and state=0 and startTime>sysdate()";
                    break;
                case going:
                    condition = "(" + condition +
                            ") and (state=1 or state=0 and startTime<sysdate()) and endTime>sysdate()";
                    break;
                case closed:
                    condition = "(" + condition + ") and (state=2 or endTime<sysdate())";
                    break;
                case canceled:
                    condition = "(" + condition + ") and state=3";
                    break;
            }
        }

        return condition;
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        if ("list".equals(page))
            setPageSize(6);

        super.beforeShowList();

        if (tag == ScheduleTag.dept && deptId == null)
        {
            deptId = DeptOwnedCrudUtils.getDefaultDeptId(authDeptIds, this);
        }
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view;

        if (tag == ScheduleTag.user || authDeptIds != null
                && authDeptIds.size() == 1)
            view = new PageTableView(true);
        else
            view = new ComplexTableView(new AuthDeptDisplay(), "deptId", true);

        if (tag == ScheduleTag.user)
            view.setCheckable("${tag==null||tag.toString()=='user'}");

        view.addComponent("事件", "title");
        view.addComponent("开始时间", "time_start", "time_end");
        view.addComponent("状态", "state");

        if ("list".equals(getPage()) || "table".equals(getPage()))
        {
            view.addColumn("事件", "title").setAlign(Align.left);
        }
        else
        {
            FieldCell fieldCell = new FieldCell("title")
            {
                @Override
                public String display(Object entity) throws Exception
                {
                    Schedule schedule = (Schedule) entity;
                    if (schedule.getPriority() != null)
                    {
                        return "<div class='schedule_name'><img src='/oa/schedule/icons/"
                                + schedule.getPriority().getIcon() + "' title='"
                                + schedule.getPriority().getName() + "'>"
                                + schedule.getTitle() + "</div>";
                    }
                    else
                    {
                        return ((Schedule) entity).getTitle();
                    }
                }
            };
            fieldCell.setOrderable(true);
            view.addColumn("事件", fieldCell).setAlign(Align.left);
        }
        view.addColumn("开始时间", new FieldCell("startTime", "yyyy-MM-dd HH:mm"));
        view.addColumn("结束时间", new FieldCell("endTime", "yyyy-MM-dd HH:mm"));

        if ("exp".equals(getAction()))
        {
            view.addColumn("内容", "content");
            if (tag == ScheduleTag.dept)
                view.addColumn("参与人", "participantNames");
        }

        if (!"list".equals(getPage()) && !"table".equals(getPage()))
        {
            view.addColumn("状态", "state");
            view.addColumn("类型", "type");
        }

        if ("exp".equals(getAction()))
            view.addColumn("执行情况", "result");

        if ("list".equals(getPage()) || "table".equals(getPage()))
        {
            view.setPage("table");
            view.importJs("/oa/schedule/list.js");
        }
        else
        {
            view.addDefaultButtons();
            if (tag == ScheduleTag.dept)
                view.makeEditable();
            else
                view.enableShow();
            view.addButton(Buttons.export("xls"));
        }

        view.importCss("/oa/schedule/schedule.css");

        return view;
    }

    @Override
    public void initEntity(Schedule entity) throws Exception
    {
        super.initEntity(entity);

        if (tag == ScheduleTag.dept && entity.getDeptId() == null)
        {
            if (deptId == null)
                deptId = DeptOwnedCrudUtils.getDefaultDeptId(authDeptIds, this);
            entity.setDeptId(deptId);
        }
    }

    @Override
    @Forwards({
            @Forward(name = "user", page = "/oa/schedule/schedule_user.ptl"),
            @Forward(name = "dept", page = "/oa/schedule/schedule_dept.ptl")})
    public String add(String forward) throws Exception
    {
        super.add(forward);

        // 单位日程和个人日程使用不同的页面
        return tag.toString();
    }

    @Override
    @Forwards({
            @Forward(name = "user", page = "/oa/schedule/schedule_user.ptl"),
            @Forward(name = "dept", page = "/oa/schedule/schedule_dept.ptl"),
            @Forward(name = "show", page = "/oa/schedule/schedule_show.ptl")})
    public String show(Integer key, String forward) throws Exception
    {
        super.show(key, forward);

        // 在个人日程管理中查看单位日程时转向show页面，不允许修改日程
        if (getEntity().getTag() == ScheduleTag.dept)
            return tag == ScheduleTag.dept && !readOnly ? "dept" : "show";
        else
            return readOnly ? "show" : "user";
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        Schedule schedule = getEntity();

        schedule.setCreator(userId);
        schedule.setDeptId(deptId);
        schedule.setState(ScheduleState.notStarted);
        schedule.setTag(tag);

        init();

        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean beforeUpdate() throws Exception
    {
        super.beforeUpdate();

        Schedule schedule = getEntity();

        schedule.setCreator(userId);

        // 不允许修改日程的所属部门
        schedule.setDeptId(null);

        // 不许修改日程类型
        schedule.setTag(null);

        if (tag == ScheduleTag.dept && schedule.getParticipants() == null && StringUtils.isEmpty(schedule.getLinkId()))
            schedule.setParticipants(Collections.EMPTY_LIST);

        init();

        return true;
    }

    private void init()
    {
        Schedule schedule = getEntity();

        if (schedule.getRemindTime() == null)
        {
            schedule.setRemindTime(Null.Time);
        }

        if (schedule.getRemindType() == null)
            schedule.setRemindType(new ScheduleRemindType[0]);
    }

    @Override
    public void afterSave() throws Exception
    {
        super.afterSave();

        Schedule schedule = getEntity();

        if (schedule.getRemindType() != null && schedule.getRemindType().length > 0)
        {
            if (schedule.getRemindTime() != null)
            {
                Jobs.addJob(new ScheduleRemindJob(schedule.getScheduleId()), schedule.getRemindTime(),
                        "schedule_remind_" + schedule.getScheduleId());
            }

            if (schedule.getRemindTime1() != null)
            {
                Jobs.addJob(new ScheduleRemindJob(schedule.getScheduleId(), true), schedule.getRemindTime1(),
                        "schedule_remind_" + schedule.getScheduleId() + "_1");
            }
        }
    }

    @Override
    protected ExportParameters getExportParameters() throws Exception
    {
        return new ExportParameters("日程列表");
    }
}
