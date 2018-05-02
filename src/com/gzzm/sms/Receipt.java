package com.gzzm.sms;

import java.util.Date;

/**
 * 回执
 *
 * @author camel
 * @date 2010-11-30
 */
public class Receipt
{
    /**
     * 短信ID
     */
    private long smsId;

    /**
     * 短信序号
     */
    private String serial;

    /**
     * 接收人电话号码
     */
    private String phone;

    /**
     * 接收时间
     */
    private Date receiveTime;

    /**
     * 错误信息，与receiveTime互斥
     */
    private String error;

    public Receipt()
    {
    }

    public long getSmsId()
    {
        return smsId;
    }

    public void setSmsId(long smsId)
    {
        this.smsId = smsId;
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
}
