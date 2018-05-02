package com.gzzm.platform.attachment;

import com.gzzm.platform.login.UserOnlineInfo;

/**
 * 通过用户控制权限
 *
 * @author camel
 * @date 2011-3-29
 */
public final class UserAttachmentAuthority implements AttachmentAuthority
{
    public static final UserAttachmentAuthority INSTANCE = new UserAttachmentAuthority();

    private UserAttachmentAuthority()
    {
    }

    public boolean isDeletable(Attachment attachment, UserOnlineInfo userOnlineInfo)
    {
        return userOnlineInfo.getUserId().equals(attachment.getUserId());
    }

    public boolean isVisible(Attachment attachment, UserOnlineInfo userOnlineInfo)
    {
        return true;
    }
}
