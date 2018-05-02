package com.gzzm.ods.flow;

import com.gzzm.ods.document.*;
import com.gzzm.ods.exchange.*;
import com.gzzm.ods.receipt.*;
import com.gzzm.ods.redhead.*;
import com.gzzm.ods.sendnumber.SendNumberDao;
import com.gzzm.platform.commons.*;
import com.gzzm.platform.flow.*;
import com.gzzm.platform.form.SystemFormContext;
import com.gzzm.platform.group.*;
import com.gzzm.platform.organ.*;
import com.gzzm.platform.weboffice.OfficeEditType;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;
import net.cyan.valmiki.flow.*;
import net.cyan.valmiki.form.*;
import net.cyan.valmiki.form.components.*;

import java.util.*;

/**
 * 具有发文功能的流程
 *
 * @author camel
 * @date 11-9-22
 */
@Service
public abstract class SendableFlowPage extends OdFlowPage implements SendFlowElement
{
    public static final String OD_SEND_CREATEDEPT = "od_send_createDept";

    @Inject
    protected static Provider<RedHeadService> redHeadServiceProvider;

    @Inject
    protected static Provider<ReceiptDao> receiptDaoProvider;

    @Inject
    protected static Provider<SendNumberDao> sendNumberDaoProvider;

    @Inject
    protected ExchangeSendService exchangeSendService;

    private ReceiptDao receiptDao;

    /**
     * 发文流程实例
     */
    @NotSerialized
    private SendFlowInstance sendFlowInstance;

    @NotSerialized
    private boolean sendFlowInstanceLoaded;

    /**
     * 发文文档
     */
    @NotSerialized
    private OfficeDocument sendDocument;

    /**
     * 发文字号ID
     */
    private Integer sendNumberId;

    /**
     * 发文的接收者
     */
    @NotSerialized
    private DocumentReceiverList receiverList;

    /**
     * 当前正在处理联合发文的部门
     */
    private List<Integer> currentUnionDeptIds;

    private Receipt sendReceipt;

    private Integer sendDeptId;

    public SendableFlowPage()
    {
    }

    @NotSerialized
    public ExchangeSendService getExchangeSendService()
    {
        return exchangeSendService;
    }

    protected ReceiptDao getReceiptDao()
    {
        if (receiptDao == null)
            receiptDao = receiptDaoProvider.get();
        return receiptDao;
    }

    public Integer getSendNumberId() throws Exception
    {
        if (sendNumberId == null && isSended())
            sendNumberId = getSendFlowInstance(true).getSendNumberId();

        return sendNumberId;
    }

    public void setSendNumberId(Integer sendNumberId)
    {
        this.sendNumberId = sendNumberId;
    }

    public Integer getSendDeptId() throws Exception
    {
        if (sendDeptId == null && isSended())
            sendDeptId = getSendFlowInstance(true).getSendDeptId();

        return sendDeptId;
    }

    public void setSendDeptId(Integer sendDeptId)
    {
        this.sendDeptId = sendDeptId;
    }

    public String getSendDeptName() throws Exception
    {
        Integer sendDeptId = getSendDeptId();
        if (sendDeptId != null)
            return getDeptService().getDeptName(sendDeptId);

        return null;
    }

    public List<Integer> getCurrentUnionDeptIds()
    {
        return currentUnionDeptIds;
    }

    protected List<Integer> getCurrentUnionDeptIds0() throws Exception
    {
        return exchangeSendService.getCurrentUnionDeptIds(getSendFlowInstance(true).getDocumentId());
    }

    public SendFlowInstance getSendFlowInstance()
    {
        return sendFlowInstance;
    }

    public void putSendFlowInstance(SendFlowInstance sendFlowInstance)
    {
        this.sendFlowInstance = sendFlowInstance;
    }

    public boolean isSendFlowInstanceLoaded()
    {
        return sendFlowInstanceLoaded;
    }

    public void setSendFlowInstanceLoaded()
    {
        this.sendFlowInstanceLoaded = true;
    }

    public SendFlowInstance getSendFlowInstance(boolean create) throws Exception
    {
        return SendFlowUtils.getSendFlowInstance(this, create);
    }

    public OfficeDocument getSendDocument() throws Exception
    {
        if (sendDocument == null)
        {
            sendDocument = getSendFlowInstance(true).getDocument();
        }

        return sendDocument;
    }

    public void putSendDocument(OfficeDocument sendDocument)
    {
        this.sendDocument = sendDocument;
    }

    public Long getSendDocumentId() throws Exception
    {
        if (isSended())
            return getSendFlowInstance(true).getDocumentId();
        else
            return null;
    }

    @Override
    public String getEncodedSendDocumentId() throws Exception
    {
        return OfficeDocument.encodeId(getSendDocumentId());
    }

    /**
     * 创建发文档
     *
     * @return 发文文档
     * @throws Exception 创建文档错误，为数据库读写错误
     */
    public OfficeDocument createSendDocument() throws Exception
    {
        OfficeDocument document = new OfficeDocument();

        //设置发文部门
        document.setSourceDept(getBusinessContext().getBusinessDept().getAllName(1));

        SimpleDeptInfo dept = getBusinessContext().getBusinessDept();
        String sourceDeptCode;
        if (!StringUtils.isEmpty(dept.getOrgCode()))
            sourceDeptCode = dept.getOrgCode();
        else
            sourceDeptCode = dept.getDeptCode();
        document.setSourceDeptCode(sourceDeptCode);

        document.setCreateDeptId(getBusinessContext().getBusinessDeptId());
        document.setSourceDocumentId(getOdFlowInstance().getDocumentId());

        getDao().add(document);

        return document;
    }

    public DocumentReceiverList getReceiverList() throws Exception
    {
        if (receiverList == null)
            receiverList = loadReceiverList();

        return receiverList;
    }

    @Override
    public DocumentReceiverList loadReceiverList() throws Exception
    {
        return SendFlowUtils.loadReceiverList(this);
    }

    /**
     * 发文表单的名称，在浏览器端的javascript中可以使用
     *
     * @return 发文表单的名称
     * @throws Exception 获得发文表单错误
     */
    public String getSendFormName() throws Exception
    {
        if (isSended())
        {
            return getSendFormContext().getFormName();
        }
        else
        {
            return null;
        }
    }

    public String getBackTextType() throws Exception
    {
        return SendFlowUtils.getBackTextType(this);
    }

    @Override
    protected void initBusinessContext(BusinessContext businessContext) throws Exception
    {
        super.initBusinessContext(businessContext);

        SendFlowUtils.initBusinessContext(this, businessContext);
    }

    @Override
    protected void initFormContext(SystemFormContext formContext) throws Exception
    {
        if (isSendFrom(formContext))
        {
            initSendFormContext(formContext);
        }

        super.initFormContext(formContext);
    }

    protected boolean isSendFrom(SystemFormContext formContext) throws Exception
    {
        return "send".equals(formContext.getFormName());
    }

    protected void initSendFormContext(SystemFormContext formContext) throws Exception
    {
        SendFlowInstance sendFlowInstance = getSendFlowInstance();
        if (sendFlowInstance != null)
        {
            Boolean sentOut = sendFlowInstance.getSentOut();
            if (sentOut != null && sentOut)
            {
                //公文发送之后有些数据项不允许修改，避免影响已经发送出去的公文
                formContext.setAuthority(Constants.Document.ATTACHMENT, FormRole.READONLY);
            }
        }

        FormData formData = formContext.getFormData();

        boolean creatorEmepty = StringUtils.isEmpty(formData.getString(Constants.Send.CREATOR));

        if (!creatorEmepty)
            formContext.setAuthority(Constants.Send.CREATOR, FormRole.READONLY);
        formContext.setAuthority(Constants.Send.CREATEDEPT, FormRole.READONLY);

        if (creatorEmepty)
            formData.setValue(Constants.Send.CREATOR, getSendCreateUser().getUserName());

        if (getSendCreateDept() != null)
            formData.setValue(Constants.Send.CREATEDEPT, getSendCreateDept().getDeptName());
    }

    /**
     * 从表单里抓取发文数据数据
     *
     * @throws Exception 加载表单错误
     */
    public void extractSendData() throws Exception
    {
        //公文发送出去后不采集数据
        Boolean sentOut = getSendFlowInstance().getSentOut();
        if (sentOut == null || !sentOut)
        {
            extractSendFlowInstance();
            extractSendDocumentData();
            extractReceiverList();


            extractSendReceipt();
        }
    }

    public void extractSendDocumentData() throws Exception
    {
        SendFlowUtils.extractSendDocumentData(this);
    }

    public void extractSendFlowInstance() throws Exception
    {
        SendFlowUtils.extractSendFlowInstance(this);
    }

    protected void extractSendReceipt() throws Exception
    {
        SystemFormContext sendFormContext = getSendFormContext();

        String receiptComponent = null;
        if (sendFormContext.getForm().getComponent(Constants.Receipt.RECEIPT) != null)
            receiptComponent = Constants.Receipt.RECEIPT;
        else if (sendFormContext.getForm().getComponent(Constants.Receipt.RECEIPT1) != null)
            receiptComponent = Constants.Receipt.RECEIPT1;

        if (receiptComponent != null && sendFormContext.isWritable(receiptComponent))
        {
            String value = sendFormContext.getFormData().getString(receiptComponent);
            ReceiptDao dao = getReceiptDao();
            Long documentId = getSendDocumentId();

            if ("1".equals(value) || "true".equals(value) || "是".equals(value))
            {
                //设置回执
                Receipt receipt = dao.getReceiptByDocumentId(documentId);
                if (receipt == null)
                {
                    receipt = new Receipt();
                    receipt.setDocumentId(documentId);
                }

                receipt.setType("meeting");
                receipt.setCreator(getUserId());

                SimpleSelectableData<Member> data =
                        sendFormContext.getFormData().getData(Constants.Receipt.ACCEPTDEPT);

                Integer deptId = null;
                if (data != null && data.getItem() != null)
                {
                    deptId = data.getItem().getId();
                }

                if (deptId == null)
                    deptId = getBusinessDeptId();

                receipt.setDeptId(deptId);

                dao.save(receipt);

                getSendDocument().setReceiptId(receipt.getReceiptId());
            }
            else
            {
                //取消回执
                Receipt receipt = dao.getReceiptByDocumentId(documentId);

                if (receipt != null)
                {
                    ReceiptComponent component = ReceiptComponents.getComponent(receipt.getType());
                    component.delete(receipt.getReceiptId());

                    dao.delete(receipt);
                }
            }
        }
    }

    public boolean hasReceiverListInForm() throws Exception
    {
        return SendFlowUtils.hasReceiverListInForm(this);
    }

    @NotSerialized
    public ReceiverListList getReceiverListFromForm() throws Exception
    {
        return SendFlowUtils.getReceiverListFromForm(this);
    }

    public void extractReceiverList() throws Exception
    {
        SendFlowUtils.extractReceiverList(this);
    }

    /**
     * 是否已经成文
     *
     * @return 已经成文返回true，未成文放回false，可以在页面和javascript中使用此方法
     * @throws Exception 数据库读取数据错误
     */
    public boolean isTextFinal() throws Exception
    {
        return SendFlowUtils.isTextFinal(this);
    }

    public boolean isSendTextEditable() throws Exception
    {
        return isSended() && isEditable() && SendFlowUtils.hasEditSendTextAction(this);
    }

    public boolean canFinalizeText() throws Exception
    {
        return isSended() && SendFlowUtils.hasFinalizeTextAction(this);
    }

    @Override
    protected void beforeShow() throws Exception
    {
        super.beforeShow();

        if (isSended())
        {
            currentUnionDeptIds = getCurrentUnionDeptIds0();
        }
    }

    @Override
    protected Object beforeCancelSend() throws Exception
    {
        Object r = super.beforeCancelSend();
        if (r != null)
            return r;

        if (isSended())
        {
            Send send = exchangeSendService.getDao().getSendByDocumentId(getSendDocumentId());

            if (send != null)
                return Tools.getMessage("ods.flow.documentSended");
        }

        if (isTextFinal() && !hasAction(getFlowContext().getNode().getActions(), FINALIZE_TEXT))
        {
            return Tools.getMessage("ods.flow.textFinal");
        }

        return null;
    }

    /**
     * 保存发文数据
     *
     * @throws Exception 保存数据错误，由数据库错误引起
     */
    @Override
    public void saveSendData() throws Exception
    {
        Boolean sentOut = getSendFlowInstance().getSentOut();
        if (sentOut == null || !sentOut)
        {
            SendFlowUtils.saveSendData(this);
        }

        OfficeDocument document = getSendDocument();
        if (!StringUtils.isEmpty(document.getSendNumber()) && !StringUtils.isEmpty(document.getTitle()) &&
                hasReceiverListInForm())
        {
            saveRecord(false);
        }
    }

    protected void saveRecord(boolean sended) throws Exception
    {
        SendRecordService.saveRecord(getSendDocument(), getOdFlowInstance(), getSendFlowInstance(true),
                getSendFormContext(), sended, new Date(), exchangeSendService.getDao());
    }

    @Override
    public void checkSendDocument() throws Exception
    {
        SendFlowUtils.checkSendDocument(this, getFlowComponentContext());
    }

    /**
     * 发送公文
     *
     * @return 收文单位列表，用于返回给客户端提示使用
     * @throws Exception 发送公文错误
     */
    public ReceiverListList sendDocument() throws Exception
    {
        if (isSended())
        {
            checkSendDocument();

            saveRecord(true);

            List<Integer> unionDeptIds = getUnionDeptIds();
            if (unionDeptIds != null && !unionDeptIds.isEmpty())
            {
                SendFlowInstance instance = getSendFlowInstance(true);
                Integer unionCount = exchangeSendService.getUnionCount(instance.getDocumentId(),
                        instance.isTextFinal() ? instance.getFinalTime() : null);

                if (unionCount == 0)
                {
                    throw new NoErrorException("ods.flow.sendUnionsFirst");
                }
            }

            List<Integer> currentUnionDeptIds = getCurrentUnionDeptIds0();
            if (currentUnionDeptIds != null && currentUnionDeptIds.size() > 0)
            {
                DeptService service = getDeptService();
                StringBuilder deptNames = new StringBuilder();
                for (Integer deptId : currentUnionDeptIds)
                {
                    if (deptNames.length() > 0)
                        deptNames.append(",");
                    deptNames.append(service.getDept(deptId).getDeptName());
                }

                throw new NoErrorException("ods.flow.unionsNotEnd", deptNames.toString());
            }

            ReceiverListList receivers = SendFlowUtils.sendDocument(this, getFlowComponentContext());

            Receipt sendReceipt = getSendReceipt();
            if (sendReceipt != null)
            {
                sendReceipt.setSendTime(new Date());
                sendReceipt.setSended(true);
                getReceiptDao().update(sendReceipt);
            }


            return receivers;
        }

        return null;
    }

    @Service(url = "/{@class}/{stepId}/sendToSelf")
    @ObjectResult
    public void sendToSelf() throws Exception
    {
        exchangeSendService.sendDocumentTo(getSendDocument(), Collections.singletonList(
                new Member(MemberType.dept, getBusinessDeptId(), getBusinessContext().getBusinessDeptName())),
                "内部", null);
    }

    @Override
    public Object execute(String actionId) throws Exception
    {
        Object result = super.execute(actionId);

        if (isSended() && result instanceof NodeReceiverSelectList)
        {
            checkText();
        }

        return result;
    }

    @Override
    protected Object beforeSend(Map<String, List<List<NodeReceiver>>> receiverMap, String actionId) throws Exception
    {
        Object result = super.beforeSend(receiverMap, actionId);

        if (result != null)
            return result;

        return null;
    }

    protected void checkText() throws Exception
    {
        for (Action action : getActions())
        {
            if (EDIT_SEND_TEXT.equals(action.getActionId()))
            {
                DocumentText text = getSendDocument().getText();

                if (text == null || StringUtils.isEmpty(text.getType()) &&
                        (text.getOtherFile() == null || StringUtils.isEmpty(text.getOtherFileName())))
                    throw new NoErrorException("ods.flow.textUnEdit");

                break;
            }
        }
    }

    @Override
    protected void extractOdData() throws Exception
    {
        super.extractOdData();

        if (isSended())
            extractSendData();
    }

    @Override
    protected void saveOdData() throws Exception
    {
        super.saveOdData();

        if (isSended())
            saveSendData();
    }

    @Override
    protected Object endOdFlow(String actionId) throws Exception
    {
        ReceiverListList receiverListList = null;

        if (isSended() && hasReceiverListInForm())
        {
            Action action = getFlowContext().getNode().getAction(actionId);
            if (isTextFinal() || !"false".equals(action.getProperty("send")))
            {
                //锁住发文流程实例，避免重复发送
                Long instanceId = getInstanceId();
                getDao().lockSendFlowInstance(instanceId);

                SendFlowInstance sendFlowInstance = getDao().getSendFlowInstance(instanceId);
                Boolean sentOut = sendFlowInstance.getSentOut();
                if (sentOut == null || !sentOut)
                {
                    receiverListList = sendDocument();

                    //发文之后终止正在办理的事项
                    getFlowContext().stopInstance();
                }
            }
        }

        Object result = super.endOdFlow(this.actionId);
        return receiverListList == null || receiverListList.getReceiverLists() == null ? result : receiverListList;
    }

    @Override
    @NotSerialized
    protected List<Action> createActions() throws Exception
    {
        List<Action> actions = super.createActions();

        SendFlowUtils.initActions(this, actions);

        return actions;
    }

    @Override
    public boolean accept(Action action) throws Exception
    {
        if (isSended())
        {
            String id = action.getActionId();
            if (SEND_UNIONDEALS.equals(id))
                return false;

            if (SEND_UNIONS.equals(id))
            {
                boolean b = false;
                FormRole role = getSendFormContext().getRole();
                for (FormPage formPage : getSendFormContext().getForm().getPages())
                {
                    FormComponent unionDeptComponent = formPage.getComponent("联合发文单位");
                    if (unionDeptComponent != null)
                    {
                        if (!role.isHidden(formPage.getName()) &&
                                !role.isHidden(formPage.getName() + '.' + unionDeptComponent.getName()))
                        {
                            return true;
                        }

                        b = true;
                    }
                }

                return !b;
            }

            if (EDIT_RECEIPT.equals(id) || REMOVE_RECEIPT.equals(id))
            {
                SystemFormContext sendFormContext = getSendFormContext();
                if (sendFormContext != null)
                {
                    if (sendFormContext.getForm().getComponent(Constants.Receipt.RECEIPT) != null)
                        return false;

                    if (sendFormContext.getForm().getComponent(Constants.Receipt.RECEIPT1) != null)
                        return false;
                }
            }
        }
        else
        {
            //没有发文不显示和发文相关的按钮
            String id = action.getActionId();

            if (SHOW_SEND_TEXT.equals(id) || EDIT_SEND_TEXT.equals(id) || FINALIZE_TEXT.equals(id) ||
                    SEND_UNIONS.equals(id) || SEND_UNIONS_SEAL.equals(id) || EDIT_RECEIPT.equals(id) ||
                    REMOVE_RECEIPT.equals(id))
            {
                return false;
            }
        }

        return super.accept(action);
    }

    /**
     * 得到联合发文单位的部门ID列表，从表单中提取
     *
     * @return 联合发文单位的部门ID列表
     * @throws Exception 从数据库加载表单错误
     */
    protected List<Integer> getUnionDeptIds() throws Exception
    {
        FormData formData = getSendFormContext().getFormData();

        MultipleSelectableData<Member> data = formData.getData(Constants.Send.UNIONDEPTS);

        if (data != null)
        {
            List<Member> members = data.getItems();

            if (members != null)
            {
                List<Integer> deptIds = new ArrayList<Integer>(members.size());
                for (Member member : members)
                {
                    if (member.getType() == MemberType.dept)
                        deptIds.add(member.getId());
                }

                return deptIds;
            }
        }

        return Collections.emptyList();
    }

    protected Receipt getSendReceipt() throws Exception
    {
        if (isSended())
        {
            if (sendReceipt == null)
                sendReceipt = getReceiptDao().getReceiptByDocumentId(getSendDocumentId());

            return sendReceipt;
        }

        return null;
    }

    public Long getSendReceiptId() throws Exception
    {
        Receipt sendReceipt = getSendReceipt();

        return sendReceipt == null ? null : sendReceipt.getReceiptId();
    }

    @Override
    public OfficeDocument getDocument(String type) throws Exception
    {
        if ("send".equals(type) || "final".equals(type))
            return getSendDocument();

        return super.getDocument(type);
    }

    @Override
    public OfficeEditType getEditType(OfficeDocument document, String type) throws Exception
    {
        if ("send".equals(type) || "final".equals(type))
        {
            return SendFlowUtils.getEditType(this, document, type);
        }

        return super.getEditType(document, type);
    }

    @Override
    public Action[] getTextActions(String type, boolean editable) throws Exception
    {
        if ("send".equals(type) || "final".equals(type))
        {
            return SendFlowUtils.getTextActions(this, type, editable);
        }

        return super.getTextActions(type, editable);
    }

    /**
     * 红头模版ID，从表单里提取
     *
     * @return 红头模版ID
     * @throws Exception 加载表单错误
     */
    @NotSerialized
    @Service(url = "/{@class}/{stepId}/redHeadId")
    public Integer getRedHeadId() throws Exception
    {
        return SendFlowUtils.getRedHeadId(this);
    }

    /**
     * 获得红头标题
     *
     * @return 红头标题列表
     * @throws Exception 加载表单错误
     */
    @NotSerialized
    @Service(url = "/{@class}/{stepId}/redHeadTitles")
    public List<String> getRedHeadTitles() throws Exception
    {
        SimpleSelectableData<RedHeadTitleInfo> data =
                getSendFormContext().getFormData().getData(Constants.Send.REDHEAD);

        if (data != null && data.getItem() != null)
        {
            Integer[] titleIds = data.getItem().getTitleIds();

            if (titleIds != null && titleIds.length > 0)
            {
                RedHeadService service = redHeadServiceProvider.get();

                List<String> titles = new ArrayList<String>(titleIds.length);
                for (Integer titleId : titleIds)
                {
                    RedHeadTitle redHeadTitle = service.getRedHeadTitle(titleId);
                    if (redHeadTitle != null)
                        titles.add(redHeadTitle.getTitle());
                }

                return titles;
            }
        }

        return null;
    }

    @Service
    public String[] getBookmarkTexts(String[] bookmarks) throws Exception
    {
        return SendFlowUtils.getBookmarkTexts(this, bookmarks);
    }

    @Service(url = "/{@class}/{stepId}/finalizeText", method = HttpMethod.post)
    @ObjectResult
    @Transactional
    public void finalizeText() throws Exception
    {
        SendFlowUtils.finalizeText(this);
    }

    @Service(url = "/{@class}/{stepId}/unFinalizeText", method = HttpMethod.post)
    @ObjectResult
    @Transactional
    public String unFinalizeText() throws Exception
    {
        return SendFlowUtils.unFinalizeText(this);
    }

    /**
     * 可追加的发文单位，指那些已经已经处理完，需要重新处理的联合发文单位
     *
     * @return 可追加的联合发文单位的ID列表，如果没有联合发文单位处理过，则返回null，通知客户端直接送所有单位处理，不需要选择
     * @throws Exception 从数据库读取数据错误
     */
    @NotSerialized
    @Service
    public List<Integer> getSelectableUnionDepts() throws Exception
    {
        SendFlowInstance instance = getSendFlowInstance(true);

        List<Union> unions = exchangeSendService
                .getUnions(instance.getDocumentId(), instance.isTextFinal() ? instance.getFinalTime() : null);

        //没有联合发文单位处理过，返回null，通知客户端直接送所有单位处理即可
        if (unions.isEmpty())
            return null;

        List<Integer> deptIds = getUnionDeptIds();

        for (Iterator<Integer> iterator = deptIds.iterator(); iterator.hasNext(); )
        {
            Integer deptId = iterator.next();

            //过滤掉那些还没有处理完的联合发文单位
            boolean exists = false;
            for (Union union : unions)
            {
                ReceiveBase receiveBase = union.getReceiveBase();
                if ((receiveBase.getState() != ReceiveState.end && receiveBase.getState() != ReceiveState.backed) &&
                        receiveBase.getDeptId().equals(deptId))
                {
                    exists = true;
                    break;
                }
            }

            if (exists)
                iterator.remove();
        }

        return deptIds;
    }

    /**
     * 送联合发文单位处理
     *
     * @param deptIds 要发送的联合办文单位的ID
     * @return 返回联合发文单位的名称
     * @throws Exception 数据库操作错误
     */
    @Service(url = "/{@class}/{stepId}/sendUnions", method = HttpMethod.post)
    @ObjectResult
    @Transactional
    public List<String> sendUnions(List<Integer> deptIds) throws Exception
    {
        checkText();

        SendFlowUtils.checkReceiver(getSendDocument());

        if (deptIds == null)
            deptIds = getUnionDeptIds();

        SendFlowInstance sendFlowInstance = getSendFlowInstance(true);
        List<Long> receiveIds = exchangeSendService.sendUnions(sendFlowInstance.getDocumentId(), deptIds,
                sendFlowInstance.isTextFinal() ? ReceiveType.unionseal : ReceiveType.union, getBusinessDeptId());

        FlowContext context = getFlowContext();
        if (context.getStep().getState() == FlowStep.DRAFT)
        {
            //送联合办文后不放草稿箱
            context.getStep().setState(FlowStep.NODEAL);
            context.updateStep();

            refreshStepQ();
        }

        sendFlowInstance.setUnionStepId(getStepId());
        sendFlowInstance.setUnionUserId(getUserId());
        getDao().update(sendFlowInstance);

        List<String> deptNames = new ArrayList<String>(receiveIds.size());
        Date now = new Date();
        SystemFlowDao systemFlowDao = getSystemFlowDao();
        for (Long receiveId : receiveIds)
        {
            ReceiveBase receiveBase = getDao().getReceiveBase(receiveId);
            String deptName = receiveBase.getDept().getDeptName();
            deptNames.add(deptName);

            Union union = getDao().getUnion(receiveId);

            Long stepId = Long.valueOf(systemFlowDao.createStepId());
            SystemFlowStep step = systemFlowDao.newFlowStep();
            step.setInstanceId(getInstanceId());
            step.setStepId(stepId);
            step.setGroupId(Long.valueOf(systemFlowDao.createStepGroupId()));
            step.setNodeId(sendFlowInstance.isTextFinal() ? "#unionseal" : "#union");
            step.setPreStepId(getStepId());
            step.setTopStepId(stepId);
            step.setReceiver("$" + deptName);
            step.setState(FlowStep.NOACCEPT);
            step.setSourceName(getBusinessContext().getUserName());
            step.setReceiveTime(now);
            step.setShowTime(now);
            step.setLastStep(true);
            systemFlowDao.add(step);

            union.setStepId(stepId);
            getDao().update(union);
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
     * 显示痕迹
     *
     * @return 转向显示文档的页面
     */
    @Service(url = "/{@class}/{stepId}/backtext/show")
    public String showBackText()
    {
        return "backtext";
    }

    /**
     * 显示痕迹
     *
     * @return 保存痕迹的文件
     * @throws Exception 从数据库获得文档错误
     */
    @Service(url = "/{@class}/{stepId}/backtext/down")
    public InputFile downBackText() throws Exception
    {
        return SendFlowUtils.downBackText(this);
    }

    @Override
    @NotSerialized
    public Integer getSendCreator() throws Exception
    {
        SendFlowInstance sendFlowInstance = getSendFlowInstance(false);
        if (sendFlowInstance == null)
            return getOdFlowInstance().getCreator();
        else if (sendFlowInstance.getCreator() == null)
            return getOdFlowInstance().getCreator();
        else
            return sendFlowInstance.getCreator();
    }

    @Override
    @NotSerialized
    public User getSendCreateUser() throws Exception
    {
        SendFlowInstance sendFlowInstance = getSendFlowInstance(false);
        if (sendFlowInstance == null)
            return getOdFlowInstance().getCreateUser();
        else if (sendFlowInstance.getCreator() == null)
            return getOdFlowInstance().getCreateUser();
        else
            return sendFlowInstance.getCreateUser();
    }

    @NotSerialized
    public Integer getSendCreateDeptId() throws Exception
    {
        SendFlowInstance sendFlowInstance = getSendFlowInstance(false);
        if (sendFlowInstance == null)
            return getOdFlowInstance().getCreateDeptId();
        else if (sendFlowInstance.getCreateDeptId() == null)
            return getOdFlowInstance().getCreateDeptId();
        else
            return sendFlowInstance.getCreateDeptId();
    }

    protected Dept getSendCreateDept() throws Exception
    {
        SendFlowInstance sendFlowInstance = getSendFlowInstance(false);
        if (sendFlowInstance == null)
            return getOdFlowInstance().getCreateDept();
        else if (sendFlowInstance.getCreateDeptId() == null)
            return getOdFlowInstance().getCreateDept();
        else
            return sendFlowInstance.getCreateDept();
    }

    /**
     * 检查发文字号是否被使用
     *
     * @return 发文字号被使用，返回使用的那个公文的标题，否则返回空
     * @throws Exception 数据库查询错误
     */
    @Service
    @ObjectResult
    public String checkSendNumber(String sendNumber) throws Exception
    {
        SendFlowInstance sendFlowInstance0 = getSendFlowInstance(false);

        if (sendFlowInstance0 == null || sendFlowInstance0.getSendNumberReminded() == null ||
                !sendFlowInstance0.getSendNumberReminded())
        {
            SendFlowInstance sendFlowInstance =
                    service.getDao().getSendFlowInstanceBySendNumber(getBusinessDeptId(), sendNumber);

            if (sendFlowInstance != null && !sendFlowInstance.getInstanceId().equals(getInstanceId()))
                return sendFlowInstance.getDocument().getTitle();
        }

        return null;
    }

    /**
     * 设置发文字号重复已经检查过，不需要再检查
     *
     * @throws Exception 数据库查询错误
     */
    @Service
    @ObjectResult
    public void setSendNumberReminded() throws Exception
    {
        SendFlowInstance sendFlowInstance0 = getSendFlowInstance(false);
        sendFlowInstance0.setSendNumberReminded(true);

        getDao().update(sendFlowInstance0);
    }

    @Override
    public String getSendOtherFileName() throws Exception
    {
        return SendFlowUtils.getOtherFileName(this);
    }

    public List<Pair<Integer, String>> getSendDepts() throws Exception
    {
        if (isSended())
        {
            Collection<Integer> authDeptIds = getUserInfo().getAuthDeptIds(OD_SEND_CREATEDEPT);
            if (authDeptIds == null || authDeptIds.isEmpty())
            {
                BusinessContext businessContext = getBusinessContext();
                return Collections.singletonList(new Pair<Integer, String>(
                        businessContext.getBusinessDeptId(), businessContext.getBusinessDeptName()));
            }
            else
            {
                DeptService service = getDeptService();
                List<DeptInfo> depts = service.getDepts(authDeptIds);

                List<Pair<Integer, String>> result = new ArrayList<Pair<Integer, String>>(depts.size());
                for (DeptInfo dept : depts)
                {
                    result.add(new Pair<Integer, String>(dept.getDeptId(), dept.getDeptName()));
                }

                return result;
            }
        }

        return null;
    }
}