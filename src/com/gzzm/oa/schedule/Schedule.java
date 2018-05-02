package com.gzzm.oa.schedule;

import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.StringUtils;
import net.cyan.commons.validate.annotation.*;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * 日程信息实体对象，对应数据库的日程信息表
 *
 * @author czf
 * @date 2010-3-10
 */
@Entity(table = "OASCHEDULE", keys = "scheduleId")
public class Schedule
{
    /**
     * 日程ID，长度11位，系统生成
     */
    @Generatable(length = 11)
    private Integer scheduleId;

    /**
     * 部门ID
     */
    @Index
    private Integer deptId;

    /**
     * 部门
     */
    private Dept dept;

    /**
     * 事件
     */
    @ColumnDescription(type = "varchar(250)", nullable = false)
    @Require
    private String title;

    /**
     * 详细内容
     */
    private char[] content;

    /**
     * 地点
     */
    @ColumnDescription(type = "varchar(250)")
    private String address;

    /**
     * 开始时间
     */
    @Require
    private Date startTime;

    /**
     * 结束时间
     */
    @LargerThan("startTime")
    @Require
    private Date endTime;

    /**
     * 优先级
     */
    @ColumnDescription(type = "number(2)")
    private Priority priority;

    /**
     * 类型
     */
    @ColumnDescription(type = "number(2)")
    private Type type;

    /**
     * 状态
     */
    @ColumnDescription(defaultValue = "0")
    private ScheduleState state;

    /**
     * 创建人或最后一个修改人，个人日程时为日程拥有者
     */
    @Index
    private Integer creator;

    /**
     * 创建者的用户信息，通过CREATOR字段关联User对象
     */
    @ToOne("CREATOR")
    @NotSerialized
    private User user;

    /**
     * 提醒方式
     */
    private ScheduleRemindType[] remindType;

    /**
     * 提醒时间
     */
    @LessThan("startTime")
    private Date remindTime;

    /**
     * 另外一次提醒时间，允许配置两次提醒时间
     */
    @LessThan("startTime")
    private Date remindTime1;

    /**
     * 日程标志，0为个人日程，1为部门日程
     */
    private ScheduleTag tag;

    /**
     * 执行情况
     */
    @ColumnDescription(type = "varchar(2000)")
    private String result;

    /**
     * 参与者，与用户的多对多关系，表示参与此会议的用户的集合，仅当此日常为部门日程时有效，个人日程此字段无效
     */
    @ManyToMany(table = "OASCHEDULEPARTICIPANTS")
    @NotSerialized
    private List<User> participants;

    @ColumnDescription(type = "varchar(20)")
    private String linkId;

    /**
     * 最后一次提醒时间
     */
    private Date lastRemindTime;

    /**
     * 最后一次提醒时间
     */
    private Date lastRemindTime1;

    public Schedule()
    {
    }

    public Integer getScheduleId()
    {
        return scheduleId;
    }

    public void setScheduleId(Integer scheduleId)
    {
        this.scheduleId = scheduleId;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Dept getDept()
    {
        return dept;
    }

    public void setDept(Dept dept)
    {
        this.dept = dept;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public char[] getContent()
    {
        return content;
    }

    public void setContent(char[] content)
    {
        this.content = content;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public Date getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public Date getEndTime()
    {
        return endTime;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
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

    public ScheduleState getState()
    {
        if (state != null)
        {
            long time = System.currentTimeMillis();
            if (state == ScheduleState.notStarted)
            {
                if (endTime != null && endTime.getTime() <= time)
                    state = ScheduleState.closed;
                else if (startTime != null && startTime.getTime() <= time)
                    state = ScheduleState.going;
            }
            else if (state == ScheduleState.going)
            {
                if (endTime != null && endTime.getTime() <= time)
                    state = ScheduleState.closed;
            }
        }
        return state;
    }

    public void setState(ScheduleState state)
    {
        this.state = state;
    }

    public Integer getCreator()
    {
        return creator;
    }

    public void setCreator(Integer creator)
    {
        this.creator = creator;
    }

    public ScheduleRemindType[] getRemindType()
    {
        return remindType;
    }

    public void setRemindType(ScheduleRemindType[] remindType)
    {
        this.remindType = remindType;
    }

    public Date getRemindTime()
    {
        return remindTime;
    }

    public void setRemindTime(Date remindTime)
    {
        this.remindTime = remindTime;
    }

    public Date getRemindTime1()
    {
        return remindTime1;
    }

    public void setRemindTime1(Date remindTime1)
    {
        this.remindTime1 = remindTime1;
    }

    public ScheduleTag getTag()
    {
        return tag;
    }

    public void setTag(ScheduleTag tag)
    {
        this.tag = tag;
    }

    public String getResult()
    {
        return result;
    }

    public void setResult(String result)
    {
        this.result = result;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public List<User> getParticipants()
    {
        return participants;
    }

    public void setParticipants(List<User> participants)
    {
        this.participants = participants;
    }

    @NotSerialized
    public String getParticipantNames()
    {
        return participants != null ? StringUtils.concat(participants, ",") : null;
    }

    public String getLinkId()
    {
        return linkId;
    }

    public void setLinkId(String linkId)
    {
        this.linkId = linkId;
    }

    public Date getLastRemindTime()
    {
        return lastRemindTime;
    }

    public void setLastRemindTime(Date lastRemindTime)
    {
        this.lastRemindTime = lastRemindTime;
    }

    public Date getLastRemindTime1()
    {
        return lastRemindTime1;
    }

    public void setLastRemindTime1(Date lastRemindTime1)
    {
        this.lastRemindTime1 = lastRemindTime1;
    }

    @Transient
    public String getLink()
    {
        return getLinkId();
    }

    /**
     * linkid之前用的是link，之后由于在某些数据库下出问题改为linkid，但是之前很多代码调用了setLink
     * 为了不修改这些代码，保留setLink方法
     *
     * @param link 链接id
     */
    public void setLink(String link)
    {
        setLinkId(link);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof Schedule))
            return false;

        Schedule that = (Schedule) o;

        return scheduleId.equals(that.scheduleId);

    }

    @Override
    public int hashCode()
    {
        return scheduleId.hashCode();
    }
}