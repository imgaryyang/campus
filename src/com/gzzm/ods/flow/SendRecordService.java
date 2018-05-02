package com.gzzm.ods.flow;

import com.gzzm.ods.document.*;
import com.gzzm.ods.exchange.*;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.form.*;
import net.cyan.commons.pool.CommonThreadPool;
import net.cyan.commons.util.StringUtils;
import net.cyan.nest.annotation.Inject;
import net.cyan.valmiki.form.FormContext;

import java.util.*;

/**
 * @author camel
 * @date 12-12-15
 */
public class SendRecordService
{
    @Inject
    private SendRecordDao dao;

    private CommonThreadPool pool;

    public SendRecordService()
    {
    }

    public boolean saveRecords() throws Exception
    {
        List<Long> instanceIds = dao.getNoRecordInstanceIds();
        if (instanceIds.size() == 0)
            return false;

        for (final Long instanceId : instanceIds)
        {
            try
            {
                saveRecord(instanceId);
            }
            catch (Throwable ex)
            {
                Tools.log(ex);
            }
        }


        return true;
    }

    public void saveRecord(Long instanceId) throws Exception
    {
        saveRecord(dao.getSendFlowInstance(instanceId));
    }

    public void saveRecord(SendFlowInstance sendFlowInstance) throws Exception
    {
        Tools.log("save record " + sendFlowInstance.getInstanceId());

        OdFlowInstance odFlowInstance = dao.getOdFlowInstance(sendFlowInstance.getInstanceId());
        if (odFlowInstance != null)
        {
            OdFlowInstanceState state = odFlowInstance.getState();
            if (state != OdFlowInstanceState.canceled && sendFlowInstance.getBodyId() != null)
            {
                OfficeDocument document = sendFlowInstance.getDocument();
                Boolean sentOut = sendFlowInstance.getSentOut();
                boolean sended = sentOut != null && sentOut;
                if (sended || !StringUtils.isEmpty(document.getSendNumber()))
                {
                    SystemFormContext formContext = new SystemFormContext(null, sendFlowInstance.getBodyId());
                    FormApi.loadFormContext(formContext);

                    Date time = odFlowInstance.getEndTime();
                    if (time == null)
                        time = odFlowInstance.getStartTime();

                    saveRecord(document, odFlowInstance, sendFlowInstance, formContext, sended, time, dao);
                }
            }
        }

        sendFlowInstance.setSaveRecorded(true);
        dao.update(sendFlowInstance);
    }

    public static void saveRecord(OfficeDocument document, OdFlowInstance odFlowInstance,
                                  SendFlowInstance sendFlowInstance, FormContext formContext, boolean sended, Date time,
                                  ExchangeSendDao exchangeSendDao) throws Exception
    {
        SendRecord record = exchangeSendDao.getSendRecordByDocumentId(document.getDocumentId());

        if (record == null)
            record = new SendRecord();

        record.setDocumentId(document.getDocumentId());
        record.setDeptId(odFlowInstance.getDeptId());
        record.setTitle(document.getTitle());
        record.setSendNumber(document.getSendNumber());
        record.setSubject(document.getSubject());
        record.setSecret(document.getSecret());
        record.setPriority(document.getPriority());
        record.setSendCount(document.getSendCount());

        if (sendFlowInstance != null && sendFlowInstance.getCreator() != null)
        {
            record.setCreator(sendFlowInstance.getCreateUser().getUserName());
        }
        else
        {
            record.setCreator(odFlowInstance.getCreateUser().getUserName());
        }

        if (sendFlowInstance != null && sendFlowInstance.getCreateDeptId() != null)
        {
            record.setCreateDeptName(sendFlowInstance.getCreateDept().getDeptName());
            record.setCreateDeptId(sendFlowInstance.getCreateDeptId());
        }
        else if (odFlowInstance.getCreateDeptId() != null)
        {
            record.setCreateDeptName(odFlowInstance.getCreateDept().getDeptName());
            record.setCreateDeptId(odFlowInstance.getCreateDeptId());
        }

        formContext.getFormData().get(Constants.Document.SIGNER);

        SignInfo signInfo = OdSignService.getSignInfo(formContext.getFormData());
        if (signInfo != null)
        {
            record.setSigner(signInfo.getSigner());
            record.setSignTime(signInfo.getSignTime());
        }

        if (record.getRecordTime() == null)
            record.setRecordTime(time);

        if (sended)
            record.setState(SendRecordState.SENDED);
        else if (record.getState() == null)
            record.setState(SendRecordState.NOSENDED);

        String receivers = document.getReceivers();
        if (receivers != null)
            record.setSendDepts(receivers.toCharArray());
        String mainReceivers = document.getMainReceivers();
        if (mainReceivers != null)
            record.setMainSendDepts(mainReceivers.toCharArray());
        record.setSendNumberId(sendFlowInstance.getSendNumberId());

        exchangeSendDao.save(record);
    }
}
