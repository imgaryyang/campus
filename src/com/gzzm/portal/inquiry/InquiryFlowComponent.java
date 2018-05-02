package com.gzzm.portal.inquiry;

import com.gzzm.ods.document.*;
import com.gzzm.ods.flow.*;
import com.gzzm.platform.attachment.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;
import net.cyan.valmiki.form.*;
import net.cyan.valmiki.form.components.FileListData;

import java.io.InputStream;
import java.util.*;

/**
 * @author camel
 * @date 12-11-21
 */
public class InquiryFlowComponent extends AbstractOdFlowComponent
{
    @Inject
    private static Provider<InquiryDao> daoProvider;

    @Inject
    private static Provider<AttachmentService> attachmentServiceProvider;

    public InquiryFlowComponent()
    {
    }

    public static void initFormData(FormData formData, InquiryProcess process)
    {
        Inquiry inquiry = process.getInquiry();

        formData.setValue(Constants.Flow.TITLE, inquiry.getTitle());
        formData.setValue(Constants.Flow.CODE, inquiry.getCode());
        formData.setValue(Constants.Flow.INQUIRERNAME, inquiry.getInquirerName());
        formData.setValue(Constants.Flow.REALNAME, inquiry.getRealName());
        formData.setValue(Constants.Flow.SENDTIME, inquiry.getSendTime());
        formData.setValue(Constants.Flow.ADDRESS, inquiry.getAddress());
        formData.setValue(Constants.Flow.PHONE, inquiry.getPhone());
        formData.setValue(Constants.Flow.EMAIL, inquiry.getEmail());
        formData.setValue(Constants.Flow.POSTCODE, inquiry.getPostcode());
        formData.setValue(Constants.Flow.TYPE, inquiry.getType() == null ? "" : inquiry.getType().getTypeName());
        formData.setValue(Constants.Flow.WAY, inquiry.getWay() == null ? "" : inquiry.getWay().getWayName());
        formData.setValue(Constants.Flow.CATALOG,
                inquiry.getCatalog() == null ? "" : inquiry.getCatalog().getCatalogName());
        formData.setValue(Constants.Flow.CONTENT,
                new String(inquiry.getContent() == null ? new char[0] : inquiry.getContent()));

        if (process.getPreviousProcess() != null)
        {
            formData.setValue(Constants.Flow.TURNFROM, process.getPreviousProcess().getDept().getDeptName());
            formData.setValue(Constants.Flow.TURNTIME, process.getStartTime());
        }
    }

    @Override
    public void initForm(FormContext formContext, OdFlowContext context) throws Exception
    {
        super.initForm(formContext, context);

        InquiryDao inquiryDao = daoProvider.get();

        InquiryProcess process = inquiryDao.getProcess(Long.valueOf(context.getOdFlowInstance().getLinkId()));

        formContext.setAuthority(Constants.Flow.TITLE, FormRole.READONLY);
        formContext.setAuthority(Constants.Flow.CODE, FormRole.READONLY);
        formContext.setAuthority(Constants.Flow.INQUIRERNAME, FormRole.READONLY);
        formContext.setAuthority(Constants.Flow.REALNAME, FormRole.READONLY);
        formContext.setAuthority(Constants.Flow.SENDTIME, FormRole.READONLY);
        formContext.setAuthority(Constants.Flow.ADDRESS, FormRole.READONLY);
        formContext.setAuthority(Constants.Flow.PHONE, FormRole.READONLY);
        formContext.setAuthority(Constants.Flow.EMAIL, FormRole.READONLY);
        formContext.setAuthority(Constants.Flow.POSTCODE, FormRole.READONLY);
        formContext.setAuthority(Constants.Flow.TYPE, FormRole.READONLY);
        formContext.setAuthority(Constants.Flow.WAY, FormRole.READONLY);
        formContext.setAuthority(Constants.Flow.CATALOG, FormRole.READONLY);
        formContext.setAuthority(Constants.Flow.CONTENT, FormRole.READONLY);
        formContext.setAuthority(Constants.Flow.TURNFROM, FormRole.READONLY);
        formContext.setAuthority(Constants.Flow.TURNTIME, FormRole.READONLY);

        initFormData(formContext.getFormData(), process);
    }

    @Override
    public void endFlow(OdFlowContext context) throws Exception
    {
        super.endFlow(context);

        FormData formData = context.getFormContext().getFormData();

        String replyContent = DataConvert.toString(formData.getData(Constants.Flow.REPLYCONTENT));
        PublishType publishType = DataConvert.convertType(PublishType.class,
                formData.getData(Constants.Flow.PUBLISHTYPE));

        InquiryDao inquiryDao = daoProvider.get();
        InquiryProcess process = inquiryDao.getProcess(Long.valueOf(context.getOdFlowInstance().getLinkId()));
        Inquiry inquiry = process.getInquiry();
        Date time = new Date();

        process.setReplyContent(replyContent.toCharArray());
        process.setEndTime(time);
        process.setUserId(context.getUserId());
        process.setTurnInnered(false);
        process.setState(ProcessState.REPLYED);

        boolean b = false;
        ComponentData attachmentData = formData.getData(Constants.Flow.ATTACHMENT);
        if (attachmentData instanceof FileListData)
        {
            FileListData fileListData = (FileListData) attachmentData;
            if (fileListData.getFileListId() != null)
            {
                String fileId = fileListData.getFileListId();
                process.setAttachmentId(Long.valueOf(fileId));
                b = true;
            }
        }

        if (!b)
        {
            OfficeDocument document = context.getDocument();
            DocumentText text = document.getText();
            if (text != null)
            {
                InputStream in;
                String fileName = text.getOtherFileName();
                if (!StringUtils.isEmpty(fileName))
                {
                    in = text.getOtherFile();
                }
                else
                {
                    in = text.getTextBody();
                    fileName = inquiry.getTitle() + "(回复)" + "." + text.getType();
                }

                if (in != null)
                {
                    Attachment attachment = new Attachment();
                    attachment.setFileName(fileName);
                    attachment.setAttachmentName(fileName);
                    attachment.setInputable(new Inputable.StreamInput(in));

                    Long attachmentId = attachmentServiceProvider.get().save(Collections.singletonList(attachment),
                            "inquiry", context.getUserId(), process.getDeptId());

                    process.setAttachmentId(attachmentId);
                }
            }
        }

        inquiryDao.update(process);

        inquiry.setState(InquiryState.REPLYED);
        inquiry.setReplyTime(time);
        inquiry.setLastTime(new Date());
        if (publishType != null)
            inquiry.setPublishType(publishType);
        inquiryDao.update(inquiry);
    }

    @Override
    public void stopFlow(OdFlowContext context) throws Exception
    {
        super.stopFlow(context);

        stopFlow(context.getOdFlowInstance());
    }

    @Override
    public void stopFlow(OdFlowInstance instance) throws Exception
    {
        super.stopFlow(instance);

        InquiryDao inquiryDao = daoProvider.get();
        InquiryProcess process = inquiryDao.getProcess(Long.valueOf(instance.getLinkId()));
        process.setTurnInnered(false);
        inquiryDao.update(process);
    }
}
