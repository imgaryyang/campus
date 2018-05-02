package com.gzzm.ods.bak;

import com.gzzm.ods.flow.OdFlowInstance;
import net.cyan.commons.transaction.Transactional;
import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * @author ccs
 * @date 12-10-16
 */
public abstract class OdDeptInstanceBakDao extends GeneralDao
{
    public OdDeptInstanceBakDao()
    {
    }

    @OQL("select b from OdDeptInstanceBak b where b.deptId=:1 order by createTime desc ")
    public abstract List<OdDeptInstanceBak> getBaks(Integer deptId) throws Exception;

    public OdDeptInstanceBak getOdDeptInstanceBak(Integer backId) throws Exception
    {
        return load(OdDeptInstanceBak.class, backId);
    }

    @Transactional
    public void bak(Long[] instanceIds, Integer bakId) throws Exception
    {
        OdDeptInstanceBak bak = get(OdDeptInstanceBak.class, bakId);
        List<OdFlowInstance> instances = bak.getInstances();

        for (Long instanceId : instanceIds)
        {
            OdFlowInstance instance = new OdFlowInstance();
            instance.setInstanceId(instanceId);

            if (!instances.contains(instance))
            {
                instances.add(instance);
            }
        }
    }

    @Transactional
    public void bak(List<OdFlowInstance> instances1, Integer bakId) throws Exception
    {
        OdDeptInstanceBak bak = get(OdDeptInstanceBak.class, bakId);
        List<OdFlowInstance> instances = bak.getInstances();

        for (OdFlowInstance instance : instances1)
        {
            if (!instances.contains(instance))
            {
                instances.add(instance);
            }
        }
    }

    @Transactional
    public void cancelBak(Long[] instanceIds, Integer bakId) throws Exception
    {
        OdDeptInstanceBak bak = get(OdDeptInstanceBak.class, bakId);
        List<OdFlowInstance> instances = bak.getInstances();

        for (Long instanceId : instanceIds)
        {
            OdFlowInstance instance = new OdFlowInstance();
            instance.setInstanceId(instanceId);

            instances.remove(instance);
        }
    }
}
