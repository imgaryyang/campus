package com.gzzm.oa.leaveword;

import com.gzzm.platform.organ.*;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

import java.util.Date;


/**
 * 发布留言信息
 *
 * @author hsy
 * @date 12-3-27
 */
@Entity(table = "OALEAVEWORD", keys = "leaveWordId")
public class LeaveWord
{
    /**
     * 留言ID
     */
    @Generatable(length = 9)
    @Require
    private Integer leaveWordId;

    /**
     * 发布留言的用户ID
     */
    @Require
    private Integer userId;

    private User user;

    /**
     * 留言发布时间
     */
    private Date leaveTime;
    /**
     * 留言内容
     */
    @ColumnDescription(type = "clob")
    @Require
    private String content;
    /**
     * 留言标题
     */
    @ColumnDescription(type = "varchar(400)")
    @Require
    private String title;

    private Integer deptId;

    private Dept dept;

    private Date createTime;

    private LeaveWordState state;

    @Require
    private Boolean anonymous;

    public LeaveWord()
    {
    }

    public Integer getLeaveWordId()
    {
        return leaveWordId;
    }

    public void setLeaveWordId(Integer leaveWordId)
    {
        this.leaveWordId = leaveWordId;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }


    public Date getLeaveTime()
    {
        return leaveTime;
    }

    public void setLeaveTime(Date leaveTime)
    {
        this.leaveTime = leaveTime;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public LeaveWordState getState()
    {
        return state;
    }

    public void setState(LeaveWordState state)
    {
        this.state = state;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
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

    public Boolean isAnonymous()
    {
        return anonymous;
    }

    public void setAnonymous(Boolean anonymous)
    {
        this.anonymous = anonymous;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof LeaveWord))
            return false;

        LeaveWord leaveWord = (LeaveWord) o;

        return leaveWordId.equals(leaveWord.leaveWordId);
    }

    @Override
    public int hashCode()
    {
        return leaveWordId.hashCode();
    }
}
