package com.gzzm.ods.bak;

import com.gzzm.ods.flow.OdFlowInstance;
import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.sql.Date;
import java.util.List;

/**
 * @author zjw
 * @date 12-10-23
 */
public abstract class OdUserBakDao extends GeneralDao
{
    public OdUserBakDao()
    {
    }

    public OdUserBak getOdUserBak(Long backId) throws Exception
    {
        return load(OdUserBak.class, backId);
    }

    public OdFlowInstance getOdFlowInstance(Long instanceId) throws Exception
    {
        return load(OdFlowInstance.class, instanceId);
    }

    @OQL("select s.instanceId from SystemFlowStep s join OdFlowInstance i " +
            "on s.instanceId=i.instanceId where s.receiver in :1 and s.receiveTime>=:2 " +
            "and s.receiveTime<:3 group by s.instanceId order by min(i.startTime)")
    public abstract List<Long> getInstanceIds(List<String> receiver, Date startTime, Date endTime);
}
