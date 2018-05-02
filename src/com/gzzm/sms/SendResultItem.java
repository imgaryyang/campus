package com.gzzm.sms;

/**
 * 短信发送结果项
 *
 * @author camel
 * @date 2010-11-30
 */
public class SendResultItem
{
    /**
     * 电话号码
     */
    private String phone;

    /**
     * 短信ID
     */
    private long smsId;

    /**
     * 错误信息，如果发送错误则记录错误信息，否则为空字符串
     */
    private String error = "";

    public SendResultItem()
    {
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public long getSmsId()
    {
        return smsId;
    }

    public void setSmsId(long smsId)
    {
        this.smsId = smsId;
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
