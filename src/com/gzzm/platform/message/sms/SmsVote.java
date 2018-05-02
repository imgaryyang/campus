package com.gzzm.platform.message.sms;

import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * 短信投票
 *
 * @author camel
 * @date 11-9-27
 */
@Entity(table = "PFSMSVOTE", keys = "voteId")
public class SmsVote
{
    @Generatable(length = 9)
    private Integer voteId;

    @Require
    @ColumnDescription(type = "varchar(250)")
    private String title;

    /**
     * 创建者
     */
    private Integer creator;

    @ToOne("CREATOR")
    private User createUser;

    @Index
    private Integer deptId;

    private Dept dept;

    /**
     * 选项列表
     */
    @NotSerialized
    @Json
    private List<String> options;

    /**
     * 投票创建时间
     */
    private Date createTime;

    /**
     * 投票结束时间
     */
    private java.sql.Date endTime;

    /**
     * 参与人
     */
    @ColumnDescription(type = "varchar(800)")
    private String receiver;

    /**
     * 是否已经发送
     */
    @ColumnDescription(defaultValue = "0")
    private Boolean sended;

    /**
     * 匿名投票，true为匿名投票，false不是匿名投票，匿名投票表示不在结果中显示投票人的手机
     */
    @ColumnDescription(defaultValue = "0")
    private Boolean anonymous;

    /**
     * 每个手机号码允许投票的次数
     */
    @ColumnDescription(defaultValue = "1", type = "number(3)")
    private Integer voteCount;

    /**
     * 限制参与者，true表示只有被邀请的人允许投票，false表示任何手机号码都允许投票
     */
    @ColumnDescription(defaultValue = "0")
    private Boolean restrictReceiver;

    public SmsVote()
    {
    }

    public Integer getVoteId()
    {
        return voteId;
    }

    public void setVoteId(Integer voteId)
    {
        this.voteId = voteId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Integer getCreator()
    {
        return creator;
    }

    public void setCreator(Integer creator)
    {
        this.creator = creator;
    }

    public User getCreateUser()
    {
        return createUser;
    }

    public void setCreateUser(User createUser)
    {
        this.createUser = createUser;
    }

    public Boolean isRestrictReceiver()
    {
        return restrictReceiver;
    }

    public void setRestrictReceiver(Boolean restrictReceiver)
    {
        this.restrictReceiver = restrictReceiver;
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

    public List<String> getOptions()
    {
        return options;
    }

    public void setOptions(List<String> options)
    {
        this.options = options;
    }

    public Boolean isAnonymous()
    {
        return anonymous;
    }

    public void setAnonymous(Boolean anonymous)
    {
        this.anonymous = anonymous;
    }

    public Integer getVoteCount()
    {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount)
    {
        this.voteCount = voteCount;
    }

    @NotSerialized
    public String getOptionsText()
    {
        List<String> options = getOptions();
        if (options == null)
            return null;

        StringBuilder buffer = new StringBuilder();
        int n = options.size();
        for (int i = 0; i < n; i++)
        {
            String option = options.get(i);
            if (buffer.length() > 0)
                buffer.append("\n");
            buffer.append(i + 1).append(".").append(option);
        }
        return buffer.toString();
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public java.sql.Date getEndTime()
    {
        return endTime;
    }

    public void setEndTime(java.sql.Date endTime)
    {
        this.endTime = endTime;
    }

    public String getReceiver()
    {
        return receiver;
    }

    public void setReceiver(String receiver)
    {
        this.receiver = receiver;
    }

    public Boolean isSended()
    {
        return sended;
    }

    public void setSended(Boolean sended)
    {
        this.sended = sended;
    }

    public String getValueText(Integer value)
    {
        List<String> options = getOptions();

        if (options != null && options.size() >= value)
            return options.get(value - 1);

        return "";
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof SmsVote))
            return false;

        SmsVote smsVote = (SmsVote) o;

        return !(voteId != null ? !voteId.equals(smsVote.voteId) : smsVote.voteId != null);
    }

    @Override
    public int hashCode()
    {
        return voteId != null ? voteId.hashCode() : 0;
    }
}
