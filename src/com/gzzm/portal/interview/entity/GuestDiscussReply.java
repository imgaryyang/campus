package com.gzzm.portal.interview.entity;

import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 留言回复
 * @author Xrd
 * @date 2018/4/4 11:20
 */
@Entity(table = "PLDISCUSSREPLY",keys="replyId")
public class GuestDiscussReply
{
    public GuestDiscussReply()
    {
    }

    @Generatable(length = 11)
    private Integer replyId;

    /**
     * 留言信息
     */
    @ColumnDescription(type="number(11)")
    private Integer discussId;

    @ToOne("DISCUSSID")
    private GuestDiscuss guestDiscuss;

    /**
     * 回复人
     */
    @ColumnDescription(type = "varchar(100)")
    private String replyName;

    /**
     * 回复时间
     */
    private Date replyTime;

    /**
     * 回复内容
     */
    @ColumnDescription(type = "varchar(4000)")
    private String replyContent;

    @ColumnDescription(type = "number(11)")
    private Integer orderId;

    public Integer getReplyId()
    {
        return replyId;
    }

    public void setReplyId(Integer replyId)
    {
        this.replyId = replyId;
    }

    public Integer getDiscussId()
    {
        return discussId;
    }

    public void setDiscussId(Integer discussId)
    {
        this.discussId = discussId;
    }

    public GuestDiscuss getGuestDiscuss()
    {
        return guestDiscuss;
    }

    public void setGuestDiscuss(GuestDiscuss guestDiscuss)
    {
        this.guestDiscuss = guestDiscuss;
    }

    public String getReplyName()
    {
        return replyName;
    }

    public void setReplyName(String replyName)
    {
        this.replyName = replyName;
    }

    public Date getReplyTime()
    {
        return replyTime;
    }

    public void setReplyTime(Date replyTime)
    {
        this.replyTime = replyTime;
    }

    public String getReplyContent()
    {
        return replyContent;
    }

    public void setReplyContent(String replyContent)
    {
        this.replyContent = replyContent;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof GuestDiscussReply)) return false;

        GuestDiscussReply that = (GuestDiscussReply) o;

        return replyId.equals(that.replyId);
    }

    @Override
    public int hashCode()
    {
        return replyId.hashCode();
    }

    @Override
    public String toString()
    {
        return this.replyContent;
    }
}
