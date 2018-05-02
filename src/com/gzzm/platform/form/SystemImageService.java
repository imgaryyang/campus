package com.gzzm.platform.form;

import com.gzzm.platform.attachment.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.*;
import net.cyan.valmiki.form.FormContext;
import net.cyan.valmiki.form.components.*;

/**
 * 将表单附件保存在PFATTACHMENT表
 *
 * @author camel
 * @date 11-9-21
 */
@Injectable(singleton = true)
public class SystemImageService implements ImageService
{
    @Inject
    private static Provider<AttachmentDao> daoProvider;

    public SystemImageService()
    {
    }

    public String getUrl(String imageId, FImage image, FormContext context) throws Exception
    {
        Attachment attachment = daoProvider.get().getAttachment(Long.valueOf(imageId), 0);

        return "/attachment/" + attachment.getEncodedId() + "/0";
    }

    public String save(ImageData data, FImage image, FormContext context) throws Exception
    {
        InputFile upload = data.getUpload();
        if (upload != null)
        {
            Attachment attachment = new Attachment();
            if (data.getImageId() != null)
                attachment.setAttachmentId(Long.valueOf(data.getImageId()));
            attachment.setAttachmentNo(0);
            attachment.setFileName(upload.getName());
            attachment.setInputable(upload.getInputable());

            SystemFormContext systemFormContext = (SystemFormContext) context;
            attachment.setUserId(systemFormContext.getUserId());
            attachment.setDeptId(systemFormContext.getDeptId());

            attachment.setTag("form");

            daoProvider.get().save(attachment);

            return attachment.getAttachmentId().toString();
        }
        else
        {
            return data.getImageId();
        }
    }
}
