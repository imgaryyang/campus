package com.gzzm.safecampus.campus.wx.message;

import java.util.Date;
import java.util.Map;

/**
 * @author Neo
 * @date 2018/4/23 21:43
 */
public abstract class AbstractMessageProcessor implements MessageProcessor
{
    /**
     * 发送微信消息的用户openId
     */
    protected String openId;

    /**
     * 发送消息的时间
     */
    protected Date sendTime;

    public AbstractMessageProcessor()
    {
    }

    @Override
    public void process(Map<String, String> map) throws Exception
    {
        openId = map.get("FromUserName");
        sendTime = new Date(Long.valueOf(map.get("CreateTime")));
        process0(map);
    }

    public abstract void process0(Map<String, String> map) throws Exception;
}
