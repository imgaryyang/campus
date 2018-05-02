package com.gzzm.oa.cloudstorage;

import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.ColumnDescription;
import net.cyan.thunwind.annotation.Generatable;
import net.cyan.thunwind.annotation.ToOne;

import java.util.Date;

/**
 * @author gyw
 * @date 2017/8/9 0009
 */
public class CloudShare {

    /**
     * 主键
     */
    @Generatable(length = 9)
    private Integer shareId;

    /**
     * 分享时间
     */
    private Date shareTime;

    /**
     * 有效天数
     */
    private Integer validDay;

    /**
     * 分享人
     */
    private  Integer shareUserId;

    @NotSerialized
    @ToOne("SHAREUSERID")
    private User shareUser;

    /**
     * 提取码
     */
    @ColumnDescription(type = "varchar2(10)")
    private String password;

    /**
     * 删除标志
     */
    @ColumnDescription(type = "number(2)",defaultValue = "0")
    private Boolean deleteTag;

    /**
     * 过期标志
     */
    @ColumnDescription(type = "number(2)")
    private Boolean overTimeTag;

    /**
     * 是否加密
     */
    @ColumnDescription(type = "number(2)")
    private Boolean encrypt;

    /**
     * 过期时间
     */
    private Date expiredTime;

    @ColumnDescription(type = "number(2)")
    private Boolean foerver;

    @ColumnDescription(type = "varchar2(50)")
    private String shareName;

    public CloudShare() {
    }

    public Integer getShareId() {
        return shareId;
    }

    public void setShareId(Integer shareId) {
        this.shareId = shareId;
    }

    public Date getShareTime() {
        return shareTime;
    }

    public void setShareTime(Date shareTime) {
        this.shareTime = shareTime;
    }

    public Integer getShareUserId() {
        return shareUserId;
    }

    public void setShareUserId(Integer shareUserId) {
        this.shareUserId = shareUserId;
    }

    public User getShareUser() {
        return shareUser;
    }

    public void setShareUser(User shareUser) {
        this.shareUser = shareUser;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getDeleteTag() {
        return deleteTag;
    }

    public void setDeleteTag(Boolean deleteTag) {
        this.deleteTag = deleteTag;
    }

    public Integer getValidDay() {
        return validDay;
    }

    public void setValidDay(Integer validDay) {
        this.validDay = validDay;
    }

    public Boolean getEncrypt() {
        return encrypt;
    }

    public void setEncrypt(Boolean encrypt) {
        this.encrypt = encrypt;
    }

    public Boolean getOverTimeTag() {
        return overTimeTag;
    }

    public void setOverTimeTag(Boolean overTimeTag) {
        this.overTimeTag = overTimeTag;
    }

    public Date getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Date expiredTime) {
        this.expiredTime = expiredTime;
    }

    public Boolean getFoerver() {
        return foerver;
    }

    public void setFoerver(Boolean foerver) {
        this.foerver = foerver;
    }

    public String getShareName() {
        return shareName;
    }

    public void setShareName(String shareName) {
        this.shareName = shareName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CloudShare)) return false;

        CloudShare that = (CloudShare) o;

        return shareId != null ? shareId.equals(that.shareId) : that.shareId == null;

    }

    @Override
    public int hashCode() {
        return shareId != null ? shareId.hashCode() : 0;
    }
}
