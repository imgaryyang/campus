package com.gzzm.platform.form;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.StringUtils;
import net.cyan.nest.annotation.Inject;
import net.cyan.valmiki.form.*;
import net.cyan.valmiki.form.components.FileListData;

import java.util.List;

/**
 * @author camel
 * @date 2017/12/29
 */
public class FormAttachmentService
{
    @Inject
    private FormAttachmentDao dao;

    private Long lastBodyId;

    public FormAttachmentService()
    {
    }

    public void makeFormAttachments() throws Exception
    {
        if (lastBodyId == null)
            lastBodyId = Tools.getConfig("lastMakeAttachmentBodyId", Long.class);

        while (true)
        {
            List<Long> bodyIds = dao.queryFormBodyIds(lastBodyId);
            if (bodyIds.size() == 0)
                return;

            for (Long bodyId : bodyIds)
            {
                Tools.log("make form attachmet:" + bodyId);

                saveFormAttachmet(FormApi.getFormContext(bodyId));

                lastBodyId = bodyId;
                Tools.setConfig("lastMakeAttachmentBodyId", lastBodyId);
            }
        }
    }

    public void saveFormAttachmet(SystemFormContext context) throws Exception
    {
        for (PageData pageData : context.getFormData().getPages())
        {
            for (ComponentData componentData : pageData)
            {
                if (componentData instanceof FileListData)
                {
                    String fileListId = ((FileListData) componentData).getFileListId();

                    if (!StringUtils.isEmpty(fileListId))
                    {
                        String[] ids = fileListId.split(",");
                        for (String id : ids)
                        {
                            if (!id.startsWith("@"))
                            {
                                Long attachmentId = Long.valueOf(id);
                                FormAttachment formAttachment = new FormAttachment();
                                formAttachment.setAttachmentId(attachmentId);
                                formAttachment.setBodyId(context.getBodyId());

                                FormApi.getDao().save(formAttachment);
                            }
                        }
                    }
                }
            }
        }
    }
}
