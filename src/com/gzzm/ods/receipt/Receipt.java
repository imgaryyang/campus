package com.gzzm.ods.receipt;

import com.gzzm.ods.document.OfficeDocument;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * 回执
 *
 * @author camel
 * @date 12-4-8
 */
@Entity(table = "ODRECEIPT", keys = "receiptId")
public class Receipt
{
    @Generatable(length = 11)
    private Long receiptId;

    @Index
    private Long documentId;

    /**
     * 回执关联的公文
     */
    @NotSerialized
    private OfficeDocument document;

    /**
     * 接收回执的部门
     */
    private Integer deptId;

    /**
     * 关联部门对象
     */
    @NotSerialized
    private Dept dept;

    /**
     * 回执发送时间
     */
    private Date sendTime;

    /**
     * 回执的类型，由各回执组件定义的一个英文单词，可扩展
     */
    @ColumnDescription(type = "varchar(50)")
    private String type;

    /**
     * 关联的对象的ID，可以为空
     */
    private Long linkId;

    /**
     * 是否已经发送
     */
    @ColumnDescription(nullable = false, defaultValue = "0")
    private Boolean sended;

    /**
     * 创建人
     */
    private Integer creator;

    @NotSerialized
    @ToOne("CREATOR")
    private User createUser;

    @NotSerialized
    @ManyToMany(table = "ODRECEIPTDEPT")
    private List<Dept> receiptDepts;

    @NotSerialized
    @OneToMany
    private List<ReceiptReply> replies;

    @Lazy(false)
    @ComputeColumn("count(select 1 from replies r where r.readed=0 and r.replied=1)")
    private Integer newReplyCount;

    /**
     * 回复时限
     */
    private Date deadline;

    /**
     * 预警时间
     */
    private Date warningTime;

    public Receipt()
    {
    }

    public Long getReceiptId()
    {
        return receiptId;
    }

    public void setReceiptId(Long receiptId)
    {
        this.receiptId = receiptId;
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

    public Date getSendTime()
    {
        return sendTime;
    }

    public void setSendTime(Date sendTime)
    {
        this.sendTime = sendTime;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public Long getLinkId()
    {
        return linkId;
    }

    public void setLinkId(Long linkId)
    {
        this.linkId = linkId;
    }

    public Boolean getSended()
    {
        return sended;
    }

    public void setSended(Boolean sended)
    {
        this.sended = sended;
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

    public List<Dept> getReceiptDepts()
    {
        return receiptDepts;
    }

    public void setReceiptDepts(List<Dept> receiptDepts)
    {
        this.receiptDepts = receiptDepts;
    }

    public List<ReceiptReply> getReplies()
    {
        return replies;
    }

    public void setReplies(List<ReceiptReply> replies)
    {
        this.replies = replies;
    }

    public Integer getNewReplyCount()
    {
        return newReplyCount;
    }

    public void setNewReplyCount(Integer newReplyCount)
    {
        this.newReplyCount = newReplyCount;
    }

    public Date getDeadline()
    {
        return deadline;
    }

    public void setDeadline(Date deadline)
    {
        this.deadline = deadline;
    }

    public Date getWarningTime()
    {
        return warningTime;
    }

    public void setWarningTime(Date warningTime)
    {
        this.warningTime = warningTime;
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o)
            return true;

        if(!(o instanceof Receipt))
            return false;

        Receipt odReceipt = (Receipt) o;

        return receiptId.equals(odReceipt.receiptId);
    }

    @Override
    public int hashCode()
    {
        return receiptId.hashCode();
    }
}
