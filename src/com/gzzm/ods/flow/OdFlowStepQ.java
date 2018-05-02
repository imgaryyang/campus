package com.gzzm.ods.flow;

import com.gzzm.ods.business.BusinessModel;
import com.gzzm.ods.document.OfficeDocument;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * @author camel
 * @date 2018/4/10
 */
@Entity(table = "ODFLOWSTEPQ", keys = "stepId")
@Indexes({
        @Index(columns = {"USERID", "STATE", "RECEIVETIME"})
})
public class OdFlowStepQ
{
    @ColumnDescription(type = "number(13)")
    private Long stepId;

    @ColumnDescription(type = "number(13)")
    private Long groupId;

    @Index
    @ColumnDescription(type = "number(12)")
    private Long instanceId;

    @ColumnDescription(type = "varchar(50)")
    private String nodeId;

    @ColumnDescription(type = "number(9)")
    private Integer userId;

    @ColumnDescription(type = "number(2)")
    private Integer state;

    private Date receiveTime;

    private Date showTime;

    private Boolean lastStep;

    @ColumnDescription(type = "varchar(250)")
    private String sourceName;

    @Index
    @ColumnDescription(type = "number(9)")
    private Integer consignationId;

    private Boolean hidden;

    @ColumnDescription(type = "number(4)")
    private Integer year;

    @ColumnDescription(type = "varchar(500)")
    private String title;

    private OdFlowInstanceState instanceState;

    @ColumnDescription(type = "varchar(250)")
    private String sendNumber;

    @ColumnDescription(type = "varchar(250)")
    private String sourceDept;

    @ColumnDescription(type = "varchar(250)")
    private String subject;

    private Long documentId;

    @Lazy(false)
    @NotSerialized
    private OfficeDocument document;

    @ColumnDescription(type = "varchar(50)")
    private String type;

    @ColumnDescription(type = "varchar(50)")
    private String tag;

    @ColumnDescription(type = "varchar(50)")
    private String priority;

    private String serial;

    private Boolean urged;

    @ColumnDescription(type = "number(7)")
    private Integer businessId;

    @ColumnDescription(type = "varchar(50)")
    private String simpleName;

    @ColumnDescription(type = "number(8)")
    private Integer sendNumberId;

    @ColumnDescription(type = "number(8)")
    private Integer receiveTypeId;

    @ColumnDescription(type = "varchar(200)")
    private String linkId;

    private Boolean firstStep;

    private Boolean collected;

    @ColumnDescription(type = "number(7)")
    private Integer createDeptId;

    @NotSerialized
    private BusinessModel business;

    public OdFlowStepQ()
    {
    }

    public Long getStepId()
    {
        return stepId;
    }

    public void setStepId(Long stepId)
    {
        this.stepId = stepId;
    }

    public Long getGroupId()
    {
        return groupId;
    }

    public void setGroupId(Long groupId)
    {
        this.groupId = groupId;
    }

    public String getNodeId()
    {
        return nodeId;
    }

    public void setNodeId(String nodeId)
    {
        this.nodeId = nodeId;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public Integer getState()
    {
        return state;
    }

    public void setState(Integer state)
    {
        this.state = state;
    }

    public Date getReceiveTime()
    {
        return receiveTime;
    }

    public void setReceiveTime(Date receiveTime)
    {
        this.receiveTime = receiveTime;
    }

    public Date getShowTime()
    {
        return showTime;
    }

    public void setShowTime(Date showTime)
    {
        this.showTime = showTime;
    }

    public Boolean getLastStep()
    {
        return lastStep;
    }

    public void setLastStep(Boolean lastStep)
    {
        this.lastStep = lastStep;
    }

    public String getSourceName()
    {
        return sourceName;
    }

    public void setSourceName(String sourceName)
    {
        this.sourceName = sourceName;
    }

    public Integer getConsignationId()
    {
        return consignationId;
    }

    public void setConsignationId(Integer consignationId)
    {
        this.consignationId = consignationId;
    }

    public Boolean getHidden()
    {
        return hidden;
    }

    public void setHidden(Boolean hidden)
    {
        this.hidden = hidden;
    }

    public Integer getYear()
    {
        return year;
    }

    public void setYear(Integer year)
    {
        this.year = year;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Long getInstanceId()
    {
        return instanceId;
    }

    public void setInstanceId(Long instanceId)
    {
        this.instanceId = instanceId;
    }

    public OdFlowInstanceState getInstanceState()
    {
        return instanceState;
    }

    public void setInstanceState(OdFlowInstanceState instanceState)
    {
        this.instanceState = instanceState;
    }

    public String getSendNumber()
    {
        return sendNumber;
    }

    public void setSendNumber(String sendNumber)
    {
        this.sendNumber = sendNumber;
    }

    public String getSourceDept()
    {
        return sourceDept;
    }

    public void setSourceDept(String sourceDept)
    {
        this.sourceDept = sourceDept;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
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

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getTag()
    {
        return tag;
    }

    public void setTag(String tag)
    {
        this.tag = tag;
    }

    public String getPriority()
    {
        return priority;
    }

    public void setPriority(String priority)
    {
        this.priority = priority;
    }

    public String getSerial()
    {
        return serial;
    }

    public void setSerial(String serial)
    {
        this.serial = serial;
    }

    public Boolean getUrged()
    {
        return urged;
    }

    public void setUrged(Boolean urged)
    {
        this.urged = urged;
    }

    public Integer getBusinessId()
    {
        return businessId;
    }

    public void setBusinessId(Integer businessId)
    {
        this.businessId = businessId;
    }

    public String getSimpleName()
    {
        return simpleName;
    }

    public void setSimpleName(String simpleName)
    {
        this.simpleName = simpleName;
    }

    public Integer getSendNumberId()
    {
        return sendNumberId;
    }

    public void setSendNumberId(Integer sendNumberId)
    {
        this.sendNumberId = sendNumberId;
    }

    public Integer getReceiveTypeId()
    {
        return receiveTypeId;
    }

    public void setReceiveTypeId(Integer receiveTypeId)
    {
        this.receiveTypeId = receiveTypeId;
    }

    public String getLinkId()
    {
        return linkId;
    }

    public void setLinkId(String linkId)
    {
        this.linkId = linkId;
    }

    public Boolean getFirstStep()
    {
        return firstStep;
    }

    public void setFirstStep(Boolean firstStep)
    {
        this.firstStep = firstStep;
    }

    public Boolean getCollected()
    {
        return collected;
    }

    public void setCollected(Boolean collected)
    {
        this.collected = collected;
    }

    public Integer getCreateDeptId()
    {
        return createDeptId;
    }

    public void setCreateDeptId(Integer createDeptId)
    {
        this.createDeptId = createDeptId;
    }

    public BusinessModel getBusiness()
    {
        return business;
    }

    public void setBusiness(BusinessModel business)
    {
        this.business = business;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof OdFlowStepQ))
            return false;

        OdFlowStepQ that = (OdFlowStepQ) o;

        return stepId.equals(that.stepId);
    }

    @Override
    public int hashCode()
    {
        return stepId.hashCode();
    }
}
