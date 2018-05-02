package com.gzzm.ods.flow;

import com.gzzm.ods.business.BusinessModel;
import com.gzzm.ods.dict.Tag;
import com.gzzm.ods.document.*;
import com.gzzm.ods.documenttemplate.DocumentTemplate;
import com.gzzm.ods.exchange.*;
import com.gzzm.ods.print.*;
import com.gzzm.ods.timeout.*;
import com.gzzm.platform.attachment.Attachment;
import com.gzzm.platform.barcode.*;
import com.gzzm.platform.commons.*;
import com.gzzm.platform.commons.filestore.FileStoreService;
import com.gzzm.platform.flow.*;
import com.gzzm.platform.form.SystemFormContext;
import com.gzzm.platform.organ.UserInfo;
import com.gzzm.platform.recent.Recent;
import com.gzzm.platform.template.TemplateInput;
import com.gzzm.platform.timeout.TimeoutService;
import com.gzzm.platform.weboffice.OfficeEditType;
import net.cyan.arachne.*;
import net.cyan.arachne.annotation.*;
import net.cyan.arachne.exts.ParameterCheck;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.*;
import net.cyan.commons.util.io.Mime;
import net.cyan.nest.annotation.Inject;
import net.cyan.valmiki.flow.*;
import net.cyan.valmiki.flow.Script;
import net.cyan.valmiki.form.*;
import net.cyan.valmiki.form.components.FCustomComponent;
import net.cyan.valmiki.form.components.list.MissingListModel;

import java.util.*;

/**
 * 公文流转的流程操作页面
 *
 * @author camel
 * @date 11-8-31
 */
@Service
public abstract class OdFlowPage extends FlowPage implements TextFlowElement
{
    static
    {
        ParameterCheck.addNoCheckURL("/od/*");
        ParameterCheck.addNoCheckURL("/ods/*");
    }

    @Inject
    protected static Provider<PrintService> printServiceProvider;

    @Inject
    private Provider<ExchangeReceiveService> receiveServiceProvider;

    @Inject
    protected static Provider<ExchangeNotifyService> exchangeNotifyServiceProvider;

    @Inject
    private static Provider<FileStoreService> fileStoreServiceProvider;


    private int year;

    @Inject
    protected OdFlowService service;

    @Inject
    private OdSignService signService;

    @Inject
    private TimeoutService timeoutService;

    @Inject
    private OdTimeoutService odTimeoutService;

    /**
     * 公文流转实例
     */
    @NotSerialized
    private OdFlowInstance odFlowInstance;

    @NotSerialized
    private OfficeDocument document;

    /**
     * 正在显示其正文的文档
     */
    @NotSerialized
    private OfficeDocument textDocument;

    /**
     * 正在显示的正文的类型
     */
    protected String textType;

    /**
     * 文档是否可编辑
     */
    protected boolean textEditable = true;

    /**
     * 文档的编辑类型
     */
    private OfficeEditType textEditType;

    /**
     * 文档格式
     */
    private String textFormat;

    /**
     * 正文，接收客户端传过来的文件
     */
    @NotSerialized
    protected InputFile text;

    /**
     * 正在编辑的用户的名称
     */
    private String editUserName;

    private PrintService printService;

    private Integer printTemplateId;

    @NotSerialized
    private PrintTemplate printTemplate;

    private String printFormName;

    private String printShowName;

    private OdFlowContext odFlowContext;

    @NotSerialized
    private ReceiveBase receiveBase;

    protected Integer receiveTypeId;

    private Long collectInstanceId;

    @Store(scope = "user", name = "officeType")
    private String officeType;

    public OdFlowPage()
    {
    }

    public int getYear()
    {
        return year;
    }

    public void setYear(int year)
    {
        this.year = year;
    }

    @Override
    @NotSerialized
    public UserInfo getUserInfo() throws Exception
    {
        return getBusinessContext().getUser();
    }

    @Override
    protected SystemFlowDao createSystemFlowDao() throws Exception
    {
        return OdSystemFlowDao.getInstance(year);
    }

    public Integer getReceiveTypeId() throws Exception
    {
        if (receiveTypeId == null)
        {
            OdFlowInstance odFlowInstance = getOdFlowInstance();
            if (odFlowInstance != null)
                receiveTypeId = odFlowInstance.getReceiveTypeId();
        }
        return receiveTypeId;
    }

    public void setReceiveTypeId(Integer receiveTypeId)
    {
        this.receiveTypeId = receiveTypeId;
    }

    public Long getCollectInstanceId()
    {
        return collectInstanceId;
    }

    public void setCollectInstanceId(Long collectInstanceId)
    {
        this.collectInstanceId = collectInstanceId;
    }

    public String getOfficeType()
    {
        return officeType;
    }

    public void setOfficeType(String officeType)
    {
        this.officeType = officeType;
    }

    @NotSerialized
    public OdFlowDao getDao()
    {
        return service.getDao();
    }

    public OdFlowInstance getOdFlowInstance() throws Exception
    {
        if (odFlowInstance == null)
        {
            odFlowInstance = getDao().getOdFlowInstance(getInstanceId());
        }

        return odFlowInstance;
    }

    @NotSerialized
    public BusinessModel getBusiness() throws Exception
    {
        return getOdFlowInstance().getBusiness();
    }

    public Integer getBusinessId() throws Exception
    {
        return getOdFlowInstance().getBusinessId();
    }

    @NotSerialized
    public OfficeDocument getDocument() throws Exception
    {
        if (document == null)
        {
            document = getOdFlowInstance().getDocument();
        }

        return document;
    }

    public boolean hasDocument() throws Exception
    {
        return getDocument() != null && getDocument().getTextId() != null;
    }

    protected List<OfficeDocument> getDocuments() throws Exception
    {
        OfficeDocument document = getDocument();

        return document == null ? null : Collections.singletonList(document);
    }

    @Service(url = "/{@class}/{stepId}/storeTo/{$0}", method = HttpMethod.post)
    @Transactional
    @ObjectResult
    public void storeTo(String target) throws Exception
    {
        List<OfficeDocument> documents = getDocuments();
        if (documents != null)
        {
            FileStoreService fileStoreService = fileStoreServiceProvider.get();

            for (OfficeDocument document : documents)
            {
                DocumentText text = document.getText();
                if (text != null && text.getTextBody() != null)
                {
                    fileStoreService.save(
                            new InputFile(text.getTextBody(), document.getTitle() + "." +
                                    (StringUtils.isEmpty(text.getType()) ? "doc" : text.getType())),
                            getBusinessContext().getUserId(), getBusinessDeptId(), target, "od_text", ""
                    );
                }

                SortedSet<Attachment> attachments = document.getAttachments();
                if (attachments != null)
                {
                    for (Attachment attachment : attachments)
                    {
                        fileStoreService.save(attachment.getInputFile(), getBusinessContext().getUserId(),
                                getBusinessDeptId(), target, "od_attachment", "");
                    }
                }

                for (PrintInfo printInfo : getPrintTemplates())
                {
                    fileStoreService.save(downFormPrint(printInfo.getTemplateId(), printInfo.getFormName(), true),
                            getBusinessContext().getUserId(), getBusinessDeptId(), target, "od_form", "");
                }
            }
        }
    }

    public Long getDocumentId() throws Exception
    {
        return getOdFlowInstance().getDocumentId();
    }

    public String getEncodedDocumentId() throws Exception
    {
        return OfficeDocument.encodeId(getDocumentId());
    }

    public String getType() throws Exception
    {
        return getOdFlowInstance().getType();
    }

    @Override
    @NotSerialized
    public String getTitle() throws Exception
    {
        return getDocument().getTitle();
    }

    public OfficeDocument getTextDocument()
    {
        return textDocument;
    }

    public Long getTextDocumentId()
    {
        return textDocument != null ? textDocument.getDocumentId() : null;
    }

    public String getEncodedTextDocumentId()
    {
        return textDocument != null ? textDocument.getEncodedId() : null;
    }

    @Override
    public void putTextDocument(OfficeDocument textDocument)
    {
        this.textDocument = textDocument;
    }

    public String getTextTitle()
    {
        return TextFlowUtils.getTextTitle(this);
    }

    public String getTextType()
    {
        return textType;
    }

    public void setTextType(String textType)
    {
        this.textType = textType;
    }

    public boolean isTextEditable()
    {
        return textEditable;
    }

    public void setTextEditable(boolean textEditable)
    {
        this.textEditable = textEditable;
    }

    public OfficeEditType getTextEditType()
    {
        return textEditType;
    }

    @Override
    public void putTextEditType(OfficeEditType textEditType)
    {
        this.textEditType = textEditType;
    }

    public String getTextFormat()
    {
        return textFormat;
    }

    public void setTextFormat(String textFormat)
    {
        this.textFormat = textFormat;
    }

    public InputFile getText()
    {
        return text;
    }

    public void setText(InputFile text)
    {
        this.text = text;
    }

    @Override
    public String getEditUserName()
    {
        return editUserName;
    }

    @Override
    public void putEditUserName(String editUserName)
    {
        this.editUserName = editUserName;
    }

    protected PrintService getPrintService()
    {
        if (printService == null)
            printService = printServiceProvider.get();

        return printService;
    }

    public Integer getPrintTemplateId()
    {
        return printTemplateId;
    }

    public void setPrintTemplateId(Integer printTemplateId)
    {
        this.printTemplateId = printTemplateId;
    }

    public String getPrintFormName()
    {
        return printFormName;
    }

    public void setPrintFormName(String printFormName)
    {
        this.printFormName = printFormName;
    }

    public String getPrintShowName()
    {
        return printShowName;
    }

    public void setPrintShowName(String printShowName)
    {
        this.printShowName = printShowName;
    }

    public PrintTemplate getPrintTemplate() throws Exception
    {
        if (printTemplate == null && printTemplateId != null)
            printTemplate = getPrintService().getPrintTemplate(printTemplateId);
        return printTemplate;
    }

    public int getQrLeft() throws Exception
    {
        PrintTemplate printTemplate = getPrintTemplate();
        if (printTemplate != null)
        {
            Integer qrLeft = printTemplate.getQrLeft();
            if (qrLeft != null)
                return qrLeft;
        }

        return 0;
    }

    public Integer getQrTop() throws Exception
    {
        PrintTemplate printTemplate = getPrintTemplate();
        if (printTemplate != null)
        {
            Integer qrTop = printTemplate.getQrTop();
            if (qrTop != null)
                return qrTop;
        }

        return null;
    }

    @Service
    @ObjectResult
    public String getLinkId() throws Exception
    {
        return getOdFlowInstance().getLinkId();
    }

    @Service
    @ObjectResult
    public void setLinkId(String linkId) throws Exception
    {
        OdFlowInstance instance = getOdFlowInstance();
        instance.setLinkId(linkId);

        getDao().update(instance);
    }

    @Override
    public boolean accept(Action action) throws Exception
    {
        if ("sendCollects".equals(action.getActionId()))
        {
            SystemFormContext formContext = getFormContext();
            if (formContext != null)
            {
                for (FormPage formPage : formContext.getForm().getPages())
                {
                    for (FormComponent component0 : formPage.getComponents())
                    {
                        for (FormComponent component : component0.getAllComponents())
                        {
                            if (component instanceof FCustomComponent &&
                                    getCollectComponentNames().contains(component.getName()))
                            {
                                return true;
                            }
                        }
                    }
                }
            }

            return false;
        }

        OdFlowInstanceState state = getOdFlowInstance().getState();

        return state != OdFlowInstanceState.deleted && super.accept(action);
    }

    @Override
    protected void initFlowContext(FlowContext flowContext) throws Exception
    {
        ScriptContext scriptContext = flowContext.getScriptContext();

        OdFlowInstance odFlowInstance = getOdFlowInstance();

        scriptContext.addVariableContainer(
                new MapVariableContainer(Collections.singletonMap("odInstance", (Object) odFlowInstance)));

        if (odFlowInstance.getState() == OdFlowInstanceState.deleted)
        {
            setReadOnly(true);
            flowContext.setEditable(false);
        }

        super.initFlowContext(flowContext);
    }

    public ReceiveBase getReceiveBase() throws Exception
    {
        if (receiveBase == null && getOdFlowInstance().getReceiveId() != null)
        {
            receiveBase = getDao().getReceiveBase(getOdFlowInstance().getReceiveId());
        }

        return receiveBase;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Class<? extends OdFlowComponent> getFlowComponentType() throws Exception
    {
        BusinessModel business = getOdFlowInstance().getBusiness();
        if (business != null)
        {
            String componentType = business.getComponentType();
            if (!StringUtils.isEmpty(componentType))
                return (Class<? extends OdFlowComponent>) Class.forName(componentType);
        }

        return null;
    }

    protected OdFlowContext getFlowComponentContext() throws Exception
    {
        if (odFlowContext == null)
        {
            odFlowContext = new OdFlowContext()
            {
                public OdFlowService getService() throws Exception
                {
                    return service;
                }

                public FlowContext getFlowContext() throws Exception
                {
                    return OdFlowPage.this.getFlowContext();
                }

                public SystemFormContext getFormContext() throws Exception
                {
                    return OdFlowPage.this.getFormContext();
                }

                public SystemFormContext getFormContext(String name) throws Exception
                {
                    return OdFlowPage.this.getFormContext(name);
                }

                public OdFlowInstance getOdFlowInstance() throws Exception
                {
                    return OdFlowPage.this.getOdFlowInstance();
                }

                public OfficeDocument getDocument() throws Exception
                {
                    return OdFlowPage.this.getDocument();
                }

                public OfficeDocument getDocument(String type) throws Exception
                {
                    return OdFlowPage.this.getDocument(type);
                }

                public BusinessContext getBusinessContext() throws Exception
                {
                    return OdFlowPage.this.getBusinessContext();
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
                    return OdFlowPage.this.getBusinessDeptId();
                }

                @Override
                public Long getInstanceId() throws Exception
                {
                    return OdFlowPage.this.getInstanceId();
                }

                @Override
                public Long getStepId() throws Exception
                {
                    return OdFlowPage.this.getStepId();
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
                public OdFlowPage getFlowPage() throws Exception
                {
                    return OdFlowPage.this;
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

        return odFlowContext;
    }

    public boolean isTextNew()
    {
        return TextFlowUtils.isTextNew(this);
    }

    @Override
    protected String getShowForward() throws Exception
    {
        return getType();
    }

    @Override
    protected void initFormContext(SystemFormContext formContext) throws Exception
    {
        super.initFormContext(formContext);

        MissingListModel.addOptionsProvider(formContext, DictOptionsProvider.INSTANCE);
    }

    /**
     * 收集数据
     *
     * @throws Exception 允许子类抛出异常
     */
    protected void extractOdData() throws Exception
    {
        FormContext formContext = getFormContext();
        if (formContext != null)
        {
            //有可能没有主表单，所以要先判断表单是否为空
            FormData formData = getFormContext().getFormData();
            OdFlowInstance odFlowInstance = getOdFlowInstance();

            //从表单提取编号
            ComponentData serialData = formData.getData(Constants.Flow.SERIAL);
            if (serialData != null)
                odFlowInstance.setSerial(serialData.toString());

            //从表单提取文件标志
            ComponentData tagData = formData.getData(Constants.Flow.TAG);
            if (tagData != null)
                odFlowInstance.setTag(tagData.toString());

            signService.initSign(odFlowInstance, formContext);

            Object deadline = formData.get(Constants.Flow.DEADLINE);
            if (deadline != null)
            {
                odFlowInstance.setDeadline(DataConvert.convertType(Date.class, deadline));
            }

            Object priority = formData.get(Constants.Document.PRIORITY);
            if (priority == null)
                priority = formData.get(Constants.Document.PRIORITY1);

            if (priority == null)
                priority = formData.get(Constants.Document.PRIORITY2);

            if (priority == null)
                priority = formData.get(Constants.Document.PRIORITY3);

            if (priority == null)
                priority = formData.get(Constants.Document.PRIORITY4);

            if (priority != null)
            {
                odFlowInstance.setPriority(DataConvert.toString(priority.toString()));
            }
        }

        if (receiveTypeId != null)
            odFlowInstance.setReceiveTypeId(receiveTypeId);
    }

    @Override
    public boolean accept() throws Exception
    {
        boolean b = super.accept();

        if (b)
        {
            refreshStepQ();
        }

        return b;
    }

    @Override
    protected boolean setRead() throws Exception
    {
        boolean b = super.setRead();

        if (b)
        {
            refreshStepQ();
        }

        return b;
    }

    /**
     * 保存数据
     *
     * @throws Exception 允许子类抛出异常
     */
    protected void saveOdData() throws Exception
    {
        //保存流程实例
        OdFlowInstance odFlowInstance = getOdFlowInstance();
        if (odFlowInstance.getState() == OdFlowInstanceState.notStarted)
            odFlowInstance.setState(OdFlowInstanceState.unclosed);

        saveDataByComponent();

        getDao().update(odFlowInstance);
    }

    /**
     * 流程结束之前的处理，一般为数据校验
     *
     * @throws Exception 允许子类抛出异常，同时也可以抛出SystemMessageException来中断流程
     * @see com.gzzm.platform.commons.SystemMessageException
     */
    protected void beforeEndOdFlow() throws Exception
    {
    }

    /**
     * 流程结束时的公文处理
     *
     * @param actionId 当前执行的动作
     * @return 执行结果
     * @throws Exception 允许子类抛出异常
     */
    protected Object endOdFlow(String actionId) throws Exception
    {
        //修改流程实例状态
        OdFlowInstance odFlowInstance = getOdFlowInstance();
        odFlowInstance.setState(OdFlowInstanceState.closed);
        odFlowInstance.setEndTime(new Date());

        getDao().update(odFlowInstance);

        ReceiveBase receiveBase = getReceiveBase();

        if (receiveBase != null && receiveBase.getState() != ReceiveState.end)
        {
            receiveBase.setState(ReceiveState.end);
            if (receiveBase.getEndTime() == null)
                receiveBase.setEndTime(new Date());
            getDao().update(receiveBase);
        }

        return null;
    }

    /**
     * 流程终止之前的处理
     *
     * @throws Exception 允许子类抛出异常，同时也可以抛出SystemMessageException来中断流程
     * @see com.gzzm.platform.commons.SystemMessageException
     */
    protected Object beforeStop() throws Exception
    {
        return super.beforeStop();
    }

    /**
     * 流程终止时的公文处理
     *
     * @return 执行结果
     * @throws Exception 允许子类抛出异常
     */
    protected Object afterStop(Object result) throws Exception
    {
        super.afterStop(result);

        //修改流程实例状态
        OdFlowInstance odFlowInstance = getOdFlowInstance();
        service.stopInstance(odFlowInstance, getUserId(), getBusinessContext().getDeptId());

        refreshStepQ();

        return null;
    }

    @Override
    protected Object beforeSave() throws Exception
    {
        //收集公文数据
        extractOdData();

        return super.beforeSave();
    }

    @Override
    protected Object afterSave(Object result) throws Exception
    {
        //保存公文数据
        saveOdData();

        refreshStepQ();

        odTimeoutService.checkInstance(getInstanceId());

        return result;
    }

    @Override
    protected Object beforeSend(Map<String, List<List<NodeReceiver>>> receiverMap, String actionId) throws Exception
    {
        Object result = super.beforeSend(receiverMap, actionId);

        if (result != null)
            return result;

        if (isEnd(receiverMap))
        {
            beforeEndOdFlow();
        }

        return null;
    }

    @Override
    protected Object afterTurn(List<NodeReceiver> receiverList, Object result) throws Exception
    {
        result = super.afterTurn(receiverList, result);

        Object r = afterSubmit(Collections.singletonMap(getNodeId(), Collections.singletonList(receiverList)));
        if (r != null)
            result = r;

        refreshStepQ();

        return result;
    }

    @Override
    protected Object afterSend(Map<String, List<List<NodeReceiver>>> receiverMap, String actionId, Object result)
            throws Exception
    {
        result = super.afterSend(receiverMap, actionId, result);

        Object r = afterSubmit(receiverMap);
        if (r != null)
            result = r;

        refreshStepQ();

        return result;
    }

    @Override
    protected Object afterEnd(Object result) throws Exception
    {
        Object ret = super.afterEnd(result);

        refreshStepQ();

        return ret;
    }

    @Override
    protected Object afterCopy(List<NodeReceiver> receiverList, Object result) throws Exception
    {
        Object ret = super.afterCopy(receiverList, result);

        refreshStepQ();

        return ret;
    }

    @Override
    protected Object afterPass(List<NodeReceiver> receiverList, Object result) throws Exception
    {
        Object ret = super.afterPass(receiverList, result);

        refreshStepQ();

        return ret;
    }

    protected Object afterSubmit(Map<String, List<List<NodeReceiver>>> receiverMap) throws Exception
    {
        //设置承办人和承办科室，deal属性为true的环节为承办环节，接收者为承办人
        if (receiverMap != null)
        {
            OdFlowInstance instance = getOdFlowInstance();

            for (Map.Entry<String, List<List<NodeReceiver>>> entry : receiverMap.entrySet())
            {
                List<List<NodeReceiver>> receiversList = entry.getValue();
                if (receiversList == null)
                    continue;

                String nodeId = entry.getKey();
                if (nodeId != null && !nodeId.startsWith("#"))
                {
                    FlowNode node = getFlowContext().getFlow().getNode(nodeId);
                    if (node != null && "true".equals(node.getProperty("deal")))
                    {
                        for (List<NodeReceiver> receivers : receiversList)
                        {
                            for (NodeReceiver receiver : receivers)
                            {
                                List<Integer> userIds = FlowApi.getUserIds(receiver.getReceiver());
                                if (userIds.size() > 0)
                                {
                                    Integer userId = userIds.get(0);

                                    instance.setDealer(userId);

                                    String deptId = receiver.getProperty("deptId");
                                    if (StringUtils.isEmpty(deptId))
                                    {
                                        instance.setDealDeptId(Integer.valueOf(deptId));
                                    }
                                    else
                                    {
                                        instance.setDealDeptId(getDao().getUser(userId).getDepts().get(0).getDeptId());
                                    }

                                    getDao().update(instance);

                                    break;
                                }
                            }

                            if (instance.getDealer() != null)
                                break;
                        }
                        break;
                    }
                }
            }
        }

        if (isEnd(receiverMap))
        {
            Object r = endOdFlow(actionId);
            if (r != null)
                return r;
        }
        else if (receiverMap != null)
        {
            ReceiveBase receiveBase = getReceiveBase();
            if (receiveBase != null && receiveBase.getState() == ReceiveState.accepted)
            {
                receiveBase.setState(ReceiveState.flowing);
                getDao().update(receiveBase);
            }
        }

        odTimeoutService.checkInstance(getInstanceId());

        return null;
    }

    @Override
    protected Object afterBack(Object result, String[] preStepIds, String remark) throws Exception
    {
        result = super.afterBack(result, preStepIds, remark);

        //撤回公文接收环节，重置公文状态
        FlowContext context = getFlowContext();
        List<FlowStep> preSteps = context.getPreSteps();
        if (preSteps.size() == 1)
        {
            FlowStep step = preSteps.get(0);
            if (step.isFirstStep() && context.getService().getNoDealSteps().size() == 1)
            {
                ReceiveBase receiveBase = getReceiveBase();
                if (receiveBase != null && receiveBase.getState() == ReceiveState.flowing)
                {
                    receiveBase.setState(ReceiveState.accepted);
                    getDao().update(receiveBase);
                }
            }
        }

        refreshStepQ();

        return result;
    }

    @Override
    protected Object afterCancelSend(Object result) throws Exception
    {
        //公文接收环节，撤回后将重置公文状态
        result = super.afterCancelSend(result);
        if (getFlowContext().isFirstStep())
        {
            ReceiveBase receiveBase = getReceiveBase();
            if (receiveBase != null && receiveBase.getState() == ReceiveState.flowing)
            {
                receiveBase.setState(ReceiveState.accepted);
                getDao().update(receiveBase);
            }
        }

        OdFlowInstance instance = getOdFlowInstance();
        if (instance.getState() == OdFlowInstanceState.closed)
        {
            instance.setState(OdFlowInstanceState.unclosed);
            service.getDao().update(instance);
        }

        refreshStepQ();

        return result;
    }

    @Override
    public String getActionName(Action action) throws Exception
    {
        String actionId = action.getActionId();
        if (actionId.startsWith("altTag_"))
        {
            String tag = actionId.substring(7);
            if (tag.equals(getTag()))
                return "取消" + tag;
            else
                return "设置" + tag;
        }

        return super.getActionName(action);
    }

    @Override
    public String getAction(Action action) throws Exception
    {
        String actionId = action.getActionId();
        if (actionId.startsWith("altTag_"))
        {
            String tag = actionId.substring(7);
            return "altTag(\"" + tag + "\")";
        }

        return super.getAction(action);
    }

    @Override
    protected List<Action> createActions() throws Exception
    {
        List<Action> actions = super.createActions();

        int n = actions.size();
        for (int i = 0; i < n; i++)
        {
            Action action = actions.get(i);
            if (CATALOG.equals(action.getActionId()))
            {
                actions.add(i + 1, UNCATALOG_ACTION);
                break;
            }
        }

        if (!isSimpleNode())
        {
            for (PrintInfo printTemplate : getPrintTemplates())
            {
                BaseScriptAction action = new BaseScriptAction("print$" + printTemplate.getTemplateId(),
                        "打印" + printTemplate.getShowName());

                action.setClient(true);
                action.setScript(new Script("printForm(" + printTemplate.getTemplateId() +
                        ",\"" + printTemplate.getFormName() + "\",\"" +
                        HtmlUtils.escapeString(printTemplate.getShowName()) +
                        "\")", "javascript"));

                actions.add(action);
            }

            if (hasDocument())
            {
                actions.add(STORE_TO_ACTION);
            }
        }

        return actions;
    }

    protected boolean accept(PrintTemplate printTemplate) throws Exception
    {
        return true;
    }

    /**
     * 获得某个文档，由于一个公文流程可能关联多个文档，如收文转发文之后同时关联收文文档和发文文档
     * 需要子类定义其实现
     *
     * @param type 文档的类型，如发文文档为send，收文文档为receive等
     * @return 文档对象
     * @throws Exception 允许子类抛出异常
     */
    public OfficeDocument getDocument(String type) throws Exception
    {
        if ("default".equals(type))
        {
            return getDocument();
        }
        else if (type != null && type.startsWith("collect_"))
        {
            Long receiveId = Long.valueOf(type.substring(8));

            Collect collect = getDao().getCollect(receiveId);

            if (collect.getDocumentId() == null)
            {
                OfficeDocument document = new OfficeDocument();
                document.setCreateDeptId(collect.getReceiveBase().getDeptId());
                document.setTitle(collect.getReceiveBase().getDocument().getTitle());

                getDao().add(document);
                collect.setDocumentId(document.getDocumentId());
                getDao().update(collect);

                return document;
            }
            else
            {
                return collect.getDocument();
            }
        }

        return null;
    }

    /**
     * 某个文档是否可编辑，是否留痕，用于做权限校验
     *
     * @param type     文档的类型，如发文文档为send，收文文档为receive等
     * @param document 文档对象
     * @return 文档的编辑方式，见EditType的定义
     * @throws Exception 允许子类抛出异常
     * @see OfficeEditType
     */
    public OfficeEditType getEditType(OfficeDocument document, String type) throws Exception
    {
        if (type != null && type.startsWith("collect_"))
        {
            Long receiveId = Long.valueOf(type.substring(8));

            if (receiveId.equals(getOdFlowInstance().getReceiveId()))
                return OfficeEditType.editable;
            else
                return OfficeEditType.readOnly;
        }

        return OfficeEditType.readOnly;
    }

    @Override
    @Service
    @ObjectResult
    public String getText(String textType, int length) throws Exception
    {
        setTextType(textType);

        return TextFlowUtils.getText(this, length);
    }

    /**
     * 显示文档
     *
     * @return 转向显示文档的页面
     * @throws Exception 从数据库获得文档错误
     */
    @Service(url = "/{@class}/{stepId}/text/{textType}/show?editable={textEditable}")
    public String showText() throws Exception
    {
        TextFlowUtils.showText(this);

        return "text";
    }


    /**
     * 保存正文
     *
     * @throws Exception 写数据库错误
     */
    @Service(url = "/{@class}/{stepId}/text/save", method = HttpMethod.post)
    @ObjectResult
    @Transactional
    public void saveText() throws Exception
    {
        TextFlowUtils.saveText(this);
    }

    @Service(url = "/{@class}/{stepId}/otherFile/save", method = HttpMethod.post)
    @ObjectResult
    @Transactional
    public void saveOtherFile(String textType) throws Exception
    {
        setTextType(textType);
        TextFlowUtils.saveOtherFile(this);
    }

    @Service(url = "/{@class}/{stepId}/otherFile/delete", method = HttpMethod.post)
    @ObjectResult
    @Transactional
    public void deleteOtherFile(String textType) throws Exception
    {
        setTextType(textType);
        TextFlowUtils.deleteOtherFile(this);
    }

    /**
     * 将正文转化为pdf
     *
     * @throws Exception 写数据库错误
     */
    @Service(url = "/{@class}/{stepId}/text/toPdf", method = HttpMethod.post)
    @ObjectResult
    @Transactional
    public void convertToPdf() throws Exception
    {
        TextFlowUtils.convertToPdf(this);
    }

    /**
     * 释放正文，让其他人可以编辑正文
     *
     * @throws Exception 写数据库错误
     */
    @Service(url = "/{@class}/{stepId}/text/release")
    @ObjectResult
    public void releaseText() throws Exception
    {
        TextFlowUtils.releaseText(this);
    }

    @NotSerialized
    public Action[] getTextActions() throws Exception
    {
        return getTextActions(textType, textEditable);
    }


    /**
     * 正文页面的动作列表
     *
     * @param type     正文类型
     * @param editable 能否编辑
     * @return 动作列表
     * @throws Exception 允许子类抛出异常
     */
    public Action[] getTextActions(String type, boolean editable) throws Exception
    {
        if (editable)
        {
            if (type != null && type.startsWith("collect_"))
            {
                Long receiveId = Long.valueOf(type.substring(8));

                if (receiveId.equals(getOdFlowInstance().getReceiveId()))
                    return new Action[]{SAVE_ACTION, HITCH_REDHEAD_ACTION};
            }

            return new Action[]{SAVE_ACTION};
        }
        else
        {
            return new Action[]{};
        }
    }

    @NotSerialized
    public List<PrintInfo> getPrintTemplates() throws Exception
    {
        List<PrintTemplate> printTemplates = getPrintService().getPrintTemplates(getOdFlowInstance().getBusinessId(),
                getBusinessDeptId(), getOdFlowInstance().getType());

        List<PrintInfo> printInfos = new ArrayList<PrintInfo>(printTemplates.size());

        for (PrintTemplate template : printTemplates)
        {
            if (accept(template))
            {
                printInfos.add(new PrintInfo(template.getTemplateId(), template.getShowName(),
                        template.getFormType() == null ? "def" : template.getFormType().toString()));
            }
        }

        return printInfos;
    }

    @Service(url = "/{@class}/{stepId}/print/show")
    public String showFormPrint() throws Exception
    {
        return "/ods/flow/print.ptl";
    }

    @Service(url = "/{@class}/{stepId}/print/down?toDoc={$0}")
    public InputFile downFormPrint(boolean toDoc) throws Exception
    {
        return downFormPrint(printTemplateId, printFormName, toDoc);
    }

    protected InputFile downFormPrint(Integer printTemplateId, String printFormName, boolean toDoc) throws Exception
    {
        PrintService service = getPrintService();
        PrintTemplate printTemplate = service.getPrintTemplate(printTemplateId);

        String path = service.getTemplatePath(printTemplate);

        if (path == null)
            throw new SystemMessageException("ods.print.notUploaded",
                    "print template " + printTemplateId + " have not bean uploaded");

        FormContext formContext = "def".equals(printFormName) ? getFormContext() : getFormContext(printFormName);

        return new InputFile(new TemplateInput(path, formContext.getTextContext(), toDoc),
                getTitle() + "_" + printTemplate.getShowName() + (toDoc ? ".doc" : ".xml"));
    }

    @Override
    protected String getNotifyApp(String nodeId, String actionId) throws Exception
    {
        if (FlowNode.PASS.equals(nodeId))
            return "odpass";
        else
            return "odflow";
    }

    @Override
    protected String getNotifyMessage(String nodeId, String actionId, String remark) throws Exception
    {
        String s = getNotifyMessage(nodeId, actionId);

        if (!StringUtils.isEmpty(s))
            return s;

        String typeName = null;
        BusinessModel business = getBusiness();
        if (business != null)
            typeName = business.getTypeName();

        String type;
        if (!StringUtils.isEmpty(typeName))
            type = "other";
        else
            type = getType();

        if (type != null)
        {
            if (FlowNode.PASS.equals(nodeId) || "传阅".equals(nodeId) || "签阅".equals(nodeId))
            {
                s = "ods.flow.passNotify." + type;
            }
            else if ("#back".equals(nodeId))
            {
                s = "ods.flow.backNotify." + type;
            }
            else if ("#reply".equals(nodeId))
            {
                s = "ods.flow.replyNotify." + type;
            }
            else
            {
                s = "ods.flow.dealNotify." + type;
            }
        }

        String message = null;
        SystemFormContext context = getFormContext();
        if (context != null)
        {
            if (context.getForm().getComponent(Constants.Flow.MESSAGE) != null)
            {
                message = context.getFormData().getString(Constants.Flow.MESSAGE);
            }
        }


        //初始化几个参数，兼容老的代码
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("0", getTitle());
        map.put("1", getBusinessContext().getUserName());
        map.put("2", remark);
        map.put("3", typeName);

        map.put("title", getTitle());
        map.put("source", getBusinessContext().getUserName());
        map.put("remark", remark);
        map.put("type", typeName);
        map.put("message", message);

        return Tools.getMessage(s, new CollectionUtils.EvalMap(getDocument(), map));
    }

    public Long getTopInstanceId() throws Exception
    {
        return getInstanceId();
    }

    public String getTopForm() throws Exception
    {
        return "";
    }

    public String getFormName(Long instanceId) throws Exception
    {
        return "";
    }

    @Service
    @NotSerialized
    public List<CollectInfo> getCollects() throws Exception
    {
        List<Collect> collects = getDao().getCollectsBytTopInstanceId(getTopInstanceId());
        List<CollectInfo> result = new ArrayList<CollectInfo>(collects.size());

        Long instanceId = getInstanceId();
        Long receiveId = getOdFlowInstance().getReceiveId();

        for (Collect collect : collects)
        {
            boolean show = false;

            if (collect.getReceiveId().equals(receiveId))
            {
                //本流程自己的数据
                show = true;
            }
            else if (collect.isHidden() == null || !collect.isHidden())
            {
                if (collect.getReceiveBase().getState() == ReceiveState.end && collect.isPublished() != null &&
                        collect.isPublished())
                {
                    //允许所有部门看的数据
                    show = true;
                }
                else if (collect.getCollectInstanceId().equals(instanceId))
                {
                    //此部门发起的数据
                    show = true;
                }
            }

            if (show)
            {
                result.add(new CollectInfo(collect, getFormName(collect.getCollectInstanceId())));
            }
        }

        return result;
    }

    @Service
    public List<CollectInfo> getCollectsByCollectInstanceId(Long collectInstanceId) throws Exception
    {
        List<Collect> collects = getDao().getCollectsByCollectInstanceId(collectInstanceId);
        List<CollectInfo> result = new ArrayList<CollectInfo>(collects.size());

        Long instanceId = getInstanceId();
        Long receiveId = getOdFlowInstance().getReceiveId();

        for (Collect collect : collects)
        {
            boolean show = false;

            if (collect.getReceiveId().equals(receiveId))
            {
                //本流程自己的数据
                show = true;
            }
            else if ((collect.isHidden() == null || !collect.isHidden()) &&
                    collect.getReceiveBase().getState() == ReceiveState.end)
            {
                if (collect.isPublished() != null && collect.isPublished())
                {
                    //允许所有部门看的数据
                    show = true;
                }
                else if (collect.getCollectInstanceId().equals(instanceId))
                {
                    //此部门发起的数据
                    show = true;
                }
            }

            if (show)
            {
                if (collect.getDocument() != null)
                {
                    DocumentText text = collect.getDocument().getText();
                    if (text != null && text.getFileSize() != null && text.getFileSize() > 0)
                    {
                        result.add(new CollectInfo(collect, null));
                    }
                }
            }
        }

        return result;
    }

    @Service(url = "/{@class}/{stepId}/showAllCollects")
    public String showAllCollectsText()
    {
        return "collectstext";
    }

    /**
     * 会签
     *
     * @param deptIds 参与会签的单位
     * @throws Exception 数据库错误
     */
    @Service(url = "/{@class}/{stepId}/sendCollects", method = HttpMethod.post)
    @ObjectResult
    @Transactional
    public List<String> sendCollects(Integer[] deptIds) throws Exception
    {
        List<Long> receiveIds = service.collect(getOdFlowInstance(), getTopInstanceId(), deptIds, getUserId(),
                getBusinessContext().getUserName(), getStepId());

        FlowContext context = getFlowContext();
        if (context.getStep().getState() == FlowStep.DRAFT)
        {
            //送会签单位后不放草稿箱
            context.getStep().setState(FlowStep.NODEAL);
            context.updateStep();

            refreshStepQ();
        }


        List<String> deptNames = new ArrayList<String>(receiveIds.size());
        for (Long receiveId : receiveIds)
        {
            ReceiveBase receiveBase = getDao().getReceiveBase(receiveId);
            deptNames.add(receiveBase.getDept().getDeptName());
        }

        try
        {
            exchangeNotifyServiceProvider.get().sendMessage(receiveIds, getBusinessDeptId(), getUserId(), false);
        }
        catch (Throwable ex)
        {
            //短信发送失败不影响其他逻辑
            Tools.log(ex);
        }

        return deptNames;
    }

    /**
     * 联合办文
     *
     * @param deptIds 参与联合办文的单位
     * @throws Exception 数据库错误
     */
    @Service(url = "/{@class}/{stepId}/sendUnionDeals", method = HttpMethod.post)
    @ObjectResult
    @Transactional
    public List<String> sendUnionDeals(Integer[] deptIds) throws Exception
    {
        List<Long> receiveIds =
                service.unionDeal(getOdFlowInstance(), deptIds, getUserId(), getBusinessContext().getUserName(),
                        getStepId());

        FlowContext context = getFlowContext();
        if (context.getStep().getState() == FlowStep.DRAFT)
        {
            //送联合办文后不放草稿箱
            context.getStep().setState(FlowStep.NODEAL);
            context.updateStep();

            refreshStepQ();
        }

        List<String> deptNames = new ArrayList<String>(receiveIds.size());
        for (Long receiveId : receiveIds)
        {
            ReceiveBase receiveBase = getDao().getReceiveBase(receiveId);
            deptNames.add(receiveBase.getDept().getDeptName());
        }

        try
        {
            exchangeNotifyServiceProvider.get().sendMessage(receiveIds, getBusinessDeptId(), getUserId(), false);
        }
        catch (Throwable ex)
        {
            //短信发送失败不影响其他逻辑
            Tools.log(ex);
        }

        return deptNames;
    }

    @Service
    @ObjectResult
    public void cancelCollect(Long receiveId) throws Exception
    {
        receiveServiceProvider.get().withdrawReceive(receiveId);
    }

    @Service
    @ObjectResult
    public void publishCollect(Long receiveId, boolean published) throws Exception
    {
        Collect collect = new Collect();
        collect.setReceiveId(receiveId);
        collect.setPublished(published);

        getDao().update(collect);
    }

    @Service
    @ObjectResult
    public void hideCollect(Long receiveId, boolean hidden) throws Exception
    {
        Collect collect = new Collect();
        collect.setReceiveId(receiveId);
        collect.setHidden(hidden);

        getDao().update(collect);
    }

    @NotSerialized
    public List<DocumentTemplate> getDocumentTemplates() throws Exception
    {
        return TextFlowUtils.getDocumentTemplates(this);
    }

    public List<Tag> getTags() throws Exception
    {
        return getDao().getTags(getUserId());
    }

    public String getTag() throws Exception
    {
        return getOdFlowInstance().getTag();
    }

    @Service
    public void modifyTag(String tag) throws Exception
    {
        OdFlowInstance flowInstance = getOdFlowInstance();
        flowInstance.setTag(tag);

        getDao().update(flowInstance);
    }

    @Service
    @Transactional
    public void deleteOdInstance() throws Exception
    {
        OdFlowInstanceDelete instanceDelete = new OdFlowInstanceDelete();
        instanceDelete.setInstanceId(getInstanceId());
        instanceDelete.setDeleteTime(new Date());
        instanceDelete.setUserId(getUserId());
        instanceDelete.setDeptId(getBusinessContext().getDeptId());
        getDao().save(instanceDelete);

        OdFlowInstance odFlowInstance = getOdFlowInstance();
        odFlowInstance.setState(OdFlowInstanceState.deleted);
        getDao().update(odFlowInstance);
    }

    /**
     * 发起一个新的流程，并将当前公文的数据复制过去
     *
     * @param businessId 新的流程的业务ID
     * @return 新流程的开始步骤的访问url
     * @throws Exception 数据库操作失败
     */
    @Service
    @Transactional
    @ObjectResult
    public String turnFlow(Integer businessId) throws Exception
    {
        getFlowContext();

        OdFlowStartContext startContext = Tools.getBean(OdFlowStartContext.class);

        startContext.setTitle(getDocument().getTitle());
        startContext.setBusinessId(businessId);
        startContext.setBusinessContext(getBusinessContext());
        startContext.setCloneBodyId(getBodyId());
        startContext.setSourceDocumentId(getDocumentId());

        startContext.start();

        return OdFlowService.getStepUrl(startContext.getStepId(), startContext.getBusiness().getType().getType());
    }

    public Boolean isCataloged() throws Exception
    {
        return getOdFlowInstance().isCataloged();
    }

    @Service
    @Transactional
    @ObjectResult
    public void catalog() throws Exception
    {
        OdFlowInstance odFlowInstance = getOdFlowInstance();
        odFlowInstance.setCataloged(true);

        getDao().update(odFlowInstance);
    }

    @Service
    @Transactional
    @ObjectResult
    public void uncatalog() throws Exception
    {
        OdFlowInstance odFlowInstance = getOdFlowInstance();
        odFlowInstance.setCataloged(false);

        getDao().update(odFlowInstance);
    }

    public boolean hasTimeout() throws Exception
    {
        return timeoutService.hasTimeoutConfig(OdTimeout.FLOW_ID, Collections.singleton(getBusinessDeptId()));
    }

    @Service(url = "/{@class}/{stepId}/qr")
    public InputFile getQRCode(int width) throws Exception
    {
        String link = "/ods/instance/" + getOdFlowInstance().getType() + "/" + getInstanceId();

        String content;
        List<BarCode> barCodes = BarCodeUtils.getBarCodeByLinkContent(link);
        if (barCodes != null && barCodes.size() > 1)
        {
            BarCode barCode = barCodes.get(0);
            content = barCode.getContent();
        }
        else
        {
            content = getQRContent() + "\r\n" + link;
        }

        if (width <= 0)
            width = 150;

        return new InputFile(BarCodeUtils.qr(content, width), getInstanceId() + ".png", Mime.PNG);
    }

    protected String getQRContent() throws Exception
    {
        StringBuilder buffer = new StringBuilder();

        getQRContent(buffer);


        List<FlowStep> noDealSteps = getFlowContext().getService().getNoDealSteps();
        if (noDealSteps.size() > 0)
        {
            buffer.append("\r\n当前环节:");

            FlowStep noDealStep = noDealSteps.get(noDealSteps.size() - 1);

            buffer.append(noDealStep.getNodeName()).append("(").append(noDealStep.getDealerName()).append(")");
        }
        else
        {
            buffer.append("\r\n").append("当前已办结");
        }

        return buffer.toString();
    }

    protected void getQRContent(StringBuilder buffer) throws Exception
    {
        OfficeDocument document = getDocument();
        buffer.append("文件标题:").append(document.getTitle());
    }

    @Override
    protected void initRecent(Recent recent) throws Exception
    {
        super.initRecent(recent);

        recent.setType("od");
    }

    public List<String> getCollectComponentNames()
    {
        return Constants.Collect.COLLECTCOMPONENTNAMES;
    }

    @Service
    @ObjectResult
    public void setOfficeType() throws Exception
    {
    }

    @Override
    public List<String> getJsFiles() throws Exception
    {
        List<String> jsFiles = super.getJsFiles();

        BusinessModel business = getBusiness();
        String jsFile = business.getJsFile();
        if (!StringUtils.isEmpty(jsFile))
        {
            if (jsFiles == null)
                jsFiles = new ArrayList<String>(1);

            jsFiles.add(jsFile);
        }

        return jsFiles;
    }

    protected void refreshStepQ() throws Exception
    {
        ((OdSystemFlowDao) getSystemFlowDao()).refreshStepQ(getInstanceId());
    }
}
