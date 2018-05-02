package com.gzzm.safecampus.device.attendance;

import com.gzzm.platform.commons.Tools;
import com.gzzm.safecampus.device.attendance.server.SocketMain;

import javax.servlet.*;

/**
 * 考勤机对接
 * @author liyabin
 * @date 2018/3/26
 */
public class UDPSocketListener implements ServletContextListener
{

    private SocketMain socketMain;

    public UDPSocketListener()
    {
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent)
    {
        try
        {
            socketMain = new SocketMain();
            Thread thread = new Thread(socketMain);
            thread.start();
        }
        catch (Exception e)
        {
            Tools.log("考勤监听启动异常！");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent)
    {
        if (socketMain != null) socketMain.destroyed();
    }
}
