package com.gzzm.portal.cms.visit;

import com.gzzm.platform.commons.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.*;

/**
 * @author sjy
 * @date 2018/3/2
 */
public class VisitDayTotalCreateJob implements Runnable
{
    @Inject
    private static Provider<VisitDayTotalService> serviceProvider;

    @Override
    public void run()
    {
        try
        {
            serviceProvider.get().createData();
        }
        catch (Throwable e)
        {
            Tools.log(e);
        }
    }
}
