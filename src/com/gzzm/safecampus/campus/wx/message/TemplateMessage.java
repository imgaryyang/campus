package com.gzzm.safecampus.campus.wx.message;

import com.gzzm.platform.organ.User;
import com.gzzm.safecampus.campus.classes.Teacher;
import com.gzzm.safecampus.wx.user.WxUser;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.ColumnDescription;
import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.annotation.Generatable;
import net.cyan.thunwind.annotation.ToOne;
import net.cyan.thunwind.storage.CommonFileColumn;

import java.util.Date;

/**
 * 模板消息发送记录
 *
 * @author Neo
 * @date 2018/4/2 13:49
 */
@Entity(table = "SCTEMPLATEMESSAGE", keys = "tmId")
public class TemplateMessage
{
    @Generatable(length = 13)
    private Integer tmId;

    /**
     * 发送人Id messageType为ATTENDANCE时表示的是老师的Id，其他的表示为PFUSER表的userId
     */
    private Integer sender;

    @NotSerialized
    @ToOne("sender")
    private Teacher teacher;

    @NotSerialized
    @ToOne("sender")
    private User user;

    /**
     * 发送时间
     */
    private Date sendTime;

    /**
     * 消息类型
     */
    private TemplateMessageType messageType;

    /**
     * 消息内容 保存到文件系统中
     */
    @CommonFileColumn(pathColumn = "path", target = "safecampus", path = "templateMsg/{yyyyMM}/{yyyyMMdd}/{tmId}")
    private String content;

    @ColumnDescription(type = "VARCHAR2(250)")
    private String path;

    /**
     * 消息Id 微信服务器返回
     */
    @ColumnDescription(type = "VARCHAR2(50)")
    private String messageId;

    /**
     * 接收人
     */
    private Integer wxUserId;

    @NotSerialized
    @ToOne
    private WxUser wxUser;

    public TemplateMessage()
    {
    }

    public Integer getTmId()
    {
        return tmId;
    }

    public void setTmId(Integer tmId)
    {
        this.tmId = tmId;
    }

    public Integer getSender()
    {
        return sender;
    }

    public void setSender(Integer sender)
    {
        this.sender = sender;
    }

    public Teacher getTeacher()
    {
        return teacher;
    }

    public void setTeacher(Teacher teacher)
    {
        this.teacher = teacher;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public String getSenderName()
    {
        return messageType == TemplateMessageType.ATTENDANCE ? teacher.getTeacherName() : user.getUserName();
    }

    public Date getSendTime()
    {
        return sendTime;
    }

    public void setSendTime(Date sendTime)
    {
        this.sendTime = sendTime;
    }

    public TemplateMessageType getMessageType()
    {
        return messageType;
    }

    public void setMessageType(TemplateMessageType messageType)
    {
        this.messageType = messageType;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getMessageId()
    {
        return messageId;
    }

    public void setMessageId(String messageId)
    {
        this.messageId = messageId;
    }

    public Integer getWxUserId()
    {
        return wxUserId;
    }

    public void setWxUserId(Integer wxUserId)
    {
        this.wxUserId = wxUserId;
    }

    public WxUser getWxUser()
    {
        return wxUser;
    }

    public void setWxUser(WxUser wxUser)
    {
        this.wxUser = wxUser;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof TemplateMessage)) return false;

        TemplateMessage that = (TemplateMessage) o;

        return tmId.equals(that.tmId);

    }

    @Override
    public int hashCode()
    {
        return tmId.hashCode();
    }
}
