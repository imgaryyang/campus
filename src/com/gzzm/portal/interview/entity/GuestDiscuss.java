package com.gzzm.portal.interview.entity;

import com.gzzm.platform.organ.User;
import com.gzzm.portal.interview.enumtype.DiscussGuestType;
import com.gzzm.portal.interview.enumtype.PublishState;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.ColumnDescription;
import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.annotation.Generatable;
import net.cyan.thunwind.annotation.ToOne;

import java.util.Date;

/**
 * 在线访谈-嘉宾发言/访客留言信息
 *
 * @author lishiwei
 * @date 2016/7/27.
 */
@Entity(table = "PLGUESTDISCUSS",keys="discussId")
public class GuestDiscuss {

    public GuestDiscuss() {
    }

    /**
     * 主键
     */
    @Generatable(length = 11)
    private Integer discussId;

    /**
     * 在线访谈信息
     */
    @ColumnDescription(type="number(8)")
    private Integer interviewId;

    @ToOne("INTERVIEWID")
    @NotSerialized
    private Interview interview;

    /**
     * 留言人类别
     */
    @ColumnDescription(type="number(1)")
    private DiscussGuestType guestType;

    /**
     * 主持人/嘉宾/网友 昵称
     */
    @ColumnDescription(type="varchar(300)")
    private String visitorName;

    /**
     * 留言人IP
     */
    @ColumnDescription(type="varchar(300)")
    private String guestIp;

    /**
     * 留言时间
     */
    private Date createTime;

    /**
     * 留言内容
     */
    private char[] content;

    /**
     * 发布状态
     */
    @ColumnDescription(type = "number(1)", defaultValue = "0")
    private PublishState state;

    /**
     * 发布人
     */
    @ColumnDescription(type="number(9)")
    private Integer publishUserId;

    @NotSerialized
    @ToOne("PUBLISHUSERID")
    private User publishUser;

    /**
     * 留言发布时间
     */
    private Date publishTime;

    @ColumnDescription(type="number(8)")
    private Integer orderId;

    public Integer getDiscussId() {
        return discussId;
    }

    public void setDiscussId(Integer discussId) {
        this.discussId = discussId;
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

    public DiscussGuestType getGuestType() {
        return guestType;
    }

    public void setGuestType(DiscussGuestType guestType) {
        this.guestType = guestType;
    }

    public String getVisitorName() {
        return visitorName;
    }

    public void setVisitorName(String visitorName) {
        this.visitorName = visitorName;
    }

    public String getGuestIp() {
        return guestIp;
    }

    public void setGuestIp(String guestIp) {
        this.guestIp = guestIp;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public char[] getContent() {
        return content;
    }

    public void setContent(char[] content) {
        this.content = content;
    }

    public PublishState getState() {
        return state;
    }

    public void setState(PublishState state) {
        this.state = state;
    }

    public Integer getPublishUserId() {
        return publishUserId;
    }

    public void setPublishUserId(Integer publishUserId) {
        this.publishUserId = publishUserId;
    }

    public User getPublishUser() {
        return publishUser;
    }

    public void setPublishUser(User publishUser) {
        this.publishUser = publishUser;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
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
        if (!(o instanceof GuestDiscuss)) return false;

        GuestDiscuss that = (GuestDiscuss) o;

        return discussId.equals(that.discussId);

    }

    @Override
    public int hashCode() {
        return discussId.hashCode();
    }
}
