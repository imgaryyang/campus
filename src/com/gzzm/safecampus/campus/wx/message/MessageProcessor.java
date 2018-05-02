package com.gzzm.safecampus.campus.wx.message;

import java.util.Map;

/**
 * 微信消息处理器
 *
 * @author Neo
 * @date 2018/4/23 17:09
 */
public interface MessageProcessor
{
    public void process(Map<String, String> map) throws Exception;

    public String getMessageType();
}