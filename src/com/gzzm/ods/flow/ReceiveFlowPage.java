package com.gzzm.ods.flow;

import com.gzzm.ods.document.*;
import com.gzzm.ods.exchange.*;
import com.gzzm.ods.print.*;
import com.gzzm.ods.receipt.*;
import com.gzzm.platform.commons.*;
import com.gzzm.platform.flow.*;
import com.gzzm.platform.form.SystemFormContext;
import com.gzzm.platform.group.Member;
import com.gzzm.platform.organ.*;
import com.gzzm.platform.weboffice.OfficeEditType;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;
import net.cyan.valmiki.flow.*;
import net.cyan.valmiki.form.*;
import net.cyan.valmiki.form.components.FileListData;

import java.util.*;

/**
 * 收文流程处理
 *
 * @author camel
 * @date 11-10-11
 */
@Service(url = "/ods/flow/receive")
public class ReceiveFlowPage extends TurnSendableFlowPage
{
    public static final String OD_RECEIVE_CREATEDEPT = "od_receive_createDept";

    /**
     * 查看来文
     */
    protected static final String SHOW_RECEIVE_TEXT = "showReceiveText";

    /**
     * 录入来文
     */
    protected static final String EDIT_RECEIVE_TEXT = "editReceiveText";

    /**
     * 查看来文
     */
    protected static final String DOWN_RECEIVE_TEXT = "downReceiveText";

    /**
     * 查看来文
     */
    protected static final String IMPORT_TEXT_FROM_RECEIVE = "importTextFromReceive";

    /**
     * 转发公文
     */
    protected static final String TURN_DOCUMENT = "turnDocument";

    /**
     * 填写回执
     */
    protected static final String FILL_RECEIPT = "fillReceipt";

    protected static final DefaultAction SHOW_RECEIVE_TEXT_ACTION = new BaseDefaultAction(SHOW_RECEIVE_TEXT, null);

    protected static final DefaultAction EDIT_RECEIVE_TEXT_ACTION = new BaseDefaultAction(EDIT_RECEIVE_TEXT, null);

    protected static final DefaultAction DOWN_RECEIVE_TEXT_ACTION = new BaseDefaultAction(DOWN_RECEIVE_TEXT, null);

    protected static final DefaultAction IMPORT_TEXT_FROM_RECEIVE_ACTION =
            new BaseDefaultAction(IMPORT_TEXT_FROM_RECEIVE, null);


    @Inject
    private ExchangeReceiveDao receiveDao;

    @NotSerialized
    private Receive receive;

    @NotSerialized
    private ReceiveBase receiveBase;

    @NotSerialized
    private Receipt receiveReceipt;

    private boolean receiveReceiptLoaded;

    /**
     * 收文的文件格式，在收文转成发文时，从收文导入正文的时候使用
     */
    private String receiveTextFormat;

    private OdFlowInstance sourceOdFlowInstance;

    private Integer receiveDeptId;

    public ReceiveFlowPage()
    {
    }

    public Integer getReceiveDeptId() throws Exception
    {
        if (receiveDeptId == null)
        {
            ReceiveBase receiveBase = getReceiveBase();
            if (receiveBase != null)
                receiveDeptId = receiveBase.getDeptId();
        }

        return receiveDeptId;
    }

    public void setReceiveDeptId(Integer receiveDeptId)
    {
        this.receiveDeptId = receiveDeptId;
    }

    public String getReceiveDeptName() throws Exception
    {
        Integer receiveDeptId = getReceiveDeptId();
        if (receiveDeptId != null)
            return getDeptService().getDeptName(receiveDeptId);

        return null;
    }

    public Integer getReceiveTypeId() throws Exception
    {
        if (receiveTypeId == null)
        {
            Receive receive = getReceive();
            if (receive != null)
                receiveTypeId = receive.getReceiveTypeId();
        }
        return receiveTypeId;
    }

    public Long getReceiveId() throws Exception
    {
        OdFlowInstance odFlowInstance = getOdFlowInstance();

        return odFlowInstance.getReceiveId();
    }

    public String getReceiveTextFormat()
    {
        return receiveTextFormat;
    }

    public Receive getReceive() throws Exception
    {
        if (receive == null)
        {
            Long receiveId = getReceiveId();
            if (receiveId != null)
                receive = getDao().getReceive(receiveId);
        }
        return receive;
    }

    public ReceiveBase getReceiveBase() throws Exception
    {
        if (receiveBase == null)
        {
            if (receive != null)
                receiveBase = receive.getReceiveBase();

            if (receiveBase == null)
            {
                Long receiveId = getReceiveId();
                if (receiveId != null)
                    receiveBase = getDao().getReceiveBase(receiveId);
            }
        }

        return receiveBase;
    }

    public Receipt getReceiveReceipt() throws Exception
    {
        if (receiveReceiptLoaded)
            return receiveReceipt;

        if (receiveReceipt == null)
        {
            receiveReceipt = getReceiptDao().getReceiptByDocumentId(getDocumentId());
            if (receiveReceipt != null)
            {
                List<Dept> receiptDepts = receiveReceipt.getReceiptDepts();
                if (receiptDepts.size() > 0)
                {
                    boolean b = false;
                    Integer deptId = getBusinessDeptId();
                    for (Dept receiptDept : receiptDepts)
                    {
                        if (receiptDept.getDeptId().equals(deptId))
                        {
                            b = true;
                            break;
                        }
                    }

                    if (!b)
                        receiveReceipt = null;
                }
            }

            receiveReceiptLoaded = true;
        }

        return receiveReceipt;
    }

    /**
     * 是否手工录入的纸质公文
     *
     * @return 是返回true，不是（包括系统发送过来的公文和外部接口发送过来的公文）返回false
     * @throws Exception 数据库读取收文记录错误
     */
    @NotSerialized
    public boolean isManual() throws Exception
    {
        ReceiveBase receiveBase = getReceiveBase();

        return receiveBase == null || receiveBase.getMethod() == ReceiveMethod.manual;
    }

    /**
     * 能否修改来文，仅当纸质收文并且第一个环节时可以修改来文
     *
     * @return 能修改来文返回true，不能返回false
     * @throws Exception 数据库读取数据错误
     */
    protected boolean isReceiveTextEditable() throws Exception
    {
        return isEditable() && isManual() && getFlowContext().isFirstStep();
    }

    @Override
    protected void initFormContext(SystemFormContext formContext) throws Exception
    {
        super.initFormContext(formContext);

        if (formContext.getFormName() == null)
        {
            formContext.setAuthority(Constants.Receive.RECEIVER, FormRole.READONLY);
            formContext.setAuthority(Constants.Receive.ACCEPTTIME, FormRole.READONLY);

            FormData formData = formContext.getFormData();

            if (isReceiveTextEditable())
            {
                //纸质收文
                if (getReceive() == null)
                {
                    //第一次录入文，记录收文日期和登记人
                    formData.setValue(Constants.Receive.RECEIVER, getBusinessContext().getUserName());
                    formData.setValue(Constants.Receive.ACCEPTTIME, new Date());
                }

                formContext.setAuthority(Constants.Document.TITLE, FormRole.WRITABLE);
                formContext.setAuthority(Constants.Document.TITLE1, FormRole.WRITABLE);
                formContext.setAuthority(Constants.Document.SOURCEDEPT, FormRole.WRITABLE);
                formContext.setAuthority(Constants.Document.SENDNUMBER, FormRole.WRITABLE);
                formContext.setAuthority(Constants.Document.SENDNUMBER1, FormRole.WRITABLE);
                formContext.setAuthority(Constants.Document.SUBJECT, FormRole.WRITABLE);
                formContext.setAuthority(Constants.Document.SECRET, FormRole.WRITABLE);
                formContext.setAuthority(Constants.Document.SECRET1, FormRole.WRITABLE);
                formContext.setAuthority(Constants.Document.SECRET2, FormRole.WRITABLE);
                formContext.setAuthority(Constants.Document.PRIORITY, FormRole.WRITABLE);
                formContext.setAuthority(Constants.Document.PRIORITY1, FormRole.WRITABLE);
                formContext.setAuthority(Constants.Document.PRIORITY2, FormRole.WRITABLE);
                formContext.setAuthority(Constants.Document.PRIORITY3, FormRole.WRITABLE);
                formContext.setAuthority(Constants.Document.PRIORITY4, FormRole.WRITABLE);
                formContext.setAuthority(Constants.Document.SENDCOUNT, FormRole.WRITABLE);
                formContext.setAuthority(Constants.Document.ATTACHMENT, FormRole.WRITABLE);
                formContext.setAuthority(Constants.Document.ATTACHMENT1, FormRole.WRITABLE);
                formContext.setAuthority(Constants.Receive.SENDTIME, FormRole.WRITABLE);
                formContext.setAuthority(Constants.Receive.RECEIVEDEPT, FormRole.WRITABLE);
            }
            else
            {
                if (StringUtils.isEmpty(formData.getString(Constants.Receive.RECEIVER)))
                {
                    String receiverName = getReceiveBase().getReceiverString();
                    if (!StringUtils.isEmpty(receiverName))
                        formData.setValue(Constants.Receive.RECEIVER, receiverName);
                }

                //非纸质收文，控制主表单的权限，一些来自收文记录的数据不允许修改
                formContext.setAuthority(Constants.Document.TITLE, FormRole.READONLY);
                formContext.setAuthority(Constants.Document.TITLE1, FormRole.READONLY);
                formContext.setAuthority(Constants.Document.SOURCEDEPT, FormRole.READONLY);
                formContext.setAuthority(Constants.Document.SENDNUMBER, FormRole.READONLY);
                formContext.setAuthority(Constants.Document.SENDNUMBER1, FormRole.READONLY);
                formContext.setAuthority(Constants.Document.SUBJECT, FormRole.READONLY);
                formContext.setAuthority(Constants.Document.SECRET, FormRole.READONLY);
                formContext.setAuthority(Constants.Document.SECRET1, FormRole.READONLY);
                formContext.setAuthority(Constants.Document.SECRET2, FormRole.READONLY);
//                formContext.setAuthority(Constants.Document.PRIORITY, FormRole.READONLY);
//                formContext.setAuthority(Constants.Document.PRIORITY1, FormRole.READONLY);
//                formContext.setAuthority(Constants.Document.PRIORITY2, FormRole.READONLY);
//                formContext.setAuthority(Constants.Document.PRIORITY3, FormRole.READONLY);
//                formContext.setAuthority(Constants.Document.PRIORITY4, FormRole.READONLY);
                formContext.setAuthority(Constants.Document.SENDCOUNT, FormRole.READONLY);
//                formContext.setAuthority(Constants.Document.ATTACHMENT, FormRole.READONLY);
//                formContext.setAuthority(Constants.Document.ATTACHMENT1, FormRole.READONLY);
                formContext.setAuthority(Constants.Receive.SENDTIME, FormRole.READONLY);
                formContext.setAuthority(Constants.Receive.RECEIVEDEPT, FormRole.READONLY);

                if (getReceiveBase().getDeadline() != null)
                    formContext.setAuthority(Constants.Flow.DEADLINE, FormRole.READONLY);
            }

            Long attachmentId = getDocument().getAttachmentId();
            if (attachmentId != null)
            {
                ComponentData attachmentData = formData.getData(Constants.Document.ATTACHMENT);
                if (attachmentData == null)
                    attachmentData = formData.getData(Constants.Document.ATTACHMENT1);
                if (attachmentData instanceof FileListData)
                {
                    FileListData fileListData = (FileListData) attachmentData;
                    if (StringUtils.isEmpty(fileListData.getFileListId()) || "@".equals(fileListData.getFileListId()))
                    {
                        fileListData.setFileListId(attachmentId.toString());
                        formContext.save();
                    }
                }
            }
        }
    }

    @Override
    protected void extractOdData() throws Exception
    {
        super.extractOdData();

        OfficeDocument document = getDocument();
        FormData formData = getFormContext().getFormData();

        ReceiveBase receiveBase = getReceiveBase();
        if (receiveBase == null)
        {
            //收文记录未创建，创建收文记录
            receiveBase = new ReceiveBase();
            receiveBase.setType(ReceiveType.receive);
            receiveBase.setMethod(ReceiveMethod.manual);
            receiveBase.setState(ReceiveState.accepted);
            receiveBase.setReceiver(getBusinessContext().getUserId());
            receiveBase.setReceiverName(getBusinessContext().getUserName());
            receiveBase.setSendType("纸质");
            receiveBase.setDocumentId(document.getDocumentId());
            receiveBase.setAcceptTime(new Date());

            if (receiveDeptId != null)
                receiveBase.setDeptId(receiveDeptId);
            else
                receiveBase.setDeptId(getBusinessDeptId());

            getDao().add(receiveBase);
            this.receiveBase = receiveBase;

            receive = new Receive();
            receive.setReceiveId(receiveBase.getReceiveId());
            getDao().add(receive);

            getOdFlowInstance().setReceiveId(receiveBase.getReceiveId());
        }

        boolean receiveTextEditable = isReceiveTextEditable();

        if (receiveTextEditable)
        {
            //纸质公文，从表单采集公文内容
            Date sendTime = formData.getValue(Constants.Receive.SENDTIME, Date.class);
            if (sendTime != null)
            {
                if (sendTime instanceof java.sql.Date)
                    sendTime = new Date(sendTime.getTime());

                if (sendTime.equals(DateUtils.truncate(sendTime)))
                {
                    Calendar calendar1 = Calendar.getInstance();
                    Calendar calendar = Calendar.getInstance();

                    calendar1.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
                    calendar1.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
                    calendar1.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
                    sendTime = calendar1.getTime();
                }

                receiveBase.setSendTime(sendTime);
            }

            String title = formData.getString(Constants.Document.TITLE);
            if (StringUtils.isEmpty(title))
                title = formData.getString(Constants.Document.TITLE1);
            if (title != null)
                document.setTitle(title.replaceAll("[\\r|\\n]+", " "));
            document.setSourceDept(formData.getString(Constants.Document.SOURCEDEPT));
            String sendNumber = formData.getString(Constants.Document.SENDNUMBER);
            if (StringUtils.isEmpty(sendNumber))
                sendNumber = formData.getString(Constants.Document.SENDNUMBER1);
            document.setSendNumber(sendNumber);
            document.setSubject(formData.getString(Constants.Document.SUBJECT));
            String secret = formData.getString(Constants.Document.SECRET);
            if (StringUtils.isEmpty(secret))
                secret = formData.getString(Constants.Document.SECRET1);
            if (StringUtils.isEmpty(secret))
                secret = formData.getString(Constants.Document.SECRET2);
            document.setSecret(secret);
            String priority = formData.getString(Constants.Document.PRIORITY);
            if (StringUtils.isEmpty(priority))
                priority = formData.getString(Constants.Document.PRIORITY1);
            if (StringUtils.isEmpty(priority))
                priority = formData.getString(Constants.Document.PRIORITY2);
            if (StringUtils.isEmpty(priority))
                priority = formData.getString(Constants.Document.PRIORITY3);
            if (StringUtils.isEmpty(priority))
                priority = formData.getString(Constants.Document.PRIORITY4);
            document.setPriority(priority);
            document.setSendCount(formData.getInt(Constants.Document.SENDCOUNT));


            //采集扩展属性
            for (DocumentAttribute documentAttribute : getDao().getDocumentAttributes())
            {
                String attributeName = documentAttribute.getAttributeName();
                document.getAttributes().put(attributeName, formData.getString(attributeName));
            }

            if (receiveDeptId != null)
                receiveBase.setDeptId(receiveDeptId);
        }

        if (isSended())
        {
            getFlowContext().getInstance().setTitle(getSendDocument().getTitle() + "\n" + document.getTitle());
        }
        else if (receiveTextEditable)
        {
            getFlowContext().getInstance().setTitle(document.getTitle());
        }

        Receive receive = getReceive();
        receive.setSerial(getOdFlowInstance().getSerial());
        receive.setReceiveTypeId(receiveTypeId);

        for (ReceiveAttribute receiveAttribute : receiveDao.getReceiveAttributes())
        {
            String attributeName = receiveAttribute.getAttributeName();
            receive.getAttributes().put(attributeName, formData.getString(attributeName));
        }
    }

    @Override
    protected void saveData() throws Exception
    {
        if (!isReceiveTextEditable())
        {
            FormData formData = getFormContext().getFormData();
            ComponentData attachmentData = formData.getData(Constants.Document.ATTACHMENT);
            if (attachmentData == null)
                attachmentData = formData.getData(Constants.Document.ATTACHMENT1);
            if (attachmentData instanceof FileListData)
            {
                FileListData fileListData = (FileListData) attachmentData;
                if (fileListData != null)
                {
                    String fileListId = fileListData.getFileListId();
                    if (fileListId == null)
                        fileListData.setFileListId("@");
                    else if (!fileListId.startsWith("@"))
                        fileListData.setFileListId("@" + fileListId);
                }
            }
        }

        super.saveData();
    }

    @Override
    protected void saveOdData() throws Exception
    {
        super.saveOdData();

        OdFlowDao dao = getDao();

        if (isReceiveTextEditable())
        {
            OfficeDocument document = getDocument();
            FormData formData = getFormContext().getFormData();

            //采集附件信息附件
            if (document.getAttachmentId() == null)
            {
                //仅当上传了附件之后，采集附件
                ComponentData attachmentData = formData.getData(Constants.Document.ATTACHMENT);
                if (attachmentData == null)
                    attachmentData = formData.getData(Constants.Document.ATTACHMENT1);
                if (attachmentData instanceof FileListData)
                {
                    FileListData fileListData = (FileListData) attachmentData;
                    if (fileListData != null && fileListData.getFileListId() != null)
                    {
                        String[] s = fileListData.getFileListId().split(",");
                        if (s.length > 0)
                        {
                            String fileListId = s[0];
                            if (fileListId.startsWith("@"))
                                fileListId = fileListId.substring(1);

                            if (fileListId.length() > 0)
                                document.setAttachmentId(Long.valueOf(fileListData.getFileListId()));
                        }
                    }
                }
            }

            dao.update(getReceiveBase());
            dao.update(document);
        }

        dao.update(getReceive());
    }

    @Override
    protected List<Action> createActions() throws Exception
    {
        List<Action> actions = super.createActions();

        //增加查看来文的按钮
        boolean showTextExists = false;
        boolean editTextExists = false;

        int n = actions.size();
        boolean receiveTextEditable = isReceiveTextEditable();
        for (int i = 0; i < n; i++)
        {
            Action action = actions.get(i);

            String actionId = action.getActionId();
            if (SHOW_RECEIVE_TEXT.equals(actionId))
            {
                if (receiveTextEditable)
                {
                    //纸质收文的第一个环节，将查看收文改成录入来文
                    actions.set(i, EDIT_RECEIVE_TEXT_ACTION);
                }
                else
                {
                    //添加下载来文的按钮
                    actions.add(i + 1, DOWN_RECEIVE_TEXT_ACTION);
                }

                showTextExists = true;
                break;
            }
            else if (EDIT_RECEIVE_TEXT.equals(actionId))
            {
                if (!isEditable() && !isManual())
                {
                    //不是可写状态，将编辑来文改成查看来文
                    actions.set(i, SHOW_RECEIVE_TEXT_ACTION);
                    actions.add(i + 1, DOWN_RECEIVE_TEXT_ACTION);
                }

                editTextExists = true;
                break;
            }
        }

        if (!showTextExists && !editTextExists)
        {
            //在前面加入查看来文或者录入来文的按钮
            if (receiveTextEditable)
            {
                actions.add(0, EDIT_RECEIVE_TEXT_ACTION);
            }
            else
            {
                actions.add(0, DOWN_RECEIVE_TEXT_ACTION);
                actions.add(0, SHOW_RECEIVE_TEXT_ACTION);
            }
        }


        //已发文，需要增加取消发文按钮
        if (isSended() && getUserId().equals(getSendFlowInstance(true).getCreator()))
        {
            n = actions.size();
            boolean cancelSendExists = false;
            int editSendTextIndex = -1;
            for (int i = 0; i < n; i++)
            {
                Action action = actions.get(i);

                String actionId = action.getActionId();
                if (CANCEL_TURN_SEND.equals(actionId))
                {
                    cancelSendExists = true;
                    break;
                }
                else if (EDIT_SEND_TEXT.equals(actionId))
                {
                    editSendTextIndex = i;
                }
            }

            if (!cancelSendExists && editSendTextIndex >= 0)
            {
                actions.add(CANCEL_TURN_SEND_ACTION);
            }
        }

        return actions;
    }

    @Override
    public boolean accept(Action action) throws Exception
    {
        String id = action.getActionId();

        if (TURN_DOCUMENT.equals(id))
        {
            Collection<Integer> turnAuthDeptIds = getUserInfo().getAuthDeptIds("od_turn_dept_select");
            return turnAuthDeptIds == null || turnAuthDeptIds.size() > 0;
        }
        else if (FILL_RECEIPT.equals(id) && (action instanceof DefaultAction))
        {
            return getReceiveReceipt() != null;
        }

        return super.accept(action);
    }

    @Override
    public Action[] getTextActions(String type, boolean editable) throws Exception
    {
        Action[] actions = super.getTextActions(type, editable);
        if ("send".equals(type) && getTextEditType() == OfficeEditType.editable && isEditable() && isNewSendText())
        {
            DocumentText text = getDocument().getText();
            if (text != null)
            {
                Action[] actions1 = new Action[actions.length + 1];
                actions1[0] = IMPORT_TEXT_FROM_RECEIVE_ACTION;
                System.arraycopy(actions, 0, actions1, 1, actions.length);

                actions = actions1;

                receiveTextFormat = text.getType();
            }
        }

        return actions;
    }

    @Override
    public String getActionName(Action action) throws Exception
    {
        if (action instanceof DefaultAction && FILL_RECEIPT.equals(action.getActionId()))
        {
            Object name = action.getActionNameObject();
            if (name == null || "".equals(name))
            {
                Receipt receipt = getReceiveReceipt();
                if (receipt != null)
                    return Tools.getMessage("com.gzzm.ods.receipt.ReceiptComponent." + receipt.getType() + ".fill");
                else
                    return null;
            }
        }

        return super.getActionName(action);
    }

    @Override
    public String getActionRemark(Action action) throws Exception
    {
        if (action instanceof DefaultAction && FILL_RECEIPT.equals(action.getActionId()))
        {
            Object remark = action.getProperty("remark");
            if (remark == null || "".equals(remark))
            {
                Receipt receipt = getReceiveReceipt();
                return Tools.getMessage("com.gzzm.ods.receipt.ReceiptComponent." + receipt.getType() + ".fill_remark");
            }
        }

        return super.getActionRemark(action);
    }

    @Override
    protected Object afterSend(Map<String, List<List<NodeReceiver>>> receiverMap, String actionId, Object result)
            throws Exception
    {
        result = super.afterSend(receiverMap, actionId, result);

        Receive receive = getReceive();
        OdFlowInstance odFlowInstance = getOdFlowInstance();

        if (odFlowInstance.getDealer() != null && !odFlowInstance.getDealer().equals(receive.getDealer()))
        {
            receive.setDealer(odFlowInstance.getDealer());
            receive.setDealDeptId(odFlowInstance.getDealDeptId());

            getDao().update(receive);
        }

        return result;
    }

    @Override
    public OfficeDocument getDocument(String type) throws Exception
    {
        if ("receive".equals(type))
            return getDocument();

        return super.getDocument(type);
    }

    @Override
    public OfficeEditType getEditType(OfficeDocument document, String type) throws Exception
    {
        if ("receive".equals(type))
        {
            List<Action> actions = getActions();
            for (Action action : actions)
            {
                if (EDIT_RECEIVE_TEXT.equals(action.getActionId()))
                    return OfficeEditType.editable;
            }

            return OfficeEditType.readOnly;
        }

        return super.getEditType(document, type);
    }

    @Override
    public boolean isNewSendText() throws Exception
    {
        if (!isSended())
            return false;

        SendFlowInstance sendFlowInstance = getSendFlowInstance(false);

        return sendFlowInstance.getSendStepId().equals(getStepId()) && getState() == FlowStep.NODEAL;
    }

    @Override
    protected Object endOdFlow(String actionId) throws Exception
    {
        Receipt receipt = getReceiveReceipt();

        if (receipt != null)
        {
            ReceiptReply receiptReply = getReceiptDao().getReceiptReply(receipt.getReceiptId(), getBusinessDeptId());
            if (receiptReply == null || receiptReply.getReplied() == null || !receiptReply.getReplied())
            {
                throw new NoErrorException("ods.flow.fillReceiptFirst");
            }
        }

        super.endOdFlow(actionId);

        Receive receive = getReceive();
        if (receive.getStepId() != null)
        {
            String stepId = receive.getStepId().toString();

            //将主办部门的流程中对应步骤表示为已完成
            SystemFlowDao systemFlowDao = getSystemFlowDao();
            FlowStep step = new FlowStep();
            step.setStepId(stepId);
            step.setDisposeTime(new Date());
            step.setState(FlowStep.DEALED);
            systemFlowDao.updateStep(step);
        }

        //通知主办单位
        exchangeNotifyServiceProvider.get().sendEndMessage(getReceiveBase());

        return result;
    }

    /**
     * 转发公文
     *
     * @param receivers 收文单位列表，即转发的目标
     * @throws Exception 转发公文错误，由数据库操作错误引起
     */
    @Service(method = HttpMethod.post)
    @ObjectResult
    public void turnDocument(List<Member> receivers) throws Exception
    {
        Long sendId = exchangeSendService.turnDocument(
                getReceiveId(), receivers, getUserId(), getBusinessContext().getDeptId());

        ExchangeSendDao exchangeSendDao = exchangeSendService.getDao();
        Send send = exchangeSendDao.getSend(sendId);
        List<ReceiveBase> receives = exchangeSendDao.getReceivesByDocumentId(send.getDocumentId());

        SystemFlowDao systemFlowDao = getSystemFlowDao();
        Date now = new Date();
        for (ReceiveBase receiveBase : receives)
        {
            Long stepId = Long.valueOf(systemFlowDao.createStepId());

            SystemFlowStep step = systemFlowDao.newFlowStep();
            step.setInstanceId(getInstanceId());
            step.setStepId(stepId);
            step.setGroupId(Long.valueOf(systemFlowDao.createStepGroupId()));
            step.setNodeId("#turn");
            step.setPreStepId(stepId);
            step.setTopStepId(stepId);
            step.setReceiver("$" + receiveBase.getDept().getDeptName());
            step.setState(FlowStep.NOACCEPT);
            step.setSourceName(getUserName());
            step.setReceiveTime(now);
            step.setShowTime(now);
            step.setLastStep(true);
            systemFlowDao.add(step);

            Receive receive = new Receive();
            receive.setReceiveId(receiveBase.getReceiveId());
            receive.setStepId(stepId);
            exchangeSendDao.update(receive);
        }

        exchangeNotifyServiceProvider.get()
                .sendMessageWithSendId(sendId, getBusinessContext().getUserId(), null, false);
    }

    /**
     * 转发文
     *
     * @param importTextFromReceive 是否从收文导入正文
     * @throws Exception 转发文错误，由数据库操作错误引起
     */
    @Service
    @ObjectResult
    public void turnSend(boolean importTextFromReceive) throws Exception
    {
        OdFlowDao dao = getDao();

        SendFlowInstance sendFlowInstance = getSendFlowInstance(true);
        sendFlowInstance.setCreator(getUserId());
        sendFlowInstance.setCreateDeptId(getBusinessContext().getDeptId());
        sendFlowInstance.setSended(true);
        sendFlowInstance.setSendStepId(getStepId());
        dao.update(sendFlowInstance);

        if (importTextFromReceive)
        {
            DocumentText receiveText = getDocument().getText();
            if (receiveText != null)
            {
                OfficeDocument sendDocument = getSendDocument();
                DocumentText sendText = sendDocument.getText();

                if (sendText == null)
                {
                    sendText = new DocumentText();
                    sendText.setTextBody(receiveText.getTextBody());
                    sendText.setFileSize(receiveText.getFileSize());
                    sendText.setType(receiveText.getType());
                    dao.add(sendText);

                    sendDocument.setTextId(sendText.getTextId());
                    sendDocument.setLastTime(new Date());
                    dao.update(sendDocument);
                }
                else
                {
                    sendText.setTextBody(receiveText.getTextBody());
                    sendText.setFileSize(receiveText.getFileSize());
                    sendText.setType(receiveText.getType());
                    dao.update(sendText);

                    sendDocument.setLastTime(new Date());
                    dao.update(sendDocument);
                }
            }
        }
    }

    /**
     * 根据输入的字符串查询已经接收的公文
     *
     * @param deptId 当前部门ID
     * @param s      输入的字符串
     * @return 返回和字符串匹配的公文列表
     * @throws Exception 数据库查询错误
     */
    @Service
    @ObjectResult
    public List<ReceiveItem> queryReceives(Integer deptId, String s) throws Exception
    {
        List<ReceiveBase> receiveBasess = getDao().queryReceives(deptId, "%" + s + "%");
        List<ReceiveItem> receiveItems = new ArrayList<ReceiveItem>(receiveBasess.size());

        for (ReceiveBase receive : receiveBasess)
            receiveItems.add(new ReceiveItem(receive));

        return receiveItems;
    }

    @Redirect
    @Service(url = "/ods/flow/receive/{stepId}/receipt")
    public String showReceipt() throws Exception
    {
        Receipt receipt = getReceiveReceipt();

        if (receipt == null)
            return null;

        ReceiptComponent component = ReceiptComponents.getComponent(receipt.getType());

        if (component != null)
        {
            boolean readOnly = true;
            List<Action> actions = getFlowContext().getExecutableActions(this);
            for (Action action : actions)
            {
                if (FILL_RECEIPT.equals(action.getActionId()))
                {
                    readOnly = false;
                    break;
                }
            }

            return component.getFillUrl(receipt, getBusinessDeptId(), readOnly);
        }

        return null;
    }

    @Override
    protected List<OfficeDocument> getDocuments() throws Exception
    {
        if (isSended())
        {
            return Arrays.asList(getDocument(), getSendDocument());
        }
        else
        {
            return super.getDocuments();
        }
    }

    private OdFlowInstance getSourceOdFlowInstance() throws Exception
    {
        if (sourceOdFlowInstance == null)
        {
            Receive receive = getReceive();
            if (receive != null)
            {
                Long sourceReceiveId = receive.getSourceReceiveId();
                if (sourceReceiveId != null)
                {
                    sourceOdFlowInstance = getDao().getOdFlowInstanceByReceiveId(sourceReceiveId);
                }
            }
        }

        return sourceOdFlowInstance;
    }

    @Override
    protected Long getBodyId(String name) throws Exception
    {
        if ("source".equals(name))
        {
            OdFlowInstance sourceOdFlowInstance = getSourceOdFlowInstance();
            if (sourceOdFlowInstance != null)
            {
                SystemFlowInstance sourceSystemFlowInstance =
                        getSystemFlowDao().getFlowInstance(sourceOdFlowInstance.getInstanceId());

                if (sourceSystemFlowInstance != null)
                {
                    return sourceSystemFlowInstance.getBodyId();
                }
            }

            return null;
        }

        return super.getBodyId(name);
    }

    @Override
    protected FormRole getRole(SystemFormContext formContext) throws Exception
    {
        if ("source".equals(formContext.getFormName()))
        {
            return formContext.getForm().getRole(Constants.Receive.TURN);
        }

        return super.getRole(formContext);
    }

    @NotSerialized
    public List<PageItem> getSourcePageItems() throws Exception
    {
        return getPageItems("source");
    }

    @Override
    public void executePageItem(PageItem item) throws Exception
    {
        if ("source".equals(item.getFormContext().getFormName()))
        {
            try
            {
                OdFlowInstance sourceOdFlowInstance = getSourceOdFlowInstance();
                if (sourceOdFlowInstance != null)
                    getBusinessContext().setBusinessDeptName(sourceOdFlowInstance.getDept().getDeptName());

                super.executePageItem(item);
            }
            finally
            {
                getBusinessContext().setBusinessDeptName(null);
            }
        }
        else
        {
            super.executePageItem(item);
        }
    }

    @Override
    public List<PrintInfo> getPrintTemplates() throws Exception
    {
        List<PrintInfo> printInfos = super.getPrintTemplates();

        PrintService printService = getPrintService();

        List<PageItem> pageItems = getSourcePageItems();
        if (pageItems != null && pageItems.size() > 0)
        {
            OdFlowInstance sourceOdFlowInstance = getSourceOdFlowInstance();

            List<PrintTemplate> printTemplates =
                    printService.getPrintTemplates(sourceOdFlowInstance.getBusinessId(),
                            sourceOdFlowInstance.getDeptId(), sourceOdFlowInstance.getType());

            for (PrintTemplate template : printTemplates)
            {
                if (template.getFormType() != FormType.send)
                {
                    printInfos.add(new PrintInfo(template.getTemplateId(),
                            sourceOdFlowInstance.getDept().getDeptName() + "" + template.getShowName(), "source"));
                }
            }
        }

        return printInfos;
    }

    @Override
    protected InputFile downFormPrint(Integer printTemplateId, String printFormName, boolean toDoc) throws Exception
    {
        if ("source".equals(printFormName))
        {
            OdFlowInstance sourceOdFlowInstance = getSourceOdFlowInstance();

            if (sourceOdFlowInstance != null)
            {
                getBusinessContext().setBusinessDeptName(sourceOdFlowInstance.getDept().getDeptName());
            }
        }

        return super.downFormPrint(printTemplateId, printFormName, toDoc);
    }

    public String getReceiveOtherFileName() throws Exception
    {
        OfficeDocument document = getDocument();
        if (document != null)
        {
            DocumentText text = document.getText();
            if (text != null)
                return text.getOtherFileName();
        }

        return null;
    }

    @Override
    protected void extractMessageFromForm() throws Exception
    {
        super.extractMessageFromForm();

        if (isSended())
        {
            extractMessageFromForm("send");
        }
    }

    @Override
    protected void getQRContent(StringBuilder buffer) throws Exception
    {
        OfficeDocument document = getDocument();
        buffer.append("收文日期:").append(DateUtils.toString(getReceiveBase().getAcceptTime()));
        buffer.append("\r\n文件标题:").append(document.getTitle());
        buffer.append("\r\n来文单位:").append(document.getSourceDept());
        buffer.append("\r\n登记人:").append(getOdFlowInstance().getCreateUser().getUserName());
    }

    public List<Pair<Integer, String>> getReceiveDepts() throws Exception
    {
        Collection<Integer> authDeptIds = getUserInfo().getAuthDeptIds(OD_RECEIVE_CREATEDEPT);
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

    /**
     * 检查收文编号是否被使用
     *
     * @return 收文编号被使用，返回使用的那个公文的标题，否则返回空
     * @throws Exception 数据库查询错误
     */
    @Service
    @ObjectResult
    public String checkSerial(String serial) throws Exception
    {
        Receive receive =
                receiveDao.getReceiveBySerial(getBusinessDeptId(), serial);

        if (receive != null && !receive.getReceiveId().equals(getReceiveId()))
            return receive.getReceiveBase().getDocument().getTitle();

        return null;
    }
}
