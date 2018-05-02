package com.gzzm.ods.exchange;

import com.gzzm.ods.exchange.back.BackPaper;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 退回公文，每个退回操作均生成一条记录
 *
 * @author camel
 * @date 2010-9-17
 */
@Entity(table = "ODBACK", keys = "backId")
public class Back
{
    /**
     * 退回ID，主键，每个退回操作的ID都不相同
     */
    @Generatable(length = 10)
    private Long backId;

    /**
     * 收文ID
     */
    @Index
    private Long receiveId;

    private ReceiveBase receiveBase;

    /**
     * 发文ID，仅当本系统发过来的公文有效，外系统发过来的公文为空
     */
    private Long sendId;

    private Send send;

    /**
     * 退回时间
     */
    private Date backTime;

    /**
     * 退回公文的用户的ID
     */
    private Integer backUserId;

    @ToOne("BACKUSERID")
    private User backUser;

    /**
     * 退回的原因
     */
    private char[] reason;

    /**
     * 被退回公文的部门
     */
    @Index
    private Integer deptId;

    private Dept dept;

    @ColumnDescription(defaultValue = "0", nullable = false)
    private BackState state;

    @ColumnDescription(defaultValue = "1")
    private Boolean notified;

    /**
     * 当需要将此公文同步到第三方系统时，记录同步状态，false为未同步，true为已同步
     */
    @ColumnDescription(defaultValue = "0")
    private Boolean syned;

    /**
     * 退文表单类型
     */
    private Integer paperId;

    @NotSerialized
    private BackPaper paper;

    public Back()
    {
    }

    public Long getBackId()
    {
        return backId;
    }

    public void setBackId(Long backId)
    {
        this.backId = backId;
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

    public BackState getState()
    {
        return state;
    }

    public void setState(BackState state)
    {
        this.state = state;
    }

    public Long getReceiveId()
    {
        return receiveId;
    }

    public void setReceiveId(Long receiveId)
    {
        this.receiveId = receiveId;
    }

    public ReceiveBase getReceiveBase()
    {
        return receiveBase;
    }

    public void setReceiveBase(ReceiveBase receiveBase)
    {
        this.receiveBase = receiveBase;
    }

    public Long getSendId()
    {
        return sendId;
    }

    public void setSendId(Long sendId)
    {
        this.sendId = sendId;
    }

    public Send getSend()
    {
        return send;
    }

    public void setSend(Send send)
    {
        this.send = send;
    }

    public Date getBackTime()
    {
        return backTime;
    }

    public void setBackTime(Date backTime)
    {
        this.backTime = backTime;
    }

    public Integer getBackUserId()
    {
        return backUserId;
    }

    public void setBackUserId(Integer backUserId)
    {
        this.backUserId = backUserId;
    }

    public User getBackUser()
    {
        return backUser;
    }

    public void setBackUser(User backUser)
    {
        this.backUser = backUser;
    }

    public char[] getReason()
    {
        return reason;
    }

    public void setReason(char[] reason)
    {
        this.reason = reason;
    }

    public Boolean getNotified()
    {
        return notified;
    }

    public void setNotified(Boolean notified)
    {
        this.notified = notified;
    }

    public Boolean getSyned()
    {
        return syned;
    }

    public void setSyned(Boolean syned)
    {
        this.syned = syned;
    }

    public Integer getPaperId()
    {
        return paperId;
    }

    public void setPaperId(Integer paperId)
    {
        this.paperId = paperId;
    }

    public BackPaper getPaper()
    {
        return paper;
    }

    public void setPaper(BackPaper paper)
    {
        this.paper = paper;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof Back))
            return false;

        Back back = (Back) o;

        return backId.equals(back.backId);
    }

    @Override
    public int hashCode()
    {
        return backId.hashCode();
    }
}
