package com.gzzm.platform.message.sms;

import java.util.Date;

/**
 * 短信回执，当用户查看或者回复了短信后，从短信服务器那里接收到的数据
 *
 * @author camel
 * @date 2010-5-23
 */
public class ReceiptInfo
{
    /**
     * 消息ID
     *
     * @see SmsLog#messageId
     */
    private String messageId;

    /**
     * 回复的手机的号码
     */
    private String phone;

    /**
     * 回执类型，0为短信接收回执，1为短信回复回执，2为短信错误回执
     */
    private int type;

    /**
     * 回复的内容，仅当type==1时有效
     */
    private String content;

    /**
     * 短信回复时间，如果服务器不支持则不需要设置此值，系统将使用轮询到此回执的时间代替之
     */
    private Date time;

    private String serial;

    public ReceiptInfo()
    {
    }

    public String getMessageId()
    {
        return messageId;
    }

    public void setMessageId(String messageId)
    {
        this.messageId = messageId;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public Date getTime()
    {
        return time;
    }

    public void setTime(Date time)
    {
        this.time = time;
    }

    public String getSerial()
    {
        return serial;
    }

    public void setSerial(String serial)
    {
        this.serial = serial;
    }
}
