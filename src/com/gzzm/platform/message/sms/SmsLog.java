package com.gzzm.platform.message.sms;

import com.gzzm.platform.organ.*;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 用于记录系统发送出去的短信
 *
 * @author camel
 * @date 2010-5-23
 */
@Entity(table = "PFSMSLOG", keys = "smsId")
@Indexes(@Index(
        columns = {"PHONE", "MESSAGEID"}
))
public class SmsLog
{
    /**
     * 短信id，主键，由uuid生成
     */
    @Generatable(name = "uuid", length = 32)
    private String smsId;

    /**
     * 消息ID，有具体的短信接口提供，用于将系统中记录的短信和短信服务的短信一一对应上
     *
     * @see ReceiptInfo#messageId
     */
    @ColumnDescription(type = "varchar(100)")
    private String messageId;

    /**
     * 消息code，对应Message中的messageCode，可能为null，用于处理处理消息反馈
     *
     * @see com.gzzm.platform.message.Message#code
     */
    @ColumnDescription(type = "varchar(100)")
    private String messageCode;

    /**
     * 接收电话号码
     */
    private String phone;

    /**
     * 短信的内容
     */
    private String content;

    /**
     * 发送时间
     */
    @Index
    private Date sendTime;

    /**
     * 定时发送时间
     */
    private Date fixedTime;

    /**
     * 接收时间
     */
    private Date receiveTime;

    /**
     * 状态
     */
    private SmsState state;

    /**
     * 接收短信用户ID，可能为空
     */
    @Index
    private Integer userId;

    private User user;

    private Integer sender;

    @ToOne("SENDER")
    private User sendUser;

    /**
     * 发送短信的部门，可能为空
     */
    private Integer fromDeptId;

    @ToOne("FROMDEPTID")
    private Dept fromDept;

    /**
     * 接收短信的部门，可能为空
     */
    private Integer toDeptId;

    @ToOne("TODEPTID")
    private Dept toDept;

    /**
     * 短信序列号
     */
    @ColumnDescription(type = "varchar(10)")
    private String serial;

    public SmsLog()
    {
    }

    public String getSmsId()
    {
        return smsId;
    }

    public void setSmsId(String smsId)
    {
        this.smsId = smsId;
    }

    public String getMessageId()
    {
        return messageId;
    }

    public void setMessageId(String messageId)
    {
        this.messageId = messageId;
    }

    public String getMessageCode()
    {
        return messageCode;
    }

    public void setMessageCode(String messageCode)
    {
        this.messageCode = messageCode;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
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

    public Date getReceiveTime()
    {
        return receiveTime;
    }

    public void setReceiveTime(Date receiveTime)
    {
        this.receiveTime = receiveTime;
    }

    public SmsState getState()
    {
        return state;
    }

    public void setState(SmsState state)
    {
        this.state = state;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public Integer getFromDeptId()
    {
        return fromDeptId;
    }

    public void setFromDeptId(Integer fromDeptId)
    {
        this.fromDeptId = fromDeptId;
    }

    public Integer getToDeptId()
    {
        return toDeptId;
    }

    public void setToDeptId(Integer toDeptId)
    {
        this.toDeptId = toDeptId;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public Integer getSender()
    {
        return sender;
    }

    public void setSender(Integer sender)
    {
        this.sender = sender;
    }

    public User getSendUser()
    {
        return sendUser;
    }

    public void setSendUser(User sendUser)
    {
        this.sendUser = sendUser;
    }

    public Dept getFromDept()
    {
        return fromDept;
    }

    public void setFromDept(Dept fromDept)
    {
        this.fromDept = fromDept;
    }

    public Dept getToDept()
    {
        return toDept;
    }

    public void setToDept(Dept toDept)
    {
        this.toDept = toDept;
    }

    public String getSerial()
    {
        return serial;
    }

    public void setSerial(String serial)
    {
        this.serial = serial;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof SmsLog))
            return false;

        SmsLog smsLog = (SmsLog) o;

        return smsId.equals(smsLog.smsId);
    }

    @Override
    public int hashCode()
    {
        return smsId.hashCode();
    }
}
