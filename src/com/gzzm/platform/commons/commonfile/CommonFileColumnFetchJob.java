package com.gzzm.platform.commons.commonfile;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 2017/5/22
 */
public class CommonFileColumnFetchJob implements Runnable
{
    @Inject
    private static Provider<CommonFileColumnDao> daoProvider;

    private Class entityType;

    public CommonFileColumnFetchJob()
    {
    }

    public Class getEntityType()
    {
        return entityType;
    }

    public void setEntityType(Class entityType)
    {
        this.entityType = entityType;
    }

    @Override
    public void run()
    {
        try
        {
            Tools.log("fetch common file " + entityType.getName());
            CommonFileColumnDao dao = daoProvider.get();
            while (true)
            {
                try
                {
                    if (!dao.fetch(entityType))
                        break;
                }
                catch (Throwable ex)
                {
                    Tools.log(ex);
                }
            }
        }
        catch (Throwable ex)
        {
            Tools.log(ex);
        }
    }
}
