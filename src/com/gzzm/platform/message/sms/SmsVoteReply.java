package com.gzzm.platform.message.sms;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 投票回复
 *
 * @author camel
 * @date 11-9-28
 */
@Entity(table = "PFSMSVOTEREPLY", keys = "replyId")
public class SmsVoteReply
{
    /**
     * 主键
     */
    @Generatable(length = 12)
    private Long replyId;

    /**
     * 回复时间
     */
    private Date replyTime;

    /**
     * 回复的电话号码
     */
    private String phone;

    /**
     * 回复的用户
     */
    private String userName;

    /**
     * 投票值，从1开始，选择了第一个选项为1，第二个选项为2，以此类推
     */
    @ColumnDescription(type = "number(2)")
    private Integer voteValue;

    /**
     * 关联的投票的ID
     */
    @Index
    private Integer voteId;

    /**
     * 关联投票对象
     */
    @NotSerialized
    private SmsVote vote;

    public SmsVoteReply()
    {
    }

    public Long getReplyId()
    {
        return replyId;
    }

    public void setReplyId(Long replyId)
    {
        this.replyId = replyId;
    }

    public Date getReplyTime()
    {
        return replyTime;
    }

    public void setReplyTime(Date replyTime)
    {
        this.replyTime = replyTime;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public Integer getVoteValue()
    {
        return voteValue;
    }

    public void setVoteValue(Integer voteValue)
    {
        this.voteValue = voteValue;
    }

    public Integer getVoteId()
    {
        return voteId;
    }

    public void setVoteId(Integer voteId)
    {
        this.voteId = voteId;
    }

    public SmsVote getVote()
    {
        return vote;
    }

    public void setVote(SmsVote vote)
    {
        this.vote = vote;
    }

    public String getVoteText()
    {
        return getVote().getValueText(voteValue);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof SmsVoteReply))
            return false;

        SmsVoteReply that = (SmsVoteReply) o;

        return replyId.equals(that.replyId);
    }

    @Override
    public int hashCode()
    {
        return replyId.hashCode();
    }
}
