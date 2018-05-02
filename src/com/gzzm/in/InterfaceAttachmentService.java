package com.gzzm.in;

import com.gzzm.platform.attachment.*;
import com.gzzm.platform.commons.*;
import net.cyan.commons.util.InputFile;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author camel
 * @date 2016/12/3
 */
public class InterfaceAttachmentService
{
    @Inject
    private FileUploadService fileUploadService;

    public InterfaceAttachmentService()
    {
    }

    public static AttachmentInfo toAttachmentInfo(Attachment attachment) throws Exception
    {
        AttachmentInfo attachmentInfo = new AttachmentInfo();

        attachmentInfo.setAttachmentName(attachment.getAttachmentName());
        attachmentInfo.setUrl("/attachment/" + attachment.getEncodedId() + "/" + attachment.getAttachmentNo());

        return attachmentInfo;
    }

    public static List<AttachmentInfo> toAttachmentInfoList(Collection<Attachment> attachments) throws Exception
    {
        List<AttachmentInfo> attachmentInfos = new ArrayList<AttachmentInfo>(attachments.size());

        for (Attachment attachment : attachments)
        {
            attachmentInfos.add(toAttachmentInfo(attachment));
        }

        return attachmentInfos;
    }

    public Long save(List<AttachmentInfo> attachmentInfos, String tag, Integer userId, Integer deptId) throws Exception
    {
        AttachmentSaver saver = Tools.getBean(AttachmentSaver.class);

        List<Attachment> attachments = new ArrayList<Attachment>(attachmentInfos.size());
        for (AttachmentInfo attachmentInfo : attachmentInfos)
        {
            Attachment attachment = new Attachment();
            attachment.setAttachmentName(attachmentInfo.getAttachmentName());
            attachment.setTag(tag);
            attachment.setUserId(userId);
            attachment.setDeptId(deptId);

            InputFile file = fileUploadService.getFile(attachmentInfo.getId());
            if (file != null)
            {
                attachment.setFileSize(file.size());
                attachment.setInputable(file.getInputable());
            }
            attachments.add(attachment);
        }
        saver.setAttachments(attachments);
        saver.save();

        return saver.getAttachmentId();
    }
}
