package com.gzzm.oa.userfile;

import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

/**
 * @author camel
 * @date 13-11-19
 */
@Entity(table = "OAUSERFILESHARE", keys = {"fileId", "userId", "deptId"})
public class UserFileShare
{
    /**
     * 要共享的文件的ID
     */
    @Index
    private Integer fileId;

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

    public UserFileShare()
    {
    }

    public Integer getFileId()
    {
        return fileId;
    }

    public void setFileId(Integer fileId)
    {
        this.fileId = fileId;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
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

        if (!(o instanceof UserFileShare))
            return false;

        UserFileShare that = (UserFileShare) o;

        return deptId.equals(that.deptId) && fileId.equals(that.fileId) && userId.equals(that.userId);
    }

    @Override
    public int hashCode()
    {
        int result = fileId.hashCode();
        result = 31 * result + userId.hashCode();
        result = 31 * result + deptId.hashCode();
        return result;
    }
}
