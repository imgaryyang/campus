package com.gzzm.ods.flow;

import com.gzzm.ods.exchange.*;
import com.gzzm.platform.flow.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.nest.annotation.Inject;
import net.cyan.valmiki.flow.FlowStep;

import java.util.*;

/**
 * @author camel
 * @date 11-9-21
 */
public class OdFlowService
{
    @Inject
    private OdFlowDao dao;

    public OdFlowService()
    {
    }

    public OdFlowDao getDao()
    {
        return dao;
    }

    public static String getPageUrl(String type)
    {
        return "/ods/flow/" + type;
    }

    public static String getStepUrl(Long stepId, String type)
    {
        return getStepUrl(stepId, type, null);
    }

    public static String getStepUrl(Long stepId, String type, Integer year)
    {
        String url = getPageUrl(type) + "/" + stepId;
        if (year != null)
        {
            url += "?year=" + year;

            if (year > 0 || year < -99)
                url += "&readOnly=true&saveOnly=true";
        }

        return url;
    }

    public static SystemFlowDao getSystemFlowDao(OdFlowInstance odFlowInstance) throws Exception
    {
        Integer year = odFlowInstance.getYear();
        if (year == null)
            return OdSystemFlowDao.getInstance();
        else
            return OdSystemFlowDao.getInstance(year);
    }

    public static String getFirstStepUrl(OdFlowInstance odFlowInstance) throws Exception
    {
        return getStepUrl(FlowApi.getFirstStepId(odFlowInstance.getInstanceId(), getSystemFlowDao(odFlowInstance)),
                odFlowInstance.getType(), odFlowInstance.getYear());
    }

    public static String getLastStepUrl(OdFlowInstance odFlowInstance) throws Exception
    {
        return getStepUrl(FlowApi.getLastStepId(odFlowInstance.getInstanceId(), getSystemFlowDao(odFlowInstance)),
                odFlowInstance.getType(), odFlowInstance.getYear());
    }

    public String getFirstStepUrl(Long instanceId) throws Exception
    {
        OdFlowInstance odFlowInstance = dao.getOdFlowInstance(instanceId);

        return getFirstStepUrl(odFlowInstance);
    }

    public String getLastStepUrl(Long instanceId) throws Exception
    {
        OdFlowInstance odFlowInstance = dao.getOdFlowInstance(instanceId);

        return getLastStepUrl(odFlowInstance);
    }

    @Transactional
    public List<Long> collect(OdFlowInstance instance, Integer[] deptIds, Integer collectUserId, String collectUserName,
                              Long collectStepId) throws Exception
    {
        return collect(instance, instance.getInstanceId(), deptIds, collectUserId, collectUserName, collectStepId);
    }

    @Transactional
    public List<Long> collect(OdFlowInstance instance, Long topInstanceId, Integer[] deptIds, Integer collectUserId,
                              String collectUserName, Long collectStepId) throws Exception
    {
        List<Long> receiveIds = new ArrayList<Long>(deptIds.length);
        for (Integer deptId : deptIds)
            receiveIds.add(collect(instance, topInstanceId, deptId, collectUserId, collectUserName, collectStepId));

        return receiveIds;
    }

    @Transactional
    public Long collect(OdFlowInstance instance, Integer deptId, Integer collectUserId, String collectUserName,
                        Long collectStepId) throws Exception
    {
        return collect(instance, instance.getInstanceId(), deptId, collectUserId, collectUserName, collectStepId);
    }

    @Transactional
    public Long collect(OdFlowInstance instance, Long topInstanceId, Integer deptId, Integer collectUserId,
                        String collectUserName, Long collectStepId) throws Exception
    {
        SystemFlowDao systemFlowDao = OdSystemFlowDao.getInstance();
        Date now = new Date();
        Long stepId = Long.valueOf(systemFlowDao.createStepId());

        ReceiveBase receiveBase = new ReceiveBase();

        receiveBase.setDeptId(deptId);
        receiveBase.setDocumentId(instance.getDocumentId());
        receiveBase.setSendTime(new Date());
        receiveBase.setState(ReceiveState.noAccepted);
        receiveBase.setType(ReceiveType.collect);
        receiveBase.setMethod(ReceiveMethod.system);
        receiveBase.setSourceDeptId(instance.getDeptId());
        dao.add(receiveBase);

        Collect collect = new Collect();
        collect.setReceiveId(receiveBase.getReceiveId());
        collect.setCollectInstanceId(instance.getInstanceId());
        collect.setTopInstanceId(topInstanceId);
        collect.setCollectUserId(collectUserId);
        collect.setCollectStepId(collectStepId);
        collect.setStepId(stepId);
        dao.add(collect);

        SystemFlowStep step = systemFlowDao.newFlowStep();
        step.setInstanceId(instance.getInstanceId());
        step.setStepId(stepId);
        step.setGroupId(Long.valueOf(systemFlowDao.createStepGroupId()));
        step.setNodeId("#collect");
        step.setPreStepId(collectStepId);
        step.setTopStepId(stepId);
        step.setReceiver("$" + dao.getDept(deptId).getDeptName());
        step.setState(FlowStep.NOACCEPT);
        step.setSourceName(collectUserName);
        step.setReceiveTime(now);
        step.setShowTime(now);
        step.setLastStep(true);
        systemFlowDao.add(step);

        return receiveBase.getReceiveId();
    }

    @Transactional
    public List<Long> unionDeal(OdFlowInstance instance, Integer[] deptIds, Integer unionUserId, String unionUserName,
                                Long unionStepId) throws Exception
    {
        List<Long> receiveIds = new ArrayList<Long>(deptIds.length);

        for (Integer deptId : deptIds)
            receiveIds.add(unionDeal(instance, deptId, unionUserId, unionUserName, unionStepId));

        return receiveIds;
    }

    @Transactional
    public Long unionDeal(OdFlowInstance instance, Integer deptId, Integer unionUserId, String unionUserName,
                          Long unionStepId)
            throws Exception
    {
        SystemFlowDao systemFlowDao = OdSystemFlowDao.getInstance();
        Date now = new Date();
        Long stepId = Long.valueOf(systemFlowDao.createStepId());

        ReceiveBase receiveBase = new ReceiveBase();

        receiveBase.setDeptId(deptId);
        receiveBase.setDocumentId(instance.getDocumentId());
        receiveBase.setSendTime(now);
        receiveBase.setState(ReceiveState.noAccepted);
        receiveBase.setType(ReceiveType.uniondeal);
        receiveBase.setMethod(ReceiveMethod.system);
        receiveBase.setSourceDeptId(instance.getDeptId());
        dao.add(receiveBase);

        UnionDeal unionDeal = new UnionDeal();
        unionDeal.setReceiveId(receiveBase.getReceiveId());
        unionDeal.setUnionInstanceId(instance.getInstanceId());
        unionDeal.setUnionUserId(unionUserId);
        unionDeal.setUnionStepId(unionStepId);
        unionDeal.setStepId(stepId);
        dao.add(unionDeal);

        SystemFlowStep step = systemFlowDao.newFlowStep();
        step.setInstanceId(instance.getInstanceId());
        step.setStepId(stepId);
        step.setGroupId(Long.valueOf(systemFlowDao.createStepGroupId()));
        step.setNodeId("#uniondeal");
        step.setPreStepId(unionStepId);
        step.setTopStepId(stepId);
        step.setReceiver("$" + dao.getDept(deptId).getDeptName());
        step.setState(FlowStep.NOACCEPT);
        step.setSourceName(unionUserName);
        step.setReceiveTime(now);
        step.setShowTime(now);
        step.setLastStep(true);
        systemFlowDao.add(step);

        return receiveBase.getReceiveId();
    }

    @SuppressWarnings("unchecked")
    public static Class<? extends OdFlowPage> getFlowPageClass(String flowTag) throws Exception
    {
        if ("copy".equals(flowTag))
            return null;

        String s;
        if ("uniondeal".equals(flowTag))
        {
            s = "UnionDeal";
        }
        else
        {
            s = Character.toUpperCase(flowTag.charAt(0)) + flowTag.substring(1);
        }
        return (Class<? extends OdFlowPage>) Class.forName("com.gzzm.ods.flow." + s + "FlowPage");
    }

    @Transactional
    public void stopInstance(OdFlowInstance odFlowInstance, Integer userId, Integer deptId) throws Exception
    {
        odFlowInstance.setState(OdFlowInstanceState.closed);

        getDao().update(odFlowInstance);

        ReceiveBase receiveBase = odFlowInstance.getReceiveBase();
        if (receiveBase != null && receiveBase.getState() != ReceiveState.end)
        {
            receiveBase.setState(ReceiveState.end);
            if (receiveBase.getEndTime() == null)
                receiveBase.setEndTime(new Date());
            getDao().update(receiveBase);
        }

        //设置终止记录
        OdFlowInstanceStop instanceStop = new OdFlowInstanceStop();
        instanceStop.setInstanceId(odFlowInstance.getInstanceId());
        instanceStop.setUserId(userId);
        instanceStop.setDeptId(deptId);
        instanceStop.setStopTime(new Date());
        getDao().save(instanceStop);
    }
}
