package com.gzzm.platform.commons.callback;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.message.comet.CometService;
import net.cyan.arachne.RequestContext;
import net.cyan.commons.cache.Cache;
import net.cyan.nest.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author camel
 * @date 2015/3/6
 */
@Injectable(singleton = true)
public final class CallbackService
{
    @Inject
    private Cache<String, Callback> cache;

    @Inject
    private CometService cometService;

    public CallbackService()
    {
    }

    public String call(String js, String method, Object... args)
    {
        return call(js, method, 1000 * 60 * 5, RequestContext.getContext().getRequest(), args);
    }

    public String call(String js, String method, HttpServletRequest request, Object... args)
    {
        return call(js, method, 1000 * 60 * 5, request, args);
    }

    public String call(String js, String method, long timeout, Object... args)
    {
        return call(js, method, timeout, RequestContext.getContext().getRequest(), args);
    }

    public String call(String js, String method, long timeout, HttpServletRequest request, Object... args)
    {
        final String[] result = new String[1];

        call(js, method, new Callback()
        {
            @Override
            public void call(String ret) throws Exception
            {
                synchronized (result)
                {
                    result[0] = ret;

                    result.notify();
                }
            }
        }, request, args);

        synchronized (result)
        {
            try
            {
                result.wait(timeout);
            }
            catch (InterruptedException ex)
            {
                //被打断，返回null
            }
        }

        return result[0];
    }

    public void call(String js, String method, Callback callback, Object... args)
    {
        call(js, method, callback, RequestContext.getContext().getRequest(), args);
    }

    public void call(String js, String method, Callback callback, HttpServletRequest request, Object... args)
    {
        UserOnlineInfo userOnlineInfo = UserOnlineInfo.getUserOnlineInfo(request);

        String id = addCallback(callback);

        CallbackMessage message = new CallbackMessage(id, js, method, args);
        cometService.sendMessage(message, userOnlineInfo.getUserId());
    }

    public String addCallback(Callback callback)
    {
        String id = Tools.getUUID();

        cache.set(id, callback);

        return id;
    }

    public void callback(String ret, String id) throws Exception
    {
        Callback callback = cache.getCache(id);
        if (callback != null)
        {
            cache.remove(id);

            callback.call(ret);
        }
    }
}
