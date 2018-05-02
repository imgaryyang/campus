package com.gzzm.sms;

import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 消息接收表
 *
 * @author camel
 * @date 2010-11-22
 */
@Indexes({
        @Index(columns = {"userId", "state", "clientCode"})
})
@Entity(table = "SMSMSMO", keys = "smsId")
public class SmsMo
{
    /**
     * 主键
     */
    @Generatable(length = 16)
    private Long smsId;

    @ColumnDescription(type = "varchar(21)")
    private String phone;

    /**
     * 短信序列号
     */
    @ColumnDescription(type = "varchar(10)")
    private String serial;

    /**
     * 接收消息的用户
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
    private Date sendTime;

    /**
     * 接收时间
     */
    @IndexTimestamp
    private Date receiveTime;

    /**
     * 状态，0表示未阅读，1表示已阅读
     */
    @ColumnDescription(type = "number(1)", defaultValue = "0")
    private Integer state;

    /**
     * 客户端代码，给每个客户端分配一个代码，避免各客户端取数据冲突
     */
    @ColumnDescription(type = "varchar(20)")
    private String clientCode;

    public SmsMo()
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

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getSerial()
    {
        return serial;
    }

    public void setSerial(String serial)
    {
        this.serial = serial;
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

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof SmsMo))
            return false;

        SmsMo sms = (SmsMo) o;

        return smsId.equals(sms.smsId);
    }

    @Override
    public int hashCode()
    {
        return smsId.hashCode();
    }
}
