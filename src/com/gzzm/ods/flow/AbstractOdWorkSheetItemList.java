package com.gzzm.ods.flow;

import com.gzzm.ods.business.*;
import com.gzzm.ods.dict.Label;
import com.gzzm.ods.exchange.*;
import com.gzzm.ods.receivetype.ReceiveTypeDao;
import com.gzzm.ods.timeout.OdTimeout;
import com.gzzm.ods.type.OdTypeDisplay;
import com.gzzm.platform.annotation.*;
import com.gzzm.platform.attachment.AttachmentInfo;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.flow.*;
import com.gzzm.platform.flow.worksheet.WorkSheetItemList;
import com.gzzm.platform.organ.*;
import com.gzzm.platform.timeout.TimeoutService;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.*;
import net.cyan.commons.util.json.JsonParser;
import net.cyan.crud.*;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.*;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.*;
import net.cyan.valmiki.flow.Action;
import net.cyan.valmiki.flow.*;

import java.util.*;

/**
 * @author camel
 * @date 11-10-13
 */
@Service(url = "/ods/flow/worksheet")
public abstract class AbstractOdWorkSheetItemList<T extends OdWorkSheetItem> extends WorkSheetItemList<T>
{
    /**
     * 我会签的事项
     */
    public static final String COLLECT = "collect";

    @Inject
    protected OdWorkSheetDao odWorkSheetDao;

    @Inject
    private ExchangeCopyDao copyDao;

    @Inject
    protected BusinessDao businessDao;

    @Inject
    private ReceiveTypeDao receiveTypeDao;

    @Inject
    private TimeoutService timeoutService;

    @AuthDeptIds
    protected Collection<Integer> authDeptIds;

    @Inject
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private List<BusinessType> businessTypes;


    /**
     * 文件标记
     */
    private String tag;

    /**
     * 用紧急程度做查询条件
     */
    private String priority;

    /**
     * 发文字号id
     */
    private Integer sendNumberId;

    /**
     * 收文类型ID
     */
    private Integer receiveTypeId;

    /**
     * 多选的收文类型ID
     */
    private Integer[] topReceiveTypeIds;

    /**
     * 不包括的收文类型ID
     */
    private Integer[] noTopReceiveTypeIds;

    /**
     * 用发文字号做查询条件
     */
    @Like
    private String sendNumber;

    /**
     * 用编号做查询条件
     */
    @Like
    private String serial;

    /**
     * 用来文单位做查询条件
     */
    @Like
    private String sourceDept;

    /**
     * 用主题词做查询条件
     */
    @Like
    private String subject;

    /**
     * 全文索引
     */
    @Contains
    private String text;

    /**
     * 是否只查询督办文件
     */
    private Boolean urged;

    /**
     * 业务ID，只查询某种业务的数据
     */
    private Integer[] businessId;

    private Integer[] excludeBusinessId;

    private Integer[] queryBusinessIds;

    /**
     * 业务类型，只过滤某种业务的数据
     */
    private String[] businessType;

    private String typeName;

    private String simpleName;

    private String odType;

    /**
     * 是否在左边显示类型树
     */
    private boolean showTypeTree;

    /**
     * 一个json，json中定义一些列条件，根据这些条件过滤公文
     *
     * @see com.gzzm.ods.dict.Label
     */
    private Integer labelId;

    protected String query;

    private List<OdFlowInstance> odInstances;

    private List<SendFlowInstance> sendInstances;

    private List<BusinessModel> businessList;

    private List<KeyValue<String>> typeList;

    private Integer createDeptId;

    private DeptTreeModel createDeptTree;

    private DeptTreeModel dealDeptTree;

    /**
     * 是否在待办和已办列表中显示被其他人接收的事项
     *
     * @see /WEB-INF/config/ods/config.properties
     */
    @Constant("od_worksheet_showAccpted")
    private Boolean showAccpted;

    /**
     * 是否在已办列表中显示被其他人接收的事项
     *
     * @see /WEB-INF/config/ods/config.properties
     */
    @Constant("od_worksheet_showAccptedDealed")
    private Boolean showAccptedDealed;

    @ConfigValue(name = "OD_STEPQ", defaultValue = "false")
    private Boolean stepQ;

    public AbstractOdWorkSheetItemList()
    {
    }

    @Override
    public int[] getNoDealStates()
    {
        if (showAccpted != null && showAccpted)
        {
            return new int[]{FlowStep.NOACCEPT, FlowStep.NODEAL, FlowStep.BACKNOACCEPT, FlowStep.BACKNODEAL,
                    FlowStep.COPYNOACCEPT, FlowStep.COPYNODEAL, FlowStep.NODEAL_REPLYED, FlowStep.ACCEPTED};
        }
        else
        {
            return super.getNoDealStates();
        }
    }

    @Override
    public int[] getDealedStates()
    {
        if ((showAccpted != null && showAccpted && showAccptedDealed == null) ||
                (showAccptedDealed != null && showAccptedDealed))
        {
            return new int[]{FlowStep.DEALED, FlowStep.COPYDEALED, FlowStep.STOPED,
                    FlowStep.DEALED_REPLYED, FlowStep.DEALED_REPLYED_NOACCEPT, FlowStep.ACCEPTED_DEALED};
        }
        else
        {
            return super.getDealedStates();
        }
    }

    @Override
    protected SystemFlowDao createSystemFlowDao() throws Exception
    {
        return OdSystemFlowDao.getInstance(year);
    }

    public Boolean getStepQ()
    {
        return stepQ;
    }

    public void setStepQ(Boolean stepQ)
    {
        this.stepQ = stepQ;
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

    public Integer[] getTopReceiveTypeIds()
    {
        return topReceiveTypeIds;
    }

    public void setTopReceiveTypeIds(Integer[] topReceiveTypeIds)
    {
        this.topReceiveTypeIds = topReceiveTypeIds;
    }

    public Integer[] getNoTopReceiveTypeIds()
    {
        return noTopReceiveTypeIds;
    }

    public void setNoTopReceiveTypeIds(Integer[] noTopReceiveTypeIds)
    {
        this.noTopReceiveTypeIds = noTopReceiveTypeIds;
    }

    @NotSerialized
    public List<Integer> getReceiveTypeIds() throws Exception
    {
        if (receiveTypeId == null && topReceiveTypeIds == null)
            return null;

        if (receiveTypeId != null)
            return receiveTypeDao.getReceiveType(receiveTypeId).getAllReceiveTypeIds();

        List<Integer> receiveTypeIds = new ArrayList<Integer>();
        for (Integer topReceiveTypeId : topReceiveTypeIds)
        {
            if (topReceiveTypeId != null)
            {
                receiveTypeIds.addAll(receiveTypeDao.getReceiveType(topReceiveTypeId).getAllReceiveTypeIds());
            }
        }

        return receiveTypeIds;
    }

    @NotSerialized
    public List<Integer> getNoReceiveTypeIds() throws Exception
    {
        if (noTopReceiveTypeIds == null)
            return null;

        List<Integer> receiveTypeIds = new ArrayList<Integer>();
        for (Integer topReceiveTypeId : noTopReceiveTypeIds)
        {
            if (topReceiveTypeId != null)
            {
                receiveTypeIds.addAll(receiveTypeDao.getReceiveType(topReceiveTypeId).getAllReceiveTypeIds());
            }
        }

        return receiveTypeIds;
    }

    public String getSendNumber()
    {
        return sendNumber;
    }

    public void setSendNumber(String sendNumber)
    {
        this.sendNumber = sendNumber;
    }

    public String getSerial()
    {
        return serial;
    }

    public void setSerial(String serial)
    {
        this.serial = serial;
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

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public Boolean getUrged()
    {
        return urged;
    }

    public void setUrged(Boolean urged)
    {
        this.urged = urged;
    }

    public Integer[] getBusinessId()
    {
        return businessId;
    }

    public void setBusinessId(Integer[] businessId)
    {
        this.businessId = businessId;
    }

    public Integer[] getExcludeBusinessId()
    {
        return excludeBusinessId;
    }

    public void setExcludeBusinessId(Integer[] excludeBusinessId)
    {
        this.excludeBusinessId = excludeBusinessId;
    }

    public Integer[] getQueryBusinessIds()
    {
        return queryBusinessIds;
    }

    public void setQueryBusinessIds(Integer[] queryBusinessIds)
    {
        this.queryBusinessIds = queryBusinessIds;
    }

    public String[] getBusinessType()
    {
        return businessType;
    }

    public void setBusinessType(String[] businessType)
    {
        this.businessType = businessType;
    }

    public String getTypeName()
    {
        return typeName;
    }

    public void setTypeName(String typeName)
    {
        this.typeName = typeName;
    }

    public String getSimpleName()
    {
        return simpleName;
    }

    public void setSimpleName(String simpleName)
    {
        this.simpleName = simpleName;
    }

    public String getOdType()
    {
        return odType;
    }

    public void setOdType(String odType)
    {
        this.odType = odType;
    }

    public boolean isShowTypeTree()
    {
        return showTypeTree;
    }

    public void setShowTypeTree(boolean showTypeTree)
    {
        this.showTypeTree = showTypeTree;
    }

    public Integer getLabelId()
    {
        return labelId;
    }

    public void setLabelId(Integer labelId)
    {
        this.labelId = labelId;
    }

    public String getQuery()
    {
        return query;
    }

    public Integer getCreateDeptId()
    {
        return createDeptId;
    }

    public void setCreateDeptId(Integer createDeptId)
    {
        this.createDeptId = createDeptId;
    }

    @Select(field = "createDeptId")
    public DeptTreeModel getCreateDeptTree()
    {
        if (createDeptTree == null)
        {
            createDeptTree = new DeptTreeModel();
            createDeptTree.setRootId(userOnlineInfo.getBureauId());
        }
        return createDeptTree;
    }

    public void setCreateDeptTree(DeptTreeModel createDeptTree)
    {
        this.createDeptTree = createDeptTree;
    }

    @Select(field = "dealDeptId")
    public DeptTreeModel getDealDeptTree()
    {
        if (dealDeptTree == null)
        {
            dealDeptTree = new DeptTreeModel();
            dealDeptTree.setRootId(userOnlineInfo.getBureauId());
        }

        return dealDeptTree;
    }

    public void setDealDeptTree(DeptTreeModel dealDeptTree)
    {
        this.dealDeptTree = dealDeptTree;
    }

    @NotSerialized
    @Select(field = "queryBusinessIds")
    public List<BusinessModel> getBusinessList()
    {
        if (businessList == null)
            businessList = null;

        return null;
    }

    @NotSerialized
    @Select(field = "typeName")
    public List<KeyValue<String>> getTypeList() throws Exception
    {
        if (typeList == null)
        {
            typeList = new ArrayList<KeyValue<String>>(0);
//            Collection<Integer> authDeptIds = this.authDeptIds;
//
//            if (authDeptIds == null)
//                authDeptIds = Arrays.asList(1, userOnlineInfo.getBureauId());
//
//            List<BusinessModel> business = businessDao.getAllBusiness(authDeptIds, businessType);
//
//            typeList = new ArrayList<KeyValue<String>>();
//
//            for (BusinessModel businessModel : business)
//            {
//                String typeName;
//                String type = businessModel.getTypeName();
//
//                if (StringUtils.isEmpty(type))
//                {
//                    type = businessModel.getType().getType();
//                    typeName = businessModel.getType().getName();
//                }
//                else
//                {
//                    typeName = type;
//                }
//
//                boolean b = false;
//                for (KeyValue type1 : typeList)
//                {
//                    if (type1.getKey().equals(type))
//                    {
//                        b = true;
//                        break;
//                    }
//                }
//
//                if (!b)
//                    typeList.add(new KeyValue<String>(type, typeName));
//            }
//
//            typeList = new ArrayList<KeyValue<String>>();
        }

        return typeList;
    }


    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        initOdType();
    }

    @Override
    protected Class<? extends FlowPage> getFlowPageClass(String flowTag) throws Exception
    {
        return OdFlowService.getFlowPageClass(flowTag);
    }

    @Override
    protected String getCatalogType()
    {
        return "od";
    }

    @Override
    public int[] getStates(String type) throws Exception
    {
        if (COLLECT.equals(type))
        {
            //我发起会签的事项
            return new int[]{FlowStep.NOACCEPT, FlowStep.NODEAL, FlowStep.BACKNOACCEPT,
                    FlowStep.BACKNODEAL, FlowStep.NODEAL_REPLYED, FlowStep.DEALED};
        }

        return super.getStates(type);
    }

    @Override
    protected String getQueryString() throws Exception
    {
        if (stepQ == null || !stepQ)
            return super.getQueryString();

        boolean distinct = isDistinct();

        String stepClassName = "OdFlowStepQ";
        if (DEPT.equals(type))
            stepClassName = "OdFlowStepD";

        StringBuilder buffer = new StringBuilder("select step.stepId,step.groupId,");
        if (DEPT.equals(type))
            buffer.append("step.deptId as receiver");
        else
            buffer.append("step.userId as receiver");

        buffer.append(" from ").append(stepClassName).append(" step ");

        joinCatalog(buffer);

        buffer.append(" where ");

        addBaseCondition(buffer, "step");

        where(buffer);

        if (distinct && !DEPT.equals(type))
        {
            buffer.append(" and step.lastStep=1");
        }

        orderBy(buffer);

        return buffer.toString();
    }

    @Override
    protected String getCountQueryString() throws Exception
    {
        if (stepQ == null || !stepQ)
            return super.getCountQueryString();

        boolean distinct = isDistinct();

        StringBuilder buffer = new StringBuilder("select count(step.stepId) from OdFlowStepQ step ");

        join(buffer);

        buffer.append(" where ");

        addBaseCondition(buffer, "step");

        where(buffer);

        if (distinct && !DEPT.equals(type))
        {
            buffer.append(" and step.lastStep=1");
        }

        return buffer.toString();
    }

    @Override
    protected void join(StringBuilder buffer) throws Exception
    {
        joinOdInstance(buffer);
        joinCatalog(buffer);
    }

    protected void joinOdInstance(StringBuilder buffer) throws Exception
    {
        Class<?> odFlowInstanceClass = OdFlowInstance.class;
        if (year > 0)
            odFlowInstanceClass = OdFlowInstanceQ.createInstanceClass(year);
        else if (year == -1)
            odFlowInstanceClass = OdFlowInstanceQ.class;

        buffer.append(" join ").append(odFlowInstanceClass.getSimpleName())
                .append(" od on step.instanceId=od.instanceId ");

        if (sendNumberId != null && sendNumberId > 0)
        {
            buffer.append(" join SendFlowInstance send on step.instanceId=send.instanceId ");
        }
    }

    protected void orderBy(StringBuilder buffer) throws Exception
    {
        if (stepQ != null && stepQ)
        {
            OrderBy orderBy = getOrderBy();
            if (orderBy == null)
            {
                if (NODEAL.equals(type))
                    buffer.append(" order by step.showTime desc");
                else
                    buffer.append(" order by step.receiveTime desc");
            }
            else
            {
                String name = orderBy.getName();
                if (name.startsWith("od."))
                {
                    int index = name.lastIndexOf('.');
                    name = "step." + name.substring(index + 1);
                }

                buffer.append(" order by ").append(name);

                if (orderBy.getType() == OrderType.desc)
                    buffer.append(" desc");
            }
        }
        else
        {
            super.orderBy(buffer);
        }
    }

    protected void addBaseCondition(StringBuilder buffer, String alias) throws Exception
    {
        Integer deptId = getDeptId();
        if (stepQ != null && stepQ && consignationId == null && deptId == null)
        {
            buffer.append(alias).append(".userId=:userId");

            if (SELF.equals(type))
            {
                buffer.append(" and ").append(alias).append(".firstStep=1 ");
            }

            if (!NOREAD.equals(type) && !READED.equals(type))
            {
                buffer.append(" and ").append(alias).append(".nodeId in ?nodeId and ").append(alias)
                        .append(".nodeId not in ?excludeNode");
            }
        }
        else
        {
            super.addBaseCondition(buffer, alias);
        }

        if (COLLECT.equals(getType()))
        {
            if (stepQ != null && stepQ)
            {
                buffer.append(" and ").append(alias).append(".collected=1");
            }
            else
            {
                buffer.append(" and (select 1 from Collect where collectStepId=").append(alias)
                        .append(".stepId) is not empty");
            }
        }
    }

    @Override
    protected void where(StringBuilder buffer) throws Exception
    {
        if (stepQ != null && stepQ)
        {
            whereStep(buffer);
            whereStepQ(buffer);
            whereCatalog(buffer);
        }
        else
        {
            whereStep(buffer);
            whereOdInstance(buffer);
            whereCatalog(buffer);
        }

        if (!StringUtils.isEmpty(query))
        {
            buffer.append(" and (").append(query).append(")");
        }
    }

    protected void whereOdInstance(StringBuilder buffer) throws Exception
    {
        if (!StringUtils.isEmpty(sendNumber) || !StringUtils.isEmpty(sourceDept) || !StringUtils.isEmpty(subject))
            buffer.append(" and od.document is not null");

        if (getInstanceState() == null || Null.isNull(getInstanceState()))
            buffer.append(" and od.state<2");
        else
            buffer.append(" and od.state=?instanceState");

        buffer.append(" and od.document.title like ?title and contains(od.document.textContent,?text) and " +
                "od.type in ?businessType and od.tag=?tag and od.priority=?priority and " +
                "od.serial like ?serial and od.document.sendNumber like ?sendNumber and " +
                "od.document.sourceDept like ?sourceDept and od.document.subject like ?subject and " +
                "od.urged=?urged and od.businessId in ?queryBusinessIds and od.simpleName=?simpleName");

        buffer.append(" and od.createDeptId=?createDeptId");

        if (excludeBusinessId != null)
        {
            buffer.append(" and od.businessId not in ?excludeBusinessId");
        }

        if (!StringUtils.isEmpty(typeName))
        {
            buffer.append(" and (od.type=?typeName or od.business.typeName=?typeName)");
        }

        if (sendNumberId != null)
        {
            buffer.append(" and send.sendNumberId=:sendNumberId");
        }
        else if (receiveTypeId != null || topReceiveTypeIds != null)
        {
            buffer.append(" and od.receiveTypeId in :receiveTypeIds");
        }
        else if (noTopReceiveTypeIds != null)
        {
            buffer.append(" and (od.receiveTypeId is null or od.receiveTypeId not in :noReceiveTypeIds)");
        }
    }

    @Override
    protected void whereStep(StringBuilder buffer) throws Exception
    {
        if (stepQ != null && stepQ && DEPT.equals(type))
        {
            buffer.append(
                    " and step.receiveTime>=?time_start and step.receiveTime<=?time_end and step.sourceName like ?sourceName");
        }
        else
        {
            super.whereStep(buffer);
        }
    }

    protected void whereStepQ(StringBuilder buffer) throws Exception
    {
        if (getInstanceState() == null || Null.isNull(getInstanceState()))
            buffer.append(" and step.instanceState<2");
        else
            buffer.append(" and step.instanceState=?instanceState");

        buffer.append(" and step.title like ?title and contains(step.document.textContent,?text) and " +
                "step.type in ?businessType and step.tag=?tag and step.priority=?priority and " +
                "step.serial like ?serial and step.sendNumber like ?sendNumber and " +
                "step.sourceDept like ?sourceDept and step.subject like ?subject and " +
                "step.urged=?urged and step.businessId in ?queryBusinessIds and step.simpleName=?simpleName");

        buffer.append(" and step.createDeptId=?createDeptId");

        if (excludeBusinessId != null)
        {
            buffer.append(" and step.businessId not in ?excludeBusinessId");
        }

        if (!StringUtils.isEmpty(typeName))
        {
            buffer.append(" and (step.type=?typeName or step.business.typeName=?typeName)");
        }

        if (sendNumberId != null)
        {
            buffer.append(" and step.sendNumberId=:sendNumberId");
        }
        else if (receiveTypeId != null || topReceiveTypeIds != null)
        {
            buffer.append(" and step.receiveTypeId in :receiveTypeIds");
        }
        else if (noTopReceiveTypeIds != null)
        {
            buffer.append(" and (step.receiveTypeId is null or step.receiveTypeId not in :noReceiveTypeIds)");
        }
    }

    @Override
    protected void initList(List<T> items) throws Exception
    {
        super.initList(items);

        loadOdInstance(items);
    }

    protected List<OdFlowInstance> getOdInstances() throws Exception
    {
        if (odInstances == null)
            odInstances = odWorkSheetDao.queryOdInstances(getInstanceIds());
        return odInstances;
    }

    protected OdFlowInstance getOdInstance(Long instanceId) throws Exception
    {
        for (OdFlowInstance instance : getOdInstances())
        {
            if (instance.getInstanceId().equals(instanceId))
                return instance;
        }

        return null;
    }

    protected void loadOdInstance(List<T> items) throws Exception
    {
        for (Iterator<T> iterator = items.iterator(); iterator.hasNext(); )
        {
            T item = iterator.next();
            OdFlowInstance odInstance = getOdInstance(Long.valueOf(item.getInstanceId()));

            if (odInstance != null)
                item.setOd(odInstance);
            else
                iterator.remove();
        }
    }

    protected List<SendFlowInstance> getSendInstances() throws Exception
    {
        if (sendInstances == null)
        {
            List<Long> instanceIds = new ArrayList<Long>();

            for (OdWorkSheetItem item : getList())
            {
                OdFlowInstance od = item.getOd();
                if ("receive".equals(od.getType()) && od.getDocument().getReceiptId() == null)
                {
                    Long instanceId = od.getInstanceId();

                    if (!instanceIds.contains(instanceId))
                        instanceIds.add(instanceId);
                }
            }

            sendInstances = odWorkSheetDao.querySendInstances(instanceIds);
        }

        return sendInstances;
    }

    protected SendFlowInstance getSendInstance(Long instanceId) throws Exception
    {
        for (SendFlowInstance instance : getSendInstances())
        {
            if (instance.getInstanceId().equals(instanceId))
                return instance;
        }

        return null;
    }

    protected boolean containsBusinessType(String... types)
    {
        if (businessType == null)
            return true;

        for (String s : businessType)
        {
            for (String type : types)
            {
                if (s.equals(type))
                    return true;
            }
        }

        return false;
    }

    @Override
    protected void beforeQuery() throws Exception
    {
        super.beforeQuery();

        if (businessId != null && queryBusinessIds == null)
            queryBusinessIds = businessId;

        if (labelId != null)
        {
            Label label = odWorkSheetDao.getLabel(labelId);
            String condition = label.getCondition();
            if (!StringUtils.isEmpty(condition))
            {
                Map<String, String> map = new JsonParser(condition).parse("Map<String,String>");
                for (Map.Entry<String, String> entry : map.entrySet())
                {
                    String key = entry.getKey();
                    if ("query".equals(key))
                    {
                        query = entry.getValue();
                    }
                    else
                    {
                        PropertyInfo property = BeanUtils.getProperty(getClass(), key);
                        if (property != null)
                        {
                            String value = entry.getValue();

                            Class type = property.getType();
                            if (type.isArray())
                            {
                                property.set(this, DataConvert.convertArray(type.getComponentType(), value.split(",")));
                            }
                            else
                            {
                                property.setObject(this, value);
                            }
                        }
                    }
                }
            }
        }

        initOdType();
    }

    private void initOdType()
    {
        if (!StringUtils.isEmpty(odType))
        {
            if ("root".equals(odType) || "time".equals(odType) || "time_all".equals(odType))
            {
                //根节点和时间节点
                sendNumberId = null;
                receiveTypeId = null;
            }
            else if (odType.startsWith("y:"))
            {
                //年份节点
                sendNumberId = null;
                receiveTypeId = null;

                int year = Integer.parseInt(odType.substring(2));

                if (getTime_start() == null)
                    setTime_start(DateUtils.toSQLDate(DateUtils.getYearStart(year).getTime()));
                if (getTime_end() == null)
                    setTime_end(DateUtils.toSQLDate(DateUtils.getYearEnd(year).getTime()));
            }
            else if (odType.startsWith("m:"))
            {
                //月份节点
                sendNumberId = null;
                receiveTypeId = null;

                String s = odType.substring(2);

                int index = s.indexOf('_');

                int year = Integer.parseInt(s.substring(0, index));
                int month = Integer.parseInt(s.substring(index + 1));

                if (getTime_start() == null)
                    setTime_start(DateUtils.toSQLDate(DateUtils.getMonthStart(year, month - 1)));
                if (getTime_end() == null)
                    setTime_end(DateUtils.toSQLDate(DateUtils.getMonthEnd(year, month - 1)));
            }
            else if (odType.startsWith("lm:"))
            {
                //最近节点
                sendNumberId = null;
                receiveTypeId = null;

                String s = odType.substring(3);

                int monthCount = Integer.parseInt(s);

                if (getTime_start() == null)
                    setTime_start(DateUtils.toSQLDate(DateUtils.addMonth(new Date(), -monthCount)));
            }
            else if (odType.startsWith("s:"))
            {
                //发文字号节点
                sendNumberId = Integer.valueOf(odType.substring(2));
                receiveTypeId = null;
            }
            else if (odType.startsWith("r:"))
            {
                //收文编号节点
                receiveTypeId = Integer.valueOf(odType.substring(2));
                sendNumberId = null;
            }
            else
            {
                //其他
                typeName = odType;
                sendNumberId = null;
                receiveTypeId = null;
            }
        }
    }

    @Override
    protected Object createListView0() throws Exception
    {
        if ("list".equals(page))
        {
            return super.createListView0();
        }
        else if (showTypeTree)
        {
            OdTypeDisplay display = Tools.getBean(OdTypeDisplay.class);

            display.setBusinessType(businessType);

            return new ComplexTableView(display, "odType", true);
        }
        else
        {
            return super.createListView0();
        }
    }

    @Override
    protected String listDisplay(T item) throws Exception
    {
        StringBuilder buffer = new StringBuilder();

        buffer.append("<div class=\"title_trunc ");

        if (NODEAL.equals(getType()) || NOREAD.equals(getType()))
        {
            if (isNew(item))
            {
                if (item.getOd().isUrged())
                    buffer.append(" title_trunc_noread_urge");
                else
                    buffer.append(" title_trunc_noread");
            }
            else
            {
                if (item.getOd().isUrged())
                    buffer.append(" title_trunc_read_urge");
                else
                    buffer.append(" title_trunc_readed");
            }
        }
        else
        {
            buffer.append(" title_trunc_readed");
        }

        buffer.append("\">");

        String priority = item.getOd().getPriority();
        if (StringUtils.isEmpty(priority))
            priority = item.getOd().getDocument().getPriority();

        if (!StringUtils.isEmpty(priority) && !"普通".equals(priority) && !"平件".equals(priority) &&
                !"常规".equals(priority))
        {
            buffer.append("<span class=\"priority\">[").append(priority).append("]</span>");
        }

        String tag = item.getOd().getTag();
        if (StringUtils.isEmpty(tag) && StringUtils.isEmpty(simpleName))
        {
            tag = item.getOd().getSimpleName();
        }

        if (!StringUtils.isEmpty(tag))
        {
            buffer.append("<span class=\"tag\">[").append(tag).append("]</span>");
        }

        buffer.append("<span><a href=\"#\" onclick=\"openStep(").append(item.getStepId()).append(",'")
                .append(item.getOd().getType()).append("');return false;\" title=\"").append(
                HtmlUtils.escapeAttribute(item.getTitle())).append("\">");

        buffer.append(HtmlUtils.escapeHtml(item.getTitleText().trim().replaceAll("[\\r|\\n]+", " ")));
        buffer.append("</a></span></div>");

        buffer.append("<span class=\"time\">");
        if (item.getReceiveTime() != null)
            buffer.append(DateUtils.toString(item.getReceiveTime(), "yyyy-MM-dd"));
        else if (item.getSubItems() != null && item.getSubItems().size() > 0)
            buffer.append(DateUtils.toString(item.getSubItems().get(0).getReceiveTime(), "yyyy-MM-dd"));
        buffer.append("</span>");

        return buffer.toString();
    }

    @Override
    protected void initView(SimplePageListView view) throws Exception
    {
        super.initView(view);

        view.importJs("/ods/flow/list.js");
        view.importCss("/ods/flow/list.css");
    }

    @Override
    protected void setRemark(PageTableView view)
    {
        if (!"select".equals(page))
            super.setRemark(view);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void initView(PageTableView view) throws Exception
    {
        if (getConsignationId() != null)
            view.setTitle("查看委托事项");
        else if ("select".equals(page))
            view.setTitle("选择公文");

        String type = getType();

        if (DEPT.equals(type) || getConsignationId() != null)
            view.setHasCheckbox(false);

        if (getCatalogId() == null && !DRAFT.equals(type) && !NODEAL.equals(type) && getInstanceState() == null)
        {
            view.addComponent("状态", new CCombox("instanceState", new Object[]{
                    new KeyValue<String>("0", "未结束"),
                    new KeyValue<String>("1", "已结束")
            }));
        }
        view.addComponent("标题", "title").setWidth("75px");
        view.addComponent("文件内容", new CInput("text").setPrompt("ods.flow.fulltext.remark")).setWidth("75px");
        view.addComponent("时间", "time_start", "time_end");

        if (DEPT.equals(type))
        {
            view.addComponent("拟稿科室", "createDeptId");
        }

        if (businessId == null && getTypeList().size() > 1)
        {
            view.addComponent("类型", "typeName");
        }

        view.addMoreComponent("来自", "sourceName");
        view.addMoreComponent("来文单位", "sourceDept");
        view.addMoreComponent("发文字号", "sendNumber");
        view.addMoreComponent("收文编号", "serial");
//        view.addMoreComponent("主题词", "subject");
        view.addMoreComponent("督办", new CCombox("urged", new Object[]{true, false}));

        if (DEALED.equals(type) || READED.equals(type))
        {
            view.addMoreComponent("包括已收藏文件", "includeCataloged");
        }

        view.addButton(Buttons.query());
        if (DRAFT.equals(type))
            view.addButton(Buttons.delete());

        if (!"select".equals(page))
        {
            addCatalogButton(view);
            addHideButton(view);
            addStopAllButton(view);
            addNoReadEndButton(view);
            addConsignButton(view);
        }
        else
        {
            view.addButton(Buttons.ok().setIcon(Buttons.getIcon("ok")));
        }

        if (!"select".equals(page))
        {
            //附件
            view.addColumn("附件", new CImage(Tools.getCommonIcon("attachment"))
                    .setHref("/ods/document/${encodedDocumentId}/attachment").setTarget("_blank")
                    .setProperty("style", "cursor:pointer").setPrompt("点击下载附件"))
                    .setDisplay("${attachment}").setLocked(true).setWidth("27")
                    .setHeader(new CImage(Tools.getCommonIcon("attachment")).setPrompt("附件"));
        }

        if (!DRAFT.equals(type) && !"select".equals(page))
        {
            view.addColumn("", new Cell()
            {
                public Object getValue(Object entity) throws Exception
                {
                    return null;
                }

                public Class getType(Class<?> entityType)
                {
                    return null;
                }

                public Object display(Object entity) throws Exception
                {
                    T item = (T) entity;
                    if (item.getOd().isUrged())
                    {
                        return "<span class=\"urge\" onclick=\"showUrge(" + item.getInstanceId() +
                                ")\" title=\"点击查看督办信息\"></span>";
                    }

                    String type = item.getOd().getType();
                    if ("send".equals(type) || "receive".equals(type) || "inner".equals(type))
                    {
                        Long receiptId = item.getOd().getDocument().getReceiptId();
                        if ("receive".equals(type))
                        {
                            if (receiptId != null)
                            {
                                boolean b = false;
                                List<Dept> receiptDepts = item.getOd().getDocument().getReceipt().getReceiptDepts();
                                if (receiptDepts.size() == 0)
                                {
                                    b = true;
                                }
                                else
                                {
                                    for (Dept receiptDept : receiptDepts)
                                    {
                                        if (receiptDept.getDeptId().equals(item.getOd().getDeptId()))
                                        {
                                            b = true;
                                            break;
                                        }
                                    }
                                }

                                if (b)
                                {
                                    return "<a href=\"#\" onclick=\"return showReceipt(" + item.getStepId() +
                                            ")\" title=\"点击查看回执信息\">回执</a>";
                                }
                            }

                            SendFlowInstance sendFlowInstance = getSendInstance(item.getOd().getInstanceId());

                            if (sendFlowInstance != null && sendFlowInstance.isSended() != null &&
                                    sendFlowInstance.isSended())
                            {
                                receiptId = sendFlowInstance.getDocument().getReceiptId();
                            }
                            else
                            {
                                receiptId = null;
                            }
                        }

                        if (receiptId != null)
                        {
                            return "<a href=\"#\" onclick=\"return trackReceipt(" + receiptId +
                                    ")\" title=\"点击跟踪回执信息\">回执</a>";
                        }
                    }

                    return null;
                }
            }).setWidth("38").setLocked(true).setHeader(new CImage("/ods/flow/urge.gif").setPrompt("督办/回执"))
                    .setAlign(Align.center);
        }

        if (NODEAL.equals(type) && !"select".equals(page))
        {
            addAcceptButton(view);
        }

        if ("select".equals(page))
        {
            view.addColumn("标题", new HrefCell(new FieldCell("titleText"))).setOrderFiled("od.document.title");
        }
        else
        {
            view.addColumn("标题", new HrefCell(new FieldCell("titleText"))
            {
                @Override
                public String display(Object entity) throws Exception
                {
                    T item = (T) entity;

                    StringBuilder buffer = new StringBuilder("<span class='title'><a href=\"#\" onclick=\"openStep(")
                            .append(item.getStepId()).append(",'").append(item.getOd().getType())
                            .append("');return false;\"");

                    buffer.append(">");

                    String priority = item.getOd().getPriority();
                    if (StringUtils.isEmpty(priority))
                        priority = item.getOd().getDocument().getPriority();

                    if (!StringUtils.isEmpty(priority) && !"普通".equals(priority) && !"平件".equals(priority) &&
                            !"常规".equals(priority))
                    {
                        buffer.append("<span class=\"priority\">[").append(priority).append("]</span>");
                    }

                    String tag = item.getOd().getTag();
                    if (StringUtils.isEmpty(tag))
                    {
                        tag = item.getOd().getSimpleName();
                    }

                    if (!StringUtils.isEmpty(tag))
                    {
                        buffer.append("<span class=\"tag\">[").append(tag).append("]</span>");
                    }

                    buffer.append(HtmlUtils.escapeHtml(item.getTitleText()));

                    buffer.append("</a></span>");

                    return buffer.toString();
                }
            }).setOrderFiled("od.document.title").setWidth("220").setAutoExpand(true);
        }

        if (!DRAFT.equals(type) && !"select".equals(page))
        {
            view.addColumn("正文/附件", new ConditionComponent().add("od.document.textId!=null",
                    new CHref("${attachment?'正文/附件':'正文'}").setAction("showText(${stepId},${od.documentId}" +
                            ",'${od.type}',${attachment.toString()},'${otherFileName}')").setProperty("class", "link")
                            .setProperty("id", "textHref${stepId}")
            )).setWidth("70");
        }

        view.addColumn("类型", new CLabel("${typeName}").setClass("${'type_'+od.type}"))
                .setAlign(Align.center).setWidth("40").setWrap(true);

        if (!SELF.equals(type) && !DRAFT.equals(type) && containsBusinessType("send", "receive"))
        {
            view.addColumn("发文字号", "od.document.sendNumber").setWidth("95").setWrap(true).setAlign(Align.center);

            String columnName;

            if (containsBusinessType("send") && containsBusinessType("receive"))
            {
                if ("exp".equals(getAction()))
                {
                    columnName = "来文单位/拟稿人";
                }
                else
                {
                    columnName = "来文单位<br>&ensp;拟稿人";
                }
            }
            else if (containsBusinessType("receive"))
            {
                columnName = "来文单位";
            }
            else
            {
                columnName = "拟稿人";
            }

            view.addColumn(columnName, "source").setOrderFiled("od.document.sourceDept").setWidth("70")
                    .setWrap(true);
        }

        if (getCatalogId() == null && !DRAFT.equals(type) && !SELF.equals(type) &&
                !"select".equals(page))
        {
            view.addColumn("来自", "sourceName").setOrderFiled("step.sourceName").setWidth("130").setWrap(true);
        }

        if (!DRAFT.equals(type) && !SELF.equals(type))
        {
            view.addColumn("来文时间", new FieldCell("receiveTime").setFormat("yyyy-MM-dd\nHH:mm:ss")).setOrderFiled(
                    "step.receiveTime").setWidth("80").setAlign(Align.center).setWrap(true);
        }
        else
        {
            view.addColumn("拟稿时间", new FieldCell("receiveTime").setFormat("yyyy-MM-dd\nHH:mm:ss")).setOrderFiled(
                    "step.receiveTime").setWidth("80").setAlign(Align.center).setWrap(true);
        }

        if (getCatalogId() == null && !NOREAD.equals(type) && !READED.equals(type) && !"select".equals(page))
        {
            view.addColumn("环节", "nodeName").setOrderFiled("step.nodeName").setAlign(Align.left).setWidth("70")
                    .setWrap(true);
        }

        if (getCatalogId() == null && !DRAFT.equals(type) && !NODEAL.equals(type))
        {
            view.addColumn("状态", new CLabel("${od.state}").setColor("${od.state.name()=='unclosed'?'red':''}"))
                    .setOrderFiled("od.state").setWidth("50");
        }

        if (getCatalogId() == null && !DRAFT.equals(type) && !"select".equals(page))
        {
            view.addColumn("已用时间", new FieldCell("workday").setOrderable(false).setUnit("工作日")).setWidth("65")
                    .setAlign(Align.center);

            if (timeoutService.hasTimeoutConfig(OdTimeout.FLOW_ID, authDeptIds))
            {
                view.addColumn("剩余时间", new FieldCell("remainingTime").setOrderable(false)).setWidth("65")
                        .setAlign(Align.center);

                view.addColumn("警告", new ConditionComponent().add("od.validTimeout!=null",
                        new CImage("/timeout/level/icon/${od.validTimeout.levelId}")
                                .setPrompt("${od.validTimeout.level.levelName}").setProperty("style", "cursor:pointer")
                )).setWidth("45");
            }
        }

        if (!"select".equals(page))
        {
            addStopButton(view);
            addCancelButton(view);

            view.addColumn("跟踪", new ConditionComponent().add("flowTag!='copy'",
                    new CHref("跟踪").setAction("track(${stepId})"))).setWidth("40");
        }

        view.addButton(Buttons.export("xls"));

        view.importJs("/ods/flow/worksheet.js");
        view.importCss("/ods/flow/worksheet.css");
    }

    @Override
    protected void deleteInstance(Long instanceId) throws Exception
    {
        super.deleteInstance(instanceId);

        OdFlowInstance instance = odWorkSheetDao.getOdInstance(instanceId);

        String componentType = instance.getBusiness().getComponentType();
        if (!StringUtils.isEmpty(componentType))
        {
            OdFlowComponent flowComponent = (OdFlowComponent) Tools.getBean(Class.forName(componentType));
            if (flowComponent != null)
                flowComponent.deleteFlow(instance);
        }

        odWorkSheetDao.deleteOdInstance(instanceId);

        List<Collect> collects = odWorkSheetDao.getCollectsByCollectInstanceId(instanceId);

        for (Collect collect : collects)
        {
            OdFlowInstance instance1 =
                    odWorkSheetDao.getOdFlowInstanceByReceiveId(collect.getReceiveId());
            if (instance1 != null)
                deleteInstance(instance1.getInstanceId());

            odWorkSheetDao.delete(ReceiveBase.class, collect.getReceiveId());
            odWorkSheetDao.delete(collect);
        }
    }

    @Service
    public List<AttachmentInfo> getAttachments(Long documentId) throws Exception
    {
        return odWorkSheetDao.getDocument(documentId).getAttachmentInfos();
    }

    @Service
    @NotSerialized
    public List<Label> getLabels() throws Exception
    {
        if (businessId == null && authDeptIds != null)
        {
            List<Label> labels = odWorkSheetDao.getLabels(authDeptIds);

            int n = labels.size();
            List<Label> labels1 = new ArrayList<Label>(n);

            for (int i = n - 1; i >= 0; i--)
            {
                Label label = labels.get(i);

                boolean b = true;
                for (Label label1 : labels1)
                {
                    if (label1.getLabelName().equals(label.getLabelName()))
                    {
                        b = false;
                        break;
                    }
                }

                if (b)
                {
                    labels1.add(0, label);
                }
            }

            return labels1;
        }

        return null;
    }

    @Override
    protected boolean stopOrEnd(Long stepId, String flowTag) throws Exception
    {
        if ("copy".equals(flowTag))
        {
            Copy copy = copyDao.getCopyByStepId(stepId);
            if (copy != null)
            {
                ReceiveBase receiveBase = copy.getReceiveBase();
                if (receiveBase.getState().ordinal() < 3)
                {
                    FlowStep step = new FlowStep();
                    step.setStepId(stepId.toString());
                    step.setState(FlowStep.PASSREPLYED);
                    OdSystemFlowDao.getInstance().updateStep(step);

                    receiveBase.setState(ReceiveState.end);
                    copyDao.update(receiveBase);
                }
            }

            return true;
        }
        else
        {
            return super.stopOrEnd(stepId, flowTag);
        }
    }

    @Override
    protected boolean isStopable(Action action, FlowPage flowPage) throws Exception
    {
        if (action instanceof RouteGroup && (flowPage instanceof SendableFlowPage))
        {
            SendableFlowPage sendableFlowPage = (SendableFlowPage) flowPage;
            if (sendableFlowPage.isSended() &&
                    (!"false".equals(action.getProperty("send")) || sendableFlowPage.isTextFinal()))
            {
                return false;
            }
        }
        return super.isStopable(action, flowPage);
    }

    @Override
    @Transactional
    public void hide() throws Exception
    {
        Long[] stepIds = getKeys();
        if (stepIds == null || stepIds.length == 0)
            return;

        super.hide();
        odWorkSheetDao.hide(stepIds);
    }

    @Override
    @Transactional
    public void unHide() throws Exception
    {
        Long[] stepIds = getKeys();
        if (stepIds == null || stepIds.length == 0)
            return;

        super.unHide();
        odWorkSheetDao.unHide(stepIds);
    }
}
