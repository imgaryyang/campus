package com.gzzm.platform.commons;

import net.cyan.thunwind.PersistenceManager;
import net.cyan.thunwind.meta.TableInfo;

/**
 * 数据库表同步工作
 *
 * @author camel
 * @date 2009-7-24
 */
public class TableSynchronizer implements Runnable
{
    private String manager = "";

    public TableSynchronizer()
    {
    }

    public String getManager()
    {
        return manager;
    }

    public void setManager(String manager)
    {
        this.manager = manager;
    }

    public void run()
    {
        try
        {
            TableInfo.synchronize(PersistenceManager.getManager(manager), null, false);
        }
        catch (Throwable ex)
        {
            Tools.log(ex);
        }
    }
}
