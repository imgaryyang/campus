package com.gzzm.platform.message.sms;

import java.util.Date;

/**
 * 短消息回复，用于做SmsReplyProcessor接口的参数使用
 *
 * @author camel
 * @date 2010-6-3
 */
public class SmsReply
{
    private String code;

    private String content;

    private String phone;

    private Date time;

    /**
     * 回执类型，0为短信接收回执，1为短信回复回执，2为短信错误回执
     */
    private int type;

    public SmsReply()
    {
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
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

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }
}
