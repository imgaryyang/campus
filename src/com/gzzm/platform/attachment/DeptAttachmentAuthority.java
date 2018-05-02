package com.gzzm.platform.attachment;

import com.gzzm.platform.login.UserOnlineInfo;

/**
 * 通过部门控制附件权限
 *
 * @author camel
 * @date 12-11-2
 */
public class DeptAttachmentAuthority implements AttachmentAuthority
{
    private Integer deptId;

    /**
     * 能否看到其他部门上传的附件
     */
    private boolean showAll;

    public DeptAttachmentAuthority(Integer deptId, boolean showAll)
    {
        this.deptId = deptId;
        this.showAll = showAll;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public boolean isShowAll()
    {
        return showAll;
    }

    public void setShowAll(boolean showAll)
    {
        this.showAll = showAll;
    }

    public boolean isDeletable(Attachment attachment, UserOnlineInfo userOnlineInfo)
    {
        return deptId.equals(attachment.getDeptId());
    }

    public boolean isVisible(Attachment attachment, UserOnlineInfo userOnlineInfo)
    {
        return showAll || deptId.equals(attachment.getDeptId());
    }
}
