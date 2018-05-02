package com.gzzm.portal.cms.information;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 2017/11/23
 */
public class InformationInvalidLinkClearJob implements Runnable
{
    @Inject
    private static Provider<InformationInvalidLinkClearService> serviceProvider;

    private String server;

    public InformationInvalidLinkClearJob()
    {
    }

    public String getServer()
    {
        return server;
    }

    public void setServer(String server)
    {
        this.server = server;
    }

    @Override
    public void run()
    {
        try
        {
            Tools.log("start clear:" + server);
            serviceProvider.get().clear(server);
        }
        catch (Throwable ex)
        {
            Tools.log(ex);
        }
    }
}
