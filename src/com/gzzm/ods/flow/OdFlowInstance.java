package com.gzzm.ods.flow;

import com.gzzm.ods.business.*;
import com.gzzm.ods.document.OfficeDocument;
import com.gzzm.ods.exchange.ReceiveBase;
import com.gzzm.ods.receivetype.ReceiveType;
import com.gzzm.ods.timeout.OdTimeout;
import com.gzzm.ods.urge.OdFlowUrge;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.organ.*;
import com.gzzm.platform.timeout.Timeout;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * 公文流程的实例对象，保存公文流程的基本信息
 *
 * @author camel
 * @date 2010-9-14
 */
@Entity(table = "ODFLOWINSTANCE", keys = "instanceId")
@Indexes({
        @Index(columns = {"DEPTID", "TYPE", "STATE"}),
        @Index(columns = {"DEPTID", "CREATEDEPTID"}),
        @Index(columns = {"STATE", "URGED"}),
        @Index(columns = {"DOCUMENTID", "TYPE"})
})
public class OdFlowInstance
{
    @Inject
    private static Provider<List<BusinessType>> businessTypesProvider;

    @Inject
    private static Provider<OdSignService> serviceProvider;

    /**
     * 实例id
     *
     * @see com.gzzm.platform.flow.SystemFlowInstance#instanceId
     */
    @ColumnDescription(type = "number(12)")
    private Long instanceId;

    /**
     * 此流程关联的业务
     */
    private Integer businessId;

    @Lazy(false)
    @NotSerialized
    private BusinessModel business;

    /**
     * 所属部门的部门ID
     */
    private Integer deptId;

    @NotSerialized
    private Dept dept;

    /**
     * 文档ID
     */
    private Long documentId;

    @Lazy(false)
    @NotSerialized
    private OfficeDocument document;

    /**
     * 文件编号，对于发文，联合发文，会签公文为发文编号，对于收文为收文编号
     */
    private String serial;

    /**
     * 业务类型，如send为发文流程,receive为收文流程，union为联合办文流程,collect为会签流程,inner为内部流程等等，可扩展
     *
     * @see com.gzzm.ods.business.BusinessModel#type
     */
    @ColumnDescription(type = "varchar(50)")
    private String type;

    /**
     * 标签，可为每个实例打不同的标签，如紧急，重要等，以用于查询和显示
     */
    @ColumnDescription(type = "varchar(50)")
    private String tag;

    /**
     * 公文流程状态，冗余字段用于查询，和PFFLOWINSTANCE表重复
     *
     * @see com.gzzm.platform.flow.SystemFlowInstance#state
     */
    private OdFlowInstanceState state;

    /**
     * 开始时间，冗余字段用于查询，和PFFLOWINSTANCE表重复
     *
     * @see com.gzzm.platform.flow.SystemFlowInstance#startTime
     */
    private Date startTime;

    /**
     * 结束时间，冗余字段用于查询，PFFLOWINSTANCE表重复
     *
     * @see com.gzzm.platform.flow.SystemFlowInstance#endTime
     */
    private Date endTime;

    /**
     * 流程创建人ID，冗余字段用于查询，PFFLOWINSTANCE表重复
     *
     * @see com.gzzm.platform.flow.SystemFlowInstance#creator
     */
    private Integer creator;

    @NotSerialized
    @ToOne("CREATOR")
    private User createUser;

    /**
     * 流程创建人所属的部门ID，即创建流程的科室，以方便后面统计
     */
    private Integer createDeptId;

    @ToOne("CREATEDEPTID")
    @NotSerialized
    private Dept createDept;

    /**
     * 承办人
     */
    private Integer dealer;

    @NotSerialized
    @ToOne("DEALER")
    private User dealUser;

    /**
     * 承办科室
     */
    private Integer dealDeptId;

    @NotSerialized
    @ToOne("DEALDEPTID")
    private Dept dealDept;

    /**
     * 对应收文ID，仅对收文，会签，联合发文有用
     */
    @Index
    @ColumnDescription(type = "number(12)")
    private Long receiveId;

    @NotSerialized
    private ReceiveBase receiveBase;

    /**
     * 收文类型编号
     */
    private Integer receiveTypeId;

    private com.gzzm.ods.receivetype.ReceiveType receiveType;

    /**
     * 督办信息
     */
    @NotSerialized
    private OdFlowUrge urge;

    /**
     * 是否已经督办
     */
    @ColumnDescription(nullable = false, defaultValue = "0")
    private Boolean urged;

    /**
     * 关联的数据ID，一般是某个业务数据对应的记录的主键值
     */
    @Index
    @ColumnDescription(type = "varchar(200)")
    private String linkId;

    /**
     * 领导是否已批示
     */
    private Boolean signed;

    /**
     * 关联的共享目录
     */
    @Index
    private Integer catalogId;

    @NotSerialized
    private OdInstanceCatalog catalog;

    /**
     * 签批的用户
     */
    @NotSerialized
    @ManyToMany(table = "ODFLOWSIGNER")
    private List<User> signers;

    /**
     * 业务简称，如呈阅，呈批等，可以为空
     */
    @ColumnDescription(type = "varchar(50)")
    private String simpleName;

    private Date deadline;

    @NotSerialized
    @ComputeColumn("select t from Timeout t where t.typeId='" + OdTimeout.FLOW_ID +
            "' and recordId=instanceId order by timeoutTime")
    private List<Timeout> timeouts;

    /**
     * 是否已经归档
     */
    @ColumnDescription(nullable = false, defaultValue = "0")
    private Boolean cataloged;

    /**
     * 设置不再归档
     */
    @ColumnDescription(nullable = false, defaultValue = "0")
    private Boolean nocataloged;

    /**
     * 是否需要检查超时
     */
    private Boolean timeoutChecked;

    @ColumnDescription(type = "varchar(50)")
    private String priority;

    /**
     * 数据所属的年份，用于对数据进行分区
     */
    @ColumnDescription(type = "number(4)")
    private Integer year;

    public OdFlowInstance()
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

    public Integer getBusinessId()
    {
        return businessId;
    }

    public void setBusinessId(Integer businessId)
    {
        this.businessId = businessId;
    }

    public BusinessModel getBusiness()
    {
        return business;
    }

    public void setBusiness(BusinessModel business)
    {
        this.business = business;
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

    public Long getDocumentId()
    {
        return documentId;
    }

    public void setDocumentId(Long documentId)
    {
        this.documentId = documentId;
    }

    public String getEncodedDocumentId()
    {
        return OfficeDocument.encodeId(documentId);
    }

    public OfficeDocument getDocument()
    {
        return document;
    }

    public void setDocument(OfficeDocument document)
    {
        this.document = document;
    }

    public String getSerial()
    {
        return serial;
    }

    public void setSerial(String serial)
    {
        this.serial = serial;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    @NotSerialized
    public String getTypeName()
    {
        BusinessModel business = getBusiness();
        if (business != null)
        {
            String typeName = business.getTypeName();
            if (!StringUtils.isEmpty(typeName))
                return typeName;
        }

        String type = getType();

        List<BusinessType> businessTypes = businessTypesProvider.get();

        for (BusinessType businessType : businessTypes)
        {
            if (businessType.getType().equals(type))
                return businessType.getName();
        }

        return Tools.getMessage("platform.flow." + type);
    }

    public String getTag()
    {
        return tag;
    }

    public void setTag(String tag)
    {
        this.tag = tag;
    }

    public OdFlowInstanceState getState()
    {
        return state;
    }

    public void setState(OdFlowInstanceState state)
    {
        this.state = state;
    }

    public Date getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public Date getEndTime()
    {
        return endTime;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
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

    public Integer getDealer()
    {
        return dealer;
    }

    public void setDealer(Integer dealer)
    {
        this.dealer = dealer;
    }

    public User getDealUser()
    {
        return dealUser;
    }

    public void setDealUser(User dealUser)
    {
        this.dealUser = dealUser;
    }

    public Integer getDealDeptId()
    {
        return dealDeptId;
    }

    public void setDealDeptId(Integer dealDeptId)
    {
        this.dealDeptId = dealDeptId;
    }

    public Dept getDealDept()
    {
        return dealDept;
    }

    public void setDealDept(Dept dealDept)
    {
        this.dealDept = dealDept;
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

    public Integer getReceiveTypeId()
    {
        return receiveTypeId;
    }

    public void setReceiveTypeId(Integer receiveTypeId)
    {
        this.receiveTypeId = receiveTypeId;
    }

    public ReceiveType getReceiveType()
    {
        return receiveType;
    }

    public void setReceiveType(ReceiveType receiveType)
    {
        this.receiveType = receiveType;
    }

    public OdFlowUrge getUrge()
    {
        return urge;
    }

    public void setUrge(OdFlowUrge urge)
    {
        this.urge = urge;
    }

    public Boolean isUrged()
    {
        return urged;
    }

    public void setUrged(Boolean urged)
    {
        this.urged = urged;
    }

    public String getLinkId()
    {
        return linkId;
    }

    public void setLinkId(String linkId)
    {
        this.linkId = linkId;
    }

    public Boolean isSigned()
    {
        return signed;
    }

    public void setSigned(Boolean signed)
    {
        this.signed = signed;
    }

    public Integer getCatalogId()
    {
        return catalogId;
    }

    public void setCatalogId(Integer catalogId)
    {
        this.catalogId = catalogId;
    }

    public OdInstanceCatalog getCatalog()
    {
        return catalog;
    }

    public void setCatalog(OdInstanceCatalog catalog)
    {
        this.catalog = catalog;
    }

    public List<User> getSigners()
    {
        return signers;
    }

    public void setSigners(List<User> signers)
    {
        this.signers = signers;
    }

    @NotSerialized
    public String getSigns() throws Exception
    {
        return serviceProvider.get().getSigns(instanceId);
    }

    public String getSimpleName()
    {
        return simpleName;
    }

    public void setSimpleName(String simpleName)
    {
        this.simpleName = simpleName;
    }

    public Date getDeadline()
    {
        return deadline;
    }

    public void setDeadline(Date deadline)
    {
        this.deadline = deadline;
    }

    public List<Timeout> getTimeouts()
    {
        return timeouts;
    }

    public void setTimeouts(List<Timeout> timeouts)
    {
        this.timeouts = timeouts;
    }

    @NotSerialized
    public Timeout getValidTimeout()
    {
        Timeout validTimeout = null;
        for (Timeout timeout : getTimeouts())
        {
            if (timeout.getValid() != null && timeout.getValid())
            {
                validTimeout = timeout;
            }
        }

        return validTimeout;
    }

    public Boolean isCataloged()
    {
        return cataloged;
    }

    public void setCataloged(Boolean cataloged)
    {
        this.cataloged = cataloged;
    }

    public Boolean getNocataloged()
    {
        return nocataloged;
    }

    public void setNocataloged(Boolean nocataloged)
    {
        this.nocataloged = nocataloged;
    }

    public Boolean getTimeoutChecked()
    {
        return timeoutChecked;
    }

    public void setTimeoutChecked(Boolean timeoutChecked)
    {
        this.timeoutChecked = timeoutChecked;
    }

    public String getPriority()
    {
        return priority;
    }

    public void setPriority(String priority)
    {
        this.priority = priority;
    }

    public Integer getYear()
    {
        return year;
    }

    public void setYear(Integer year)
    {
        this.year = year;
    }

    @Override
    public String toString()
    {
        return getDocument().getTitle();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof OdFlowInstance))
            return false;

        OdFlowInstance that = (OdFlowInstance) o;

        return instanceId.equals(that.instanceId);
    }

    @Override
    public int hashCode()
    {
        return instanceId.hashCode();
    }
}