package com.gzzm.platform.message.sms;

import java.io.Serializable;

/**
 * 短消息接收者
 *
 * @author camel
 * @date 2010-5-23
 */
public class SmsReceiver implements Serializable
{
    private static final long serialVersionUID = -4407916953942703573L;

    /**
     * 接收者的电话
     */
    private String phone;

    /**
     * 接收者的姓名
     */
    private String name;

    /**
     * 接收者如果是一个系统用户的话，对应系统用户的id
     */
    private Integer userId;

    public SmsReceiver(String phone, String name)
    {
        this.phone = phone;
        this.name = name;
    }

    public SmsReceiver(String phone, String name, Integer userId)
    {
        this.phone = phone;
        this.name = name;
        this.userId = userId;
    }

    public SmsReceiver(Integer userId)
    {
        this.userId = userId;
    }

    public String getPhone()
    {
        return phone;
    }

    void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getName()
    {
        return name;
    }

    void setName(String name)
    {
        this.name = name;
    }

    public Integer getUserId()
    {
        return userId;
    }
}
