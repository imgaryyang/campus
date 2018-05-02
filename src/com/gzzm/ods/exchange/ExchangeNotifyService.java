package com.gzzm.ods.exchange;

import com.gzzm.ods.document.*;
import com.gzzm.ods.flow.*;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.message.*;
import com.gzzm.platform.message.sms.SmsMessageSender;
import net.cyan.commons.util.StringUtils;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * @author camel
 * @date 12-12-20
 */
public class ExchangeNotifyService
{
    /**
     * 接收短信提醒的应用
     */
    public static final String NOTIFY_APP = "od_exchange_notify";

    public static final String BACK_NOTIFY_APP = "od_back_notify";

    @Inject
    private ExchangeReceiveDao dao;

    @Inject
    private OdFlowDao odFlowDao;

    public ExchangeNotifyService()
    {
    }

    public void sendMessage(List<Long> receiveIds, Integer fromDeptId, Integer userId, boolean force)
            throws Exception
    {
        sendMessage(receiveIds, fromDeptId, userId, null, force);
    }

    public void sendMessage(List<Long> receiveIds, Integer fromDeptId, Integer userId, String content, boolean force)
            throws Exception
    {
        for (ReceiveBase receive : dao.getReceiveBases(receiveIds))
        {
            sendMessage(receive, fromDeptId, userId, content, force);
        }
    }

    public void sendMessageWithReceiveId(Long receiveId, Integer fromDeptId, Integer userId, String content,
                                         boolean force) throws Exception
    {
        sendMessage(dao.getReceiveBase(receiveId), fromDeptId, userId, content, force);
    }

    public void sendMessageWithSendId(Long sendId, Integer userId, String content, boolean force) throws Exception
    {
        Send send = dao.getSend(sendId);
        for (ReceiveBase receive : dao.getReceives(send.getDocumentId()))
        {
            sendMessage(receive, send.getDeptId(), userId, content, force);
        }
    }

    public void sendMessageWithSendId(Long sendId, Integer userId, boolean force) throws Exception
    {
        sendMessageWithSendId(sendId, userId, null, force);
    }

    public void sendMessageToNoAccepteds(Long sendId, Integer userId, String content, boolean force) throws Exception
    {
        Send send = dao.getSend(sendId);
        for (ReceiveBase receive : dao.getNoAcceptedReceives(send.getDocumentId()))
        {
            if (receive.getType() != ReceiveType.copy)
            {
                sendMessage(receive, send.getDeptId(), userId, content, force);
            }
        }
    }

    public void sendMessageToNoAccepteds(Long sendId, Integer userId, boolean force) throws Exception
    {
        sendMessageToNoAccepteds(sendId, userId, null, force);
    }

    private void sendMessage(ReceiveBase receive, Integer fromDeptId, Integer userId, String content, boolean force)
            throws Exception
    {
        if (receive.getType() == ReceiveType.copy)
        {
            //指定通知内容的时候，不通知抄送类型的
            if (StringUtils.isEmpty(content))
            {
                Copy copy = dao.getCopy(receive.getReceiveId());

                Message message = new Message();
                message.setApp("odpass");
                message.setMessage(Tools.getMessage("ods.exchange.notify." + receive.getType(), receive));
                message.setForce(force);
                message.setSender(userId);
                message.setFromDeptId(fromDeptId);
                message.setUserId(receive.getReceiver());
                message.setUrls("/ods/copy?stepId=" + copy.getStepId());
                message.send();
            }
        }
        else
        {
            Message message = new Message();
            message.setApp("odexchange");

            if (StringUtils.isEmpty(content))
                content = Tools.getMessage("ods.exchange.notify." + receive.getType(), receive);

            NoticeType noticeType;
            if (force)
            {
                noticeType = NoticeType.NOTIFY;
            }
            else
            {
                noticeType = receive.getDocument().getNoticeType();
                if (noticeType == NoticeType.NOTIFY)
                    force = true;
            }

            message.setMessage(content);
            message.setForce(force);
            message.setSender(userId);
            message.setFromDeptId(fromDeptId);
            message.setUrls(ReceiveBase.getReceiveUrls(receive.getType()));


            //NOTIFY_APP为接收短信的应用，是一个虚拟的菜单
            //考虑到管理员有权限查看各部门的收文，但是不接收短信，所以不以收文菜单来区分是否发送短信
            //而是根据此虚拟菜单来区分是否应该发送短信

            if (noticeType == null || noticeType == NoticeType.AUTO)
            {
                message.sendTo(NOTIFY_APP, receive.getDeptId());
            }
            else if (noticeType == NoticeType.NOTIFY)
            {
                message.setMethods(SmsMessageSender.SMS, ImMessageSender.IM);
                message.sendTo(NOTIFY_APP, receive.getDeptId());
            }
            else
            {
                message.setMethods(ImMessageSender.IM);
                message.sendTo(NOTIFY_APP, receive.getDeptId());
            }
        }


        ReceiveBase receive1 = new ReceiveBase();
        receive1.setReceiveId(receive.getReceiveId());
        receive1.setNotified(true);
        dao.update(receive1);
    }

    public void sendBackMessage(Long backId) throws Exception
    {
        sendMessage(dao.getBack(backId));
    }

    public void sendMessage(Back back) throws Exception
    {
        if (back.getDeptId() != null)
        {
            Message message = new Message();
            message.setApp("odexchange");
            message.setMessage(Tools.getMessage("ods.exchange.notify.back", back));
            message.setSender(back.getBackUserId());
            message.setFromDeptId(back.getReceiveBase().getDeptId());
            message.setUrl("/ods/backlist");

            message.sendTo(BACK_NOTIFY_APP, back.getDeptId());

            Back back1 = new Back();
            back1.setBackId(back.getBackId());
            back1.setNotified(true);
            dao.update(back1);
        }
    }

    public void sendEndMessage(ReceiveBase receive) throws Exception
    {
        Long stepId = null;
        Integer userId = null;
        Integer toDeptId = null;
        OdFlowInstance odFlowInstance = null;

        if (receive.getType() == ReceiveType.union || receive.getType() == ReceiveType.unionseal)
        {
            Union union = dao.getUnion(receive.getReceiveId());

            toDeptId = union.getUnionDeptId();
            SendFlowInstance sendFlowInstance = odFlowDao.getSendFlowInstanceByDocumentId(receive.getDocumentId());
            stepId = sendFlowInstance.getUnionStepId();
            userId = sendFlowInstance.getUnionUserId();

            odFlowInstance = odFlowDao.getOdFlowInstance(sendFlowInstance.getInstanceId());
        }
        else if (receive.getType() == ReceiveType.collect)
        {
            Collect collect = dao.getCollect(receive.getReceiveId());

            userId = collect.getCollectUserId();
            stepId = collect.getCollectStepId();

            odFlowInstance = odFlowDao.getOdFlowInstance(collect.getCollectInstanceId());
            toDeptId = odFlowInstance.getDeptId();
        }
        else if (receive.getType() == ReceiveType.uniondeal)
        {
            UnionDeal unionDeal = dao.getUnionDeal(receive.getReceiveId());

            userId = unionDeal.getUnionUserId();
            stepId = unionDeal.getUnionStepId();

            odFlowInstance = odFlowDao.getOdFlowInstance(unionDeal.getUnionInstanceId());
            toDeptId = odFlowInstance.getDeptId();
        }

        if (stepId != null && userId != null)
        {
            Message message = new Message();
            message.setApp("odflow");
            message.setMessage(Tools.getMessage("ods.exchange.notify." + receive.getType() + ".reply", receive));
            message.setUserId(userId);
            message.setFromDeptId(receive.getDeptId());
            message.setToDeptId(toDeptId);
            message.setUrl(OdFlowService.getStepUrl(stepId, odFlowInstance.getType()));

            message.send();
        }
    }

    public boolean notifyReceives() throws Exception
    {
        List<ReceiveBase> receives = dao.getNoNotifiedReceives();

        if (receives.size() == 0)
            return false;

        for (ReceiveBase receive : receives)
        {
            OfficeDocument document = receive.getDocument();
            Integer deptId = document.getCreateDeptId();
            if (deptId == null)
                deptId = receive.getDeptId();
            sendMessage(receive, deptId, 1, null, false);
        }

        return true;
    }

    public boolean notifyBacks() throws Exception
    {
        List<Back> backs = dao.getNoNotifiedBacks();

        if (backs.size() == 0)
            return false;

        for (Back back : backs)
        {
            sendMessage(back);
        }

        return true;
    }
}
