package com.gzzm.platform.attachment;

import com.gzzm.platform.login.UserOnlineInfo;

/**
 * 附件权限控制，主要控制附件是否能删除
 *
 * @author camel
 * @date 2011-3-29
 */
public interface AttachmentAuthority
{
    /**
     * 判断某附件能否被删除
     *
     * @param attachment     附件对象
     * @param userOnlineInfo 当前用户在线信息
     * @return 如果允许删除返回true，不能返回false
     */
    public boolean isDeletable(Attachment attachment, UserOnlineInfo userOnlineInfo);

    /**
     * 判断某附件能否被删除
     *
     * @param attachment     附件对象
     * @param userOnlineInfo 当前用户在线信息
     * @return 如果允许删除返回true，不能返回false
     */
    public boolean isVisible(Attachment attachment, UserOnlineInfo userOnlineInfo);
}