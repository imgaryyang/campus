package com.gzzm.ods.exchange;

import com.gzzm.ods.document.OfficeDocument;
import com.gzzm.ods.sendnumber.SendNumber;
import com.gzzm.platform.organ.Dept;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.*;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 公文发送记录
 *
 * @author camel
 * @date 12-8-28
 */
@Entity(table = "ODSENDRECORD", keys = "recordId")
public class SendRecord
{
    /**
     * 主键
     */
    @Generatable(length = 11)
    private Long recordId;

    @Index
    private Integer deptId;

    /**
     * 所属部门，即发文的部门
     */
    @NotSerialized
    private Dept dept;

    /**
     * 公文的标题
     */
    @Require
    @ColumnDescription(type = "varchar(500)")
    private String title;

    /**
     * 发文编号
     */
    @ColumnDescription(type = "varchar(50)")
    private String sendNumber;

    /**
     * 主题词
     */
    @ColumnDescription(type = "varchar(250)")
    private String subject;

    /**
     * 密级
     */
    @ColumnDescription(type = "varchar(50)")
    private String secret;

    /**
     * 优先级
     */
    @ColumnDescription(type = "varchar(50)")
    private String priority;

    /**
     * 份数
     */
    @ColumnDescription(type = "number(5)")
    private Integer sendCount;

    /**
     * 拟稿人
     */
    @Require
    @ColumnDescription(type = "varchar(50)")
    private String creator;

    private Integer createDeptId;

    @NotSerialized
    @ToOne("CREATEDEPTID")
    private Dept createDept;

    /**
     * 拟稿科室
     */
    @Require
    @ColumnDescription(type = "varchar(250)")
    private String createDeptName;

    /**
     * 登记时间
     */
    private Date recordTime;

    /**
     * 签发人
     */
    @ColumnDescription(type = "varchar(50)")
    private String signer;

    /**
     * 签发时间
     */
    private Date signTime;

    /**
     * 发往部门
     */
    @NotSerialized
    private char[] sendDepts;

    @NotSerialized
    private char[] mainSendDepts;

    @ColumnDescription(nullable = false, defaultValue = "1")
    private SendRecordState state;

    /**
     * 关联的公文文档，如果为手工录入，则为空，否则从发文流程中复制过来
     */
    @Index
    private Long documentId;

    @NotSerialized
    private OfficeDocument document;

    private Integer sendNumberId;

    private SendNumber sendNumberObject;

    @ColumnDescription(type = "varchar(4000)")
    private String remark;

    public SendRecord()
    {
    }

    public Long getRecordId()
    {
        return recordId;
    }

    public void setRecordId(Long recordId)
    {
        this.recordId = recordId;
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

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getSendNumber()
    {
        return sendNumber;
    }

    public void setSendNumber(String sendNumber)
    {
        this.sendNumber = sendNumber;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getSecret()
    {
        return secret;
    }

    public void setSecret(String secret)
    {
        this.secret = secret;
    }

    public String getPriority()
    {
        return priority;
    }

    public void setPriority(String priority)
    {
        this.priority = priority;
    }

    public Integer getSendCount()
    {
        return sendCount;
    }

    public void setSendCount(Integer sendCount)
    {
        this.sendCount = sendCount;
    }

    public String getCreator()
    {
        return creator;
    }

    public void setCreator(String creator)
    {
        this.creator = creator;
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

    public String getCreateDeptName()
    {
        return createDeptName;
    }

    public void setCreateDeptName(String createDeptName)
    {
        this.createDeptName = createDeptName;
    }

    public Date getRecordTime()
    {
        return recordTime;
    }

    public void setRecordTime(Date recordTime)
    {
        this.recordTime = recordTime;
    }

    public String getSigner()
    {
        return signer;
    }

    public void setSigner(String signer)
    {
        this.signer = signer;
    }

    public Date getSignTime()
    {
        return signTime;
    }

    public void setSignTime(Date signTime)
    {
        this.signTime = signTime;
    }

    @NotSerialized
    public String getCreatorText()
    {
        if (!StringUtils.isEmpty(creator))
        {
            String s = creator;

            if (!StringUtils.isEmpty(createDeptName))
                s += "(" + createDeptName + ")";

            return s;
        }

        return "";
    }

    @NotSerialized
    public String getSignTimeText()
    {
        if (signTime != null)
            return DateUtils.toString(signTime, "yyyy-MM-dd");
        return "";
    }

    @NotSerialized
    public String getSignText()
    {
        if (!StringUtils.isEmpty(signer))
        {
            String s = signer;

            if (signTime != null)
                s += "(" + getSignTimeText() + ")";

            return s;
        }

        return "";
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

    public char[] getSendDepts()
    {
        return sendDepts;
    }

    public void setSendDepts(char[] sendDepts)
    {
        this.sendDepts = sendDepts;
    }

    public char[] getMainSendDepts()
    {
        return mainSendDepts;
    }

    public void setMainSendDepts(char[] mainSendDepts)
    {
        this.mainSendDepts = mainSendDepts;
    }

    public SendRecordState getState()
    {
        return state;
    }

    public void setState(SendRecordState state)
    {
        this.state = state;
    }

    public Integer getSendNumberId()
    {
        return sendNumberId;
    }

    public void setSendNumberId(Integer sendNumberId)
    {
        this.sendNumberId = sendNumberId;
    }

    public SendNumber getSendNumberObject()
    {
        return sendNumberObject;
    }

    public void setSendNumberObject(SendNumber sendNumberObject)
    {
        this.sendNumberObject = sendNumberObject;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    @Override
    public String toString()
    {
        return title;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof SendRecord))
            return false;

        SendRecord that = (SendRecord) o;

        return recordId.equals(that.recordId);
    }

    @Override
    public int hashCode()
    {
        return recordId.hashCode();
    }
}
