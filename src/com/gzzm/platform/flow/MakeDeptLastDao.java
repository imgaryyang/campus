package com.gzzm.platform.flow;

import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * @author camel
 * @date 2016/9/11
 */
public abstract class MakeDeptLastDao extends GeneralDao
{
    public MakeDeptLastDao()
    {
    }

    @OQL("select instanceId from SystemFlowStep s where deptLast is null order by s.stepId limit 30")
    public abstract List<Long> getInstanceIds();
}
