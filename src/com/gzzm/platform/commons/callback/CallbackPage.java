package com.gzzm.platform.commons.callback;

import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 2015/3/6
 */
@Service
public class CallbackPage
{
    @Inject
    private CallbackService service;

    public CallbackPage()
    {
    }

    @ObjectResult
    @Service(url = "/callback", method = HttpMethod.post)
    public void callback(String ret, String id) throws Exception
    {
        service.callback(ret, id);
    }
}
