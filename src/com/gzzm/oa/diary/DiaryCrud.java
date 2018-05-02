package com.gzzm.oa.diary;

import com.gzzm.platform.annotation.*;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.*;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.Align;

import java.util.Collection;

/**
 * @author wxj
 * @date 11-10-27
 */
@Service(url = "/oa/diary/diaryQuery")
public class DiaryCrud extends BaseNormalCrud<Diary, Integer>
{
    /**
     * 获取当前用户ID
     */
    @UserId
    @NotCondition
    private Integer userId;

    /**
     * 时间段查询的开始
     */
    @Lower(column = "diaryTime")
    private java.sql.Date time_start;

    /**
     * 时间段查询的结束
     */
    @Upper(column = "diaryTime")
    private java.sql.Date time_end;

    /**
     * 日志所在的日期
     */
    private java.sql.Date diaryTime;

    private DiaryQueryType type;

    @Like("user.userName")
    private String userName;

    @AuthDeptIds
    private Collection<Integer> authDeptIds;

    public DiaryCrud()
    {
        addOrderBy(new OrderBy("diaryTime", OrderType.desc));
        addOrderBy(new OrderBy("createTime", OrderType.asc));
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

    public java.sql.Date getTime_start()
    {
        return time_start;
    }

    public void setTime_start(java.sql.Date time_start)
    {
        this.time_start = time_start;
    }

    public java.sql.Date getTime_end()
    {
        return time_end;
    }

    public void setTime_end(java.sql.Date time_end)
    {
        this.time_end = time_end;
    }

    public DiaryQueryType getType()
    {
        return type;
    }

    public void setType(DiaryQueryType type)
    {
        this.type = type;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public java.sql.Date getDiaryTime()
    {
        return diaryTime;
    }

    public void setDiaryTime(java.sql.Date diaryTime)
    {
        this.diaryTime = diaryTime;
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        if (type == DiaryQueryType.allDiary && authDeptIds != null)
            return "userId in (select userId from UserDept where deptId in :authDeptIds) ";
        else if (type == DiaryQueryType.myDiary)
            return "userId=?userId";
        return null;
    }

    @Override
    public void initEntity(Diary entity) throws Exception
    {
        super.initEntity(entity);

        if (diaryTime != null)
        {
            getEntity().setDiaryTime(diaryTime);
        }
        else
        {
            getEntity().setDiaryTime(new java.sql.Date(System.currentTimeMillis()));
        }
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(true);

        view.addComponent("日志时间", "time_start", "time_end");

        if (type != DiaryQueryType.myDiary)
        {
            view.addComponent("用户名", "userName");
            view.addButton(Buttons.query());
            view.addColumn("用户", "user.userName").setWidth("200");
            view.addColumn("日志标题", "title").setAutoExpand(true);
            view.addColumn("日志时间", "diaryTime");
            view.addColumn("填写时间", "createTime");
            view.addColumn("工作量", "amount").setAlign(Align.center);
            view.makeEditable("", "查看");
        }
        else
        {
            view.addColumn("日志标题", "title").setWidth("200");
            view.addColumn("日志时间", "diaryTime");
            view.addColumn("填写时间", "createTime");
            view.addColumn("工作量", "amount").setAlign(Align.center);
            view.addColumn("日志内容", "content").setAutoExpand(true);
            view.defaultInit();
        }

        view.addButton(Buttons.export("xls"));
        return view;
    }

    @Override
    @Forward(page = "/oa/diary/diary.ptl")
    public String add(String forward) throws Exception
    {
        return super.add(forward);
    }

    @Override
    @Forwards({
            @Forward(name = "show", page = "/oa/diary/diary_show.ptl"),
            @Forward(name = "update", page = "/oa/diary/diary.ptl")})
    public String show(Integer key, String forward) throws Exception
    {
        super.show(key, forward);

        if (type == DiaryQueryType.allDiary)
            return "show";
        else
            return "update";
    }

    public boolean beforeInsert() throws Exception
    {
        getEntity().setCreateTime(new java.util.Date());
        getEntity().setUserId(getUserId());
        return true;
    }

    @Override
    @Forward(page = "/oa/diary/diary.ptl")
    public String duplicate(Integer key, String forward) throws Exception
    {
        return super.duplicate(key, forward);
    }
}
