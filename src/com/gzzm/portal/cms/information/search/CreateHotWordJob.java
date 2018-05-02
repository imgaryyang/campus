package com.gzzm.portal.cms.information.search;

import com.gzzm.platform.commons.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.*;

/**
 * 热门搜索词生成任务
 *
 * @author sjy
 * @date 2018/2/23
 */
public class CreateHotWordJob implements Runnable
{
    @Inject
    private static Provider<SearchRecordService> serviceProvider;

    @Override
    public void run()
    {
        try
        {
            serviceProvider.get().createHotWord();
        }
        catch (Exception e)
        {
            Tools.log(e);
        }
    }
}
