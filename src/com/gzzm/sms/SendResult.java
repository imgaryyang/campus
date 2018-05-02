package com.gzzm.sms;

import net.cyan.commons.annotation.*;

import java.util.List;

/**
 * 短信发送结果
 *
 * @author camel
 * @date 2010-11-30
 */
public class SendResult
{
    /**
     * 短信发送错误，如果没有错误则为空字符串
     */
    private String error = "";

    @ElementWrapper("items")
    @ElementName("element")
    private List<SendResultItem> items;

    public SendResult()
    {
    }

    public String getError()
    {
        return error;
    }

    public void setError(String error)
    {
        this.error = error;
    }

    public List<SendResultItem> getItems()
    {
        return items;
    }

    public void setItems(List<SendResultItem> items)
    {
        this.items = items;
    }
}
