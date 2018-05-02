package com.gzzm.sms;

import java.util.Date;

/**
 * 一条短信的信息，用于短信接收接口中使用
 *
 * @author camel
 * @date 2010-11-29
 */
public class SmsItem
{
    /**
     * 短信序号
     */
    private String serial;

    /**
     * 来信号码
     */
    private String phone;

    /**
     * 短信内容
     */
    private String content;

    /**
     * 发送时间
     */
    private Date sendTime;

    /**
     * 接收时间
     */
    private Date receiveTime;

    public SmsItem()
    {
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

    public Date getReceiveTime()
    {
        return receiveTime;
    }

    public void setReceiveTime(Date receiveTime)
    {
        this.receiveTime = receiveTime;
    }
}
