package com.gzzm.ods.exchange;

import com.gzzm.ods.document.OfficeDocument;
import com.gzzm.ods.redhead.RedHead;
import com.gzzm.ods.sendnumber.SendNumber;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.StringUtils;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 公文发文信息
 *
 * @author camel
 * @date 2010-9-9
 */
@Entity(table = "ODSEND", keys = "sendId")
@Indexes({@Index(columns = {"DEPTID", "STATE"})})
public class Send
{
    /**
     * 发文ID
     */
    @Generatable(length = 11)
    private Long sendId;

    /**
     * 发文时间
     */
    private Date sendTime;

    /**
     * 发文部门
     */
    private Integer deptId;

    /**
     * 关联发文部门对象
     */
    @NotSerialized
    private Dept dept;

    /**
     * 状态，0为正常，1为已撤回
     */
    @ColumnDescription(defaultValue = "0")
    private SendState state;

    /**
     * 文件创建人的用户ID
     */
    @Index
    private Integer creator;

    @NotSerialized
    @ToOne("CREATOR")
    private User createUser;

    /**
     * 文件创建人的名称，以防创建人名称被修改
     */
    @ColumnDescription(type = "varchar(50)")
    private String creatorName;

    /**
     * 文件创建人所属的部门ID，即创建流程的科室，以方便后面统计
     */
    private Integer createDeptId;

    @NotSerialized
    @ToOne("CREATEDEPTID")
    private Dept createDept;

    /**
     * 文档ID
     */
    @Index
    private Long documentId;

    @Lazy(false)
    @NotSerialized
    private OfficeDocument document;

    /**
     * 发文字号ID
     */
    private Integer sendNumberId;

    /**
     * 使用的发文字号
     */
    @NotSerialized
    private SendNumber sendNumber;

    /**
     * 红头模板ID
     */
    private Integer redHeadId;

    @NotSerialized
    private RedHead redHead;

    private Integer sender;

    @ToOne("SENDER")
    private User sendUser;

    /**
     * 文件发送人的名称，以防发送人名称被修改
     */
    private String senderName;

    /**
     * 消息通知
     */
    @ColumnDescription(type = "varchar(4000)")
    private String message;

    public Send()
    {
    }

    public Long getSendId()
    {
        return sendId;
    }

    public void setSendId(Long sendId)
    {
        this.sendId = sendId;
    }

    public Date getSendTime()
    {
        return sendTime;
    }

    public void setSendTime(Date sendTime)
    {
        this.sendTime = sendTime;
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

    public SendState getState()
    {
        return state;
    }

    public void setState(SendState state)
    {
        this.state = state;
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

    public User getCreatorUser()
    {
        return createUser;
    }

    public String getCreatorName()
    {
        return creatorName;
    }

    public void setCreatorName(String creatorName)
    {
        this.creatorName = creatorName;
    }

    @NotSerialized
    public String getCreatorString()
    {
        if (!StringUtils.isEmpty(creatorName))
            return creatorName;

        User user = getCreateUser();
        if (user != null)
            return user.getUserName();

        return null;
    }

    public Integer getCreateDeptId()
    {
        return createDeptId;
    }

    public void setCreateDeptId(Integer createDeptId)
    {
        this.createDeptId = createDeptId;
    }

    public Dept getCreateDept()
    {
        return createDept;
    }

    public void setCreateDept(Dept createDept)
    {
        this.createDept = createDept;
    }

    public Long getDocumentId()
    {
        return documentId;
    }

    public void setDocumentId(Long documentId)
    {
        this.documentId = documentId;
    }

    public OfficeDocument getDocument()
    {
        return document;
    }

    public void setDocument(OfficeDocument document)
    {
        this.document = document;
    }

    public String getEncodedDocumentId()
    {
        return OfficeDocument.encodeId(documentId);
    }

    public Integer getSendNumberId()
    {
        return sendNumberId;
    }

    public void setSendNumberId(Integer sendNumberId)
    {
        this.sendNumberId = sendNumberId;
    }

    public SendNumber getSendNumber()
    {
        return sendNumber;
    }

    public void setSendNumber(SendNumber sendNumber)
    {
        this.sendNumber = sendNumber;
    }

    public Integer getRedHeadId()
    {
        return redHeadId;
    }

    public void setRedHeadId(Integer redHeadId)
    {
        this.redHeadId = redHeadId;
    }

    public RedHead getRedHead()
    {
        return redHead;
    }

    public void setRedHead(RedHead redHead)
    {
        this.redHead = redHead;
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

    public String getSenderName()
    {
        return senderName;
    }

    public void setSenderName(String senderName)
    {
        this.senderName = senderName;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof Send))
            return false;

        Send send = (Send) o;

        return sendId.equals(send.sendId);
    }

    @Override
    public int hashCode()
    {
        return sendId.hashCode();
    }
}
