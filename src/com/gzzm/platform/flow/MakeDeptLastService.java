package com.gzzm.platform.flow;

import com.gzzm.platform.commons.Tools;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author camel
 * @date 2016/9/11
 */
public class MakeDeptLastService
{
    @Inject
    private DefaultSystemFlowDao flowDao;

    @Inject
    private MakeDeptLastDao makeDeptLastDao;

    public MakeDeptLastService()
    {
    }

    public boolean make() throws Exception
    {
        List<Long> instanceIds = makeDeptLastDao.getInstanceIds();

        if (instanceIds.size() == 0)
            return false;

        for (Long instanceId : new HashSet<Long>(instanceIds))
        {
            Tools.log("make deptlast:" + instanceId);
            flowDao.makeDeptLast(instanceId);
        }

        return true;
    }
}
