package com.gzzm.platform.flow;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.form.*;
import com.gzzm.platform.message.*;
import com.gzzm.platform.message.Message;
import com.gzzm.platform.message.sms.SmsMessageSender;
import com.gzzm.platform.organ.*;
import com.gzzm.platform.recent.*;
import net.cyan.arachne.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.*;
import net.cyan.valmiki.flow.*;
import net.cyan.valmiki.form.*;
import net.cyan.valmiki.form.components.*;
import net.cyan.valmiki.form.components.list.MissingListModel;

import java.util.*;

/**
 * 流程操作页面，提供流程操作的http入口
 *
 * @author camel
 * @date 2010-9-20
 */
@Service
@Forward(name = "message", page = "/platform/flow/message.ptl")
public abstract class FlowPage extends FormBodyPage implements Filter<Action>
{
    public static final String NOTIFY_MESSAGE = "notifyMessage";

    public static final String SAVE = "save";

    public static final String SEND_TO_ALL_NEXT_NODES = "sendToAllNextNodes";

    public static final DefaultAction SAVE_ACTION = new BaseDefaultAction(SAVE, null);

    @SuppressWarnings("UnusedDeclaration")
    public static final DefaultAction SEND_TO_ALL_NEXT_NODES_ACTION =
            new BaseDefaultAction(SEND_TO_ALL_NEXT_NODES, null);

    @Inject
    protected static Provider<DeptService> deptServiceProvider;

    /**
     * 步骤ID，当前操作的步骤
     */
    private Long stepId;

    /**
     * 当前实例id
     */
    private Long instanceId;

    /**
     * 是否以只读方式打开文档
     */
    private boolean readOnly;

    /**
     * 以可编辑方式打开文档
     */
    private boolean editable;

    /**
     * 只允许保存，不允许其他动作
     */
    private boolean saveOnly;

    /**
     * 流程上下文
     */
    @NotSerialized
    private FlowContext flowContext;

    private Long bodyId;

    private Integer businessDeptId;

    /**
     * 缓存动作列表，避免重复加载
     */
    @NotSerialized
    private List<Action> actions;

    private List<Action> actions0;

    /**
     * 当前正在操作的动作ID
     */
    protected String actionId;

    /**
     * 流程扩展组件，为流程提供额外的功能
     */
    @NotSerialized
    private FlowComponent flowComponent;

    private Map<String, FlowExtension> extensions;

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @BeanList
    private Map<String, FlowExtensionFactory> extensionFactories;

    protected FlowComponentContext flowComponentContext;

    private SystemFlowDao systemFlowDao;

    private List<String> roles;

    /**
     * 扩展的数据，允许流程扩展设置数据
     */
    private Object data;

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @BeanList
    private List<FlowComponentProvider> flowComponentProviders;

    /**
     * 流程消息接收者
     */
    private List<Integer> messageReceivers;

    public FlowPage()
    {
    }

    public Object getData()
    {
        return data;
    }

    public void setData(Object data)
    {
        this.data = data;
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

    protected SystemFlowDao createSystemFlowDao(Class<? extends SystemFlowInstance> instanceClass,
                                                Class<? extends SystemFlowStep> stepClass) throws Exception
    {
        return SystemFlowDao.getInstance(instanceClass, stepClass);
    }

    protected void lockInstance() throws Exception
    {
        getSystemFlowDao().lockFlowInstanceByStepId(getStepId());
    }

    public Long getStepId()
    {
        return stepId;
    }

    public void setStepId(Long stepId)
    {
        this.stepId = stepId;
    }

    public void setEditable(boolean editable)
    {
        this.editable = editable;
    }

    public boolean isReadOnly()
    {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly)
    {
        this.readOnly = readOnly;
    }

    public boolean isSaveOnly()
    {
        return saveOnly;
    }

    public void setSaveOnly(boolean saveOnly)
    {
        this.saveOnly = saveOnly;
    }

    @NotSerialized
    public String getTitle() throws Exception
    {
        return getFlowContext().getInstance().getTitle();
    }

    public boolean isEditable() throws Exception
    {
        return getFlowContext().isEditable();
    }

    public boolean isFirstStep() throws Exception
    {
        return getFlowContext().getStep().isFirstStep();
    }

    public Integer getUserId() throws Exception
    {
        return getBusinessContext().getUserId();
    }

    @NotSerialized
    public String getUserName() throws Exception
    {
        return getBusinessContext().getUserName();
    }

    public Long getGroupId() throws Exception
    {
        return Long.valueOf(getFlowContext().getStep().getGroupId());
    }

    public List<String> getRoles() throws Exception
    {
        if (roles == null)
        {
            FlowContext flowContext = getFlowContext();
            FlowNode node = flowContext.getNode();
            if (node != null)
            {
                String s = node.getProperty("role", flowContext.getScriptContext());

                if (s != null)
                    roles = Arrays.asList(s.split(","));
            }
        }

        return roles;
    }

    public boolean hasRole(String roleName) throws Exception
    {
        List<String> roles = getRoles();
        return roles != null && roles.contains(roleName);
    }

    @Override
    public Integer getBusinessDeptId() throws Exception
    {
        if (businessDeptId == null)
        {
            businessDeptId = FlowApi.getDeptId(getFlowContext().getInstance());
        }

        return businessDeptId;
    }

    public boolean isTest() throws Exception
    {
        return FlowApi.isTest(getFlowContext().getInstance());
    }

    @NotSerialized
    public String getFlowTag() throws Exception
    {
        return getFlowContext().getInstance().getFlowTag();
    }

    @Override
    protected Long getBodyId(String name) throws Exception
    {
        if (StringUtils.isEmpty(name))
            return getBodyId();

        return null;
    }

    protected Long getBodyId() throws Exception
    {
        if (bodyId == null)
        {
            bodyId = FlowApi.getBodyId(getFlowContext().getInstance());
        }
        return bodyId;
    }

    protected DeptService getDeptService()
    {
        return deptServiceProvider.get();
    }

    @Override
    protected boolean isReadOnly(SystemFormContext formContext) throws Exception
    {
        return !isEditable();
    }

    @Override
    protected FormRole getRole(SystemFormContext formContext) throws Exception
    {
        List<String> roles = getRoles();

        FormRole role;
        if (roles == null)
        {
            role = formContext.getForm().createDefaultRole();
        }
        else
        {
            role = formContext.getForm().getRole(roles);
        }

        return role;
    }

    public FlowContext getFlowContext() throws Exception
    {
        if (flowContext == null)
        {
            flowContext = FlowApi.loadContext(stepId, getSystemFlowDao());

            BusinessContext businessContext = getBusinessContext();

            if (!isReadable(flowContext))
            {
                throw new SystemMessageException(Messages.NO_AUTH_RECORD,
                        "no auth," + ",stepId:" + getStepId() + ",userId:" + businessContext.getUserId());
            }

            if (readOnly)
            {
                if (FlowApi.getReceivers(businessContext.getUserId()).contains(flowContext.getStep().getReceiver()))
                    readOnly = false;
            }

            if (!isWritable(flowContext))
            {
                flowContext.setEditable(false);
            }

            if (!readOnly && editable)
            {
                flowContext.setEditable(true);
            }

            initFlowContext(flowContext);
        }

        return flowContext;
    }

    public Long getInstanceId() throws Exception
    {
        if (instanceId == null)
            instanceId = Long.valueOf(getFlowContext().getInstance().getInstanceId());

        return instanceId;
    }

    @NotSerialized
    public String getNodeName() throws Exception
    {
        FlowContext flowContext = getFlowContext();

        String nodeName = flowContext.getStep().getNodeName();

        if (StringUtils.isEmpty(nodeName))
        {
            FlowNode node = flowContext.getFlow().getNode(flowContext.getStep().getNodeId());
            if (node != null)
                nodeName = node.getNodeName();
        }

        if (StringUtils.isEmpty(nodeName))
        {
            nodeName = Tools.getMessage("cyan.valmiki.flow.node." + flowContext.getStep().getNodeId() + ".name");
        }

        return nodeName;
    }

    public String getNodeName(String nodeId) throws Exception
    {
        String nodeName = null;

        FlowContext flowContext = getFlowContext();

        FlowNode node = flowContext.getFlow().getNode(nodeId);
        if (node != null)
            nodeName = node.getNodeName(getFlowContext().getScriptContext());

        if (StringUtils.isEmpty(nodeName))
        {
            nodeName = Tools.getMessage("cyan.valmiki.flow.node." + nodeId + ".name");
        }

        return nodeName;
    }

    public String getNodeId() throws Exception
    {
        return getFlowContext().getStep().getNodeId();
    }

    public int getState() throws Exception
    {
        return getFlowContext().getStep().getState();
    }

    public int getInstanceState() throws Exception
    {
        return getFlowContext().getInstance().getState();
    }

    public String getSourceName() throws Exception
    {
        return getFlowContext().getStep().getSourceName();
    }

    public String getActionId()
    {
        return actionId;
    }

    @NotSerialized
    public Action getAction() throws Exception
    {
        return getAction(actionId);
    }

    @Override
    protected void initFormContext(SystemFormContext formContext) throws Exception
    {
        super.initFormContext(formContext);

        formContext.setOperationId(getStepId().toString());

        MissingListModel.addOptionsProvider(formContext, DictOptionsProvider.INSTANCE);

        initFormContextByComponent(formContext);
    }

    @SuppressWarnings("unchecked")
    protected void initFormContextByComponent(SystemFormContext formContext) throws Exception
    {
        //调用扩展功能提取扩展数据
        FlowComponent flowComponent = getFlowComponent();
        if (flowComponent != null)
            flowComponent.initForm(formContext, getFlowComponentContext());

        for (FlowExtension extension : getExtensions().values())
        {
            extension.initForm(formContext, getFlowComponentContext());
        }
    }

    /**
     * 初始化流程上下文，可以被子类继承覆盖其逻辑
     *
     * @param flowContext 流程上下文
     * @throws Exception 初始化错误，允许子类抛出异常
     */
    protected void initFlowContext(FlowContext flowContext) throws Exception
    {
        //初始化系统变量
        ScriptContext scriptContext = flowContext.getScriptContext();

        scriptContext.addVariableContainer(new MapVariableContainer(getBusinessContext()));

        if (getBodyId() != null)
        {
            scriptContext.addVariableContainer(new MapVariableContainer(getFormContext().getContext()));
        }

        initFlowContextByComponent();
    }

    @SuppressWarnings("unchecked")
    protected void initFlowContextByComponent() throws Exception
    {
        //调用扩展功能提取扩展数据
        FlowComponent flowComponent = getFlowComponent();
        if (flowComponent != null)
            flowComponent.init(getFlowComponentContext());

        for (FlowExtension extension : getExtensions().values())
        {
            extension.init(getFlowComponentContext());
        }
    }

    /**
     * 是否允许查看此步骤，可以被子类继承覆盖其逻辑
     *
     * @param contex 流程上下文
     * @return 允许查看返回true，不允许查看返回false
     * @throws Exception 允许子类抛出异常
     */
    @SuppressWarnings("UnusedDeclaration")
    protected boolean isReadable(FlowContext contex) throws Exception
    {
        BusinessContext businessContext = getBusinessContext();
        if (businessContext.getUser().getUserType() == UserType.out)
        {
            if (!businessContext.getUserId().toString().equals(flowContext.getStep().getReceiver()))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * 是否允许操作此步骤，可以被子类继承覆盖其逻辑
     *
     * @param context 流程上下文
     * @return 允许操作返回true，不允许查看返回false
     * @throws Exception 允许子类抛出异常
     */
    @SuppressWarnings("UnusedDeclaration")
    protected boolean isWritable(FlowContext context) throws Exception
    {
        return !readOnly;
    }

    /**
     * 是否自动接收步骤，可以被子类继承覆盖其逻辑
     *
     * @param context 流程上下文
     * @return 允许自动接收返回true，不自动接收返回false
     * @throws Exception 允许子类抛出异常
     */
    @SuppressWarnings("UnusedDeclaration")
    protected boolean isAutoReceive(FlowContext context) throws Exception
    {
        int state = context.getStep().getState();

        if (state != FlowStep.NOACCEPT)
            return true;

        if (isAutoReceiveInGroup(context) || context.getGroupSize() == 1)
        {
            FlowNode node = context.getNode();
            return !(node != null && "false".equals(node.getProperty("autoAccept")));
        }

        return false;
    }

    @SuppressWarnings("UnusedDeclaration")
    protected boolean isAutoReceiveInGroup(FlowContext context) throws Exception
    {
        return false;
    }

    public boolean accept(Action action) throws Exception
    {
        if (readOnly)
        {
            if (action instanceof DefaultAction)
            {
                String actionId = action.getActionId();
                if (DefaultAction.CANCELSEND.equals(actionId) || DefaultAction.COPY.equals(actionId) ||
                        DefaultAction.ACCEPT.equals(actionId))
                    return false;
            }
            else if (action instanceof RouteGroup)
            {
                return false;
            }
        }

        FlowComponent flowComponent = getFlowComponent();
        if (flowComponent != null && !flowComponent.accept(action, getFlowComponentContext()))
            return false;

        for (FlowExtension extension : getExtensions().values())
        {
            if (!extension.accept(action, getFlowComponentContext()))
                return false;
        }

        return true;
    }

    public boolean isSimpleNode() throws Exception
    {
        return "true".equals(getFlowContext().getNode().getProperty("simple"));
    }

    public List<Action> getActions() throws Exception
    {
        if (actions == null)
        {
            actions0 = createActions();

            if (!isSimpleNode())
            {
                for (FlowExtension extension : getExtensions().values())
                {
                    extension.initActions(actions0, getFlowComponentContext());
                }
            }

            actions = new ArrayList<Action>(actions0.size());
            for (Action action : actions0)
            {
                String name = getActionName(action);
                if (!StringUtils.isEmpty(name))
                    actions.add(action);
            }
        }
        return actions;
    }

    public static boolean hasAction(List<Action> actions, String actionId)
    {
        for (Action action : actions)
        {
            if (actionId.equals(action.getActionId()))
                return true;
        }

        return false;
    }

    public boolean hasAction(String actionId) throws Exception
    {
        getActions();
        return hasAction(actions0, actionId);
    }

    /**
     * 获得当前能执行的动作
     *
     * @return 动作列表
     * @throws Exception 一般为脚本执行错误
     */
    protected List<Action> createActions() throws Exception
    {
        if (isSaveOnly())
        {
            List<Action> actions = new ArrayList<Action>();
            if (isEditable())
                actions.add(SAVE_ACTION);

            return actions;
        }

        List<Action> actions = getFlowContext().getExecutableActions(this);

        if (isEditable())
        {
            boolean saveable = false;
            if (getState() == FlowStep.PASSACCEPTED)
            {
                //传阅环节在两种情况添加保存按钮，1有回复动作，2表单上有可写控件
                for (Action action : actions)
                {
                    if (action instanceof DefaultAction && DefaultAction.PASSREPLY.equals(action.getActionId()))
                    {
                        saveable = true;
                        break;
                    }
                }

                SystemFormContext formContext = getFormContext();
                if (formContext != null)
                {
                    FormRole role = getRole(formContext);
                    for (Map.Entry<String, Set<String>> entry : role.getAuthorities().entrySet())
                    {
                        Set<String> authorities = entry.getValue();
                        if (authorities != null && authorities.contains(FormRole.WRITABLE))
                        {
                            saveable = true;
                            break;
                        }
                    }
                }
            }
            else
            {
                saveable = true;
            }

            if (saveable)
            {
                boolean exists = false;
                for (Action action : actions)
                {
                    if (SAVE.equals(action.getActionId()))
                    {
                        exists = true;
                        break;
                    }
                }

                if (!exists)
                {
                    //能编辑，添加一个保存按钮
                    actions.add(0, SAVE_ACTION);
                }
            }
        }
        else if (getState() == FlowStep.NOACCEPT && getFlowContext().isAcceptable())
        {
            //添加接收按钮
            actions.add(0, DefaultAction.ACCEPTACTION);
        }

        if (readOnly)
        {
            for (Iterator<Action> iterator = actions.iterator(); iterator.hasNext(); )
            {
                Action action = iterator.next();
                String actionId = action.getActionId();
                if (DefaultAction.CANCELSEND.equals(actionId) || DefaultAction.COPY.equals(actionId) ||
                        SAVE.equals(actionId) || DefaultAction.ACCEPT.equals(actionId))
                {
                    iterator.remove();
                }
            }
        }

        return actions;
    }

    /**
     * 获得某个动作对应的javascript
     *
     * @param action 动作对象
     * @return 对用的javascript
     * @throws Exception 允许子类抛出异常
     */
    public String getAction(Action action) throws Exception
    {
        String prompt = action.getPrompt();
        if (prompt != null)
            prompt = "'" + StringUtils.escapeString(prompt) + "'";

        if (action instanceof DefaultAction)
        {
            return "window." + action.getActionId() + "(" + ")";
        }
        else if (action instanceof ScriptAction)
        {
            ScriptAction scriptAction = (ScriptAction) action;

            if (scriptAction.isClient())
            {
                return scriptAction.getScript().getScript();
            }
            else if (scriptAction.isCheckData())
            {
                return "if(checkData('" + action.getActionId() + "')){execute('" + action.getActionId() + "'," +
                        prompt + ");}";
            }
            else
            {
                return "execute('" + action.getActionId() + "'," + prompt + ");";
            }
        }
        else
        {
            return "if(checkData('" + action.getActionId() + "')){execute('" + action.getActionId() + "'," + prompt +
                    ");}";
        }
    }

    /**
     * 动作的名称
     *
     * @param action 动作
     * @return 动作的名称
     * @throws Exception 执行脚本错误，或者运行子类抛出异常
     */
    public String getActionName(Action action) throws Exception
    {
        return action.getActionName(getFlowContext().getScriptContext());
    }

    /**
     * 动作的说明
     *
     * @param action 动作
     * @return 动作的说明，用于提示用户
     * @throws Exception 执行脚本错误，或者运行子类抛出异常
     */
    public String getActionRemark(Action action) throws Exception
    {
        return action.getProperty("remark", getFlowContext().getScriptContext());
    }

    /**
     * 动作的颜色
     *
     * @param action 动作
     * @return 动作的说明，用于提示用户
     * @throws Exception 执行脚本错误，或者运行子类抛出异常
     */
    public String getActionStyle(Action action) throws Exception
    {
        return action.getProperty("style", getFlowContext().getScriptContext());
    }

    protected String getShowForward() throws Exception
    {
        return null;
    }

    /**
     * 打开文档
     *
     * @return 转向的页面
     * @throws Exception 数据库查询错误
     */
    @Service(url = "/{@class}/{stepId}")
    public String show() throws Exception
    {
        beforeLoad();

        FlowContext context = getFlowContext();

        if (!readOnly && isAutoReceive(context))
        {
            //打开文档时自动接收或者将文档设置为只读
            setRead();
        }

        beforeShow();

        context.invokeEvent(DefaultEvent.BEFORESHOW);

        return getShowForward();
    }

    protected boolean setRead() throws Exception
    {
        if (getFlowContext().setRead())
        {
            getSystemFlowDao().setStepRead(getStepId());
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * 执行扩展的跳转页面
     *
     * @param name    扩展的名称
     * @param forward 转向的页面的名称
     * @return 扩展页面路径
     * @throws Exception 由扩展定义的错误
     */
    @Service(url = "/{@class}/{stepId}/extension/{$0}/{$1}", method = HttpMethod.get)
    public String showExtensionForward(String name, String forward) throws Exception
    {
        FlowExtension extension = getExtensions().get(name);
        if (extension != null)
        {
            FlowComponentContext context = getFlowComponentContext();
            return extension.forward(forward, context);
        }

        return null;
    }

    /**
     * 下载扩展的文件
     *
     * @param name 扩展的名称
     * @param file 转向的文件的名称
     * @return 扩展文件
     * @throws Exception 由扩展定义的错误
     */
    @Service(url = "/{@class}/{stepId}/extension/{$0}/down/{$1}", method = HttpMethod.get)
    public InputFile downExtensionFile(String name, String file) throws Exception
    {
        FlowExtension extension = getExtensions().get(name);
        if (extension != null)
        {
            FlowComponentContext context = getFlowComponentContext();
            return extension.down(file, context);
        }

        return null;
    }

    /**
     * 加载数据之前需要执行的代码，提供给子类做扩展
     *
     * @throws Exception 允许子类抛出异常
     */
    protected void beforeLoad() throws Exception
    {
    }

    /**
     * 加载数据之后，显示数据之前需要执行的代码，提供给子类做扩展
     *
     * @throws Exception 允许子类抛出异常
     */
    protected void beforeShow() throws Exception
    {
        readMessage();

        beforeShowByComponent();

        saveRecent();
    }

    public void readMessage() throws Exception
    {
        getSystemFlowDao().setMessageReaded(getInstanceId(), getUserId());
    }

    @SuppressWarnings("unchecked")
    protected void beforeShowByComponent() throws Exception
    {
        //调用扩展功能
        FlowComponent flowComponent = getFlowComponent();
        if (flowComponent != null)
            flowComponent.beforeShow(getFlowComponentContext());

        for (FlowExtension extension : getExtensions().values())
        {
            extension.beforeShow(getFlowComponentContext());
        }
    }

    protected void saveRecent() throws Exception
    {
        Recent recent = new Recent();
        initRecent(recent);

        if (!StringUtils.isEmpty(recent.getType()) && !StringUtils.isEmpty(recent.getRecentName()))
        {
            RecentService service = Tools.getBean(RecentService.class);
            service.saveRecent(recent);
        }
    }

    protected void initRecent(Recent recent) throws Exception
    {
        recent.setUserId(getUserId());
        recent.setRecentName(getTitle());
        recent.setTarget(RecentTarget.TAB);
        recent.setUrl(getUrl(getStepId()));
        recent.setShowTime(getFlowContext().getStep().getShowTime());
    }

    /**
     * 接收，需要启动事务
     *
     * @throws Exception 接收错误，可能是数据库错误，也可能是执行脚本错误
     */
    @Service(url = "/{@class}/{stepId}.accept", method = HttpMethod.post)
    @Transactional
    public boolean accept() throws Exception
    {
        boolean b = getFlowContext().accept();

        if (b)
        {
            flowContext.getStep().setProperty("dealer", getUserId());
            flowContext.getStep().setDealerName(getUserName());
            flowContext.updateStep();
        }

        return b;
    }

    /**
     * 保存，需要启动事务
     *
     * @throws Exception 保存错误，可能是数据库错误，也可能是执行脚本错误
     */
    @Service(url = "/{@class}/{stepId}.save", method = HttpMethod.post)
    @Transactional
    public void save() throws Exception
    {
        //不可编辑时不保存
        if (!isEditable())
            return;

        collectData();

        FlowContext flowContext = getFlowContext();
        Object result = flowContext.invokeEvent(DefaultEvent.BEFORESAVE);
        if (result != null)
        {
            //beforeSave返回结果，中断保存逻辑
            setResult(result);
            return;
        }

        result = beforeSave();
        if (result != null)
        {
            //beforeSave返回结果，中断保存逻辑
            setResult(result);
            return;
        }

        flowContext.getStep().setProperty("dealer", getUserId());
        flowContext.getStep().setDealerName(getUserName());
        flowContext.saveData();

        result = flowContext.invokeEvent(DefaultEvent.SAVE);

        Object r = afterSave(result);
        if (r != null)
            result = r;

        saveData();

        setResult(result);
    }

    /**
     * 采集数据
     *
     * @throws Exception 采集数据错误
     */
    protected void collectData() throws Exception
    {
        Long bodyId = getBodyId();
        if (bodyId != null)
        {
            lockFormBody();

            //采集表单数据
            collectFormData();
        }
    }

    /**
     * 保存数据
     *
     * @throws Exception 保存数据错误
     */
    protected void saveData() throws Exception
    {
        //先保存表单数据
        Long bodyId = getBodyId();
        if (bodyId != null)
            saveForm(getFormContext());
    }

    protected void saveContext() throws Exception
    {
        FlowContext flowContext = getFlowContext();
        if (flowContext.invokeEvent(DefaultEvent.BEFORESAVE) != null)
        {
            //beforeSave返回结果，中断保存逻辑
            return;
        }

        if (beforeSave() != null)
        {
            //beforeSave返回结果，中断保存逻辑
            return;
        }

        flowContext.getStep().setProperty("dealer", getUserId());
        flowContext.getStep().setDealerName(getUserName());
        flowContext.saveData();

        afterSave(flowContext.invokeEvent(DefaultEvent.SAVE));

        saveData();
    }

    /**
     * 保存之前执行的代码
     *
     * @return 返回null表示继续执行保存动作，否则终止执行，并将结果返回
     * @throws Exception 允许子类抛出异常
     */
    protected Object beforeSave() throws Exception
    {
        extractDataByComponent();

        return null;
    }

    @SuppressWarnings("unchecked")
    protected void extractDataByComponent() throws Exception
    {
        //调用扩展功能提取扩展数据
        FlowComponent flowComponent = getFlowComponent();
        if (flowComponent != null)
            flowComponent.extractData(getFlowComponentContext());

        for (FlowExtension extension : getExtensions().values())
        {
            extension.extractData(getFlowComponentContext());
        }
    }

    /**
     * 保存之后执行的代码
     *
     * @param result 保存动作的执行结果
     * @return 返回的结果
     * @throws Exception 允许子类抛出异常
     */
    protected Object afterSave(Object result) throws Exception
    {
        saveDataByComponent();

        return result;
    }

    @SuppressWarnings("unchecked")
    protected void saveDataByComponent() throws Exception
    {
        //调用扩展功能保存扩展数据
        FlowComponent flowComponent = getFlowComponent();
        if (flowComponent != null)
            flowComponent.saveData(getFlowComponentContext());

        for (FlowExtension extension : getExtensions().values())
        {
            extension.saveData(getFlowComponentContext());
        }
    }

    /**
     * 执行动作
     *
     * @param actionId 动作ID
     * @return 动作返回的结果，如果是路线组或者动作定义了接收者列表，则返回NodeReceiverSelectList对象
     * @throws Exception 执行动作错误，可能是数据库错误，也可能是执行脚本错误
     */
    @Service(url = "/{@class}/{stepId}/{$0}.do", method = HttpMethod.post)
    @Transactional
    public Object execute(String actionId) throws Exception
    {
        this.actionId = actionId;

        FlowContext context = getFlowContext();

        Object result = context.executeAction(actionId);

        if (result instanceof NodeReceiverSelectList && isTest())
        {
            //测试流程，往每个环节添加当前用户为接收者
            List<NodeReceiverSelect> receiverSelects = ((NodeReceiverSelectList) result).getList();
            int n = receiverSelects.size();

            NodeReceiverSelect lastReceiverSelect = null;

            for (int i = 0; i < n; i++)
            {
                NodeReceiverSelect receiverSelect = receiverSelects.get(i);

                if (lastReceiverSelect != null &&
                        !receiverSelect.getNodeId().equals(lastReceiverSelect.getNodeId()))
                {
                    NodeReceiverSelect selfReceiverSelect = new NodeReceiverSelect();

                    selfReceiverSelect.setNodeId(lastReceiverSelect.getNodeId());
                    selfReceiverSelect.setNodeName(lastReceiverSelect.getNodeName());
                    selfReceiverSelect.setSelectType("&".equals(lastReceiverSelect.getReceiver()) ?
                            SelectType.multiple_require : lastReceiverSelect.getSelectType());
                    selfReceiverSelect.setReceiver(getBusinessContext().getUserId().toString());
                    selfReceiverSelect.setReceiverName(getBusinessContext().getUserName());
                    selfReceiverSelect.setProperty("deptId", getBusinessContext().getDeptId());

                    receiverSelects.add(i++, selfReceiverSelect);
                    n++;
                }

                if (FlowNode.END.equals(receiverSelect.getNodeId()) ||
                        "#".equals(receiverSelect.getReceiver()))
                {
                    lastReceiverSelect = null;
                }
                else
                {
                    lastReceiverSelect = receiverSelect;
                }
            }

            if (lastReceiverSelect != null)
            {
                NodeReceiverSelect selfReceiverSelect = new NodeReceiverSelect();

                selfReceiverSelect.setNodeId(lastReceiverSelect.getNodeId());
                selfReceiverSelect.setNodeName(lastReceiverSelect.getNodeName());
                selfReceiverSelect.setSelectType("&".equals(lastReceiverSelect.getReceiver()) ?
                        SelectType.multiple_require : lastReceiverSelect.getSelectType());
                selfReceiverSelect.setReceiver(getBusinessContext().getUserId().toString());
                selfReceiverSelect.setReceiverName(getBusinessContext().getUserName());
                selfReceiverSelect.setProperty("deptId", getBusinessContext().getDeptId());

                receiverSelects.add(selfReceiverSelect);
            }
        }

        saveContext();

        return result;
    }

    /**
     * 执行扩展动作
     *
     * @param name     扩展的名称
     * @param actionId 动作ID
     * @return 动作返回的结果
     * @throws Exception 执行动作错误
     */
    @Service(url = "/{@class}/{stepId}/extension/{$0}/{$1}.do", method = HttpMethod.post)
    public Object executeExtensionAction(String name, String actionId, Object[] args) throws Exception
    {
        FlowExtension extension = getExtensions().get(name);
        if (extension != null)
        {
            FlowComponentContext context = getFlowComponentContext();
            return extension.execute(actionId, args, context);
        }

        return null;
    }

    /**
     * 发送到下一个环节
     *
     * @param receiverSelects 接收者列表，包含环节信息和接收者信息
     * @param actionId        动作ID
     * @throws Exception 发送错误，可能是数据库错误，也可能是执行脚本错误
     */
    @Service(url = "/{@class}/{stepId}.send", method = HttpMethod.post)
    @Transactional
    @SuppressWarnings("unchecked")
    public void send(List<NodeReceiverSelect> receiverSelects, String actionId) throws Exception
    {
        lockInstance();

        Object result = send0(receiverSelects, actionId);
        if (result == null)
        {
            result = new NodeReceiverList((List) receiverSelects);
        }

        saveContext();

        setResult(result);
    }

    private Object send0(List<NodeReceiverSelect> receiverSelects, String actionId) throws Exception
    {
        this.actionId = actionId;

        FlowContext context = getFlowContext();

        Map<String, List<List<NodeReceiver>>> receiverMap =
                context.getReceiverMap(initReceiverSelectList(receiverSelects), SystemNodeReceiverGroup.INSTANCE);

        Object result = beforeSend(receiverMap, actionId);

        if (result != null)
        {
            //beforeSend返回结果，中断发送逻辑
            return result;
        }

        result = context.sendTo(receiverMap, actionId);
        makeDeptLast();

        Object r = afterSend(receiverMap, actionId, result);
        if (r != null)
            result = r;

        return result;
    }

    @Service(url = "/{@class}/{stepId}.sendAll", method = HttpMethod.post)
    @Transactional
    public void sendToAllNextNodes() throws Exception
    {
        FlowContext context = getFlowContext();

        Object result = null;

        List<Action> actions = context.getExecutableActions(this);
        for (Action action : actions)
        {
            if (action instanceof RouteGroup)
            {
                String actionId = action.getActionId();
                Object obj = context.executeAction(actionId);

                if (obj instanceof NodeReceiverSelectList)
                {
                    Object r = send0(((NodeReceiverSelectList) obj).getList(), actionId);

                    if (r != null)
                    {
                        result = r;
                        break;
                    }
                }
            }
        }

        saveContext();

        setResult(result);
    }

    public Object sendToNode(String nodeId) throws Exception
    {
        return sendToNode(getFlowContext().getFlow().getNode(nodeId));
    }

    @SuppressWarnings("unchecked")
    public Object sendToNode(FlowNode node) throws Exception
    {
        FlowContext context = getFlowContext();

        List<NodeReceiverSelect> receiverSelects =
                FlowUtils.calculateReceiverList(context.getFlow(), node, context.getScriptContext(),
                        SelectType.no);

        Map<String, List<List<NodeReceiver>>> receiverMap =
                context.getReceiverMap(initReceiverSelectList(receiverSelects), SystemNodeReceiverGroup.INSTANCE);

        Object result = context.sendTo(receiverMap, actionId);
        makeDeptLast();

        if (result == null)
        {
            result = receiverMap.get(node.getNodeId());
        }

        return result;
    }

    /**
     * 发送之前执行的代码
     *
     * @param receiverMap 提交到的下环节及对应的接收者信息
     * @param actionId    动作ID
     * @return 返回null表示继续执行发送动作，否则终止执行，并将结果返回
     * @throws Exception 允许子类抛出异常
     */
    @SuppressWarnings("UnusedDeclaration")
    protected Object beforeSend(Map<String, List<List<NodeReceiver>>> receiverMap, String actionId) throws Exception
    {
        beforeSendByComponent(receiverMap);

        if (isEnd(receiverMap))
        {
            beforeEndFlowByComponent();
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    protected void beforeSendByComponent(Map<String, List<List<NodeReceiver>>> receiverMap) throws Exception
    {
        //调用扩展功能
        FlowComponent flowComponent = getFlowComponent();
        if (flowComponent != null)
            flowComponent.beforeSend(getFlowComponentContext(), receiverMap);

        for (FlowExtension extension : getExtensions().values())
        {
            extension.beforeSend(getFlowComponentContext(), receiverMap);
        }
    }

    @SuppressWarnings("unchecked")
    protected void beforeEndFlowByComponent() throws Exception
    {
        //调用扩展功能
        FlowComponent flowComponent = getFlowComponent();
        if (flowComponent != null)
            flowComponent.beforeEndFlow(getFlowComponentContext());

        for (FlowExtension extension : getExtensions().values())
        {
            extension.beforeEndFlow(getFlowComponentContext());
        }
    }

    /**
     * 提交之后执行的代码
     *
     * @param receiverMap 提交到的下环节及对应的接收者信息
     * @param result      提交动作执行的结果
     * @param actionId    动作ID
     * @return 返回给客户端的结果
     * @throws Exception 允许子类抛出异常
     */
    @SuppressWarnings("UnusedDeclaration")
    protected Object afterSend(Map<String, List<List<NodeReceiver>>> receiverMap, String actionId, Object result)
            throws Exception
    {
        if (receiverMap == null || receiverMap.size() > 1 || !receiverMap.containsKey(FlowNode.END))
            extractMessageFromForm();

        //通知办理人
        if (result == null)
        {
            if (receiverMap != null)
            {
                for (Map.Entry<String, List<List<NodeReceiver>>> entry : receiverMap.entrySet())
                {
                    String nodeId = entry.getKey();

                    if (entry.getValue() != null)
                    {
                        for (List<NodeReceiver> receivers : entry.getValue())
                        {
                            notify(receivers, nodeId, actionId, null);
                        }
                    }
                }
            }
            else if (FlowNode.COPY.equals(getNodeId()))
            {
                FlowContext context = getFlowContext();
                List<FlowStep> preSteps = context.getPreSteps();

                List<NodeReceiver> receiverList = new ArrayList<NodeReceiver>(preSteps.size());
                for (FlowStep step : preSteps)
                {
                    NodeReceiver receiver =
                            new NodeReceiver(step.getProperty("dealer").toString(), step.getDealerName());
                    receiver.setStepId(step.getStepId());
                    receiverList.add(receiver);
                }

                notify(receiverList, "#reply", actionId, null);
            }
        }

        afterSendByComponent(receiverMap);

        if (isEnd(receiverMap))
        {
            endFlowByComponent();
        }

        return result;
    }

    protected void extractMessageFromForm() throws Exception
    {
        extractMessageFromForm(null);
    }

    /**
     * 从表单中提取消息
     *
     * @param formName 表单名称
     * @throws Exception 数据库操作错误
     */
    protected void extractMessageFromForm(String formName) throws Exception
    {
        SystemFormContext formContext = getFormContext(formName);
        if (formContext != null)
        {
            WebForm form = formContext.getForm();
            String stepIdS = getStepId().toString();

            for (ComponentData componentData : formContext.getFormData())
            {
                if (componentData instanceof ParallelTextData)
                {
                    for (ParallelTextItem item : ((ParallelTextData) componentData).getItems())
                    {
                        if (stepIdS.equals(item.getOperationId()))
                        {
                            String text = item.getText();
                            if (!StringUtils.isEmpty(text))
                            {
                                text = HtmlUtils.getPlainText(text).trim();
                                if (!StringUtils.isEmpty(text) && !"阅".equals(text) &&
                                        !"已阅".equals(text) && !"阅。".equals(text) &&
                                        !"已阅。".equals(text))
                                {
                                    extractMessage(item, (ParallelTextData) componentData, form);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    protected void extractMessage(ParallelTextItem item, ParallelTextData data, WebForm form) throws Exception
    {
        SystemFlowDao dao = getSystemFlowDao();
        FParallelText component = form.getComponent(data.getFullName());
        String shareType = component.getProperty("shareType");

        for (Integer userId : getMessageReceivers())
        {
            boolean b = true;

            if (SystemFormContext.DEPT.equals(shareType) || SystemFormContext.BUREAU.equals(shareType))
            {
                User user = null;
                try
                {
                    user = dao.getUser(userId);
                }
                catch (Exception ex)
                {
                    Tools.wrapException(ex);
                }

                if (SystemFormContext.DEPT.equals(shareType))
                {
                    //只要和操作者在同一个部门就可写
                    Integer deptId = getBusinessContext().getUserId();
                    b = false;
                    if (deptId != null)
                    {
                        for (Dept dept : user.getDepts())
                        {
                            if (dept.getDeptId().equals(deptId))
                            {
                                b = true;
                                break;
                            }
                        }
                    }
                }
                else if (SystemFormContext.BUREAU.equals(shareType))
                {
                    Integer bureauId = getBusinessContext().getBureauId();
                    b = false;
                    if (bureauId != null)
                    {
                        for (Dept dept : user.getDepts())
                        {
                            if (dept.getParentDept(1).getDeptId().equals(bureauId))
                            {
                                b = true;
                                break;
                            }
                        }
                    }
                }
            }

            if (b)
            {
                sendMessageToUser(item.getText(), userId);
            }
        }
    }

    protected void sendMessageToUser(String content, Integer userId) throws Exception
    {
        SystemFlowDao dao = getSystemFlowDao();

        content = Chinese.truncate(HtmlUtils.getPlainText(content), 800);

        FlowStepMessage message = dao.getMessage(stepId, userId, content);
        if (message != null)
            return;

        message = new FlowStepMessage();
        message.setUserId(userId);
        message.setContent(content);
        message.setStepId(stepId);
        message.setSender(getUserId());
        message.setSendTime(new Date());
        message.setInstanceId(getInstanceId());
        message.setReaded(false);

        dao.add(message);
    }

    protected List<Integer> getMessageReceivers() throws Exception
    {
        if (messageReceivers == null)
        {
            messageReceivers = new ArrayList<Integer>();
            List<Integer> noReceivers = new ArrayList<Integer>();

            String stepId = getStepId().toString();

            List<FlowStep> allSteps = getFlowContext().getService().getAllSteps();
            for (FlowStep step : allSteps)
            {
                try
                {
                    Integer userId = Integer.valueOf(step.getReceiver());

                    if (!userId.equals(getUserId()))
                    {
                        int state = step.getState();
                        if (state == FlowStep.NOACCEPT || state == FlowStep.BACKNOACCEPT ||
                                state == FlowStep.COPYNOACCEPT || state == FlowStep.PASSNOACCEPT ||
                                state == FlowStep.DEALED_REPLYED_NOACCEPT)
                        {
                            noReceivers.add(userId);
                        }
                        else if (stepId.equals(step.getPreStepId()))
                        {
                            noReceivers.add(userId);
                        }
                        else
                        {
                            messageReceivers.add(userId);
                        }
                    }
                }
                catch (NumberFormatException ex)
                {
                    //接收者不是整数，不是一个用于，跳过
                }
            }

            messageReceivers.removeAll(noReceivers);
        }

        return messageReceivers;
    }

    @SuppressWarnings("unchecked")
    protected void afterSendByComponent(Map<String, List<List<NodeReceiver>>> receiverMap) throws Exception
    {
        //调用扩展功能
        FlowComponent flowComponent = getFlowComponent();
        if (flowComponent != null)
            flowComponent.afterSend(getFlowComponentContext(), receiverMap);

        for (FlowExtension extension : getExtensions().values())
        {
            extension.afterSend(getFlowComponentContext(), receiverMap);
        }
    }

    @SuppressWarnings("unchecked")
    protected void endFlowByComponent() throws Exception
    {
        //调用扩展功能
        FlowComponent flowComponent = getFlowComponent();
        if (flowComponent != null)
            flowComponent.endFlow(getFlowComponentContext());

        for (FlowExtension extension : getExtensions().values())
        {
            extension.endFlow(getFlowComponentContext());
        }
    }


    /**
     * 判断是否提交到结束环节
     *
     * @param receiverMap 提交到的下环节及对应的接收者信息
     * @return 如果提交到结束环节返回true，否则返回false
     */
    protected boolean isEnd(Map<String, List<List<NodeReceiver>>> receiverMap)
    {
        return receiverMap != null && receiverMap.containsKey(FlowNode.END);
    }

    /**
     * 初始化接收者，可以对客户端提交过来的接收者进行过滤，转化等
     *
     * @param receiverSelects 接收者列表
     * @return 过滤转化后的接收者列表
     * @throws Exception 转化过程中发生的错误，一般为数据库错误，子类可以自行扩展
     */
    protected List<NodeReceiverSelect> initReceiverSelectList(List<NodeReceiverSelect> receiverSelects) throws Exception
    {
        FlowApi.initReceiversWithApp(receiverSelects);
        FlowApi.initReceiverName(receiverSelects);
        setConsignationInfo(receiverSelects);

        return receiverSelects;
    }

    /**
     * 设置委托信息，子类如果不需要委托可覆盖掉此方法
     *
     * @param receivers 接收者列表
     * @throws Exception 设置委托信息错误，由数据库错误引起
     */
    protected void setConsignationInfo(List<? extends NodeReceiverInfo> receivers) throws Exception
    {
        //初始化接收者，设置委托信息
        FlowApi.setConsignationInfo(receivers, getConsignationModules());
    }

    protected String[] getConsignationModules() throws Exception
    {
        return new String[]{getFlowTag()};
    }

    /**
     * 转办
     *
     * @param receiverList 接收者列表
     * @throws Exception 发送错误，可能是数据库错误，也可能是执行脚本错误
     */
    @Service(url = "/{@class}/{stepId}.turn", method = HttpMethod.post)
    @Transactional
    public void turn(List<NodeReceiver> receiverList) throws Exception
    {
        lockInstance();

        FlowContext context = getFlowContext();

        receiverList = initTurnReceiverSelectList(receiverList);

        Object result = beforeTurn(receiverList);
        if (result != null)
        {
            //beforeTurn返回结果，中断抄送逻辑
            setResult(result);
            return;
        }

        result = context.turn(receiverList);

        makeDeptLast();

        Object r = afterTurn(receiverList, result);
        if (r != null)
            result = r;

        if (result == null)
        {
            result = new NodeReceiverList(receiverList);
        }

        setResult(result);

    }

    /**
     * 转办之前执行的代码
     *
     * @param receiverList 接收者列表
     * @return 返回null表示继续执行发送动作，否则终止执行，并将结果返回
     * @throws Exception 允许子类抛出异常
     */
    @SuppressWarnings("UnusedDeclaration")
    protected Object beforeTurn(List<NodeReceiver> receiverList) throws Exception
    {
        return null;
    }

    /**
     * 转办之后执行的代码
     *
     * @param receiverList 接收者列表
     * @param result       提交动作执行的结果
     * @return 返回给客户端的结果
     * @throws Exception 允许子类抛出异常
     */
    @SuppressWarnings("UnusedDeclaration")
    protected Object afterTurn(List<NodeReceiver> receiverList, Object result) throws Exception
    {
        extractMessageFromForm();

        //短信通知
        notify(receiverList, getNodeId(), "turn", null);

        return result;
    }

    /**
     * 初始化转办的接收者，可以对客户端提交过来的接收者进行过滤，转化等
     *
     * @param receivers 接收者列表
     * @return 过滤转化后的接收者列表
     * @throws Exception 转化过程中发生的错误，一般为数据库错误，子类可以自行扩展
     */
    protected List<NodeReceiver> initTurnReceiverSelectList(List<NodeReceiver> receivers) throws Exception
    {
        FlowApi.initReceiverName(receivers);
        setConsignationInfo(receivers);

        return receivers;
    }

    /**
     * 抄送
     *
     * @param receiverList 接收者列表
     * @throws Exception 发送错误，可能是数据库错误，也可能是执行脚本错误
     */
    @Service(url = "/{@class}/{stepId}.copy", method = HttpMethod.post)
    @Transactional
    public void copy(List<NodeReceiver> receiverList) throws Exception
    {
        lockInstance();

        FlowContext context = getFlowContext();

        receiverList = initCopyReceiverSelectList(receiverList);

        Object result = beforeCopy(receiverList);
        if (result != null)
        {
            //beforeCopy返回结果，中断抄送逻辑
            setResult(result);
            return;
        }

        result = context.copy(receiverList);
        makeDeptLast();

        Object r = afterCopy(receiverList, result);
        if (r != null)
            result = r;

        if (result == null)
        {
            result = new NodeReceiverList(receiverList);
        }

        setResult(result);
    }

    /**
     * 抄送之前执行的代码
     *
     * @param receiverList 接收者列表
     * @return 返回null表示继续执行发送动作，否则终止执行，并将结果返回
     * @throws Exception 允许子类抛出异常
     */
    @SuppressWarnings("UnusedDeclaration")
    protected Object beforeCopy(List<NodeReceiver> receiverList) throws Exception
    {
        beforeSendByComponent(Collections.singletonMap("#copy", Collections.singletonList(receiverList)));

        return null;
    }

    /**
     * 抄送之后执行的代码
     *
     * @param receiverList 接收者列表
     * @param result       提交动作执行的结果
     * @return 返回给客户端的结果
     * @throws Exception 允许子类抛出异常
     */
    @SuppressWarnings("UnusedDeclaration")
    protected Object afterCopy(List<NodeReceiver> receiverList, Object result) throws Exception
    {
        extractMessageFromForm();

        afterSendByComponent(Collections.singletonMap("#copy", Collections.singletonList(receiverList)));

        //这里可以加入调用发送消息的代码
        notify(receiverList, "#copy", "copy", null);

        return result;
    }

    /**
     * 初始化抄送的接收者，可以对客户端提交过来的接收者进行过滤，转化等
     *
     * @param receivers 接收者列表
     * @return 过滤转化后的接收者列表
     * @throws Exception 转化过程中发生的错误，一般为数据库错误，子类可以自行扩展
     */
    protected List<NodeReceiver> initCopyReceiverSelectList(List<NodeReceiver> receivers) throws Exception
    {
        FlowApi.initReceiverName(receivers);
        setConsignationInfo(receivers);

        return receivers;
    }

    /**
     * 传阅
     *
     * @param receiverList 接收者列表
     * @throws Exception 发送错误，可能是数据库错误，也可能是执行脚本错误
     */
    @Service(url = "/{@class}/{stepId}.pass", method = HttpMethod.post)
    @Transactional
    public void pass(List<NodeReceiver> receiverList) throws Exception
    {
        lockInstance();

        FlowContext context = getFlowContext();

        receiverList = initPassReceiverSelectList(receiverList);

        Object result = beforePass(receiverList);
        if (result != null)
        {
            //beforePass返回结果，中断传阅逻辑
            setResult(result);
            return;
        }

        result = context.pass(receiverList);

        Object r = afterPass(receiverList, result);
        if (r != null)
            result = r;

        setResult(result);
    }

    /**
     * 传阅之前执行的代码
     *
     * @param receiverList 接收者列表
     * @return 返回null表示继续执行发送动作，否则终止执行，并将结果返回
     * @throws Exception 允许子类抛出异常
     */
    @SuppressWarnings("UnusedDeclaration")
    protected Object beforePass(List<NodeReceiver> receiverList) throws Exception
    {
        beforeSendByComponent(Collections.singletonMap("#pass", Collections.singletonList(receiverList)));

        return null;
    }

    /**
     * 传阅之后执行的代码
     *
     * @param receiverList 接收者列表
     * @param result       提交动作执行的结果
     * @return 返回给客户端的结果
     * @throws Exception 允许子类抛出异常
     */
    @SuppressWarnings("UnusedDeclaration")
    protected Object afterPass(List<NodeReceiver> receiverList, Object result) throws Exception
    {
        extractMessageFromForm();

        afterSendByComponent(Collections.singletonMap("#pass", Collections.singletonList(receiverList)));

        //这里可以加入调用发送消息的代码
        notify(receiverList, "#pass", "pass", null);

        return result;
    }

    /**
     * 初始化传阅的接收者，可以对客户端提交过来的接收者进行过滤，转化等
     *
     * @param receivers 接收者列表
     * @return 过滤转化后的接收者列表
     * @throws Exception 转化过程中发生的错误，一般为数据库错误，子类可以自行扩展
     */
    protected List<NodeReceiver> initPassReceiverSelectList(List<NodeReceiver> receivers) throws Exception
    {
        //默认传阅不启用委托功能

        return receivers;
    }

    /**
     * 抄送或传阅回复
     *
     * @throws Exception 回复错误，可能是数据库错误，也可能是执行脚本错误
     */
    @Service(url = "/{@class}/{stepId}.reply", method = HttpMethod.post)
    @Transactional
    @SuppressWarnings("unchecked")
    public void reply() throws Exception
    {
        lockInstance();

        FlowContext context = getFlowContext();

        Object result = beforeSend(null, "reply");

        if (result != null)
        {
            //beforeSend返回结果，中断发送逻辑
            setResult(result);
            return;
        }

        result = context.reply();

        Object r = afterSend(null, "reply", result);
        if (r != null)
            result = r;

        setResult(result);
    }

    /**
     * 完成之前执行的代码
     *
     * @return 返回null表示继续执行发送动作，否则终止执行，并将结果返回
     * @throws Exception 允许子类抛出异常
     */
    protected Object beforeEnd() throws Exception
    {
        return null;
    }

    /**
     * 完成之后执行的代码
     *
     * @param result 提交动作执行的结果
     * @return 返回给客户端的结果
     * @throws Exception 允许子类抛出异常
     */
    @SuppressWarnings("UnusedDeclaration")
    protected Object afterEnd(Object result) throws Exception
    {
        //这里可以加入调用发送消息的代码

        extractMessageFromForm();

        return result;
    }

    /**
     * 终止之前执行的代码
     *
     * @return 返回null表示继续执行发送动作，否则终止执行，并将结果返回
     * @throws Exception 允许子类抛出异常
     */
    protected Object beforeStop() throws Exception
    {
        beforeStopByComponent();

        return null;
    }

    @SuppressWarnings("unchecked")
    protected void beforeStopByComponent() throws Exception
    {
        //调用扩展功能
        FlowComponent flowComponent = getFlowComponent();
        if (flowComponent != null)
            flowComponent.beforeStopFlow(getFlowComponentContext());

        for (FlowExtension extension : getExtensions().values())
        {
            extension.beforeStopFlow(getFlowComponentContext());
        }
    }

    /**
     * 终止之后执行的代码
     *
     * @param result 提交动作执行的结果
     * @return 返回给客户端的结果
     * @throws Exception 允许子类抛出异常
     */
    @SuppressWarnings("UnusedDeclaration")
    protected Object afterStop(Object result) throws Exception
    {
        afterStopByComponent();

        return result;
    }

    @SuppressWarnings("unchecked")
    protected void afterStopByComponent() throws Exception
    {
        //调用扩展功能
        FlowComponent flowComponent = getFlowComponent();
        if (flowComponent != null)
            flowComponent.stopFlow(getFlowComponentContext());

        for (FlowExtension extension : getExtensions().values())
        {
            extension.stopFlow(getFlowComponentContext());
        }
    }

    /**
     * 完成
     *
     * @throws Exception 完成错误，可能是数据库错误，也可能是执行脚本错误
     */
    @Service(url = "/{@class}/{stepId}.end", method = HttpMethod.post)
    @Transactional
    @SuppressWarnings("unchecked")
    public void end() throws Exception
    {
        FlowContext context = getFlowContext();

        Object result = beforeEnd();

        if (result != null)
        {
            //beforeEnd返回结果，中断发送逻辑
            setResult(result);
            return;
        }

        result = context.end();

        Object r = afterEnd(result);
        if (r != null)
            result = r;

        saveContext();

        setResult(result);
    }

    /**
     * 终止流程
     *
     * @throws Exception 终止错误，可能是数据库错误，也可能是执行脚本错误
     */
    @Service(url = "/{@class}/{stepId}.stop", method = HttpMethod.post)
    @Transactional
    @SuppressWarnings("unchecked")
    public void stop() throws Exception
    {
        lockInstance();

        FlowContext context = getFlowContext();

        Object result = beforeStop();

        if (result != null)
        {
            //beforeSend返回结果，中断发送逻辑
            setResult(result);
            return;
        }

        result = context.stop();

        Object r = afterStop(result);
        if (r != null)
            result = r;

        setResult(result);
    }

    /**
     * 打回
     *
     * @param remark 打回的原因和说明
     * @throws Exception 退回错误，可能是数据库错误，也可能是执行脚本错误
     */
    @Service(url = "/{@class}/{stepId}.back", method = HttpMethod.post)
    @Transactional
    public void back(String[] preStepIds, String remark) throws Exception
    {
        lockInstance();

        FlowContext context = getFlowContext();

        Object result = beforeBack(preStepIds, remark);
        if (result != null)
        {
            //beforeBack返回结果，中断传阅逻辑
            setResult(result);
            return;
        }

        result = context.back(preStepIds);

        FlowStepBack stepBack = new FlowStepBack();
        stepBack.setStepId(getStepId());
        stepBack.setRemark(remark);
        stepBack.setBackTime(new Date());
        stepBack.setUserId(getUserId());
        getSystemFlowDao().add(stepBack);

        Object r = afterBack(result, preStepIds, remark);
        if (r != null)
            result = r;

        setResult(result);
    }

    /**
     * 退回之前执行的代码
     *
     * @param remark 退回的意见
     * @return 返回null表示继续执行退回动作，否则终止执行，并将结果返回
     * @throws Exception 允许子类抛出异常
     */
    @SuppressWarnings("UnusedDeclaration")
    protected Object beforeBack(String[] preStepIds, String remark) throws Exception
    {
        return null;
    }

    /**
     * 退回之后执行的代码
     *
     * @param result 提交动作执行的结果
     * @param remark 退回的意见
     * @return 返回给客户端的结果
     * @throws Exception 允许子类抛出异常
     */
    @SuppressWarnings("UnusedDeclaration")
    protected Object afterBack(Object result, String[] preStepIds, String remark) throws Exception
    {
        //这里可以加入调用发送消息的代码

        FlowContext context = getFlowContext();
        List<FlowStep> steps;
        if (preStepIds == null)
        {
            steps = context.getPreSteps();
        }
        else
        {
            FlowService service = context.getService();
            steps = new ArrayList<FlowStep>(preStepIds.length);
            for (String preStepId : preStepIds)
            {
                steps.add(service.getStep(preStepId));
            }
        }

        List<NodeReceiver> receiverList = new ArrayList<NodeReceiver>(steps.size());
        for (FlowStep step : steps)
        {
            NodeReceiver receiver =
                    new NodeReceiver(step.getProperty("dealer").toString(), step.getDealerName());
            receiver.setStepId(step.getStepId());
            receiverList.add(receiver);
        }

        extractMessageFromForm();
        notify(receiverList, "#back", "back", remark);

        if (result == null)
            result = new NodeReceiverList(receiverList);
        return result;
    }

    /**
     * 撤回
     *
     * @throws Exception 退回错误，可能是数据库错误，也可能是执行脚本错误
     */
    @Service(url = "/{@class}/{stepId}.cancelSend", method = HttpMethod.post)
    @Transactional
    public void cancelSend() throws Exception
    {
        lockInstance();

        FlowContext context = getFlowContext();

        Object result = beforeCancelSend();
        if (result != null)
        {
            //beforePass返回结果，中断传阅逻辑
            setResult(result);
            return;
        }

        result = context.cancelSend();

        Object r = afterCancelSend(result);
        if (r != null)
            result = r;

        setResult(result);
    }

    /**
     * 撤回之前执行的代码
     *
     * @return 返回null表示继续执行撤回动作，否则终止执行，并将结果返回
     * @throws Exception 允许子类抛出异常
     */
    protected Object beforeCancelSend() throws Exception
    {
        return null;
    }

    /**
     * 撤回之后执行的代码
     *
     * @param result 提交动作执行的结果
     * @return 返回给客户端的结果
     * @throws Exception 允许子类抛出异常
     */
    @SuppressWarnings("UnusedDeclaration")
    protected Object afterCancelSend(Object result) throws Exception
    {
        //这里可以加入调用发送消息的代码

        return result;
    }

    @Service(url = "/{@class}/{stepId}/receiver")
    @Forward(page = "/platform/flow/receiverselect.ptl")
    public String receiverSelect(String actionId) throws Exception
    {
        this.actionId = actionId;
        return null;
    }

    @NotSerialized
    public List<FlowStep> getPreSteps() throws Exception
    {
        return getFlowContext().getPreSteps();
    }

    @NotSerialized
    @Service(url = "/{@class}/{stepId}/preStepsCount")
    public int getPreStepsCount() throws Exception
    {
        return getPreSteps().size();
    }

    @Service(url = "/{@class}/{stepId}/preSteps")
    @Forward(page = "/platform/flow/presteps.ptl")
    public String showPreSteps() throws Exception
    {
        return null;
    }

    protected String getUrl(Long stepId) throws Exception
    {
        return RequestContext.getContext().getFormClassInfo().getUrl() + "/" + stepId;
    }

    protected Action getAction(String actionId) throws Exception
    {
        if (actionId != null)
        {
            FlowNode node = getFlowContext().getNode();
            if (node != null)
            {
                return node.getAction(actionId);
            }
        }

        return null;
    }

    protected String getNotifyMessage(String nodeId, String actionId, String remark) throws Exception
    {
        return null;
    }

    protected String getNotifyMessage(String nodeId, String actionId) throws Exception
    {
        if (actionId != null)
        {
            Action action = getAction(actionId);
            if (action != null)
            {
                String message = action.getProperty(NOTIFY_MESSAGE);
                if (!StringUtils.isEmpty(message))
                    return message;
            }
        }

        if (nodeId != null)
        {
            FlowNode node = getFlowContext().getFlow().getNode(nodeId);
            if (node != null)
            {
                String message = node.getProperty(NOTIFY_MESSAGE);
                if (!StringUtils.isEmpty(message))
                    return message;
            }
        }

        return null;
    }

    protected void notify(List<NodeReceiver> receivers, String nodeId, String actionId, String remark) throws Exception
    {
        if (!isTest())
        {
            for (NodeReceiver receiver : receivers)
            {
                notify(receiver, nodeId, actionId, remark);
            }
        }
    }

    protected void notify(NodeReceiver receiver, String nodeId, String actionId, String remark) throws Exception
    {
        String[] methods = getNotifyMethods(nodeId, actionId);

        if (methods == null || methods.length > 0)
        {
            for (Integer userId : FlowApi.getUserIds(receiver.getReceiver()))
            {
                if (!getUserId().equals(userId))
                {
                    String content = getNotifyMessage(nodeId, actionId, remark);

                    if (content != null)
                    {
                        Message message = new Message();

                        message.setSender(getUserId());
                        message.setUserId(userId);
                        message.setMessage(content);
                        message.setUrl(getUrl(Long.valueOf(receiver.getStepId())));
                        message.setMethods(methods);
                        message.setApp(getNotifyApp(nodeId, actionId));

                        Boolean smsNotify = isSmsNotify();
                        if (smsNotify != null && smsNotify)
                            message.setForce(true);

                        message.send();
                    }
                }
            }
        }
    }

    protected String[] getNotifyMethods(String nodeId, String actionId) throws Exception
    {
        if (actionId != null)
        {
            String[] methods = getNotifyMethods(getAction(actionId));
            if (methods != null)
            {
                return methods;
            }
        }

        if (nodeId != null)
        {
            FlowNode node = getFlowContext().getFlow().getNode(nodeId);
            String[] methods = getNotifyMethods(node);
            if (methods != null)
            {
                return methods;
            }
        }

        return getDefaultNotifyMethods(nodeId, actionId);
    }

    protected String[] getNotifyMethods(FlowProperty property) throws Exception
    {
        if (property != null)
        {
            if ("false".equals(property.getProperty("notify")))
                return new String[]{};

            String s = property.getProperty("notifyMethod");
            if (!StringUtils.isEmpty(s))
                return s.split(",");
        }

        return null;
    }


    @SuppressWarnings("UnusedDeclaration")
    protected String[] getDefaultNotifyMethods(String nodeId, String actionId) throws Exception
    {
        Boolean smsNotify = isSmsNotify();

        if (smsNotify == null)
        {
            return null;
        }
        else
        {
            if (smsNotify)
            {
                if (FlowNode.PASS.equals(nodeId))
                    return null;
                else
                    return new String[]{ImMessageSender.IM, SmsMessageSender.SMS};
            }
            else
            {
                return new String[]{ImMessageSender.IM};
            }
        }
    }

    @NotSerialized
    public Boolean isSmsNotify() throws Exception
    {
        SystemFormContext context = getFormContext();

        if (context != null)
        {
            if (context.getForm().getComponent("短信通知") != null)
            {
                String sms = context.getFormData().getString("短信通知");
                return "是".equals(sms) || "true".equals(sms) || "1".equals(sms);
            }
        }

        return null;
    }

    protected String getNotifyApp(String nodeId, String actionId) throws Exception
    {
        return getFlowTag();
    }

    public FlowExtension getFlowExtension(String name) throws Exception
    {
        return getExtensions().get(name);
    }

    protected Class<? extends FlowComponent> getFlowComponentType() throws Exception
    {
        return null;
    }

    protected FlowComponent getFlowComponent() throws Exception
    {
        if (flowComponent == null)
        {
            flowComponent = createFlowComponent();
        }

        return flowComponent;
    }

    protected FlowComponent createFlowComponent() throws Exception
    {
        Class<? extends FlowComponent> componentType = getFlowComponentType();
        if (componentType != null)
            return Tools.getBean(componentType);

        if (flowComponentProviders != null)
        {
            for (FlowComponentProvider provider : flowComponentProviders)
            {
                FlowComponent component = provider.getComponent(getFlowComponentContext());
                if (component != null)
                    return component;
            }
        }

        return null;
    }

    public boolean containsExtension(String name) throws Exception
    {
        return getExtensions().containsKey(name);
    }

    public Map<String, FlowExtension> getExtensions() throws Exception
    {
        if (extensions == null)
        {
            extensions = new HashMap<String, FlowExtension>();

            if (extensionFactories != null)
            {
                for (Map.Entry<String, FlowExtensionFactory> entry : extensionFactories.entrySet())
                {
                    FlowExtension extension = entry.getValue().getExtension(getFlowComponentContext());
                    if (extension != null)
                    {
                        extension.initExtension(getFlowComponentContext());
                        extensions.put(entry.getKey(), extension);
                    }
                }
            }
        }

        return extensions;
    }

    protected FlowComponentContext getFlowComponentContext() throws Exception
    {
        if (flowComponentContext == null)
        {
            flowComponentContext = new FlowComponentContext()
            {
                public FlowContext getFlowContext() throws Exception
                {
                    return FlowPage.this.getFlowContext();
                }

                public SystemFormContext getFormContext() throws Exception
                {
                    return FlowPage.this.getFormContext();
                }

                public SystemFormContext getFormContext(String name) throws Exception
                {
                    return FlowPage.this.getFormContext(name);
                }

                public BusinessContext getBusinessContext() throws Exception
                {
                    return FlowPage.this.getBusinessContext();
                }

                public UserInfo getUserInfo() throws Exception
                {
                    return getBusinessContext().getUser();
                }

                public Integer getUserId() throws Exception
                {
                    return getBusinessContext().getUserId();
                }

                public Integer getBusinessDeptId() throws Exception
                {
                    return FlowPage.this.getBusinessDeptId();
                }

                @Override
                public Long getInstanceId() throws Exception
                {
                    return FlowPage.this.getInstanceId();
                }

                @Override
                public Long getStepId() throws Exception
                {
                    return FlowPage.this.getStepId();
                }

                public Object getParameter(String name) throws Exception
                {
                    return RequestContext.getContext().getRequest().getParameter(name);
                }

                public Object[] getParameterValues(String name) throws Exception
                {
                    return RequestContext.getContext().getRequest().getParameterValues(name);
                }

                @Override
                public FlowPage getFlowPage() throws Exception
                {
                    return FlowPage.this;
                }

                @Override
                public String getTitle() throws Exception
                {
                    return getFlowContext().getInstance().getTitle();
                }

                @Override
                public void setTitle(String title) throws Exception
                {
                    getFlowContext().getInstance().setTitle(title);
                }
            };
        }

        return flowComponentContext;
    }

    public List<String> getJsFiles() throws Exception
    {
        List<String> jsFiles = null;

        FlowComponent component = getFlowComponent();

        if (component != null)
        {
            String jsFile = component.getJsFile();

            if (jsFile != null)
            {
                jsFiles = new ArrayList<String>();
                jsFiles.add(jsFile);
            }
        }

        for (FlowExtension extension : getExtensions().values())
        {
            String jsFile = extension.getJsFile();

            if (jsFile != null)
            {
                if (jsFiles == null)
                    jsFiles = new ArrayList<String>();
                jsFiles.add(jsFile);
            }
        }

        return jsFiles;
    }

    public String getClassName()
    {
        return BeanUtils.getRealClass(getClass()).getName();
    }

    @Override
    @Service(url = {"/{@class}/{stepId}/form/{formName}/{componentName}/{id}/image",
            "/{@class}/{stepId}/component/{componentName}/{id}/image"})
    public byte[] downParallelTextImage(String formName, String componentName, String id) throws Exception
    {
        return super.downParallelTextImage(formName, componentName, id);
    }

    @Transactional
    @Override
    @Service(url = {"/{@class}/{stepId}/form/{formName}/{componentName}/{id}/thumb",
            "/{@class}/{stepId}/component/{componentName}/{id}/thumb"})
    public byte[] downParallelTextThumbImage(String formName, String componentName, String id) throws Exception
    {
        return super.downParallelTextThumbImage(formName, componentName, id);
    }

    @Transactional
    @Override
    @Service(url = {"/{@class}/{stepId}/form/{formName}/{componentName}/{id}/thumb1",
            "/{@class}/{stepId}/component/{componentName}/{id}/thumb1"})
    public byte[] downParallelTextThumbImage1(String formName, String componentName, String id) throws Exception
    {
        return super.downParallelTextThumbImage(formName, componentName, id);
    }

    @Transactional
    @Override
    @Service(url = {"/{@class}/{stepId}/form/{formName}/{componentName}/{id}/thumb2",
            "/{@class}/{stepId}/component/{componentName}/{id}/thumb2"})
    public byte[] downParallelTextThumbImage2(String formName, String componentName, String id) throws Exception
    {
        return super.downParallelTextThumbImage(formName, componentName, id);
    }

    @Override
    @Service(url = {"/{@class}/{stepId}/form/{formName}/{componentName}/{id}/originalImage",
            "/{@class}/{stepId}/component/{componentName}/{id}/originalImage"})
    public byte[] downParallelTextOriginalImage(String formName, String componentName, String id) throws Exception
    {
        return super.downParallelTextOriginalImage(formName, componentName, id);
    }

    protected void makeDeptLast() throws Exception
    {
        SystemFlowDao dao = getSystemFlowDao();
        dao.makeDeptLast(getInstanceId());
    }
}