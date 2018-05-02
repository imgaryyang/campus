package com.gzzm.safecampus.campus.wx.message;

import java.util.Map;

/**
 * @author Neo
 * @date 2018/4/23 20:54
 */
public class TextMessageProcessor extends SimpleMessageProcessor
{
    public static final String TEXT = "text";

    public TextMessageProcessor()
    {
    }

    @Override
    public String getMessageType()
    {
        return TEXT;
    }

    @Override
    public void process0(Map<String, String> map) throws Exception
    {
        System.out.println("process text message");
    }
}
