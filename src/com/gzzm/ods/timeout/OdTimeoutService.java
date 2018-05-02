package com.gzzm.ods.timeout;

import com.gzzm.ods.exchange.*;
import com.gzzm.ods.flow.*;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.timeout.TimeoutCheck;
import net.cyan.nest.annotation.Inject;

import java.util.Date;

/**
 * @author camel
 * @date 2014/5/28
 */
public class OdTimeoutService
{
    @Inject
    private OdTimeoutDao dao;

    public OdTimeoutService()
    {
    }

    public void checkInstances() throws Exception
    {
        for (OdFlowInstance instance : dao.getShouldCheckTimeoutInstances())
        {
            try
            {
                checkInstance(instance);
            }
            catch (Throwable ex)
            {
                //检查一条记录错误，不影响检查其他记录，只记录日志

                Tools.log("check timeout error,instanceId:" + instance.getInstanceId(), ex);
            }
        }
    }

    public void checkInstance(Long instanceId) throws Exception
    {
        checkInstance(dao.getOdFlowInstance(instanceId));
    }

    /**
     * 检查超时
     *
     * @param instance 要检查超时的记录
     * @throws Exception 数据库操作错误
     */
    public void checkInstance(OdFlowInstance instance) throws Exception
    {
        OdFlowInstanceState state = instance.getState();

        if (state == OdFlowInstanceState.notStarted || state == OdFlowInstanceState.deleted)
            return;

        //开始计时的时间
        java.util.Date startTime = instance.getStartTime();
        if (startTime == null)
            return;

        //已经结束，判别是否超时的时间为结束的时间，否则为当前时间
        java.util.Date time;
        java.util.Date endTime;
        if (state == OdFlowInstanceState.unclosed)
        {
            time = new Date();
            endTime = null;
        }
        else
        {
            time = instance.getEndTime();
            endTime = instance.getEndTime();
        }

        if (time == null)
            return;

        TimeoutCheck timeoutCheck = new TimeoutCheck();

        timeoutCheck.setTypeId(OdTimeout.FLOW_ID);
        timeoutCheck.setStartTime(startTime);
        timeoutCheck.setDeadline(instance.getDeadline());
        timeoutCheck.setTime(time);
        timeoutCheck.setEndTime(endTime);
        timeoutCheck.setRecordId(instance.getInstanceId());
        timeoutCheck.setDeptId(instance.getDeptId());
        timeoutCheck.setObj(instance);

        timeoutCheck.check();

        if (instance.getDeadline() == null && timeoutCheck.getDeadline() != null)
        {
            instance.setTimeoutChecked(true);
            dao.update(instance);
        }
    }

    public void checkReceives() throws Exception
    {
        for (ReceiveBase receiveBase : dao.getShouldCheckTimeoutReceives())
        {
            try
            {
                checkReceive(receiveBase);
            }
            catch (Throwable ex)
            {
                //检查一条记录错误，不影响检查其他记录，只记录日志

                Tools.log("check timeout error,receiveId:" + receiveBase.getReceiveId(), ex);
            }
        }
    }

    public void checkReceive(Long receiveId) throws Exception
    {
        checkReceive(dao.getReceiveBase(receiveId));
    }

    public void checkReceive(ReceiveBase receiveBase) throws Exception
    {
        ReceiveState state = receiveBase.getState();

        if (state == ReceiveState.withdrawn || state == ReceiveState.backed || state == ReceiveState.canceled)
            return;

        //开始计时的时间
        java.util.Date startTime = receiveBase.getSendTime();
        if (startTime == null)
            return;

        //已经结束，判别是否超时的时间为结束的时间，否则为当前时间
        java.util.Date time;
        java.util.Date endTime;
        if (state == ReceiveState.end)
        {
            time = receiveBase.getEndTime();
            endTime = receiveBase.getEndTime();

        }
        else
        {
            time = new Date();
            endTime = null;
        }

        if (time == null)
            return;


        TimeoutCheck timeoutCheck = new TimeoutCheck();

        timeoutCheck.setTypeId(OdTimeout.FLOW_ID);
        timeoutCheck.setStartTime(startTime);
        timeoutCheck.setDeadline(receiveBase.getDeadline());
        timeoutCheck.setTime(time);
        timeoutCheck.setEndTime(endTime);
        timeoutCheck.setRecordId(receiveBase.getReceiveId());
        timeoutCheck.setDeptId(receiveBase.getDeptId());
        timeoutCheck.setObj(receiveBase);

        timeoutCheck.check();
    }
}
