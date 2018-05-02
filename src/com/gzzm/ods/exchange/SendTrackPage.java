package com.gzzm.ods.exchange;

import com.gzzm.ods.document.OfficeDocument;
import com.gzzm.platform.commons.*;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.group.Member;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.organ.DeptInfo;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.StringUtils;
import net.cyan.crud.annotation.Equals;
import net.cyan.crud.exporters.ExportParameters;
import net.cyan.crud.view.*;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 发文跟踪
 *
 * @author camel
 * @date 11-10-9
 */
@Service(url = "/ods/sendtrack")
public class SendTrackPage extends BaseQueryCrud<ReceiveBase, Long>
{
    @Inject
    private ExchangeSendService sendService;

    @Inject
    private ExchangeReceiveService receiveService;

    @Inject
    private ExchangeNotifyService notifyService;

    @Inject
    private CancelSendRule cancelSendRule;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    /**
     * 发文的文档ID
     */
    @Equals("documentId")
    private Long documentId;

    private Long sendId;

    private Send send;

    @NotSerialized
    private OfficeDocument document;

    /**
     * 主键列表
     */
    @NotSerialized
    private Long[] receiveIds;

    /**
     * 通知提醒的内容
     */
    private String notifyMessage;

    public SendTrackPage()
    {
        setPageSize(-1);

        addOrderBy("state");
        addOrderBy("sendTime");
        addOrderBy("dept.leftValue");
    }

    public Long getDocumentId() throws Exception
    {
        if (documentId == null)
            documentId = getSend().getDocumentId();

        return documentId;
    }

    public void setDocumentId(Long documentId)
    {
        this.documentId = documentId;
    }

    public Long getSendId()
    {
        return sendId;
    }

    public void setSendId(Long sendId)
    {
        this.sendId = sendId;
    }

    public Long[] getReceiveIds()
    {
        return receiveIds;
    }

    public void setReceiveIds(Long[] receiveIds)
    {
        this.receiveIds = receiveIds;
    }

    public String getNotifyMessage()
    {
        return notifyMessage;
    }

    public void setNotifyMessage(String notifyMessage)
    {
        this.notifyMessage = notifyMessage;
    }

    private Send getSend() throws Exception
    {
        if (send == null)
            send = sendService.getDao().getSend(sendId);
        return send;
    }

    public OfficeDocument getDocument() throws Exception
    {
        if (document == null)
        {
            if (sendId == null)
                document = sendService.getDao().getDocument(documentId);
            else
                document = getSend().getDocument();
        }

        return document;
    }

    @NotSerialized
    @Service
    public Integer getAcceptedReceiveCount() throws Exception
    {
        return receiveService.getDao().getAcceptedReceiveCount(getDocumentId());
    }

    @NotSerialized
    @Service
    public Integer getNoAcceptedReceiveCount() throws Exception
    {
        return receiveService.getDao().getNoAcceptedReceiveCount(getDocumentId());
    }

    @Service(method = HttpMethod.post)
    @ObjectResult
    public void reSend(Long sendId, List<Member> receivers) throws Exception
    {
        Send send = sendService.getDao().getSend(sendId);
        List<Long> receiveIds = sendService.reSend(send.getDocumentId(), receivers);

        notifyService.sendMessage(receiveIds, send.getDeptId(), userOnlineInfo.getUserId(), null, false);
    }

    @Service(url = "/ods/receive/{$0}/withdraw", method = HttpMethod.post)
    @ObjectResult
    public void withdraw(Long receiveId) throws Exception
    {
        receiveService.withdrawReceive(receiveId);
    }

    @Service
    @ObjectResult
    public void withdrawSelected() throws Exception
    {
        if (receiveIds != null)
            receiveService.withdrawReceives(receiveIds);
    }

    @Service(url = "/ods/send/{sendId}/withdraw/all?force={$0}", method = HttpMethod.post)
    @ObjectResult
    @Transactional
    public String withdrawAll(boolean force) throws Exception
    {
        Send send = sendService.getDao().getSend(sendId);

        if (send.getState() == SendState.canceled)
            throw new NoErrorException("ods.exchange.sendCaneled");

        //撤回全部收文
        DeptInfo dept = receiveService.withdrawAllReceives(send.getDocumentId(), force);

        if (dept != null)
            return dept.getDeptName();

        //取消发文
        sendService.cancelSend(send, userOnlineInfo);

        return null;
    }

    @Service(url = "/ods/send/{sendId}/withdraw/noAccepted", method = HttpMethod.post)
    @ObjectResult
    @Transactional
    public void withdrawNoAccepted() throws Exception
    {
        Send send = sendService.getDao().getSend(sendId);

        //撤回全部收文
        receiveService.withdrawAllNoAcceptedReceives(send.getDocumentId());
    }

    @Service(url = "/ods/send/{sendId}/cancel", method = HttpMethod.post)
    @ObjectResult
    @Transactional
    public void cancel() throws Exception
    {
        Send send = sendService.getDao().getSend(sendId);

        if (send.getState() == SendState.canceled)
            throw new NoErrorException("ods.exchange.sendCaneled");

        //撤回全部收文
        receiveService.cancelAllReceives(send.getDocumentId());

        send.setState(SendState.canceled);
        sendService.getDao().update(send);
    }

    @Service(method = HttpMethod.post)
    public void notifySelected() throws Exception
    {
        if (receiveIds != null)
        {
            Send send = sendService.getDao().getSend(sendId);
            notifyService
                    .sendMessage(Arrays.asList(receiveIds), send.getDeptId(), userOnlineInfo.getUserId(), notifyMessage,
                            true);
        }
    }

    @Service(method = HttpMethod.post)
    public void notifyNoAccepted() throws Exception
    {
        notifyService.sendMessageToNoAccepteds(sendId, userOnlineInfo.getUserId(), notifyMessage, true);
    }

    @Service(url = "/ods/send/{sendId}/notify")
    public String showNotify() throws Exception
    {
        if (StringUtils.isEmpty(notifyMessage))
            notifyMessage = Tools.getMessage("ods.exchange.notify.send", getSend());

        return "notify";
    }

    @Override
    @Service(url = {"/ods/document/{documentId}/track", "/ods/send/{sendId}/track"})
    public String showList(Integer pageNo) throws Exception
    {
        return super.showList(pageNo);
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        return "receivebase.type in (0,5)";
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(sendId != null);

        view.setCheckBoxName("receiveIds");

        view.setTitle("发文跟踪-{document.title}");

        Integer acceptedCount = getAcceptedReceiveCount();
        Integer noAcceptedCount = getNoAcceptedReceiveCount();

        view.setRemark(Tools.getMessage("ods.exchange.trackremark", acceptedCount + noAcceptedCount, acceptedCount,
                noAcceptedCount));

        if (sendId != null)
        {
            Send send = getSend();
            view.addButton("补发", "reSend(" + send.getSendId() + "," + send.getDeptId() + ")")
                    .setIcon(Buttons.getIcon("right2"));


            //还有公文没接收，可以撤回
            if (noAcceptedCount > 0)
                view.addButton("从选择的部门撤回公文", "withdrawSelected()").setIcon(Buttons.getIcon("left3"));

            if (acceptedCount == 0 || cancelSendRule == null || cancelSendRule.cancelable(send, userOnlineInfo))
            {
                view.addButton("全部撤回", "withdrawAll()").setIcon(Buttons.getIcon("left3"));
                view.addButton("作废公文", "cancel()").setIcon(Buttons.getIcon("remove"));
            }

            view.addButton("发短信给选择的部门", "notifySelected()").setIcon(Buttons.getIcon("phone"));

            if (noAcceptedCount > 0)
            {
                //没有公文接收，可以全部发短信
                view.addButton("发短信给未接收的部门", "notifyNoAccepted()").setIcon(Buttons.getIcon("phone"));
            }
        }

        view.addButton(Buttons.export("xls"));

        view.addColumn("收文部门", "type.name()=='copy'?receiverString:dept.deptName")
                .setOrderFiled("dept.leftValue");
        view.addColumn("类型", "sendType").setAlign(Align.center).setWidth("120");
        view.addColumn("发送时间", "sendTime");
        view.addColumn("状态", "state");
        view.addColumn("接收人", new FieldCell("receiverString").setOrderable(false));
        view.addColumn("接收时间", "acceptTime");
        view.addColumn("联系人和电话", new FieldCell("receiversInfo").setOrderable(false)).setWrap(true).setWidth("150");

        if (sendId != null)
        {
            view.addColumn("撤回", new ConditionComponent().add("state.toString()=='noAccepted'",
                    new CButton("撤回", "withdraw(${receiveId})"))).setWidth("60");
        }

        view.importJs("/ods/exchange/track.js");

        return view;
    }

    @Override
    protected ExportParameters getExportParameters() throws Exception
    {
        return new ExportParameters("发文跟踪");
    }
}
