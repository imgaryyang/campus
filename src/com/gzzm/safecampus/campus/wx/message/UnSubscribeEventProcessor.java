package com.gzzm.safecampus.campus.wx.message;

import java.util.Map;

/**
 * @author Neo
 * @date 2018/4/23 21:22
 */
public class UnSubscribeEventProcessor extends EventMessageProcessor
{
    private static final String UNSUBSCRIBE = "unsubscribe";

    public UnSubscribeEventProcessor()
    {
    }

    @Override
    public void process0(Map<String, String> map) throws Exception
    {
        System.out.println("process unsubscribe message");
    }

    @Override
    public String getMessageType()
    {
        return UNSUBSCRIBE;
    }
}
