package com.gzzm.ods.flow;

import com.gzzm.ods.business.BusinessModel;
import com.gzzm.ods.document.OfficeDocument;
import com.gzzm.ods.receivetype.ReceiveType;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.Provider;
import net.cyan.commons.util.compile.Compile;
import net.cyan.nest.annotation.Inject;
import net.cyan.thunwind.PersistenceManager;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * 将odflowinstance复制一份，用于做列表查询，当将数据按时间分表时不影响业务
 *
 * @author camel
 * @date 2018/3/1
 */
@Entity(table = "ODFLOWINSTANCEQ", keys = "instanceId")
public class OdFlowInstanceQ
{
    private final static Map<Integer, Class<? extends OdFlowInstanceQ>> INSTANCE_CLASS_MAP =
            new HashMap<Integer, Class<? extends OdFlowInstanceQ>>();

    @Inject
    private static Provider<OdFlowDao> daoProvider;

    @ColumnDescription(type = "number(12)")
    private Long instanceId;

    private Integer businessId;

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
     * 收文类型编号
     */
    private Integer receiveTypeId;

    private com.gzzm.ods.receivetype.ReceiveType receiveType;

    /**
     * 是否已经督办
     */
    @ColumnDescription(nullable = false, defaultValue = "0")
    private Boolean urged;

    /**
     * 业务简称，如呈阅，呈批等，可以为空
     */
    @ColumnDescription(type = "varchar(50)")
    private String simpleName;

    @ColumnDescription(type = "varchar(50)")
    private String priority;

    @SuppressWarnings("unchecked")
    protected synchronized static Class<? extends OdFlowInstanceQ> createInstanceClass(int year) throws Exception
    {
        Class<? extends OdFlowInstanceQ> c = INSTANCE_CLASS_MAP.get(year);
        if (c == null)
        {
            String className = "OdFlowInstanceQ_" + year;

            String s = "package com.gzzm.ods.flow;\n" +
                    "import net.cyan.thunwind.annotation.*;\n" +
                    "@Entity(table = \"ODFLOWINSTANCEQ_" + year + "\", keys = \"instanceId\")\n" +
                    "@Optimized(false)\n" +
                    "public class " + className + " extends OdFlowInstanceQ\n{}";

            c = Compile.compile(s, "com.gzzm.ods.flow." + className, false);

            PersistenceManager.getManager("").addEntityClass(c);

            INSTANCE_CLASS_MAP.put(year, c);
        }

        return c;
    }

    public OdFlowInstanceQ()
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

    public Boolean getUrged()
    {
        return urged;
    }

    public void setUrged(Boolean urged)
    {
        this.urged = urged;
    }

    public String getSimpleName()
    {
        return simpleName;
    }

    public void setSimpleName(String simpleName)
    {
        this.simpleName = simpleName;
    }

    public String getPriority()
    {
        return priority;
    }

    public void setPriority(String priority)
    {
        this.priority = priority;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof OdFlowInstanceQ))
            return false;

        OdFlowInstanceQ that = (OdFlowInstanceQ) o;

        return instanceId.equals(that.instanceId);
    }

    @Override
    public int hashCode()
    {
        return instanceId.hashCode();
    }

    public void copyForm(OdFlowInstance instance)
    {
        setInstanceId(instance.getInstanceId());
        setBusinessId(instance.getBusinessId());
        setDeptId(instance.getDeptId());
        setDocumentId(instance.getDocumentId());
        setSerial(instance.getSerial());
        setType(instance.getType());
        setTag(instance.getTag());
        setState(instance.getState());
        setCreator(instance.getCreator());
        setCreateDeptId(instance.getCreateDeptId());
        setDealer(instance.getDealer());
        setDealDeptId(instance.getDealDeptId());
        setReceiveTypeId(instance.getReceiveTypeId());
        setUrged(instance.isUrged());
        setSimpleName(instance.getSimpleName());
        setPriority(instance.getPriority());
    }

    @AfterAdd
    @AfterUpdate
    public static void save(OdFlowInstance instance) throws Exception
    {
        OdFlowInstanceQ instanceQ = new OdFlowInstanceQ();
        instanceQ.copyForm(instance);

        daoProvider.get().save(instanceQ);
    }
}
