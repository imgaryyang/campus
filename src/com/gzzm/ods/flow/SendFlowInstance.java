package com.gzzm.ods.flow;

import com.gzzm.ods.document.OfficeDocument;
import com.gzzm.ods.redhead.RedHead;
import com.gzzm.ods.sendnumber.SendNumber;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.Inputable;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.storage.CommonFileColumn;

import java.util.Date;

/**
 * 发文流程实例，保存和发文流程相关的信息
 *
 * @author camel
 * @date 11-9-22
 */
@Entity(table = "ODFLOWSENDINSTANCE", keys = "instanceId")
public class SendFlowInstance
{
    /**
     * 实例id
     *
     * @see com.gzzm.platform.flow.SystemFlowInstance#instanceId
     */
    @ColumnDescription(type = "number(12)")
    private Long instanceId;

    /**
     * 发文文档ID，对于发文流程，和OdFlowInstance中的documentId相同，对于其他流程，不相同
     */
    @Index
    private Long documentId;

    /**
     * 关联发文表单
     */
    private OfficeDocument document;

    /**
     * 是否成文，默认为未成文
     */
    @ColumnDescription(defaultValue = "0")
    private Boolean textFinal;

    /**
     * 成文时间
     */
    private Date finalTime;

    /**
     * 发文字号ID，可以为空
     */
    private Integer sendNumberId;

    /**
     * 关联发文字号对象
     */
    private SendNumber sendNumber;

    /**
     * 红头模板ID，可以为空
     */
    private Integer redHeadId;

    /**
     * 关联红头模板对象
     */
    private RedHead redHead;

    /**
     * 备份的公文正文，用于取消成文时恢复正文
     */
    @CommonFileColumn(pathColumn = "backFilePath", target = "{backTarget}", defaultTarget = "od",
            path = "{yyyyMM}/{yyyyMMdd}/od/back/{instanceId}", clear = true)
    private Inputable backTextBody;

    @ColumnDescription(type = "varchar(20)")
    private String backTextType;


    /**
     * 备份正文内容保存的路径
     */
    @ColumnDescription(type = "varchar(250)")
    private String backFilePath;


    /**
     * 备份文件保存在哪个文件服务上，调用哪个文件服务读取文件
     */
    @ColumnDescription(type = "varchar(50)")
    private String backTarget;

    /**
     * 表单数据的ID
     */
    @ColumnDescription(type = "number(12)")
    private Long bodyId;

    /**
     * 做联合办文的步骤ID，用于联合办文单位反馈
     */
    @ColumnDescription(type = "number(13)")
    private Long unionStepId;

    /**
     * 发起联合办文的用户的id，用于联合办文单位反馈
     */
    private Integer unionUserId;

    @ToOne("UNIONUSERID")
    private User unionUser;

    /**
     * 发文的拟稿人，如果是发文和OdFlowInstance的creator，如果是转发文，则为转发发文的人
     */
    private Integer creator;

    @ToOne("CREATOR")
    @NotSerialized
    private User createUser;

    /**
     * 发文拟稿人所属的部门ID，即创拟稿的科室，以方便后面统计
     */
    private Integer createDeptId;

    @ToOne("CREATEDEPTID")
    @NotSerialized
    private Dept createDept;

    /**
     * 是否已经转发文
     */
    @ColumnDescription(defaultValue = "1")
    private Boolean sended;

    /**
     * 转发文的步骤ID
     */
    @ColumnDescription(type = "number(13)")
    private Long sendStepId;

    @Index
    @ColumnDescription(defaultValue = "0", nullable = false)
    private Boolean saveRecorded;

    /**
     * 是否提醒过发文编号重复，如果已经提醒过则不再提醒
     */
    @Index
    @ColumnDescription(defaultValue = "0", nullable = false)
    private Boolean sendNumberReminded;

    /**
     * 发文单位，即以哪个单位的名义发文，默认为空，表示已业务单位的名义发文
     */
    private Integer sendDeptId;

    @NotSerialized
    @ToOne("SENDDEPTID")
    private Dept sendDept;

    private Boolean sentOut;

    public SendFlowInstance()
    {
    }

    public Long getInstanceId()
    {
        return instanceId;
    }

    public void setInstanceId(Long instanceId)
    {
        this.instanceId = instanceId;
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

    public Boolean isTextFinal()
    {
        return textFinal;
    }

    public void setTextFinal(Boolean textFinal)
    {
        this.textFinal = textFinal;
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

    public Inputable getBackTextBody()
    {
        return backTextBody;
    }

    public void setBackTextBody(Inputable backTextBody)
    {
        this.backTextBody = backTextBody;
    }

    public String getBackTextType()
    {
        return backTextType;
    }

    public void setBackTextType(String backTextType)
    {
        this.backTextType = backTextType;
    }

    public String getBackFilePath()
    {
        return backFilePath;
    }

    public void setBackFilePath(String backFilePath)
    {
        this.backFilePath = backFilePath;
    }

    public String getBackTarget()
    {
        return backTarget;
    }

    public void setBackTarget(String backTarget)
    {
        this.backTarget = backTarget;
    }

    public Date getFinalTime()
    {
        return finalTime;
    }

    public void setFinalTime(Date finalTime)
    {
        this.finalTime = finalTime;
    }

    public Long getBodyId()
    {
        return bodyId;
    }

    public void setBodyId(Long bodyId)
    {
        this.bodyId = bodyId;
    }

    public Long getUnionStepId()
    {
        return unionStepId;
    }

    public void setUnionStepId(Long unionStepId)
    {
        this.unionStepId = unionStepId;
    }

    public Integer getUnionUserId()
    {
        return unionUserId;
    }

    public void setUnionUserId(Integer unionUserId)
    {
        this.unionUserId = unionUserId;
    }

    public User getUnionUser()
    {
        return unionUser;
    }

    public void setUnionUser(User unionUser)
    {
        this.unionUser = unionUser;
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

    public Boolean isSended()
    {
        return sended;
    }

    public void setSended(Boolean sended)
    {
        this.sended = sended;
    }

    public Long getSendStepId()
    {
        return sendStepId;
    }

    public void setSendStepId(Long sendStepId)
    {
        this.sendStepId = sendStepId;
    }

    public Boolean getSaveRecorded()
    {
        return saveRecorded;
    }

    public void setSaveRecorded(Boolean saveRecorded)
    {
        this.saveRecorded = saveRecorded;
    }

    public Boolean getSendNumberReminded()
    {
        return sendNumberReminded;
    }

    public void setSendNumberReminded(Boolean sendNumberReminded)
    {
        this.sendNumberReminded = sendNumberReminded;
    }

    public Integer getSendDeptId()
    {
        return sendDeptId;
    }

    public void setSendDeptId(Integer sendDeptId)
    {
        this.sendDeptId = sendDeptId;
    }

    public Dept getSendDept()
    {
        return sendDept;
    }

    public void setSendDept(Dept sendDept)
    {
        this.sendDept = sendDept;
    }

    public Boolean getSentOut()
    {
        return sentOut;
    }

    public void setSentOut(Boolean sentOut)
    {
        this.sentOut = sentOut;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof SendFlowInstance))
            return false;

        SendFlowInstance that = (SendFlowInstance) o;

        return instanceId.equals(that.instanceId);
    }

    @Override
    public int hashCode()
    {
        return instanceId.hashCode();
    }
}
