package com.gzzm.platform.form;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

/**
 * 保存表单和附件的关系，方便后续维护
 *
 * @author camel
 * @date 2017/12/28
 */
@Entity(table = "PFFORMATTACHMENT", keys = {"bodyId", "attachmentId"})
public class FormAttachment
{
    private Long bodyId;

    @NotSerialized
    private FormBody formBody;

    @ColumnDescription(type = "number(12)")
    private Long attachmentId;

    public FormAttachment()
    {
    }

    public Long getBodyId()
    {
        return bodyId;
    }

    public void setBodyId(Long bodyId)
    {
        this.bodyId = bodyId;
    }

    public FormBody getFormBody()
    {
        return formBody;
    }

    public void setFormBody(FormBody formBody)
    {
        this.formBody = formBody;
    }

    public Long getAttachmentId()
    {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId)
    {
        this.attachmentId = attachmentId;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof FormAttachment))
            return false;

        FormAttachment that = (FormAttachment) o;

        return attachmentId.equals(that.attachmentId) && bodyId.equals(that.bodyId);
    }

    @Override
    public int hashCode()
    {
        int result = bodyId.hashCode();
        result = 31 * result + attachmentId.hashCode();
        return result;
    }
}
