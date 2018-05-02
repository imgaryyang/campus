package com.gzzm.platform.recent;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.transaction.*;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 2016/1/15
 */
public class RecentService
{
    @Inject
    private RecentDao dao;

    public RecentService()
    {
    }

    @Transactional(mode = TransactionMode.not_supported)
    public void saveRecent(Recent recent) throws Exception
    {
        try
        {
            dao.saveRecent(recent);
        }
        catch (Throwable ex)
        {
            //保存最近文件错误不影响其他逻辑
            Tools.log(ex);
        }
    }
}
