package com.gzzm.platform.flow.worksheet;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.flow.*;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.message.Message;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.*;
import net.cyan.commons.util.*;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.*;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.ObjectColumn;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;
import net.cyan.valmiki.flow.Action;
import net.cyan.valmiki.flow.*;

import java.sql.Date;
import java.util.*;

/**
 * 待办事项列表
 *
 * @author camel
 * @date 11-10-12
 */
@Service
public abstract class WorkSheetItemList<I extends FlowWorkSheetItem> extends BaseOQLQueryCrud<I>
        implements KeyBaseCrud<I, Long>, DeletableCrud<I, Long>
{
    /**
     * 草稿箱
     *
     * @see #type
     */
    public static final String DRAFT = "draft";

    /**
     * 待办
     *
     * @see #type
     */
    public static final String NODEAL = "nodeal";

    /**
     * 已办
     *
     * @see #type
     */
    public static final String DEALED = "dealed";

    /**
     * 未阅
     *
     * @see #type
     */
    public static final String NOREAD = "noread";

    /**
     * 已阅
     *
     * @see #type
     */
    public static final String READED = "readed";

    /**
     * 我发起的事项
     */
    public static final String SELF = "self";

    /**
     * 部门文件
     *
     * @see #type
     */
    public static final String DEPT = "dept";

    /**
     * 我的所有文件
     */
    public static final String ALL = "all";

    public static final String[] PASSNODES = new String[]{"传阅", "签阅"};

    @Inject
    private static Provider<WorkSheetCatalogTreeModel> catalogTreeModelProvider;

    private WorkSheetDao workSheetDao;

    @Inject
    protected WorkSheetCatalogDao catalogDao;

    @Inject
    protected UserOnlineInfo userOnlineInfo;

    private Long[] keys;

    /**
     * 列表的类型，为一个英文单词，如用draft表示草稿箱，用nodeal表示待办，用dealed表示已办，可由子类扩展
     *
     * @see #DRAFT
     * @see #NODEAL
     * @see #DEALED
     * @see #NOREAD
     * @see #READED
     * @see #DEPT
     */
    protected String type;

    @Like
    private String title;

    @Lower
    private Date time_start;

    @Upper
    private Date time_end;

    @Like
    private String sourceName;

    /**
     * 委托ID，当用委托ID做查询条件时，receivers这个条件无效
     *
     * @see #getReceivers()
     */
    protected Long consignationId;

    /**
     * 收藏夹ID
     */
    private Integer catalogId;

    /**
     * 用流程实例状态做查询条件
     */
    private Integer instanceState;

    /**
     * 用状态做查询条件
     */
    private int[] state;

    /**
     * 环节名称列表
     */
    private String[] nodeId;

    /**
     * 排除在外的环节ID列表
     */
    private String[] excludeNode;

    /**
     * 加载的步骤列表所关联的所有流程实例的实例id列表
     */
    private List<Long> instanceIds;

    /**
     * 加载的步骤锁管理的所有流程实例
     */
    private List<SystemFlowInstance> instances;

    /**
     * 是否包括已收藏文件
     */
    @Require
    private boolean includeCataloged;

    /**
     * 用户选择器
     */
    private PageUserSelector userSelector;

    protected String page;

    private Boolean hidden = Boolean.FALSE;

    private SystemFlowDao systemFlowDao;

    protected boolean acceptable;

    protected boolean last;

    protected int year;

    public WorkSheetItemList()
    {
    }

    protected SystemFlowDao getSystemFlowDao() throws Exception
    {
        if (systemFlowDao == null)
            systemFlowDao = createSystemFlowDao();

        return systemFlowDao;
    }

    protected SystemFlowDao createSystemFlowDao() throws Exception
    {
        return DefaultSystemFlowDao.getInstance();
    }

    @SuppressWarnings("unchecked")
    protected WorkSheetDao getWorkSheetDao() throws Exception
    {
        if (workSheetDao == null)
        {
            SystemFlowDao systemFlowDao = getSystemFlowDao();

            workSheetDao = Tools.getBean(WorkSheetDao.class);

            workSheetDao.setInstanceClass(systemFlowDao.getInstanceClass());
            workSheetDao.setStepClass(systemFlowDao.getStepClass());
        }
        return workSheetDao;
    }

    protected String getInstanceClassName() throws Exception
    {
        return getSystemFlowDao().getInstanceClass().getName();
    }

    protected String getStepClassName() throws Exception
    {
        return getSystemFlowDao().getStepClass().getName();
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public Long[] getKeys()
    {
        return keys;
    }

    public void setKeys(Long[] keys)
    {
        this.keys = keys;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Date getTime_start()
    {
        return time_start;
    }

    public void setTime_start(Date time_start)
    {
        this.time_start = time_start;
    }

    public Date getTime_end()
    {
        return time_end;
    }

    public void setTime_end(Date time_end)
    {
        this.time_end = time_end;
    }

    public String getSourceName()
    {
        return sourceName;
    }

    public void setSourceName(String sourceName)
    {
        this.sourceName = sourceName;
    }

    public Long getConsignationId()
    {
        return consignationId;
    }

    public void setConsignationId(Long consignationId)
    {
        this.consignationId = consignationId;
    }

    public Integer getCatalogId()
    {
        return catalogId;
    }

    public void setCatalogId(Integer catalogId)
    {
        this.catalogId = catalogId;
    }

    public Integer getInstanceState()
    {
        return instanceState;
    }

    public void setInstanceState(Integer instanceState)
    {
        this.instanceState = instanceState;
    }

    public int[] getState()
    {
        return state;
    }

    public void setState(int[] state)
    {
        this.state = state;
    }

    public String[] getNodeId()
    {
        return nodeId;
    }

    public void setNodeId(String[] nodeId)
    {
        this.nodeId = nodeId;
    }

    public String[] getExcludeNode()
    {
        return excludeNode;
    }

    public void setExcludeNode(String[] excludeNode)
    {
        this.excludeNode = excludeNode;
    }

    public boolean isIncludeCataloged()
    {
        return includeCataloged;
    }

    public void setIncludeCataloged(boolean includeCataloged)
    {
        this.includeCataloged = includeCataloged;
    }

    public String getPage()
    {
        return page;
    }

    public void setPage(String page)
    {
        this.page = page;
    }

    public Boolean getHidden()
    {
        return hidden;
    }

    public void setHidden(Boolean hidden)
    {
        this.hidden = hidden;
    }

    public boolean isAcceptable()
    {
        return acceptable;
    }

    public void setAcceptable(boolean acceptable)
    {
        this.acceptable = acceptable;
    }

    public boolean isLast()
    {
        return last;
    }

    public void setLast(boolean last)
    {
        this.last = last;
    }

    public int getYear()
    {
        return year;
    }

    public void setYear(int year)
    {
        this.year = year;
    }

    @NotSerialized
    public Integer getUserId()
    {
        return userOnlineInfo.getUserId();
    }

    public WorkSheetCatalogTreeModel getCatalogTree()
    {
        String catalogType = getCatalogType();
        if (catalogType != null)
        {
            WorkSheetCatalogTreeModel tree = catalogTreeModelProvider.get();
            tree.setType(catalogType);

            return tree;
        }

        return null;
    }

    protected String getCatalogType()
    {
        return null;
    }

    public Long getKey(I item) throws Exception
    {
        return Long.valueOf(item.getStepId());
    }

    /**
     * 用流程标记做查询条件
     *
     * @return 所有允许在此列表中显示的流程标记
     * @throws Exception 允许子类抛出异常
     * @see SystemFlowInstance#flowTag
     */
    @NotSerialized
    public String[] getFlowTags() throws Exception
    {
        return null;
    }

    /**
     * 获得允许在此列表中出现的流程步骤状态
     *
     * @return 步骤状态列表
     * @throws Exception 允许子类抛出异常
     * @see com.gzzm.platform.flow.SystemFlowStep#state
     * @see net.cyan.valmiki.flow.FlowStep
     */
    @NotSerialized
    public int[] getStates() throws Exception
    {
        if (state != null)
            return state;

        if (consignationId != null)
            return null;

        return getStates(type);
    }

    public int[] getStates(String type) throws Exception
    {
        if (DRAFT.equals(type))
        {
            //草稿箱
            return new int[]{FlowStep.DRAFT};
        }
        else if (NODEAL.equals(type))
        {
            //待办
            return getNoDealStates();
        }
        else if (DEALED.equals(type))
        {
            //已办
            return getDealedStates();
        }
        else if (NOREAD.equals(type))
        {
            //未阅
            return new int[]{FlowStep.PASSNOACCEPT, FlowStep.PASSACCEPTED};
        }
        else if (READED.equals(type))
        {
            //已阅
            return new int[]{FlowStep.PASSREPLYED};
        }
        else if (DEPT.equals(type))
        {
            //科室公文
            return new int[]{FlowStep.NOACCEPT, FlowStep.NODEAL, FlowStep.BACKNOACCEPT,
                    FlowStep.BACKNODEAL, FlowStep.NODEAL_REPLYED, FlowStep.DEALED_REPLYED,
                    FlowStep.DEALED_REPLYED_NOACCEPT, FlowStep.DEALED, FlowStep.STOPED};
        }
        else if (ALL.equals(type))
        {
            //全部公文
            return new int[]{FlowStep.NOACCEPT, FlowStep.NODEAL, FlowStep.BACKNOACCEPT,
                    FlowStep.BACKNODEAL, FlowStep.NODEAL_REPLYED, FlowStep.DEALED, FlowStep.PASSREPLYED,
                    FlowStep.PASSNOACCEPT, FlowStep.PASSACCEPTED, FlowStep.PASSREPLYED, FlowStep.COPYNOACCEPT,
                    FlowStep.COPYNODEAL, FlowStep.COPYDEALED, FlowStep.STOPED, FlowStep.DRAFT};
        }
        else if (SELF.equals(type))
        {
            //我发起的事项
            return new int[]{FlowStep.NOACCEPT, FlowStep.NODEAL, FlowStep.BACKNOACCEPT,
                    FlowStep.BACKNODEAL, FlowStep.NODEAL_REPLYED, FlowStep.DEALED};
        }

        return null;
    }

    @NotSerialized
    public int[] getNoDealStates()
    {
        return new int[]{FlowStep.NOACCEPT, FlowStep.NODEAL, FlowStep.BACKNOACCEPT, FlowStep.BACKNODEAL,
                FlowStep.COPYNOACCEPT, FlowStep.COPYNODEAL, FlowStep.NODEAL_REPLYED};
    }

    @NotSerialized
    public int[] getDealedStates()
    {
        return new int[]{FlowStep.DEALED, FlowStep.COPYDEALED, FlowStep.STOPED,
                FlowStep.DEALED_REPLYED, FlowStep.DEALED_REPLYED_NOACCEPT};
    }

    @NotSerialized
    public Integer getDeptId() throws Exception
    {
        if (DEPT.equals(type))
            return userOnlineInfo.getDeptId();

        return null;
    }

    /**
     * 当前用户对应的所有接收者，包括作为部门，岗位的接收者，与consignationId互斥
     *
     * @return 当前用户对应的接收者列表
     * @throws Exception 数据库读取数据错误
     * @see #consignationId
     */
    @NotSerialized
    public List<String> getReceivers() throws Exception
    {
        return FlowApi.getReceivers(userOnlineInfo.getUserId());
    }

    /**
     * 是否同一个流程实例只显示最后一条，默认已办和部门文件不重复显示
     *
     * @return 如果distinct返回true，否则返回false
     * @throws Exception 允许子类抛出异常
     */
    protected boolean isDistinct() throws Exception
    {
        return (consignationId != null || catalogId != null || DEALED.equals(type) || READED.equals(type) ||
                DEPT.equals(type) || ALL.equals(type)) && nodeId == null && (hidden == null || !hidden);
    }

    protected Class<? extends FlowPage> getFlowPageClass(String flowTag) throws Exception
    {
        return FlowPage.class;
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        if ("list".equals(page))
            setPageSize(6);

        super.beforeShowList();
    }

    protected String getQueryString() throws Exception
    {
        boolean distinct = isDistinct();

        StringBuilder buffer = new StringBuilder(
                "select step.stepId,step.groupId,step.receiver from " + getStepClassName() + " step ");

        if (distinct && last)
        {
            buffer.append(" left join ").append(getStepClassName())
                    .append(" step1 on step1.instanceId=step.instanceId and ")
                    .append("step1.stepId>step.stepId and step1.state not in(").append(FlowStep.ACCEPTED_DEALED)
                    .append(",").append(FlowStep.PASSACCEPTED).append(",").append(FlowStep.PASSNOACCEPT).append(",")
                    .append(FlowStep.PASSREPLYED).append(") and ");
            addBaseCondition(buffer, "step1");
        }

        join(buffer);

        buffer.append(" where ");

        addBaseCondition(buffer, "step");

        where(buffer);

        if (distinct)
        {
            if (last)
                buffer.append(" and step1.stepId is null ");
            else if (DEPT.equals(type))
                buffer.append(" and step.deptLast=1");
            else
                buffer.append(" and step.lastStep=1");
        }

        buffer.append(" group by step.stepId,step.groupId,step.receiver ");

        orderBy(buffer);

        return buffer.toString();
    }

    @Override
    protected String getCountQueryString() throws Exception
    {
        boolean distinct = isDistinct();

        StringBuilder buffer = new StringBuilder("select count(distinct step.stepId) from ").append(getStepClassName())
                .append(" step ");

        if (distinct && last)
        {
            buffer.append(" left join ").append(getStepClassName())
                    .append(" step1 on step1.instanceId=step.instanceId and ")
                    .append("step1.receiveTime>step.receiveTime and step1.stepId<>step.stepId and step1.state not in(")
                    .append(FlowStep.ACCEPTED_DEALED).append(",").append(FlowStep.PASSACCEPTED).append(",")
                    .append(FlowStep.PASSNOACCEPT).append(",").append(FlowStep.PASSREPLYED).append(") and ");
            addBaseCondition(buffer, "step1");
        }

        join(buffer);

        buffer.append(" where ");

        addBaseCondition(buffer, "step");

        where(buffer);

        if (distinct)
        {
            if (last)
                buffer.append(" and step1.stepId is null ");
            else if (DEPT.equals(type))
                buffer.append(" and step.deptLast=1");
            else
                buffer.append(" and step.lastStep=1");
        }

        return buffer.toString();
    }

    protected void addBaseCondition(StringBuilder buffer, String alias) throws Exception
    {
        if (consignationId == null)
        {
            Integer deptId = getDeptId();
            if (deptId == null)
            {
                buffer.append(alias).append(".receiver in :receivers ");

                if (SELF.equals(type))
                {
                    buffer.append(" and ").append(alias).append(".preStepId =0 ");
                }
            }
            else
            {
                buffer.append(alias).append(".deptId=:deptId ");
            }

            if (!NOREAD.equals(type) && !READED.equals(type))
            {
                buffer.append(" and ").append(alias).append(".nodeId in ?nodeId and ").append(alias)
                        .append(".nodeId not in ?excludeNode");
            }
        }
        else
        {
            buffer.append(alias).append(".consignationId=:consignationId ");
        }
    }

    protected void join(StringBuilder buffer) throws Exception
    {
        joinInstance(buffer);
        joinCatalog(buffer);
    }

    protected void joinInstance(StringBuilder buffer) throws Exception
    {
        buffer.append(" join ").append(getInstanceClassName()).append(
                " instance on step.instanceId=instance.instanceId ");
    }

    protected void joinCatalog(StringBuilder buffer) throws Exception
    {
        if (catalogId != null)
        {
            buffer.append(
                    " join WorkSheetCatalogList catalog on catalog.instanceId=step.instanceId and catalog.userId=:userId ");
        }
        else if (!includeCataloged && (DEALED.equals(type) || READED.equals(type)))
        {
            buffer.append(
                    " left join WorkSheetCatalogList catalog on catalog.instanceId=step.instanceId and catalog.userId=:userId ");
        }
    }

    protected void where(StringBuilder buffer) throws Exception
    {
        whereStep(buffer);
        whereInstance(buffer);
        whereCatalog(buffer);
    }

    protected void whereStep(StringBuilder buffer) throws Exception
    {
        buffer.append(
                " and step.receiveTime>=?time_start and step.receiveTime<=?time_end and step.sourceName like ?sourceName");

        if (hidden != null)
        {
            buffer.append(" and step.hidden=:hidden");
        }

        if (NOREAD.equals(type))
        {
            if (nodeId == null)
                buffer.append(" and step.state in ?states");
            else
                buffer.append(
                        " and (step.state in ?states or (step.nodeId in ?nodeId and step.state in ?noDealStates))");
        }
        else if (READED.equals(type))
        {
            if (nodeId == null)
                buffer.append(" and step.state in ?states");
            else
                buffer.append(
                        " and (step.state in ?states or (step.nodeId in ?nodeId and step.state in ?dealedStates))");
        }
        else
        {
            buffer.append(" and step.state in ?states");
        }
    }

    protected void whereInstance(StringBuilder buffer) throws Exception
    {
        buffer.append(" and instance.title like ?title and instance.state=?instanceState" +
                " and instance.flowTag in ?flowTags ");
    }

    protected void whereCatalog(StringBuilder buffer) throws Exception
    {
        if (catalogId != null)
        {
            if (catalogId != 0)
                buffer.append(" and catalog.catalogId=:catalogId ");
        }
        else if (!includeCataloged && (DEALED.equals(type) || READED.equals(type)))
        {
            buffer.append(" and catalog.instanceId is null ");
        }
    }

    protected void orderBy(StringBuilder buffer) throws Exception
    {
        OrderBy orderBy = getOrderBy();
        if (orderBy == null)
        {
            if (NODEAL.equals(type))
                buffer.append(" order by max(step.showTime) desc");
            else
                buffer.append(" order by max(step.receiveTime) desc");
        }
        else if (orderBy.getType() == OrderType.asc)
        {
            buffer.append(" order by min(").append(orderBy.getName()).append(")");
        }
        else if (orderBy.getType() == OrderType.desc)
        {
            buffer.append(" order by max(").append(orderBy.getName()).append(") desc");
        }
    }

    protected List<Long> getInstanceIds() throws Exception
    {
        if (instanceIds == null)
        {
            instanceIds = new ArrayList<Long>();

            for (FlowWorkSheetItem item : getList())
            {
                if (item.getInstanceId() != null)
                {
                    Long instanceId = Long.valueOf(item.getInstanceId());

                    if (!instanceIds.contains(instanceId))
                        instanceIds.add(instanceId);
                }
            }
        }

        return instanceIds;
    }

    protected List<SystemFlowInstance> getInstances() throws Exception
    {
        if (instances == null)
            instances = getWorkSheetDao().queryInstances(getInstanceIds());

        return instances;
    }

    protected SystemFlowInstance getInstance(Long instanceId) throws Exception
    {
        for (SystemFlowInstance instance : getInstances())
        {
            if (instance.getInstanceId().equals(instanceId))
                return instance;
        }

        return null;
    }

    @Override
    @Transactional(mode = TransactionMode.supported)
    public List<I> getList() throws Exception
    {
        return super.getList();
    }

    @Override
    protected void loadList() throws Exception
    {
        super.loadList();

        List<I> items = list;

        if (items.size() > 0)
        {
            FlowUtils.initWorkSheetItemList(items, getSystemFlowDao());

            for (Iterator<I> iterator = items.iterator(); iterator.hasNext(); )
            {
                I item = iterator.next();

                if (item.getInstanceId() == null)
                    iterator.remove();
            }

            initList(items);
        }
    }

    protected void initList(List<I> items) throws Exception
    {
        loadInstanceInfo(items);
    }

    /**
     * 为每个步骤加载流程实例中的数据
     *
     * @param items 列表
     * @throws Exception 从数据库加载数据错误
     */
    protected void loadInstanceInfo(List<I> items) throws Exception
    {
        for (FlowWorkSheetItem item : items)
        {
            try
            {
                SystemFlowInstance instance = getInstance(Long.valueOf(item.getInstanceId()));
                loadInstanceInfo(item, instance);
            }
            catch (Exception ex)
            {
                throw new SystemException(
                        "load instance info failed,stepId:" + item.getStepId() + ",instanceId:" + item.getInstanceId(),
                        ex);
            }
        }
    }

    protected void loadInstanceInfo(FlowWorkSheetItem item, SystemFlowInstance instance) throws Exception
    {
        item.setTitle(instance.getTitle());
        item.setFlowTag(instance.getFlowTag());
        if (instance.getState() != null)
            item.setInstanceState(instance.getState());
    }

    @Override
    protected void initListOrCount() throws Exception
    {
        if (NOREAD.equals(type) || READED.equals(type))
        {
            if (nodeId == null)
                nodeId = PASSNODES;
        }
        else if (NODEAL.equals(type) || DEALED.equals(type))
        {
            if (excludeNode == null)
                excludeNode = PASSNODES;
        }
    }

    protected Object createListView0() throws Exception
    {
        PageTableView view;

        if ("list".equals(getPage()))
        {
            return new SimplePageListView()
            {
                @Override
                @SuppressWarnings("unchecked")
                public Object display(Object obj) throws Exception
                {
                    return listDisplay((I) obj);
                }
            };
        }
        else if (catalogId == null)
        {
            view = new PageTableView(true);
        }
        else
        {
            WorkSheetCatalogDisplay catalogDisplay = Tools.getBean(WorkSheetCatalogDisplay.class);
            catalogDisplay.setType(getCatalogType());

            view = new ComplexTableView(catalogDisplay, "catalogId", true).setEnableDrag(true);

            SelectableTreeView catalogView = (SelectableTreeView) catalogDisplay.getView();
            catalogView.enableViewDrop(Actions.moveToLeft(false));
        }

        return view;
    }

    @Override
    protected Object createListView() throws Exception
    {
        Object view = createListView0();

        if (view instanceof PageTableView)
        {
            PageTableView tableView = (PageTableView) view;

            if (NODEAL.equals(type) || NOREAD.equals(type) || catalogId != null)
                setNewBold(tableView);

            setRemark(tableView);

            initView(tableView);

            tableView.importJs("/platform/flow/worksheet.js");
        }
        else if (view instanceof SimplePageListView)
        {
            initView((SimplePageListView) view);
        }

        return view;
    }

    protected String listDisplay(I item) throws Exception
    {
        StringBuilder buffer = new StringBuilder();

        buffer.append("<div class=\"title_trunc ");

        if (NODEAL.equals(type) || NOREAD.equals(type))
        {
            if (isNew(item))
                buffer.append(" title_trunc_noread");
            else
                buffer.append(" title_trunc_readed");
        }

        buffer.append("\"><a href=\"#\" onclick=\"openStep(").append(item.getStepId())
                .append(");return false;\" title=\"").append(HtmlUtils.escapeAttribute(item.getTitle()))
                .append("\">");

        buffer.append(HtmlUtils.escapeHtml(item.getTitle().trim().replaceAll("[\\r|\\n]+", " ")));
        buffer.append("</a></div>");

        buffer.append("<span class=\"time\">");
        buffer.append(DateUtils.toString(item.getReceiveTime(), "yyyy-MM-dd"));
        buffer.append("</span>");

        return buffer.toString();
    }

    protected void initView(SimplePageListView view) throws Exception
    {
        view.addAction(com.gzzm.platform.commons.crud.Action.more());

        view.importJs("/platform/flow/list.js");
        view.importCss("/platform/flow/list.css");
    }

    protected abstract void initView(PageTableView view) throws Exception;

    protected void setNewBold(PageTableView view)
    {
        view.setClass("${crud$.isNew(this)?'new_bold':''}");
    }

    protected void setRemark(PageTableView view)
    {
        if (!DRAFT.equals(type) && !NOREAD.equals(type) && !READED.equals(type))
        {
            view.setRowRemark(new ObjectColumn(null, "crud$.getRemark(this)"));
        }
    }

    protected void addNoReadEndButton(PageTableView view)
    {
        if (NOREAD.equals(type))
        {
            view.addButton(new CButton(Tools.getMessage("platform.flow.endNoRead"), "end()"));
        }
    }

    protected void addHideButton(PageTableView view)
    {
        if (hidden != null)
        {
            if (hidden)
            {
                view.addButton(new CButton(Tools.getMessage("platform.flow.unHide"), "unHide()"));
            }
            else if (!DRAFT.equals(type) && !DEPT.equals(type) && catalogId == null)
            {
                view.addButton(new CButton(Tools.getMessage("platform.flow.hide"), "hide()"));
            }
        }
    }

    protected void addAcceptButton(PageTableView view)
    {
        view.addColumn("接收", new ConditionComponent().add("crud$.isAcceptable(this)",
                new CButton("接收", "accept(${stepId})").setPrompt("platform.flow.acceptPrompt"))
                .add("crud$.isCancelAcceptable(this)", new CButton("撤销", "cancelAccept(${stepId})")
                        .setPrompt("platform.flow.cancelAcceptPrompt"))
                .add("crud$.isAccepted(this)", new CButton("接收", "").setProperty("disabled", null)
                        .setPrompt("platform.flow.acceptedPrompt"))).setWidth("50");
    }

    protected void addStopButton(PageTableView view)
    {
        if (NODEAL.equals(type) || DEALED.equals(type))
        {
            String s0 = "platform.flow.stopNoDeal";
            String s = Tools.getMessage(s0);

            if (!s0.equals(s))
            {
                view.addColumn(s, new ConditionComponent().add("crud$.isStopable0(this)",
                        new CButton(s, "stop(${stepId})"))).setWidth("40");
            }
        }
    }

    protected void addStopAllButton(PageTableView view)
    {
        if (NODEAL.equals(type))
        {
            String s0 = "platform.flow.stopAllNoDeal";
            String s = Tools.getMessage(s0);

            if (!s0.equals(s))
            {
                view.addButton(new CButton(s, "stopAll()"));
            }
        }
    }

    protected void addCancelButton(PageTableView view)
    {
        if (DEALED.equals(type))
        {
            view.addColumn("撤回", new ConditionComponent().add("crud$.isCancelSendable0(this)",
                    new CButton("撤回", "cancelSend(${stepId})"))).setWidth("40");
        }
    }

    protected void addCatalogButton(PageTableView view)
    {
        if (getCatalogId() != null)
        {
            view.addButton(new CMenuButton("catalogTree", "收藏到")).setIcon(Buttons.getIcon("catalog"))
                    .setProperty("onclick", "catalog(this.value)");
            view.addButton(new CButton("取消收藏", "cancelCatalog()"));
        }
        else if (DEALED.equals(type) || READED.equals(type) || NODEAL.equals(type) || NOREAD.equals(type))
        {
            if (getCatalogType() != null)
            {
                view.addButton(new CMenuButton("catalogTree", "收藏")).setIcon(Buttons.getIcon("catalog"))
                        .setProperty("onclick", "catalog(this.value)");
            }
        }
    }

    protected void addConsignButton(PageTableView view)
    {
        if (NODEAL.equals(type))
        {
            view.addButton(new CMenuButton("userSelector", "委托给").setAction("consign(this.value)")
                    .setIcon("/platform/flow/icon/consign.gif"));
        }
    }

    public boolean isNew(I item)
    {
        int state = item.getState();

        return state == FlowStep.NOACCEPT || state == FlowStep.BACKNOACCEPT || state == FlowStep.COPYNOACCEPT ||
                state == FlowStep.PASSNOACCEPT || state == FlowStep.NODEAL_REPLYED ||
                state == FlowStep.DEALED_REPLYED_NOACCEPT;
    }

    public boolean isAcceptable(I item) throws Exception
    {
        return item.getState() == FlowStep.NOACCEPT && (item.getReceiverList().size() > 1 || acceptable) &&
                item.getWaitForItems().size() == 0;
    }

    public boolean isCancelAcceptable(I item) throws Exception
    {
        return item.getState() == FlowStep.NODEAL && (item.getReceiverList().size() > 1 || acceptable);
    }

    public boolean isAccepted(I item) throws Exception
    {
        return item.getState() == FlowStep.ACCEPTED;
    }

    public WorkSheetRemark getRemark(I item) throws Exception
    {
        if (item.isMultiple())
        {
            WorkSheetRemark remark = new WorkSheetRemark();

            remark.setSubItems(item.getSubItems());
            remark.setWaitForItems(item.getWaitForItems());

            return remark;
        }

        return null;
    }

    @Service
    @Transactional
    public boolean accept(Long stepId) throws Exception
    {
        FlowContext flowContext = FlowApi.loadContext(stepId, getSystemFlowDao());
        boolean b = flowContext.accept();

        if (b)
        {
            flowContext.getStep().setProperty("dealer", getUserId());
            flowContext.getStep().setDealerName(userOnlineInfo.getUserName());
            flowContext.updateStep();
        }

        return b;
    }

    @Service
    @Transactional
    @ObjectResult
    public void cancelAccept(Long stepId) throws Exception
    {
        FlowApi.loadContext(stepId, getSystemFlowDao()).cancelAccept();
    }

    @Service
    @Transactional
    public boolean remove(Long key) throws Exception
    {
        return key != null && removeAll(new Long[]{key}) > 0;
    }

    /**
     * 批量删除，使用事务
     *
     * @param keys 要删除的步骤ID
     * @return 是否真的删除了数据，如果数据库中没有此记录则无法真的删除，返回false，否则返回true
     * @throws Exception 删除异常
     */
    @Service
    @Transactional
    public int removeAll(Long[] keys) throws Exception
    {
        if (keys != null)
        {
            setKeys(keys);
        }

        keys = getKeys();
        if (keys != null)
        {
            List<Long> instanceIds = getWorkSheetDao().queryInstanceIdsByStepIds(keys);

            for (Long instanceId : instanceIds)
            {
                deleteInstance(instanceId);
            }

            return instanceIds.size();
        }

        return 0;
    }

    protected void deleteInstance(Long instanceId) throws Exception
    {
        FlowApi.getController(instanceId, getSystemFlowDao()).deleteInstance();
    }

    @Service(method = HttpMethod.post)
    @Transactional
    public void hide() throws Exception
    {
        Long[] stepIds = getKeys();
        if (stepIds == null || stepIds.length == 0)
            return;

        getWorkSheetDao().hide(stepIds);
    }

    @Service(method = HttpMethod.post)
    @Transactional
    public void unHide() throws Exception
    {
        Long[] stepIds = getKeys();
        if (stepIds == null || stepIds.length == 0)
            return;

        getWorkSheetDao().unHide(stepIds);
    }

    @Service(method = HttpMethod.post)
    @Transactional
    public void catalog(Integer catalogId) throws Exception
    {
        Long[] stepIds = getKeys();
        if (stepIds == null || stepIds.length == 0)
            return;

        List<Long> instanceIds = getWorkSheetDao().queryInstanceIdsByStepIds(stepIds);
        Integer userId = getUserId();

        for (Long instanceId : instanceIds)
        {
            WorkSheetCatalogList list = new WorkSheetCatalogList();
            list.setInstanceId(instanceId);
            list.setUserId(userId);
            list.setCatalogId(catalogId);

            catalogDao.save(list);
        }
    }

    @Service(method = HttpMethod.post)
    @Transactional
    public void cancelCatalog() throws Exception
    {
        Long[] stepIds = getKeys();
        if (stepIds == null || stepIds.length == 0)
            return;

        List<Long> instanceIds = getWorkSheetDao().queryInstanceIdsByStepIds(stepIds);
        Integer userId = getUserId();

        catalogDao.cancelCatalog(userId, instanceIds);
    }

    @Service(method = HttpMethod.post)
    @Transactional
    @SuppressWarnings("unchecked")
    public void end() throws Exception
    {
        Long[] stepIds = getKeys();
        if (stepIds == null || stepIds.length == 0)
            return;

        for (Long stepId : stepIds)
        {
            stopOrEnd(stepId);
        }
    }

    @Service(method = HttpMethod.post)
    @Transactional
    public void stopAll() throws Exception
    {
        Long[] stepIds = getKeys();
        if (stepIds == null || stepIds.length == 0)
            return;

        for (Long stepId : stepIds)
        {
            if (!stopOrEnd(stepId))
            {
                String title = workSheetDao.getTitleWithStepId(stepId);
                throw new NoErrorException("platform.flow.noAuthToStopAll", title);
            }
        }
    }

    @Service(method = HttpMethod.post)
    @Transactional
    @SuppressWarnings("unchecked")
    public void stop(Long stepId) throws Exception
    {
        if (!stopOrEnd(stepId))
            throw new NoErrorException("platform.flow.noAuthToStop");
    }

    protected boolean stopOrEnd(Long stepId) throws Exception
    {
        if (stepId == null)
            return true;

        String flowTag = getWorkSheetDao().getFlowTagWithStepId(stepId);

        return stopOrEnd(stepId, flowTag);
    }

    protected boolean stopOrEnd(Long stepId, String flowTag) throws Exception
    {
        Class<? extends FlowPage> c = getFlowPageClass(flowTag);
        if (c == null)
            return false;

        FlowPage flowPage = Tools.getBean(c);
        flowPage.setStepId(stepId);
        FlowContext context = flowPage.getFlowContext();
        context.setRead();

        Action endAction = null;

        for (Action action : context.getExecutableActions(flowPage))
        {
            if (action instanceof DefaultAction)
            {
                if (DefaultAction.STOP.equals(action.getActionId()))
                {
                    flowPage.stop();
                    return true;
                }
                else if (DefaultAction.END.equals(action.getActionId()) ||
                        DefaultAction.PASSEND.equals(action.getActionId()))
                {
                    flowPage.end();
                    return true;
                }
            }
            else if (action instanceof RouteGroup)
            {
                if (endAction == null)
                {
                    List<Route> routes = ((RouteGroup) action).getRoutes();
                    for (Route route : routes)
                    {
                        if (FlowNode.END.equals(route.getNextNodeId()))
                        {
                            if (isStopable(action, flowPage))
                                endAction = action;
                            break;
                        }
                    }
                }
            }
        }

        if (endAction != null)
        {
            Object result = flowPage.execute(endAction.getActionId());

            if (result instanceof NodeReceiverSelectList)
            {
                List<NodeReceiverSelect> list = ((NodeReceiverSelectList) result).getList();
                if (list.size() == 1 && FlowNode.END.equals(list.get(0).getNodeId()))
                {
                    flowPage.send(list, endAction.getActionId());
                    return true;
                }
            }
        }

        return false;
    }

    protected boolean isStopable(Action action, FlowPage flowPage) throws Exception
    {
        return true;
    }

    public boolean isStopable0(FlowWorkSheetItem item) throws Exception
    {
        int state = item.getState();

        return item.getInstanceState() != 1 &&
                (state == FlowStep.NODEAL || state == FlowStep.BACKNODEAL || state == FlowStep.NODEAL_REPLYED);
    }

    public boolean isStopable(FlowWorkSheetItem item) throws Exception
    {
        int state = item.getState();
        if (state == FlowStep.NODEAL || state == FlowStep.BACKNODEAL || state == FlowStep.NODEAL_REPLYED)
        {
            Long stepId = Long.valueOf(item.getStepId());
            Class<? extends FlowPage> c = getFlowPageClass(getWorkSheetDao().getFlowTagWithStepId(stepId));

            FlowPage flowPage = Tools.getBean(c);
            flowPage.setStepId(stepId);

            FlowContext context = flowPage.getFlowContext();

            for (Action action : context.getExecutableActions(flowPage))
            {
                if (action instanceof DefaultAction && DefaultAction.STOP.equals(action.getActionId()))
                    return true;
            }
        }

        return false;
    }

    @Service
    @Transactional
    @SuppressWarnings("unchecked")
    public void cancelSend(Long stepId) throws Exception
    {
        if (stepId == null)
            return;

        Class<? extends FlowPage> c = getFlowPageClass(getWorkSheetDao().getFlowTagWithStepId(stepId));
        FlowPage flowPage = Tools.getBean(c);
        flowPage.setStepId(stepId);
        FlowContext context = flowPage.getFlowContext();
        int state = context.getStep().getState();

        if (state == FlowStep.DEALED || state == FlowStep.DEALED_REPLYED)
        {
            for (Action action : context.getExecutableActions(flowPage))
            {
                if (action instanceof DefaultAction && DefaultAction.CANCELSEND.equals(action.getActionId()))
                {
                    flowPage.cancelSend();
                    return;
                }
            }
        }

        throw new NoErrorException("platform.flow.noAuthToCancel");
    }

    public boolean isCancelSendable0(FlowWorkSheetItem item) throws Exception
    {
        int state = item.getState();
        return state == FlowStep.DEALED || state == FlowStep.DEALED_REPLYED;
    }

    public boolean isCancelSendable(FlowWorkSheetItem item) throws Exception
    {
        int state = item.getState();
        if (state == FlowStep.DEALED || state == FlowStep.DEALED_REPLYED)
        {
            FlowContext context = FlowApi.loadContext(Long.valueOf(item.getStepId()), getSystemFlowDao());

            for (Action action : context.getExecutableActions(null))
            {
                if (action instanceof DefaultAction && DefaultAction.CANCELSEND.equals(action.getActionId()))
                    return true;
            }
        }

        return false;
    }

    public Class<Long> getKeyType()
    {
        return Long.class;
    }

    public String getOwnerField()
    {
        return "catalogId";
    }

    @Service
    @ObjectResult
    @SuppressWarnings("UnusedDeclaration")
    public void moveTo(Long key, Integer ownerKey, Integer oldOwnerKey) throws Exception
    {
        setKeys(new Long[]{key});

        catalog(ownerKey);
    }

    @Service(method = HttpMethod.post)
    @ObjectResult
    @SuppressWarnings("UnusedDeclaration")
    public void moveAllTo(Long[] keys, Integer ownerKey, Integer oldOwnerKey) throws Exception
    {
        if (keys != null)
            setKeys(keys);

        catalog(ownerKey);
    }

    public PageUserSelector getUserSelector()
    {
        if (userSelector == null)
        {
            userSelector = new PageUserSelector();
            String appId = UserInfo.getAppIdByUrl("/Consignation?type=0");
            if (appId == null)
                appId = UserInfo.getAppIdByUrl("/Consignation?type=0&rejectable=true");
            userSelector.setAppId(appId);
        }
        return userSelector;
    }

    @Service
    @ObjectResult
    public void consign(Integer consignee) throws Exception
    {
        if (getKeys() != null && getKeys().length > 0)
        {
            Integer consignationId = getWorkSheetDao().consign(getKeys(), userOnlineInfo.getUserId(), consignee);

            Message message = new Message();
            message.setSender(userOnlineInfo.getUserId());
            message.setUserId(consignee);
            message.setMessage(Tools.getMessage("platform.flow.consignationTo", userOnlineInfo.getUserName(),
                    getWorkSheetDao().getTitleWithStepId(getKeys()[0])));
            message.setUrl("/ods/flow/worksheet?consignationId=" + consignationId);

            message.send();
        }
    }
}