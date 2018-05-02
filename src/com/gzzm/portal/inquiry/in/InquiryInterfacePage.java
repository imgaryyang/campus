package com.gzzm.portal.inquiry.in;

import com.gzzm.in.*;
import com.gzzm.platform.attachment.Attachment;
import com.gzzm.platform.attachment.AttachmentSaver;
import com.gzzm.platform.commons.event.Events;
import com.gzzm.portal.inquiry.*;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.Json;
import net.cyan.arachne.annotation.RequestBody;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.IOUtils;
import net.cyan.commons.util.StringUtils;
import net.cyan.commons.util.io.Base64;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author lfx
 * @date 17-4-21
 */
@Service
public class InquiryInterfacePage implements InterfacePage
{
    @InterfaceDeptIds
    private Collection<Integer> deptIds;

    @InterfaceDeptId
    private Integer deptId;

    @Inject
    private InquiryService service;

    @Inject
    private InquiryInfterfaceDao dao;

    @Inject
    private AttachmentSaver attachmentSaver;

    public InquiryInterfacePage()
    {
    }

    @Service(url = "/interface/inquiry/send", method = HttpMethod.post)
    @Transactional
    @Json
    public ReceiveInquiryResult receiveInquiry(@RequestBody InquiryInfo inquiryInfo) throws Exception
    {
        Integer deptId;
        String deptIdS = inquiryInfo.getOrgCode();
        if (!StringUtils.isEmpty(deptIdS))
        {
            deptId = dao.getDeptIdByDeptCode(deptIdS);
            if (!StringUtils.isEmpty(deptIdS))
            {
                checkDeptId(deptId);
            }
            else
            {
                deptId = this.deptId;
            }
        }
        else
        {
            deptId = this.deptId;
        }
        Inquiry inquiry = new Inquiry();
        inquiry.setCode(inquiryInfo.getCode());
        inquiry.setTitle(inquiryInfo.getTitle());
        inquiry.setInquirerName(inquiryInfo.getInquirerName());
        inquiry.setRealName(inquiryInfo.getRealName());
        inquiry.setSendTime(inquiryInfo.getSendTime());
        inquiry.setContent(inquiryInfo.getContent().toCharArray());
        inquiry.setPhone(inquiryInfo.getPhone());
        inquiry.setPostcode(inquiryInfo.getPostcode());
        inquiry.setAddress(inquiryInfo.getAddress());
        inquiry.setEmail(inquiryInfo.getEmail());
        inquiry.setDeptId(deptId);
        inquiry.setCatalogId(inquiryInfo.getCatalogId());
        Date time = new Date();
        inquiry.setSendTime(time);
        inquiry.setLastTime(time);
        inquiry.setState(InquiryState.NOACCEPTED);
        ImgAttachmentInfo[] attachmentInfoList = inquiryInfo.getAttachmentInfoList();
        if (attachmentInfoList != null && attachmentInfoList.length > 0)
        {
            ArrayList<Attachment> attachments = new ArrayList<Attachment>();
            for (ImgAttachmentInfo img : attachmentInfoList)
            {
                String attachmentContent = img.getAttachmentContent();
                if (StringUtils.isEmpty(attachmentContent))
                {
                    continue;
                }
                Attachment attachment = new Attachment();
                attachments.add(attachment);
                attachment.setAttachmentName(img.getAttachmentName());
                attachment.setFileName(img.getAttachmentName());
                attachment.setDeptId(deptId);
                attachment.setTag("inquiry");
                attachment.setInputable(IOUtils.createInput(Base64.base64ToByteArray(attachmentContent)));
            }
            if (attachments.size() > 0)
            {
                attachmentSaver.setAttachments(attachments);
                attachmentSaver.save();
                inquiry.setAttachmentId(attachmentSaver.getAttachmentId());
            }
        }
        dao.add(inquiry);

        //同时往收信部门写一条待处理的记录
        InquiryProcess process = new InquiryProcess();
        process.setDeptId(inquiry.getDeptId());
        process.setInquiryId(inquiry.getInquiryId());
        process.setStartTime(time);
        process.setState(ProcessState.NOACCEPTED);
        process.setLastProcess(true);
        dao.add(process);

        Events.invoke(service.getDao().getProcess(process.getProcessId()), "receive");

        inquiry.setCode(inquiryInfo.getCode());

        InquiryInterface inquiryInterface = new InquiryInterface();
        inquiryInterface.setInquiryId(inquiry.getInquiryId());
        inquiryInterface.setDeptId(inquiry.getDeptId());
        inquiryInterface.setCode(inquiry.getCode());
        inquiryInterface.setSendTime(new Date());
        inquiryInterface.setReceive(false);
        service.getDao().save(inquiryInterface);
        ReceiveInquiryResult result = new ReceiveInquiryResult();
        result.setSuccess(true);
        result.setInquiryId(inquiry.getInquiryId());
        return result;
    }

    @Service(url = "/interface/inquiry/uploadAttachments", method = HttpMethod.post)
    @Transactional
    @Json
    public BooleanResult uploadAttachments(@RequestBody UploadInfo uploadInfo) throws Exception
    {
        BooleanResult result = new BooleanResult();
        if (uploadInfo == null || uploadInfo.getInquiryId() == null)
        {
            result.setSuccess(false);
            result.setError("来信ID不能为空");
        }
        else if (uploadInfo.getAttachmentInfoList() == null || uploadInfo.getAttachmentInfoList().size() == 0)
        {
            result.setSuccess(false);
            result.setError("附件内容不能为空");
        }
        else
        {
            Inquiry inquiry = dao.getInquiry(deptId, uploadInfo.getInquiryId());
            if (inquiry == null)
            {
                result.setSuccess(false);
                result.setError("未查询到相关来信");
            }
            else
            {
                Long attachmentId = inquiry.getAttachmentId();
                ArrayList<Attachment> attachments = new ArrayList<Attachment>();
                for (ImgAttachmentInfo img : uploadInfo.getAttachmentInfoList())
                {
                    String attachmentContent = img.getAttachmentContent();
                    if (StringUtils.isEmpty(attachmentContent))
                    {
                        continue;
                    }
                    Attachment attachment = new Attachment();
                    attachments.add(attachment);
                    attachment.setAttachmentName(img.getAttachmentName());
                    attachment.setFileName(img.getAttachmentName());
                    attachment.setDeptId(deptId);
                    attachment.setTag("inquiry");
                    attachment.setInputable(IOUtils.createInput(Base64.base64ToByteArray(attachmentContent)));
                }
                if (attachments.size() > 0)
                {
                    if (attachmentId != null)
                    {
                        attachmentSaver.setAttachmentId(attachmentId);
                    }
                    attachmentSaver.setAttachments(attachments);
                    attachmentSaver.save();
                    if (attachmentId == null)
                    {
                        inquiry.setAttachmentId(attachmentSaver.getAttachmentId());
                        dao.update(inquiry);
                    }
                    result.setSuccess(true);
                }
            }
        }
        return result;
    }

    @Service(url = "/interface/inquiry/queryInquiryById", method = HttpMethod.all)
    @Transactional
    @Json
    public QueryInquiryResult queryInquiryById(Long inquiryId) throws Exception
    {
        QueryInquiryResult replay = new QueryInquiryResult();
        if (inquiryId == null || inquiryId == 0)
        {
            replay.setError("inquiryId不能为空");
            return replay;
        }
        Inquiry inquiry = dao.load(Inquiry.class, inquiryId);
        if (inquiry != null)
        {
            InquiryProcess lastProcess = inquiry.getLastProcess();
            replay.setCode(inquiry.getCode());
            replay.setTitle(inquiry.getTitle());
            replay.setEndTime(lastProcess.getEndTime());
            char[] replyContent = lastProcess.getReplyContent();
            if (replyContent != null)
            {
                replay.setReplyContent(new String(replyContent));
            }
            else
            {
                replay.setReplyContent("");
            }
            replay.setOrgCode(lastProcess.getDept().getDeptCode());
            replay.setState(inquiry.getState().ordinal());
            SortedSet<Attachment> attachments = lastProcess.getAttachments();
            if (attachments != null && attachments.size() > 0)
            {
                Attachment attachment = attachments.first();
                replay.setAttachmentUrl("/attachment/" + attachment.getEncodedId() + "/" + attachment.getAttachmentNo());
            }
        }
        else
        {
            replay.setError("未查询到信访inquiryId为：" + inquiryId + "的信息");
        }
        return replay;

    }

    @Service(url = "/interface/inquiry/results", method = HttpMethod.get)
    @Transactional
    @Json
    public InquiryResult inquiryResults() throws Exception
    {
        InquiryResult result = new InquiryResult();
        List<InquiryProcess> processes = dao.getProcesses(deptIds);
        List<InquiryReply> replyList = new ArrayList<InquiryReply>();
        List<Long> inquiryIds = new ArrayList<Long>();
        for (InquiryProcess process : processes)
        {
            Inquiry inquiry = process.getInquiry();
            InquiryReply replay = new InquiryReply();
            replay.setCode(inquiry.getCode());
            replay.setTitle(inquiry.getTitle());
            replay.setEndTime(process.getEndTime());
            char[] replyContent = process.getReplyContent();
            if (replyContent != null)
            {
                replay.setReplyContent(new String(replyContent));
            }
            else
            {
                replay.setReplyContent("");
            }
            replay.setOrgCode(process.getDept().getDeptCode());
            replay.setState(inquiry.getState().ordinal());

            SortedSet<Attachment> attachments = process.getAttachments();
            if (attachments != null && attachments.size() > 0)
            {
                Attachment attachment = attachments.first();
                replay.setAttachmentUrl("/attachment/" + attachment.getEncodedId() + "/" + attachment.getAttachmentNo());
            }
            replyList.add(replay);
            inquiryIds.add(process.getInquiryId());
        }
        if (!inquiryIds.isEmpty())
        {
            dao.updateReceive(inquiryIds, new Date());
        }
        result.setInquiryReplys(replyList);
        result.setError("");
        return result;

    }

    private void checkDeptId(Integer deptId) throws Exception
    {
        InterfaceDeptCheck.checkDeptId(deptId, deptIds);
    }
}
