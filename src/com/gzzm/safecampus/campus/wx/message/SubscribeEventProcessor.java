package com.gzzm.safecampus.campus.wx.message;

import com.gzzm.safecampus.campus.wx.tag.TagService;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

import java.util.Map;

/**
 * @author Neo
 * @date 2018/4/23 21:04
 */
public class SubscribeEventProcessor extends EventMessageProcessor
{
    @Inject
    private static Provider<TagService> tagServiceProvider;

    public static final String SUBSCRIBE = "subscribe";

    public SubscribeEventProcessor()
    {
    }

    @Override
    public void process0(Map<String, String> map) throws Exception
    {
        //如该微信用户已认证身份，则根据身份给该微信用户打上标签
        tagServiceProvider.get().taggingUser(openId);
    }

    @Override
    public String getMessageType()
    {
        return SUBSCRIBE;
    }
}
