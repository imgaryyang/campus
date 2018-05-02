package com.gzzm.platform.commons;

import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

/**
 * 系统数据初始化
 *
 * @author camel
 * @date 2009-11-25
 */
public abstract class SysDataInit implements Runnable
{
    @Inject
    private static Provider<SysDataInitDao> daoProvider;

    public SysDataInit()
    {
        Chinese.init();
    }

    public void run()
    {
        try
        {
            daoProvider.get().init();
        }
        catch (Exception ex)
        {
            Tools.wrapException(ex);
        }
    }
}
