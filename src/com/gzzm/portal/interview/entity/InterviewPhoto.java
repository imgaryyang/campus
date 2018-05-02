package com.gzzm.portal.interview.entity;

import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.ColumnDescription;
import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.annotation.Generatable;
import net.cyan.thunwind.annotation.ToOne;

import java.util.Date;

/**
 *
 * 在线访谈-图片信息
 *
 * @author lishiwei
 * @date 2016/7/28.
 */
@Entity(table = "PLINTERVIEWPHOTO", keys = "photoId")
public class InterviewPhoto {

    public InterviewPhoto() {
    }

    /**
     * 主键
     */
    @Generatable(length = 11)
    private Integer photoId;

    /**
     * 在线访谈信息
     */
    @ColumnDescription(type = "number(8)")
    private Integer interviewId;

    @NotSerialized
    @ToOne("INTERVIEWID")
    private Interview interview;

    /**
     * 标题
     */
    @ColumnDescription(type="varchar2(500)")
    private String title;

    /**
     * 图片
     */
    private byte[] photo;

    /**
     * 上传人
     */
    @ColumnDescription(type = "number(9)")
    private Integer creatorId;

    @NotSerialized
    @ToOne("CREATORID")
    private User creator;

    /**
     * 上传时间
     */
    private Date createTime;

    /**
     * 排序
     */
    @ColumnDescription(type = "number(8)")
    private Integer orderId;

    public Integer getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Integer photoId) {
        this.photoId = photoId;
    }

    public Integer getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(Integer interviewId) {
        this.interviewId = interviewId;
    }

    public Interview getInterview() {
        return interview;
    }

    public void setInterview(Interview interview) {
        this.interview = interview;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InterviewPhoto)) return false;

        InterviewPhoto that = (InterviewPhoto) o;

        return photoId.equals(that.photoId);

    }

    @Override
    public int hashCode() {
        return photoId.hashCode();
    }
}
