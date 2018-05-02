package com.gzzm.platform.flow;

import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 流程过程中产生的消息，例如有人回复了新的内容，通知相关用户
 *
 * @author camel
 * @date 2015/3/18
 */
@Entity(table = "PFFLOWSTEPMESSAGE", keys = "messageId")
@Indexes({
        @Index(columns = {"USERID", "READED"}),
        @Index(columns = {"INSTANCEID", "USERID"}),
        @Index(columns = {"STEPID", "USERID"})
})
public class FlowStepMessage
{
    @Generatable(length = 13)
    private Long messageId;

    /**
     * 接收消息的用户
     */
    private Integer userId;

    private User user;

    @ColumnDescription(type = "varchar(4000)")
    private String content;

    private Date sendTime;

    private Long instanceId;

    @NotSerialized
    private SystemFlowInstance instance;

    @ColumnDescription(nullable = false, defaultValue = "0")
    private Boolean readed;

    /**
     * 产生消息的步骤
     */
    private Long stepId;

    private Integer sender;

    @ToOne("SENDER")
    @NotSerialized
    private User sendUser;

    public FlowStepMessage()
    {
    }

    public Long getMessageId()
    {
        return messageId;
    }

    public void setMessageId(Long messageId)
    {
        this.messageId = messageId;
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

    public SystemFlowInstance getInstance()
    {
        return instance;
    }

    public void setInstance(SystemFlowInstance instance)
    {
        this.instance = instance;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public Long getInstanceId()
    {
        return instanceId;
    }

    public void setInstanceId(Long instanceId)
    {
        this.instanceId = instanceId;
    }

    public Date getSendTime()
    {
        return sendTime;
    }

    public void setSendTime(Date sendTime)
    {
        this.sendTime = sendTime;
    }

    public Boolean getReaded()
    {
        return readed;
    }

    public void setReaded(Boolean readed)
    {
        this.readed = readed;
    }

    public Long getStepId()
    {
        return stepId;
    }

    public void setStepId(Long stepId)
    {
        this.stepId = stepId;
    }

    public Integer getSender()
    {
        return sender;
    }

    public void setSender(Integer sender)
    {
        this.sender = sender;
    }

    public User getSendUser()
    {
        return sendUser;
    }

    public void setSendUser(User sendUser)
    {
        this.sendUser = sendUser;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof FlowStepMessage))
            return false;

        FlowStepMessage that = (FlowStepMessage) o;

        return messageId.equals(that.messageId);
    }

    @Override
    public int hashCode()
    {
        return messageId.hashCode();
    }
}
