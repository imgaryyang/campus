package com.gzzm.oa.userfile;

import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

/**
 * 文件夹共享
 *
 * @author camel
 * @date 13-11-19
 */
@Entity(table = "OAUSERFILEFOLDERSHARE", keys = {"folderId", "userId", "deptId"})
public class UserFileFolderShare
{
    @Index
    private Integer folderId;

    /**
     * 共享给哪个用户，如果共享给部门则为0
     */
    private Integer userId;

    @NotSerialized
    private User user;

    /**
     * 共享给哪个部门，如果共享给用户则为0
     */
    private Integer deptId;

    @NotSerialized
    private Dept dept;

    /**
     * 共享类型
     */
    private ShareType type;

    public UserFileFolderShare()
    {
    }

    public Integer getFolderId()
    {
        return folderId;
    }

    public void setFolderId(Integer folderId)
    {
        this.folderId = folderId;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Dept getDept()
    {
        return dept;
    }

    public void setDept(Dept dept)
    {
        this.dept = dept;
    }

    public ShareType getType()
    {
        return type;
    }

    public void setType(ShareType type)
    {
        this.type = type;
    }

    public String getName()
    {
        if (type == ShareType.USER)
            return getUser().getUserName();
        else
            return getDept().getDeptName();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof UserFileFolderShare))
            return false;

        UserFileFolderShare that = (UserFileFolderShare) o;

        return deptId.equals(that.deptId) && folderId.equals(that.folderId) && userId.equals(that.userId);
    }

    @Override
    public int hashCode()
    {
        int result = folderId.hashCode();
        result = 31 * result + userId.hashCode();
        result = 31 * result + deptId.hashCode();
        return result;
    }
}
