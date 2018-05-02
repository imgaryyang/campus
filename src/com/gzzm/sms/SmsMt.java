package com.gzzm.sms;

import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 消息发送表
 *
 * @author camel
 * @date 2010-11-15
 */
@Entity(table = "SMSMSMT", keys = "smsId")
@Indexes({@Index(columns = {"PHONE", "MESSAGEID"}),
        @Index(columns = {"STATE", "SENDTIME", "USERID"})
})
public class SmsMt
{
    /**
     * 主键
     */
    @Generatable(length = 16)
    private Long smsId;

    /**
     * 消息ID，由网关生成
     */
    @Index
    @ColumnDescription(type = "varchar(50)")
    private String messageId;

    /**
     * 短信序列号
     */
    @ColumnDescription(type = "varchar(10)")
    private String serial;

    /**
     * 目标手机号码
     */
    @ColumnDescription(type = "varchar(21)")
    private String phone;

    /**
     * 发送消息的用户
     */
    private Integer userId;

    private SmsUser user;

    /**
     * 发送消息的网关
     */
    private Integer gatewayId;

    private Gateway gateway;

    /**
     * 短信内容
     */
    @FullText
    private char[] content;

    /**
     * 发送时间
     */
    @IndexTimestamp
    private Date sendTime;

    /**
     * 接收时间
     */
    private Date receiveTime;

    /**
     * 发送失败的错误
     */
    private String error;

    /**
     * 状态，0表示未接收，1表示已接收或发送错误未阅读，2表示已阅读，-1表示发送中，4表示服务已停止
     */
    @ColumnDescription(type = "number(1)", defaultValue = "0")
    private Integer state;

    /**
     * 客户端代码，给每个客户端分配一个代码，避免各客户端取数据冲突
     */
    @ColumnDescription(type = "varchar(20)")
    private String clientCode;

    private GatewayType gatewayType;

    public SmsMt()
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

    public String getMessageId()
    {
        return messageId;
    }

    public void setMessageId(String messageId)
    {
        this.messageId = messageId;
    }

    public String getSerial()
    {
        return serial;
    }

    public void setSerial(String serial)
    {
        this.serial = serial;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public SmsUser getUser()
    {
        return user;
    }

    public void setUser(SmsUser user)
    {
        this.user = user;
    }

    public Integer getGatewayId()
    {
        return gatewayId;
    }

    public void setGatewayId(Integer gatewayId)
    {
        this.gatewayId = gatewayId;
    }

    public Gateway getGateway()
    {
        return gateway;
    }

    public void setGateway(Gateway gateway)
    {
        this.gateway = gateway;
    }

    public char[] getContent()
    {
        return content;
    }

    public void setContent(char[] content)
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

    public Date getReceiveTime()
    {
        return receiveTime;
    }

    public void setReceiveTime(Date receiveTime)
    {
        this.receiveTime = receiveTime;
    }

    public String getError()
    {
        return error;
    }

    public void setError(String error)
    {
        this.error = error;
    }

    public Integer getState()
    {
        return state;
    }

    public void setState(Integer state)
    {
        this.state = state;
    }

    public String getClientCode()
    {
        return clientCode;
    }

    public void setClientCode(String clientCode)
    {
        this.clientCode = clientCode;
    }

    public GatewayType getGatewayType()
    {
        return gatewayType;
    }

    public void setGatewayType(GatewayType gatewayType)
    {
        this.gatewayType = gatewayType;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof SmsMt))
            return false;

        SmsMt sms = (SmsMt) o;

        return smsId.equals(sms.smsId);
    }

    @Override
    public int hashCode()
    {
        return smsId.hashCode();
    }
}
