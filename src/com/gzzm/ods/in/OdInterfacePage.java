package com.gzzm.ods.in;

import com.gzzm.in.*;
import com.gzzm.ods.document.*;
import com.gzzm.ods.exchange.*;
import com.gzzm.ods.flow.Constants;
import com.gzzm.platform.commons.*;
import com.gzzm.platform.group.*;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.*;
import net.cyan.commons.util.io.Mime;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author camel
 * @date 2016/12/3
 */
@Service
public class OdInterfacePage implements InterfacePage
{
    @Inject
    private OdInterfaceDao dao;

    @Inject
    private ExchangeSendService sendService;

    @Inject
    private FileUploadService fileUploadService;

    @Inject
    private InterfaceAttachmentService attachmentService;

    @InterfaceDeptIds
    private Collection<Integer> deptIds;

    @InterfaceDeptId
    private Integer deptId;

    public OdInterfacePage()
    {
    }

    @Service(url = "/interface/ods/receives")
    public ReceiveList getReceives(Integer count) throws Exception
    {
        if (count == null)
            count = 30;

        List<ReceiveBase> receives = dao.getReceives(deptIds, count);
        List<ReceiveInfo> receiveInfos = new ArrayList<ReceiveInfo>(receives.size());

        for (ReceiveBase receive : receives)
        {
            receiveInfos.add(toReceiveInfo(receive));
        }

        ReceiveList receiveList = new ReceiveList();
        receiveList.setReceives(receiveInfos);

        return receiveList;
    }

    private ReceiveInfo toReceiveInfo(ReceiveBase receive) throws Exception
    {
        OfficeDocument document = receive.getDocument();
        DocumentText text = document.getText();

        ReceiveInfo receiveInfo = new ReceiveInfo();
        receiveInfo.setReceiveId(receive.getReceiveId());
        receiveInfo.setDeptId(receive.getDeptId().toString());
        receiveInfo.setType(receive.getSendType());
        receiveInfo.setTitle(document.getTitle());
        receiveInfo.setSourceDept(document.getSourceDept());
        receiveInfo.setSendNumber(document.getSendNumber());
        receiveInfo.setSubject(document.getSubject());
        receiveInfo.setSecret(document.getSecret());
        receiveInfo.setPriority(document.getPriority());
        receiveInfo.setSendTime(receive.getSendTime());

        if (!StringUtils.isEmpty(text.getOtherFileName()))
        {
            receiveInfo.setTextType(IOUtils.getExtName(text.getOtherFileName()));
        }
        else
        {
            receiveInfo.setTextType(text.getType());
        }

        receiveInfo.setUrl("/interface/ods/text/" + IDEncoder.encode(document.getDocumentId()));

        if (document.getAttachments() != null)
            receiveInfo.setAttachments(InterfaceAttachmentService.toAttachmentInfoList(document.getAttachments()));

        return receiveInfo;
    }

    private void checkReceive(ReceiveBase receive) throws Exception
    {
        checkDeptId(receive == null ? null : receive.getDeptId());
    }

    private void checkDeptId(Integer deptId) throws Exception
    {
        InterfaceDeptCheck.checkDeptId(deptId, deptIds);
    }

    @Service(url = "/interface/ods/receive/{receiveId}/get")
    public BooleanResult getReceive(Long receiveId) throws Exception
    {
        Receive receive = dao.getReceive(receiveId);

        checkReceive(receive == null ? null : receive.getReceiveBase());

        receive.setSyned(true);
        dao.update(receive);

        return new BooleanResult(true);
    }

    @Service(url = "/interface/ods/receive/{receiveId}/accept")
    public BooleanResult acceptReceive(Long receiveId, Date acceptTime) throws Exception
    {
        if (acceptTime == null)
            acceptTime = new Date();

        ReceiveBase receive = dao.getReceiveBase(receiveId);

        checkReceive(receive);

        receive.setAcceptTime(acceptTime);
        receive.setState(ReceiveState.flowing);
        dao.update(receive);

        return new BooleanResult(true);
    }

    @Service(url = "/interface/ods/receive/{receiveId}/back")
    @Transactional
    public BooleanResult backReceive(Long receiveId, Date backTime, String reason) throws Exception
    {
        if (backTime == null)
            backTime = new Date();

        ReceiveBase receive = dao.getReceiveBase(receiveId);

        checkReceive(receive);

        receive.setState(ReceiveState.backed);
        dao.update(receive);

        Back back = new Back();
        back.setReceiveId(receiveId);
        back.setBackTime(backTime);
        back.setReason(reason.toCharArray());
        back.setState(BackState.NODEALED);

        if (receive.getMethod() == ReceiveMethod.system)
        {
            //从系统收文，需要设置发文ID
            Send send = sendService.getDao().getSendByDocumentId(receive.getDocumentId());

            back.setSendId(send.getSendId());
            back.setDeptId(send.getDeptId());
        }

        dao.save(back);

        return new BooleanResult(true);
    }

    @Service(url = "/interface/ods/send", method = HttpMethod.post)
    @Transactional
    @ContentType(Mime.JSON_DEFAULT)
    public BooleanResult send(@RequestBody SendInfo sendInfo) throws Exception
    {
        Integer deptId;

        String deptIdS = sendInfo.getDeptId();
        if (!StringUtils.isEmpty(deptIdS))
        {
            deptId = Integer.valueOf(deptIdS);

            checkDeptId(deptId);
        }
        else
        {
            deptId = this.deptId;
        }

        DocumentText documentText = new DocumentText();
        if (!StringUtils.isEmpty(sendInfo.getContentId()))
        {
            InputFile file = fileUploadService.getFile(sendInfo.getContentId());
            if (file != null)
            {
                documentText.setType(sendInfo.getTextType());
                documentText.setTextBody(file.getInputStream());
                documentText.setFileSize(file.size());
            }
        }
        dao.add(documentText);

        OfficeDocument document = new OfficeDocument();
        document.setTextId(documentText.getTextId());
        document.setTitle(sendInfo.getTitle());
        document.setSourceDept(sendInfo.getSourceDept());
        document.setSendNumber(sendInfo.getSendNumber());
        document.setSubject(sendInfo.getSubject());
        document.setSecret(sendInfo.getSecret());
        document.setPriority(sendInfo.getPriority());

        if (sendInfo.getAttachments() != null)
        {
            document.setAttachmentId(attachmentService.save(sendInfo.getAttachments(), "od", null, deptId));
        }

        dao.add(document);

        Send send = new Send();
        send.setDeptId(deptId);
        send.setSendTime(sendInfo.getSendTime());
        send.setDocumentId(document.getDocumentId());
        send.setState(SendState.sended);
        dao.add(send);

        String[] receiveDeptIds = sendInfo.getReceiveDeptIds();
        List<Member> members = new ArrayList<Member>(receiveDeptIds.length);
        for (String receiveDeptId : receiveDeptIds)
        {
            members.add(new Member(MemberType.dept, Integer.valueOf(receiveDeptId), null));
        }

        sendService.sendDocumentTo(document, members, Constants.Document.MAINSEND, null);

        return new BooleanResult(true);
    }
}
