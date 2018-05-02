package com.gzzm.ods.dispatch;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

/**
 * @author gyw
 * @date 2017/7/26 0026
 */
public class DispatchTimer implements Runnable
{

    @Inject
    private static Provider<DispatchRecordService> provider;

    public DispatchTimer()
    {
    }

    @Override
    public void run()
    {
        DispatchRecordService service = provider.get();

        //记录红牌
        try
        {
            service.checkRedWarm();
        }
        catch (Exception e)
        {
            Tools.log("红牌记录异常", e);
        }

        //预警提醒
        try
        {
            service.warningNotify();
        }
        catch (Exception e)
        {
            Tools.log("黄牌预警提醒异常", e);
        }

        //红牌提醒
        try
        {
            service.redNotify();
        }
        catch (Exception e)
        {
            Tools.log("红牌提醒异常", e);
        }
    }
}
