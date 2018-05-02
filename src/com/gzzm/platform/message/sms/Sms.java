package com.gzzm.platform.message.sms;

import com.gzzm.platform.organ.*;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 个人发送的短信
 *
 * @author camel
 * @date 2010-5-23
 */
@Entity(table = "PFSMS", keys = "smsId")
public class Sms
{
    @Generatable(length = 16)
    private Long smsId;

    /**
     * 发送者用户ID
     *
     * @see #user
     */
    private Integer userId;

    /**
     * 短信内容
     */
    private String content;

    /**
     * 发送的部门，当短信以部门名义发送时为此部门的部门ID，否则为null
     */
    @Index
    private Integer deptId;

    /**
     * 短信发送时间
     */
    @Index
    private Date sendTime;

    /**
     * 定时时间
     */
    private Date fixedTime;

    /**
     * 通过发送者的id关联User对象
     *
     * @see #userId
     */
    private User user;

    /**
     * 关联部门对象
     *
     * @see #deptId
     */
    private Dept dept;

    /**
     * 是否删除，1为已删除，0为未删除，删除短信的时候不正在删除，做假删除标记
     */
    @ColumnDescription(type = "number(1)", defaultValue = "0")
    private Integer deleteTag;

    @ColumnDescription(nullable = false, defaultValue = "1")
    private Boolean sign;

    @ColumnDescription(nullable = false, defaultValue = "1")
    private Boolean requireReply;

    public Sms()
    {
    }

    public Long getSmsId()
    {
        return smsId;
    }

    public void setSmsId(Long smsId)
    {
        this.smsId = smsId;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Date getSendTime()
    {
        return sendTime;
    }

    public void setSendTime(Date sendTime)
    {
        this.sendTime = sendTime;
    }

    public Date getFixedTime()
    {
        return fixedTime;
    }

    public void setFixedTime(Date fixedTime)
    {
        this.fixedTime = fixedTime;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public Dept getDept()
    {
        return dept;
    }

    public void setDept(Dept dept)
    {
        this.dept = dept;
    }

    public Integer getDeleteTag()
    {
        return deleteTag;
    }

    public void setDeleteTag(Integer deleteTag)
    {
        this.deleteTag = deleteTag;
    }

    public Boolean isSign()
    {
        return sign;
    }

    public void setSign(Boolean sign)
    {
        this.sign = sign;
    }

    public Boolean getRequireReply()
    {
        return requireReply;
    }

    public void setRequireReply(Boolean requireReply)
    {
        this.requireReply = requireReply;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof Sms))
            return false;

        Sms sms = (Sms) o;

        return smsId.equals(sms.smsId);
    }

    @Override
    public int hashCode()
    {
        return smsId.hashCode();
    }
}
