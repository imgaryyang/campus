package com.gzzm.platform.login;

import javax.servlet.http.*;

/**
 * 监听session失效时，让用户退出，由于把session改到了cache里，session超时不再起作用
 *
 * @author camel
 * @date 2009-7-24
 */
public class LoginSessionListener implements HttpSessionListener
{
    public LoginSessionListener()
    {
    }

    public void sessionCreated(HttpSessionEvent event)
    {
    }

    public void sessionDestroyed(HttpSessionEvent event)
    {
    }
}