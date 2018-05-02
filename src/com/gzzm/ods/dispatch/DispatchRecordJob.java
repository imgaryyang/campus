package com.gzzm.ods.dispatch;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

/**
 * @author LDP
 * @date 2017/7/25
 */
public class DispatchRecordJob implements Runnable
{
    @Inject
    private static Provider<DispatchRecordService> provider;

    public DispatchRecordJob()
    {
    }

    @Override
    public void run()
    {
        try
        {
            provider.get().sendToDispatchRecord();
        }
        catch (Exception e)
        {
            Tools.log("限时办文数据同步失败：", e);
        }
    }
}
