package com.gzzm.platform.flow.worksheet;

import com.gzzm.platform.consignation.*;
import com.gzzm.platform.flow.*;
import net.cyan.commons.transaction.*;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.sql.Date;
import java.util.*;

/**
 * 和待办列表相关的数据库操作
 *
 * @author camel
 * @date 11-10-13
 */
public abstract class WorkSheetDao extends GeneralDao
{
    private Class<? extends SystemFlowInstance> instanceClass;

    private Class<? extends SystemFlowStep> stepClass;

    public WorkSheetDao()
    {
    }

    @Transactional(mode = TransactionMode.none)
    public void setInstanceClass(Class<? extends SystemFlowInstance> instanceClass)
    {
        this.instanceClass = instanceClass;
    }

    @Transactional(mode = TransactionMode.none)
    public void setStepClass(Class<? extends SystemFlowStep> stepClass)
    {
        this.stepClass = stepClass;
    }

    @Transactional(mode = TransactionMode.none)
    public String getInstanceName()
    {
        return instanceClass.getName();
    }

    @Transactional(mode = TransactionMode.none)
    public String getStepName()
    {
        return stepClass.getName();
    }

    @OQL("select i from @instanceName i where i.instanceId in :1")
    public abstract List<SystemFlowInstance> queryInstances(Collection<Long> instanceIds) throws Exception;

    @OQL("select instanceId from @stepName where stepId in :1")
    public abstract List<Long> queryInstanceIdsByStepIds(Long[] stepIds) throws Exception;

    @OQL("select i.title from @stepName s join @instanceName i on s.instanceId=i.instanceId and s.stepId=:1")
    public abstract String getTitleWithStepId(Long stepId) throws Exception;

    @OQL("select i.flowTag from @stepName s join @instanceName i on s.instanceId=i.instanceId and s.stepId=:1")
    public abstract String getFlowTagWithStepId(Long stepId) throws Exception;

    @Transactional
    public Integer consign(Long[] stepIds, Integer consigner, Integer consignee) throws Exception
    {
        Date date = new Date(System.currentTimeMillis());

        Consignation consignation = new Consignation();
        consignation.setConsigner(consigner);
        consignation.setConsignee(consignee);
        consignation.setStartTime(date);
        consignation.setEndTime(date);
        consignation.setState(ConsignationState.end);

        add(consignation);

        consign(stepIds, consigner.toString(), consignee.toString(), consignation.getConsignationId());

        return consignation.getConsignationId();
    }

    @OQLUpdate("update @stepName set receiver=:3,consignationId=:4,state=0 where stepId in :1 and receiver=:2")
    protected abstract void consign(Long[] stepIds, String consigner, String consignee, Integer consignationId)
            throws Exception;

    @OQLUpdate("update @stepName set hidden=1 where stepId in :1")
    public abstract void hide(Long[] stepIds) throws Exception;

    @OQLUpdate("update @stepName set hidden=0 where stepId in :1")
    public abstract void unHide(Long[] stepIds) throws Exception;
}
