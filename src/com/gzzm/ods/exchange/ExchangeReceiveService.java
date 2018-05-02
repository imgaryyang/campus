package com.gzzm.ods.exchange;

import com.gzzm.ods.flow.*;
import com.gzzm.platform.commons.NoErrorException;
import com.gzzm.platform.flow.FlowApi;
import com.gzzm.platform.organ.DeptInfo;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.Null;
import net.cyan.nest.annotation.Inject;
import net.cyan.valmiki.flow.FlowStep;

import java.util.*;

/**
 * 公文交换收文服务，和收文相关的一些逻辑
 *
 * @author camel
 * @date 11-10-11
 */
public class ExchangeReceiveService
{
    @Inject
    private ExchangeReceiveDao dao;

    @Inject
    private OdFlowDao odFlowDao;

    public ExchangeReceiveService()
    {
    }

    public ExchangeReceiveDao getDao()
    {
        return dao;
    }

    /**
     * 撤销接收
     *
     * @param receiveId 收文ID
     * @throws Exception 由数据库错误引起
     */
    @Transactional
    public void cannelReceive(Long receiveId) throws Exception
    {
        //锁住收文记录，避免撤回过程中发生其他操作
        dao.lockReceiveBase(receiveId);

        ReceiveBase receiveBase = dao.getReceiveBase(receiveId);

        if (receiveBase == null)
            return;

        ReceiveState state = receiveBase.getState();

        //公文没有接收，不用撤回
        if (state == ReceiveState.noAccepted)
            return;

        if (state == ReceiveState.backed)
            throw new NoErrorException("ods.exchange.receiveBacked");

        if (state == ReceiveState.withdrawn)
            throw new NoErrorException("ods.exchange.receiveWithdrawn");

        if (state == ReceiveState.canceled)
            throw new NoErrorException("ods.exchange.receiveCanceled");

        if (receiveBase.getMethod() == ReceiveMethod.manual)
        {
            //手工收文，删除
            dao.delete(receiveBase);
        }
        else
        {
            //不是手工收文，撤销接收
            receiveBase.setState(ReceiveState.noAccepted);
            receiveBase.setReceiver(Null.Integer);
            receiveBase.setReceiverName(Null.String);
            receiveBase.setAcceptTime(Null.Timestamp);
            dao.update(receiveBase);
        }

        cancelOdFlowInstance(receiveBase.getReceiveId());

        Long stepId = null;
        switch (receiveBase.getType())
        {
            case receive:
            {
                Receive receive = dao.getReceive(receiveId);
                stepId = receive.getStepId();
                break;
            }
            case collect:
            {
                //会签
                Collect collect = dao.getCollect(receiveId);
                stepId = collect.getStepId();
                break;
            }
            case union:
            {
                Union union = dao.getUnion(receiveId);
                stepId = union.getStepId();
                break;
            }
            case uniondeal:
            {
                UnionDeal unionDeal = dao.getUnionDeal(receiveId);
                stepId = unionDeal.getStepId();
                break;
            }
            case copy:
            {
                Copy copy = dao.getCopy(receiveId);
                stepId = copy.getStepId();
                break;
            }
        }

        if (stepId != null)
        {
            //将主办部门的流程中对应步骤表示为未接收
            FlowStep step = new FlowStep();
            step.setStepId(stepId.toString());
            step.setState(FlowStep.NOACCEPT);

            OdSystemFlowDao.getInstance().updateStep(step);
        }
    }

    public void withdrawReceive(Long receiveId) throws Exception
    {
        withdrawReceive(receiveId, false);
    }

    /**
     * 撤回某份公文
     *
     * @param receiveId 要撤回的公文的ID
     * @throws Exception 数据库操作错误
     */
    @Transactional
    public void withdrawReceive(Long receiveId, boolean force) throws Exception
    {
        //锁住收文记录，避免撤回过程中发生其他操作
        dao.lockReceiveBase(receiveId);

        ReceiveBase receiveBase = dao.getReceiveBase(receiveId);

        ReceiveState state = receiveBase.getState();

        if (state == ReceiveState.backed)
            throw new NoErrorException("ods.exchange.receiveBacked");

        if (state == ReceiveState.withdrawn)
            throw new NoErrorException("ods.exchange.receiveWithdrawn");

        if (state == ReceiveState.canceled)
            throw new NoErrorException("ods.exchange.receiveCanceled");

        if (state != ReceiveState.noAccepted && !force)
            throw new NoErrorException("ods.exchange.receiveReceivedWhenWithdraw", receiveBase.getDept().getDeptName());

        withdraw(receiveBase, ReceiveState.withdrawn);
    }

    /**
     * 撤回若干份公文
     *
     * @param receiveIds 要撤回的公文的ID列表
     * @throws Exception 数据库操作错误
     */
    @Transactional
    public void withdrawReceives(Long[] receiveIds) throws Exception
    {
        //锁住收文记录，避免撤回过程中发生其他操作
        for (Long receiveId : receiveIds)
            dao.lockReceiveBase(receiveId);

        List<ReceiveBase> receiveBases = dao.getReceiveBases(Arrays.asList(receiveIds));

        for (ReceiveBase receiveBase : receiveBases)
        {
            ReceiveState state = receiveBase.getState();

            if (state == ReceiveState.backed)
                throw new NoErrorException("ods.exchange.receiveBacked");

            if (state == ReceiveState.withdrawn)
                throw new NoErrorException("ods.exchange.receiveWithdrawn");

            if (state == ReceiveState.canceled)
                throw new NoErrorException("ods.exchange.receiveCanceled");

            if (state != ReceiveState.noAccepted)
                throw new NoErrorException("ods.exchange.receiveReceivedWhenWithdraw",
                        receiveBase.getDept().getDeptName());
        }

        for (ReceiveBase receiveBase : receiveBases)
        {
            withdraw(receiveBase, ReceiveState.withdrawn);
        }
    }

    /**
     * 撤回某份公文的所有的收文
     *
     * @param documentId 公文ID
     * @param force      是否强制撤回，如果为true，表示即使有公文被接收了也将其撤回，并取消内部处理流程，如果为false表示只要有公文被接收了就不能撤回
     * @return 返回其中一个已经接收的部门
     * @throws Exception 数据库操作错误
     */
    @Transactional
    public DeptInfo withdrawAllReceives(Long documentId, boolean force) throws Exception
    {
        //获得所有收文的ID，用于锁住收文
        List<Long> receiveIds = dao.getReceiveIds(documentId);

        //锁住所有要撤回的公文，避免撤回过程中发生其他操作
        for (Long receiveId : receiveIds)
            dao.lockReceiveBase(receiveId);

        List<ReceiveBase> receiveBases = dao.getReceiveBases(receiveIds);

        if (!force)
        {
            //循环一遍，判断是否有已经被接收的
            for (ReceiveBase receiveBase : receiveBases)
            {
                ReceiveState state = receiveBase.getState();
                if (state == ReceiveState.accepted || state == ReceiveState.flowing || state == ReceiveState.end)
                {
                    return receiveBase.getDept();
//                    throw new NoErrorException("ods.exchange.receiveReceivedWhenWithdraw",
//                            receiveBase.getDept().getDeptName());
                }
            }
        }

        //将所有收文撤回
        for (ReceiveBase receiveBase : receiveBases)
        {
            ReceiveState state = receiveBase.getState();

            if (state != ReceiveState.backed && state != ReceiveState.withdrawn && state != ReceiveState.canceled)
                withdraw(receiveBase, ReceiveState.withdrawn);
        }

        return null;
    }

    /**
     * 撤回某份公文所有未接收的收文
     *
     * @param documentId 公文ID
     * @throws Exception 数据库操作错误
     */
    @Transactional
    public void withdrawAllNoAcceptedReceives(Long documentId) throws Exception
    {
        //获得所有未接收的收文的ID，用于锁住收文
        List<Long> receiveIds = dao.getNoAcceptedReceiveIds(documentId);

        //锁住所有要撤回的公文，避免撤回过程中发生其他操作
        for (Long receiveId : receiveIds)
            dao.lockReceiveBase(receiveId);

        List<ReceiveBase> receiveBases = dao.getReceiveBases(receiveIds);

        //将所有收文撤回
        for (ReceiveBase receiveBase : receiveBases)
        {
            ReceiveState state = receiveBase.getState();

            if (state == ReceiveState.noAccepted)
                withdraw(receiveBase, ReceiveState.withdrawn);
        }
    }

    /**
     * 作废某份公文的所有的收文
     *
     * @param documentId 公文ID
     * @throws Exception 数据库操作错误
     */
    @Transactional
    public void cancelAllReceives(Long documentId) throws Exception
    {
        //获得所有收文的ID，用于锁住收文
        List<Long> receiveIds = dao.getReceiveIds(documentId);

        //锁住所有要撤回的公文，避免撤回过程中发生其他操作
        for (Long receiveId : receiveIds)
            dao.lockReceiveBase(receiveId);

        List<ReceiveBase> receiveBases = dao.getReceiveBases(receiveIds);

        //将所有收文撤回
        for (ReceiveBase receiveBase : receiveBases)
        {
            ReceiveState state = receiveBase.getState();

            if (state != ReceiveState.backed && state != ReceiveState.withdrawn && state != ReceiveState.canceled)
                withdraw(receiveBase, ReceiveState.canceled);
        }
    }

    @Transactional
    public void withdraw(ReceiveBase receiveBase, ReceiveState state) throws Exception
    {
        if (receiveBase.getState() != ReceiveState.noAccepted && receiveBase.getType() != ReceiveType.copy)
        {
            //已经接收，删除内部处理流程
            cancelOdFlowInstance(receiveBase.getReceiveId());
        }

        //将状态改成已撤回
        receiveBase.setState(state);
        dao.update(receiveBase);

        Long stepId = null;
        Long receiveId = receiveBase.getReceiveId();
        switch (receiveBase.getType())
        {
            case receive:
            {
                Receive receive = dao.getReceive(receiveId);
                stepId = receive.getStepId();
                break;
            }
            case collect:
            {
                //会签
                Collect collect = dao.getCollect(receiveId);
                stepId = collect.getStepId();
                break;
            }
            case union:
            {
                Union union = dao.getUnion(receiveId);
                stepId = union.getStepId();
                break;
            }
            case uniondeal:
            {
                UnionDeal unionDeal = dao.getUnionDeal(receiveId);
                stepId = unionDeal.getStepId();
                break;
            }
            case copy:
            {
                Copy copy = dao.getCopy(receiveId);
                stepId = copy.getStepId();
                break;
            }
        }

        if (stepId != null)
            OdSystemFlowDao.getInstance().deleteStep(stepId.toString());
    }

    /**
     * 撤销收文的内部流转实例
     *
     * @param receiveId 收文ID
     * @throws Exception 数据库操作错误
     */

    private void cancelOdFlowInstance(Long receiveId) throws Exception
    {
        OdFlowInstance odFlowInstance = odFlowDao.getOdFlowInstanceByReceiveId(receiveId);
        if (odFlowInstance != null)
        {
            odFlowInstance.setState(OdFlowInstanceState.canceled);
            dao.update(odFlowInstance);

            FlowApi.getController(odFlowInstance.getInstanceId(), OdSystemFlowDao.getInstance()).deleteInstance();
        }
    }

    /**
     * 退回公文
     *
     * @param receiveId 要退回的收文的ID
     * @param reason    退回的原因
     * @param userId    操作退回的用户的id
     * @return 生成的退回记录的ID，返回null表示没有退回成功或者没有退回的必要
     * @throws Exception 数据库插入数据错误
     */
    @Transactional
    public Long back(Long receiveId, String reason, Integer userId) throws Exception
    {
        //锁住收文记录，避免撤回过程中发生其他操作
        dao.lockReceiveBase(receiveId);

        ReceiveBase receiveBase = dao.getReceiveBase(receiveId);

        ReceiveState state = receiveBase.getState();

        if (state == ReceiveState.backed)
            throw new NoErrorException("ods.exchange.receiveBacked");

        if (state == ReceiveState.withdrawn)
            throw new NoErrorException("ods.exchange.receiveWithdrawn");

        if (state == ReceiveState.canceled)
            throw new NoErrorException("ods.exchange.receiveCanceled");

        //公文已经接收，不能退回
        if (state != ReceiveState.noAccepted)
        {
            cannelReceive(receiveId);
        }

        if (receiveBase.getMethod() == ReceiveMethod.manual)
        {
            //手工收文，不可退回
        }
        else
        {
            //不是手工收文，退回公文
            receiveBase.setState(ReceiveState.backed);
            dao.update(receiveBase);

            Back back = new Back();
            back.setReceiveId(receiveBase.getReceiveId());
            back.setBackTime(new Date());
            back.setReason(reason.toCharArray());
            back.setBackUserId(userId);

            Long stepId = null;

            switch (receiveBase.getType())
            {
                case receive:
                {
                    if (receiveBase.getMethod() == ReceiveMethod.system)
                    {
                        //从系统收文，需要设置发文ID
                        Send send = dao.getSendByDocumentId(receiveBase.getDocumentId());

                        if (send != null)
                        {
                            back.setSendId(send.getSendId());
                            back.setDeptId(send.getDeptId());
                        }
                    }

                    Receive receive = dao.getReceive(receiveId);
                    stepId = receive.getStepId();

                    break;
                }
                case collect:
                {
                    //会签
                    Collect collect = dao.getCollect(receiveId);
                    back.setDeptId(collect.getCollectInstance().getDeptId());

                    stepId = collect.getStepId();

                    break;
                }
                case union:
                {
                    Union union = dao.getUnion(receiveId);
                    back.setDeptId(union.getUnionDeptId());

                    stepId = union.getStepId();

                    break;
                }
                case uniondeal:
                {
                    UnionDeal unionDeal = dao.getUnionDeal(receiveId);
                    back.setDeptId(unionDeal.getUnionInstance().getDeptId());

                    stepId = unionDeal.getStepId();

                    break;
                }
                case copy:
                {
                    Send send = dao.getSendByDocumentId(receiveBase.getDocumentId());
                    back.setSendId(send.getSendId());
                    back.setDeptId(send.getDeptId());

                    Copy copy = dao.getCopy(receiveId);
                    stepId = copy.getStepId();

                    break;
                }
            }

            dao.add(back);

            if (stepId != null)
            {
                //将主办部门的流程中对应步骤表示为已退回
                FlowStep step = new FlowStep();
                step.setStepId(stepId.toString());
                step.setState(FlowStep.BACKED);
                step.setDisposeTime(new Date());
                step.setAcceptTime(new Date());

                OdSystemFlowDao.getInstance().updateStep(step);
            }

            return back.getBackId();
        }

        return null;
    }
}
