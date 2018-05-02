package com.gzzm.platform.update;

import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 13-5-22
 */
public class UpdateSQLJob implements Runnable
{
    @Inject
    private static Provider<UpdateSQLService> serviceProvider;

    public UpdateSQLJob()
    {
    }

    public void run()
    {
        serviceProvider.get().execute();
    }
}
