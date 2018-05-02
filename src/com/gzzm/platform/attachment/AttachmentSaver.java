package com.gzzm.platform.attachment;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

import java.util.Collection;

/**
 * 保存附件列表
 *
 * @author camel
 * @date 2010-4-20
 */
public class AttachmentSaver
{
    @Inject
    private static Provider<UserOnlineInfo> userOnlineInfoProvider;

    @Inject
    private AttachmentDao dao;

    /**
     * 附件列表ID，如果是新附件列表，为null
     */
    private Long attachmentId;

    /**
     * 新增加的附件列表
     */
    private Collection<Attachment> attachments;

    /**
     * 要删除掉的附件列表，可以为null
     */
    private Integer[] deleteds;

    /**
     * 权限控制，主要控制能否删除某个附件
     */
    private AttachmentAuthority authority;

    public AttachmentSaver()
    {
    }

    public AttachmentDao getDao()
    {
        return dao;
    }

    public Long getAttachmentId()
    {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId)
    {
        this.attachmentId = attachmentId;
    }

    public Collection<Attachment> getAttachments()
    {
        return attachments;
    }

    public void setAttachments(Collection<Attachment> attachments)
    {
        this.attachments = attachments;
    }

    public Integer[] getDeleteds()
    {
        return deleteds;
    }

    public void setDeleteds(Integer... deleteds)
    {
        this.deleteds = deleteds;
    }

    public AttachmentAuthority getAuthority()
    {
        return authority;
    }

    public void setAuthority(AttachmentAuthority authority)
    {
        this.authority = authority;
    }

    @Transactional
    public void delete() throws Exception
    {
        UserOnlineInfo userOnlineInfo = null;
        if (authority != null)
        {
            userOnlineInfo = userOnlineInfoProvider.get();
        }

        for (Attachment attachment : dao.getAttachments(attachmentId, deleteds))
        {
            //判断你是否有权限删除附件
            if (authority != null && !authority.isDeletable(attachment, userOnlineInfo))
                throw new SystemMessageException(Messages.NO_AUTH_RECORD,
                        "no auth,attachment,attachmentId:" + attachment.getAttachmentId() + ",attachmentNo:" +
                                attachment.getAttachmentNo());

            attachment.delete();
            dao.delete(attachment);
        }
    }

    @Transactional
    public void clearAll() throws Exception
    {
        deleteds = null;
        delete();
    }

    @Transactional
    public void save() throws Exception
    {
        if (attachmentId != null && deleteds != null && deleteds.length > 0)
        {
            delete();
        }

        if (attachments != null && attachments.size() > 0)
            attachmentId = dao.save(attachmentId, attachments);
    }
}
