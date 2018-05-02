package com.gzzm.ods.flow;

import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * 和公文关联相关的操作的dao
 *
 * @author camel
 * @date 13-12-1
 */
public abstract class OdLinkDao extends GeneralDao
{
    public OdLinkDao()
    {
    }

    public OdFlowInstanceLink getLink(Long instanceId, Long linkInstanceId) throws Exception
    {
        return load(OdFlowInstanceLink.class, instanceId, linkInstanceId);
    }

    @OQL("select l from OdFlowInstanceLink l where instanceId=:1 order by linkTime,linkInstanceId")
    public abstract List<OdFlowInstanceLink> getLinks(Long instanceId) throws Exception;

    @OQL("select distinct s.instanceId from SystemFlowStep s where stepId in :1")
    public abstract List<Long> getInstanceIdsByStepIds(List<Long> stepIds) throws Exception;
}
