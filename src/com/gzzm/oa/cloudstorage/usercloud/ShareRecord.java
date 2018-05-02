package com.gzzm.oa.cloudstorage.usercloud;

import com.gzzm.oa.userfile.ShareType;
import com.gzzm.platform.organ.Dept;
import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.annotation.Index;

/**
 * @author gyw
 * @date 2017/8/25 0025
 */

@Entity(table = "OASHARERECORD", keys = {"shareId", "userId", "deptId"})
public class ShareRecord {

    /**
     * 要共享的文件的ID
     */
    @Index
    private Integer shareId;

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

    public ShareRecord() {
    }

    public Integer getShareId() {
        return shareId;
    }

    public void setShareId(Integer shareId) {
        this.shareId = shareId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getDeptId() {
        return deptId;
    }

    public void setDeptId(Integer deptId) {
        this.deptId = deptId;
    }

    public Dept getDept() {
        return dept;
    }

    public void setDept(Dept dept) {
        this.dept = dept;
    }

    public ShareType getType() {
        return type;
    }

    public void setType(ShareType type) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShareRecord)) return false;

        ShareRecord that = (ShareRecord) o;

        if (shareId != null ? !shareId.equals(that.shareId) : that.shareId != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        return deptId != null ? deptId.equals(that.deptId) : that.deptId == null;

    }

    @Override
    public int hashCode() {
        int result = shareId != null ? shareId.hashCode() : 0;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (deptId != null ? deptId.hashCode() : 0);
        return result;
    }
}
