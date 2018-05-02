package com.gzzm.ods.flow;

import com.gzzm.ods.business.BusinessModel;
import com.gzzm.ods.dict.Label;
import com.gzzm.ods.document.OfficeDocument;
import com.gzzm.ods.exchange.Collect;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.*;

/**
 * @author camel
 * @date 11-10-13
 */
public abstract class OdWorkSheetDao extends GeneralDao
{
    public OdWorkSheetDao()
    {
    }

    @OQL("select i,[i.document],[i.document.text],[i.document.receipt],[i.receiveBase] from " +
            "OdFlowInstance i where i.instanceId in :1")
    public abstract List<OdFlowInstance> queryOdInstances(List<Long> instanceIds) throws Exception;

    @OQL("select i,[i.document] from SendFlowInstance i where i.instanceId in :1")
    public abstract List<SendFlowInstance> querySendInstances(List<Long> instanceIds) throws Exception;

    public OdFlowInstance getOdInstance(Long instanceId) throws Exception
    {
        return load(OdFlowInstance.class, instanceId);
    }

    public SendFlowInstance getSendFlowInstance(Long instanceId) throws Exception
    {
        return load(SendFlowInstance.class, instanceId);
    }

    public void deleteOdInstance(Long instanceId) throws Exception
    {
        delete(OdFlowInstance.class, instanceId);
    }

    public abstract List<BusinessModel> getBusinessList(Integer userId);

    public OfficeDocument getDocument(Long documentId) throws Exception
    {
        return get(OfficeDocument.class, documentId);
    }

    @OQL("select l from Label l where deptId in :1 order by dept.leftValue,orderId")
    public abstract List<Label> getLabels(Collection<Integer> deptIds) throws Exception;

    @OQL("select l from Label l where deptId=:1 order by orderId")
    public abstract List<Label> getLabels(Integer deptId) throws Exception;

    @OQL("select c from Collect c where collectInstanceId=:1")
    public abstract List<Collect> getCollectsByCollectInstanceId(Long collectInstanceId) throws Exception;

    @OQL("select o from OdFlowInstance o where receiveId=:1 and state<2")
    public abstract OdFlowInstance getOdFlowInstanceByReceiveId(Long receiveId) throws Exception;

    @LoadByKey
    public abstract Label getLabel(Integer labelId);

    @OQLUpdate("update OdFlowStepQ set hidden=1 where stepId in :1")
    public abstract void hide(Long[] stepIds) throws Exception;

    @OQLUpdate("update OdFlowStepQ set hidden=0 where stepId in :1")
    public abstract void unHide(Long[] stepIds) throws Exception;
}
