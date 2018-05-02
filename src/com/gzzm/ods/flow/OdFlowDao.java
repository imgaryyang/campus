package com.gzzm.ods.flow;

import com.gzzm.ods.business.BusinessModel;
import com.gzzm.ods.dict.Tag;
import com.gzzm.ods.exchange.*;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * @author camel
 * @date 11-9-21
 */
public abstract class OdFlowDao extends ExchangeDao
{
    public OdFlowDao()
    {
    }

    public BusinessModel getBusiness(Integer businessId) throws Exception
    {
        return load(BusinessModel.class, businessId);
    }

    @OQL("select r from ReceiveBase r where r.deptId=:1 and r.type=0 and r.document.title like :2 order by r.acceptTime desc limit 10")
    public abstract List<ReceiveBase> queryReceives(Integer deptId, String s) throws Exception;

    public OdFlowInstance getOdFlowInstance(Long instanceId) throws Exception
    {
        return load(OdFlowInstance.class, instanceId);
    }

    public SendFlowInstance getSendFlowInstance(Long instanceId) throws Exception
    {
        return load(SendFlowInstance.class, instanceId);
    }

    public void lockOdFlowInstance(Long instanceId) throws Exception
    {
        lock(OdFlowInstance.class, instanceId);
    }

    public void lockSendFlowInstance(Long instanceId) throws Exception
    {
        lock(SendFlowInstance.class, instanceId);
    }

    @GetByField("documentId")
    public abstract SendFlowInstance getSendFlowInstanceByDocumentId(Long documentId) throws Exception;

    /**
     * 获得某个收文接收后的公文流转流程实例
     *
     * @param receiveId 收文ID
     * @return 收文接收后的公文流转流程实例
     * @throws Exception 数据库查询错误
     */
    @OQL("select o from OdFlowInstance o where receiveId=:1 and state<2")
    public abstract OdFlowInstance getOdFlowInstanceByReceiveId(Long receiveId) throws Exception;

    /**
     * 获得某个发文流转过程公文流转流程实例
     *
     * @param documentId 发文的公文ID
     * @return 发文流转过程公文流转流程实例
     * @throws Exception 数据库查询错误
     */
    @OQL("select o from OdFlowInstance o,SendFlowInstance s where o.instanceId=s.instanceId and s.documentId=:1")
    public abstract OdFlowInstance getSendOdFlowInstanceByDocumentId(Long documentId) throws Exception;

    /**
     * 获得某个发文的抄送实例
     *
     * @param documentId 发文的公文ID
     * @return 发文抄送实例
     * @throws Exception 数据库查询错误
     */
    @OQL("select o from OdFlowInstance o where o.documentId=:1 and type='copy'")
    public abstract OdFlowInstance getCopyOdFlowInstanceByDocumentId(Long documentId) throws Exception;

    @OQL("select i from OdFlowInstance i where deptId in ?1 and signed is null order by startTime limit 30")
    public abstract List<OdFlowInstance> queryNotSignedInstances(Collection<Integer> deptIds) throws Exception;

    public OdInstanceCatalog getInstanceCatalog(Integer catalogId) throws Exception
    {
        return load(OdInstanceCatalog.class, catalogId);
    }

    public OdInstanceCatalog getRootInstanceCatalog() throws Exception
    {
        OdInstanceCatalog rootCatalog = getInstanceCatalog(0);

        if (rootCatalog == null)
        {
            rootCatalog = new OdInstanceCatalog(0, "根目录");
            add(rootCatalog);
        }

        return rootCatalog;
    }

    @OQL("select c from OdInstanceCatalog c where deptId=:1 and parentCatalogId=0 order by orderId")
    public abstract List<OdInstanceCatalog> getTopInstanceCatalogs(Integer deptId) throws Exception;

    @OQLUpdate("update OdFlowInstance set catalogId=:2 where instanceId in :1")
    public abstract void catalog(Long[] instanceIds, Integer catalogId) throws Exception;

    @OQLUpdate("update OdFlowInstance set catalogId=null where instanceId in :1")
    public abstract void cancelCatalog(Long[] instanceIds) throws Exception;

    @OQLUpdate("update OdFlowInstance set cataloged=1 where instanceId in :1")
    public abstract void catalog(Long[] instanceIds) throws Exception;

    @OQLUpdate("update OdFlowInstance set cataloged=0 where instanceId in :1")
    public abstract void uncatalog(Long[] instanceIds) throws Exception;

    @OQLUpdate("update OdFlowInstance set nocataloged=1 where instanceId in :1")
    public abstract void nocatalog(Long[] instanceIds) throws Exception;

    @OQL("select c from Collect c where topInstanceId=:1 and receiveBase.state<4 order by receiveBase.sendTime")
    public abstract List<Collect> getCollectsBytTopInstanceId(Long topInstanceId) throws Exception;

    @OQL("select c from Collect c where collectInstanceId=:1 and receiveBase.state<4 order by receiveBase.sendTime")
    public abstract List<Collect> getCollectsByCollectInstanceId(Long collectInstanceId) throws Exception;

    @OQL("select i from OdFlowInstance i where linkId=:1 and business.componentType=:2")
    public abstract List<OdFlowInstance> getOdFlowInstancesByLinkId(String linkId, String componentType)
            throws Exception;

    @OQL("select t from Tag t where userId is null or userId=:1 order by case when userId is null then 1 else 0 end,orderId")
    public abstract List<Tag> getTags(Integer userId);

    @OQL("select s from SendFlowInstance s join OdFlowInstance i on s.instanceId=i.instanceId " +
            "where i.deptId=:1 and s.document.sendNumber=:2 and s.sended=1 and i.state in (0,1) " +
            "order by i.startTime limit 1")
    public abstract SendFlowInstance getSendFlowInstanceBySendNumber(Integer deptId, String sendNumber);

    public void deleteOdFlowInstance(Long instanceId) throws Exception
    {
        delete(OdFlowInstance.class, instanceId);
    }
}