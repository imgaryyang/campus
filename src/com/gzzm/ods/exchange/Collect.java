package com.gzzm.ods.exchange;

import com.gzzm.ods.document.OfficeDocument;
import com.gzzm.ods.flow.OdFlowInstance;
import com.gzzm.ods.urge.CollectUrge;
import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 会签公文
 *
 * @author camel
 * @date 2010-9-9
 */
@Entity(table = "ODCOLLECT", keys = "receiveId")
public class Collect
{
    /**
     * 收文ID
     */
    @ColumnDescription(type = "number(12)")
    private Long receiveId;

    @NotSerialized
    @Lazy(false)
    private ReceiveBase receiveBase;

    /**
     * 发起会签的流程的instanceId
     */
    @Index
    private Long collectInstanceId;

    @NotSerialized
    @ToOne("COLLECTINSTANCEID")
    private OdFlowInstance collectInstance;

    /**
     * 最初发起会签的那个流程的instanceId，当一个流程发起会签后，会签流程可以继续将此会签转发
     */
    @Index
    private Long topInstanceId;

    @NotSerialized
    @ToOne("TOPINSTANCEID")
    private OdFlowInstance topInstance;

    private Integer collectUserId;

    @NotSerialized
    @ToOne("COLLECTUSERID")
    private User collectUser;

    @Index
    @ColumnDescription(type = "number(13)")
    private Long collectStepId;

    /**
     * 要发送回给主办单位的文档
     */
    private Long documentId;

    /**
     * 要发送会给主办单位的文档
     */
    @NotSerialized
    private OfficeDocument document;

    /**
     * 办结时间
     */
    private Date endTime;

    /**
     * 是否发布给所有用户查看
     */
    private Boolean published;

    /**
     * 是否隐藏起来，所有用户都看不到
     */
    private Boolean hidden;

    /**
     * 此会签记录在主办单位流程中的stepId，每个会签记录会在主办单位的流程中生成一个step，以方便主办单位跟踪
     */
    @ColumnDescription(type = "number(13)")
    private Long stepId;

    private Boolean urged;

    @NotSerialized
    private CollectUrge urge;

    public Collect()
    {
    }

    public Long getReceiveId()
    {
        return receiveId;
    }

    public void setReceiveId(Long receiveId)
    {
        this.receiveId = receiveId;
    }

    public ReceiveBase getReceiveBase()
    {
        return receiveBase;
    }

    public void setReceiveBase(ReceiveBase receiveBase)
    {
        this.receiveBase = receiveBase;
    }

    public Long getCollectInstanceId()
    {
        return collectInstanceId;
    }

    public void setCollectInstanceId(Long collectInstanceId)
    {
        this.collectInstanceId = collectInstanceId;
    }

    public OdFlowInstance getCollectInstance()
    {
        return collectInstance;
    }

    public void setCollectInstance(OdFlowInstance collectInstance)
    {
        this.collectInstance = collectInstance;
    }

    public Long getTopInstanceId()
    {
        return topInstanceId;
    }

    public void setTopInstanceId(Long topInstanceId)
    {
        this.topInstanceId = topInstanceId;
    }

    public OdFlowInstance getTopInstance()
    {
        return topInstance;
    }

    public void setTopInstance(OdFlowInstance topInstance)
    {
        this.topInstance = topInstance;
    }

    public Integer getCollectUserId()
    {
        return collectUserId;
    }

    public void setCollectUserId(Integer collectUserId)
    {
        this.collectUserId = collectUserId;
    }

    public User getCollectUser()
    {
        return collectUser;
    }

    public void setCollectUser(User collectUser)
    {
        this.collectUser = collectUser;
    }

    public Long getCollectStepId()
    {
        return collectStepId;
    }

    public void setCollectStepId(Long collectStepId)
    {
        this.collectStepId = collectStepId;
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

    public Date getEndTime()
    {
        return endTime;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    public Boolean isPublished()
    {
        return published;
    }

    public void setPublished(Boolean published)
    {
        this.published = published;
    }

    public Boolean isHidden()
    {
        return hidden;
    }

    public void setHidden(Boolean hidden)
    {
        this.hidden = hidden;
    }

    public Long getStepId()
    {
        return stepId;
    }

    public void setStepId(Long stepId)
    {
        this.stepId = stepId;
    }

    public Boolean isUrged()
    {
        return urged;
    }

    public void setUrged(Boolean urged)
    {
        this.urged = urged;
    }

    public CollectUrge getUrge()
    {
        return urge;
    }

    public void setUrge(CollectUrge urge)
    {
        this.urge = urge;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof Collect))
            return false;

        Collect collect = (Collect) o;

        return receiveId.equals(collect.receiveId);

    }

    @Override
    public int hashCode()
    {
        return receiveId.hashCode();
    }
}
