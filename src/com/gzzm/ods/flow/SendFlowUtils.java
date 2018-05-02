package com.gzzm.ods.flow;

import com.gzzm.ods.document.*;
import com.gzzm.ods.exchange.*;
import com.gzzm.ods.redhead.RedHeadTitleInfo;
import com.gzzm.platform.commons.*;
import com.gzzm.platform.flow.*;
import com.gzzm.platform.form.SystemFormContext;
import com.gzzm.platform.group.Member;
import com.gzzm.platform.organ.Dept;
import com.gzzm.platform.weboffice.OfficeEditType;
import net.cyan.commons.util.*;
import net.cyan.commons.util.io.CacheData;
import net.cyan.nest.annotation.Inject;
import net.cyan.valmiki.flow.*;
import net.cyan.valmiki.form.*;
import net.cyan.valmiki.form.components.*;

import java.util.*;

/**
 * @author camel
 * @date 2014/8/28
 */
public final class SendFlowUtils implements SendFlowActions
{
    @Inject
    protected static Provider<ExchangeNotifyService> exchangeNotifyServiceProvider;

    private SendFlowUtils()
    {
    }

    public static void initBusinessContext(SendFlowElement element, BusinessContext businessContext) throws Exception
    {
        businessContext.put("$sended", element.isSended());
        businessContext.put("$textFinal", element.isTextFinal());
    }

    public static SendFlowInstance getSendFlowInstance(SendFlowElement element, boolean create) throws Exception
    {
        SendFlowInstance sendFlowInstance = element.getSendFlowInstance();
        if (sendFlowInstance == null)
        {
            Long instanceId = element.getInstanceId();

            if (!element.isSendFlowInstanceLoaded())
            {
                element.setSendFlowInstanceLoaded();

                sendFlowInstance = element.getDao().getSendFlowInstance(instanceId);
                element.putSendFlowInstance(sendFlowInstance);
            }
            else
            {
                sendFlowInstance = element.getSendFlowInstance();
            }

            if (sendFlowInstance == null && create)
            {
                //发文实例未创建，创建之
                sendFlowInstance = new SendFlowInstance();

                sendFlowInstance.setInstanceId(instanceId);
                sendFlowInstance.setTextFinal(false);
                sendFlowInstance.setSended(true);

                sendFlowInstance.setCreator(element.getUserId());
                sendFlowInstance.setCreateDeptId(element.getUserInfo().getDeptId());

                OfficeDocument sendDocument = element.createSendDocument();
                sendFlowInstance.setDocumentId(sendDocument.getDocumentId());

                element.putSendDocument(sendDocument);

                SystemFormContext sendFromContext = element.createSendFormContext();

                if (sendFromContext != null)
                {
                    Long bodyId = sendFromContext.getBodyId();

                    sendFlowInstance.setBodyId(bodyId);
                    element.getDao().add(sendFlowInstance);

                    InstanceBody instanceBody = new InstanceBody();
                    instanceBody.setInstanceId(instanceId);
                    instanceBody.setBodyId(bodyId);
                    instanceBody.setTitle("发文表单");
                    element.getDao().save(instanceBody);
                }
                else
                {
                    element.getDao().add(sendFlowInstance);
                }

                element.putSendFlowInstance(sendFlowInstance);
            }
        }

        return sendFlowInstance;
    }

    public static boolean isSended(SendFlowElement element) throws Exception
    {
        SendFlowInstance sendFlowInstance = element.getSendFlowInstance(false);

        return sendFlowInstance != null && sendFlowInstance.isSended() != null && sendFlowInstance.isSended();
    }

    public static boolean isTextFinal(SendFlowElement element) throws Exception
    {
        if (element.isSended())
        {
            SendFlowInstance sendFlowInstance = element.getSendFlowInstance(false);
            return sendFlowInstance != null && sendFlowInstance.isTextFinal() != null && sendFlowInstance.isTextFinal();
        }

        return false;
    }

    public static boolean hasAction(SendFlowElement element, String actionId) throws Exception
    {
        return FlowPage.hasAction(element.getActions(), actionId);
    }

    public static boolean hasEditSendTextAction(SendFlowElement element) throws Exception
    {
        return hasAction(element, EDIT_SEND_TEXT);
    }

    public static boolean hasFinalizeTextAction(SendFlowElement element) throws Exception
    {
        return hasAction(element, FINALIZE_TEXT);
    }

    public static Action[] getTextActions(SendFlowElement element, String type, boolean editable) throws Exception
    {
        if ("send".equals(type))
        {
            if (editable)
            {
                if (element.canFinalizeText())
                {
                    //有成文权限，除了保存按钮以外，添加套红，成文按钮
                    return new Action[]{FlowPage.SAVE_ACTION, SendableFlowPage.HITCH_REDHEAD_ACTION,
                            SendableFlowPage.FINALIZE_TEXT_ACTION, SendableFlowPage.UN_FINALIZE_TEXT_ACTION};
                }
                else if (element.getSendFormContext() != null &&
                        element.getSendFormContext().isWritable(Constants.Send.SELECTREDHEAD))
                {
                    //有选择红头模板权限，除了保存按钮以外，添加套红按钮
                    return new Action[]{FlowPage.SAVE_ACTION, SendableFlowPage.HITCH_REDHEAD_ACTION};
                }
                else
                {
                    //没有成文权限，仅留保存按钮，没有套红和成文按钮
                    return new Action[]{FlowPage.SAVE_ACTION};
                }
            }
            else
            {
                return new Action[]{};
            }
        }
        else if ("final".equals(type))
        {
            return new Action[]{FlowPage.SAVE_ACTION, FINALIZE_TEXT_ACTION};
        }
        else if (editable)
        {
            return new Action[]{FlowPage.SAVE_ACTION};
        }
        else
        {
            return new Action[]{};
        }
    }

    public static void initActions(SendFlowElement element, List<Action> actions) throws Exception
    {
        if (element.isSended())
        {
            //添加查看正文的按钮
            int showTextIndex = -1;

            int n = actions.size();
            for (int i = 0; i < n; i++)
            {
                Action action = actions.get(i);

                String actionId = action.getActionId();
                if (SendableFlowPage.SHOW_SEND_TEXT.equals(actionId))
                {
                    showTextIndex = i;
                }
                else if (SendableFlowPage.EDIT_SEND_TEXT.equals(actionId))
                {
                    SendFlowInstance sendFlowInstance = element.getSendFlowInstance();
                    Boolean sentOut = sendFlowInstance.getSentOut();
                    if (!element.isEditable() || sentOut != null && sentOut)
                    {
                        //只读状态或者流程结束状态，不允许编辑正文，把编辑正文按钮换成查看正文
                        actions.set(i, SendableFlowPage.SHOW_SEND_TEXT_ACTION);
                    }

                    showTextIndex = i;
                }
                else if (SendableFlowPage.SEND_UNIONS.equals(actionId))
                {
                    //添加送联合发文单位盖章的按钮
                    actions.add(++i, SendableFlowPage.SEND_UNIONS_SEAL_ACTION);
                    n++;
                }
                else if (SendableFlowPage.FINALIZE_TEXT.equals(actionId))
                {
                    //添加取消成文按钮
                    actions.add(++i, SendableFlowPage.UN_FINALIZE_TEXT_ACTION);
                    n++;
                }
                else if (SendableFlowPage.EDIT_RECEIPT.equals(actionId))
                {
                    //添加删除回执按钮
                    actions.add(++i, SendableFlowPage.REMOVE_RECEIPT_ACTION);
                    n++;
                }
            }

            if (showTextIndex < 0 && element.isSended())
            {
                SendFlowInstance sendFlowInstance = element.getSendFlowInstance(false);

                OfficeDocument document = sendFlowInstance.getDocument();
                if (document != null)
                {
                    DocumentText text = document.getText();
                    if (text != null && text.getFileSize() != null && text.getFileSize() > 0)
                    {
                        //在前面加入查看正文的按钮
                        String nodeId = element.getNodeId();

                        actions.add(0, element.isEditable() && element.getFlowContext().getInstance().getState() == 0 &&
                                !FlowNode.PASS.equals(nodeId) && !element.isTextFinal() &&
                                FlowNode.COPY.equals(nodeId) ? SendableFlowPage.EDIT_SEND_TEXT_ACTION :
                                SendableFlowPage.SHOW_SEND_TEXT_ACTION);
                        showTextIndex = 0;
                    }
                }
            }

            actions.add(showTextIndex + 1, SendableFlowPage.SHOW_BACK_TEXT_ACTION);
        }
    }

    public static void finalizeText(SendFlowElement element) throws Exception
    {
        OfficeDocument document = element.getSendDocument();
        SendFlowInstance instance = element.getSendFlowInstance(true);
        DocumentText documentText = document.getText();
        OdFlowDao dao = element.getDao();

        Inputable text = null;
        if (element.getText() != null)
            text = element.getText().getInputable();

        if (text != null && text.size() > 0)
        {
            CacheData cacheData = new CacheData();
            IOUtils.copyStream(text.getInputStream(), cacheData);
            text = cacheData;
        }

        if (documentText == null)
        {
            documentText = new DocumentText();
            documentText.setTextBody(text.getInputStream());
            documentText.setUnsealedText(text.getInputStream());
            documentText.setFileSize(text.size());
            dao.add(documentText);

            document.setTextId(documentText.getTextId());
        }
        else
        {
            instance.setBackTextBody(InputFile.toInputable(documentText.getTextBody()));
            instance.setBackTextType(documentText.getType());

            if (text != null && text.size() > 0)
            {
                documentText.setTextBody(text.getInputStream());
                documentText.setUnsealedText(text.getInputStream());
                documentText.setFileSize(text.size());
                dao.update(documentText);
            }
        }

        document.setFinalTime(new Date());
        dao.update(document);

        instance.setTextFinal(true);
        instance.setFinalTime(new Date());
        dao.update(instance);

        //保存文档备份
        DocumentTextBak bak = new DocumentTextBak();
        bak.setTextId(documentText.getTextId());
        bak.setUserId(element.getUserId());
        bak.setSaveTime(new Date());
        if (text != null)
            bak.setTextBody(text.getInputStream());
        bak.setFileSize(documentText.getFileSize());
        dao.add(bak);
    }

    public static String unFinalizeText(SendFlowElement element) throws Exception
    {
        OfficeDocument document = element.getSendDocument();
        SendFlowInstance instance = element.getSendFlowInstance(true);
        DocumentText documentText = document.getText();
        OdFlowDao dao = element.getDao();

        if (instance.getBackTextBody() != null)
        {
            documentText.setTextBody(instance.getBackTextBody().getInputStream());
            documentText.setFileSize(instance.getBackTextBody().size());

            if (!StringUtils.isEmpty(instance.getBackTextType()))
                documentText.setType(instance.getBackTextType());

            dao.update(documentText);
        }

        instance.setTextFinal(false);
        instance.setFinalTime(Null.Timestamp);
        dao.update(instance);

        document.setFinalTime(Null.Timestamp);
        dao.update(document);

        return instance.getBackTextType();
    }

    public static Integer getRedHeadId(FormContext formContext)
    {
        if (formContext == null)
            return null;

        SimpleSelectableData<RedHeadTitleInfo> data = formContext.getFormData().getData(Constants.Send.REDHEAD);

        if (data != null && data.getItem() != null)
        {
            return data.getItem().getRedHeadId();
        }

        return null;
    }

    public static Integer getRedHeadId(SendFlowElement element) throws Exception
    {
        return getRedHeadId(element.getSendFormContext());
    }

    @SuppressWarnings("unchecked")
    public static ReceiverListList getReceiverListFromForm(SendFlowElement element) throws Exception
    {
        ReceiverListList receiverListList = new ReceiverListList();

        SystemFormContext sendFormContext = element.getSendFormContext();
        if (sendFormContext != null)
        {
            FormData formData = sendFormContext.getFormData();

            //查找所有部门选择控件
            for (ComponentData data : formData)
            {
                if (data != null)
                {
                    String dataName = data.getName();
                    if (!Constants.Send.UNIONDEPTS.equals(dataName) &&
                            !Constants.Receipt.ACCEPTDEPT.equals(dataName))
                    {
                        //不采集联合发文单位，联合发文单位不是收文部门
                        if (data instanceof MultipleSelectableData)
                        {
                            MultipleSelectableData multipleSelectableData = (MultipleSelectableData) data;
                            if (multipleSelectableData.getItemClass() == Member.class)
                            {
                                //此控件为部门选择控件
                                List<Member> receivers = multipleSelectableData.getItems();

                                if (receivers != null && receivers.size() > 0)
                                {
                                    //创建一个接收者列表
                                    ReceiverList receiverList = receiverListList.getReceiverList(dataName);
                                    if (receiverList == null)
                                    {
                                        receiverList = new ReceiverList(dataName);
                                        receiverListList.addReceiverList(receiverList);
                                    }

                                    for (Member receiver : receivers)
                                    {
                                        receiverList.addReceiver(receiver);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return receiverListList;
    }

    public static String[] getBookmarkTexts(SendFlowElement element, String[] bookmarks) throws Exception
    {
        SystemFormContext formContext = element.getSendFormContext();
        Map<String, Object> map = formContext.getTextContext();

        String[] texts = new String[bookmarks.length];

        for (int i = 0; i < bookmarks.length; i++)
        {
            String bookmark = bookmarks[i];

            String text;
            if (Constants.RedHead.BUSINESSDEPTNAME.equals(bookmark))
            {
                text = formContext.getBusinessDeptName();
            }
            else if (Constants.RedHead.RECEIVERS.equals(bookmark))
            {
                List<ReceiverList> receiverLists = element.getReceiverListFromForm().getReceiverLists();
                if (receiverLists == null)
                {
                    text = "";
                }
                else
                {
                    StringBuilder buffer = new StringBuilder();
                    for (ReceiverList receiverList : receiverLists)
                    {
                        if (!Constants.RedHead.MAINSEND.equals(receiverList.getSendType()))
                        {
                            if (buffer.length() > 0)
                                buffer.append("\n");

                            buffer.append(receiverList.toString());
                        }
                    }

                    text = buffer.toString();
                }
            }
            else if (Constants.RedHead.MAINSEND.equals(bookmark))
            {
                text = "";
                List<ReceiverList> receiverLists = element.getReceiverListFromForm().getReceiverLists();
                if (receiverLists != null)
                {
                    for (ReceiverList receiverList : receiverLists)
                    {
                        if (Constants.RedHead.MAINSEND.equals(receiverList.getSendType()))
                        {
                            text = receiverList.getReceivers().toString();
                        }
                    }
                }
            }
            else if (Constants.RedHead.SIGNER.equals(bookmark))
            {
                text = (String) map.get(bookmark);

                if (StringUtils.isEmpty(text))
                {
                    SystemFormContext sendFormContext = element.getSendFormContext();
                    if (sendFormContext != null)
                        text = OdSignService.getSigner(sendFormContext.getFormData());

                    if (text == null)
                    {
                        text = "";
                    }
                }
            }
            else if (Constants.RedHead.SIGNTIME.equals(bookmark))
            {
                text = (String) map.get(bookmark);

                if (StringUtils.isEmpty(text))
                {
                    SystemFormContext sendFormContext = element.getSendFormContext();
                    if (sendFormContext != null)
                    {
                        Date date = OdSignService.getSignTime(sendFormContext.getFormData());
                        if (date != null)
                            text = DateUtils.toString(date, "yyyy年M月d日");
                    }

                    if (text == null)
                    {
                        text = "";
                    }
                }
            }
            else if (Constants.RedHead.SENDTIME.equals(bookmark))
            {
                Object value = map.get(bookmark);
                if (value == null || "".equals(value))
                {
                    text = "";
                }
                else
                {
                    Date date = DataConvert.convertType(Date.class, value);

                    if (date == null)
                        text = DataConvert.getValueString(value);
                    else
                        text = DateUtils.toString(date, "yyyy年M月d日");
                }
            }
            else if (Constants.RedHead.PRIORITY.equals(bookmark) || Constants.RedHead.PRIORITY1.equals(bookmark))
            {
                text = element.getSendDocument().getPriority();
            }
            else
            {
                text = (String) map.get(bookmark);
            }

            texts[i] = text;
        }

        return texts;
    }

    public static String getBackTextType(SendFlowElement element) throws Exception
    {
        if (element.isSended())
            return element.getSendFlowInstance().getBackTextType();
        else
            return null;
    }

    public static InputFile downBackText(SendFlowElement element) throws Exception
    {
        return new InputFile(element.getSendFlowInstance(false).getBackTextBody(), element.getTextTitle() + ".doc");
    }

    @SuppressWarnings("UnusedDeclaration")
    public static OfficeEditType getEditType(SendFlowElement element, OfficeDocument document, String type)
            throws Exception
    {
        if ("send".equals(type) || "final".equals(type))
        {
            return element.isSendTextEditable() ?
                    (element.isNewSendText() ? OfficeEditType.editable : OfficeEditType.track) :
                    OfficeEditType.readOnly;
        }

        return OfficeEditType.readOnly;
    }

    public static DocumentReceiverList loadReceiverList(SendFlowElement element) throws Exception
    {
        //从数据库中加载发文接收者
        OfficeDocument sendDocument = element.getSendDocument();

        DocumentReceiverList receiverList = sendDocument.getReceiverList();
        if (receiverList == null)
        {
            OdFlowDao dao = element.getDao();

            receiverList = new DocumentReceiverList();
            receiverList.setReceivers(new ReceiverListList());

            dao.add(receiverList);

            sendDocument.setReceiverListId(receiverList.getReceiverListId());
            dao.update(sendDocument);
        }

        return receiverList;
    }

    public static void extractSendDocumentData(SendFlowElement element) throws Exception
    {
        SystemFormContext sendFormContext = element.getSendFormContext();

        if (sendFormContext == null)
            return;

        FormData formData = sendFormContext.getFormData();

        OfficeDocument document = element.getSendDocument();

        //发文基本信息
        String title = formData.getString(Constants.Document.TITLE);
        if (title == null)
            title = formData.getString(Constants.Document.TITLE1);
        if (title != null)
            document.setTitle(title.replaceAll("[\\r|\\n]+", " "));
        document.setSendNumber(formData.getString(Constants.Document.SENDNUMBER));
        document.setSubject(formData.getString(Constants.Document.SUBJECT));

        String secret = formData.getString(Constants.Document.SECRET);
        if (StringUtils.isEmpty(secret))
            secret = formData.getString(Constants.Document.SECRET2);
        document.setSecret(secret);

        String priority = formData.getString(Constants.Document.PRIORITY);
        if (StringUtils.isEmpty(priority))
            priority = formData.getString(Constants.Document.PRIORITY1);
        if (StringUtils.isEmpty(priority))
            priority = formData.getString(Constants.Document.PRIORITY4);

        if (priority == null)
            priority = "";

        document.setPriority(priority);


        try
        {
            document.setSendCount(formData.getInt(Constants.Document.SENDCOUNT));
        }
        catch (NumberFormatException ex)
        {
            //份数不是整数，跳过
        }

        //采集扩展属性
        for (DocumentAttribute documentAttribute : element.getDao().getDocumentAttributes())
        {
            String attributeName = documentAttribute.getAttributeName();
            document.getAttributes().put(attributeName, formData.getString(attributeName));
        }

        SignInfo signInfo = OdSignService.getSignInfo(formData);
        if (signInfo != null)
        {
            document.setSigner(signInfo.getSigner());
            document.setSignTime(signInfo.getSignTime());
        }

        Integer sendDeptId = element.getSendDeptId();
        if (sendDeptId != null)
        {
            Dept dept = element.getDao().getDept(sendDeptId);
            document.setSourceDept(dept.getAllName(1));

            String sourceDeptCode;
            if (!StringUtils.isEmpty(dept.getOrgCode()))
                sourceDeptCode = dept.getOrgCode();
            else
                sourceDeptCode = dept.getDeptCode();

            document.setSourceDeptCode(sourceDeptCode);
            document.setCreateDeptId(sendDeptId);
        }
    }

    public static void extractSendFlowInstance(SendFlowElement element) throws Exception
    {
        SendFlowInstance sendFlowInstance = element.getSendFlowInstance(true);

        sendFlowInstance.setSendNumberId(element.getSendNumberId());
        sendFlowInstance.setRedHeadId(element.getRedHeadId());
        sendFlowInstance.setSendDeptId(element.getSendDeptId());
    }

    public static void extractReceiverList(SendFlowElement element) throws Exception
    {
        element.getReceiverList().setReceivers(element.getReceiverListFromForm());
    }

    public static boolean hasReceiverListInForm(SendFlowElement element) throws Exception
    {
        WebForm form = element.getSendFormContext().getForm();

        //查找所有部门选择控件
        for (FormPage page : form.getPages())
        {
            for (FormComponent component0 : page.getComponents())
            {
                for (FormComponent component : component0.getAllComponents())
                {
                    if (component instanceof FSelectable && !Constants.Send.UNIONDEPTS.equals(component.getName()) &&
                            !Constants.Receipt.ACCEPTDEPT.equals(component.getName()) &&
                            ((FSelectable) component).getItemClass() == Member.class)
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static void saveSendData(SendFlowElement element) throws Exception
    {
        OfficeDocument document = element.getSendDocument();

        SystemFormContext sendFormContext = element.getSendFormContext();
        if (sendFormContext == null)
            return;

        FormData formData = sendFormContext.getFormData();

        //采集附件信息附件
        if (document.getAttachmentId() == null)
        {
            //仅当上传了附件之后，采集附件
            ComponentData attachmentData = formData.getData(Constants.Document.ATTACHMENT);
            if (attachmentData instanceof FileListData)
            {
                FileListData fileListData = (FileListData) attachmentData;
                if (fileListData.getFileListId() != null)
                {
                    String[] fileIds = fileListData.getFileListId().split(",");
                    for (String fileId : fileIds)
                    {
                        if (!fileId.startsWith("@"))
                            document.setAttachmentId(Long.valueOf(fileId));
                    }
                }
            }
        }

        //流程如果已经结束，即公文已经发送，不允许修改数据
        OdFlowDao dao = element.getDao();

        Boolean smsNotify = element.isSmsNotify();
        if (smsNotify == null)
            document.setNoticeType(NoticeType.AUTO);
        else if (smsNotify)
            document.setNoticeType(NoticeType.NOTIFY);
        else
            document.setNoticeType(NoticeType.NON_NOTIFY);

        dao.update(element.getSendFlowInstance(true));
        dao.update(document);
        dao.update(element.getReceiverList());
    }

    public static ReceiverListList sendDocument(SendFlowElement element, FlowComponentContext context) throws Exception
    {
        if (element.isSended())
        {
            element.checkSendDocument();

            OfficeDocument document = element.getSendDocument();

            //创建发文记录
            SendFlowInstance sendFlowInstance = element.getSendFlowInstance(true);

            Send send = new Send();

            Integer sendDeptId = element.getSendDeptId();
            if (sendDeptId == null)
                sendDeptId = element.getBusinessDeptId();
            send.setDeptId(sendDeptId);

            send.setCreator(element.getSendCreator());
            send.setCreatorName(element.getSendCreateUser().getUserName());
            send.setCreateDeptId(element.getSendCreateDeptId());
            send.setSender(element.getUserId());
            send.setSenderName(element.getUserInfo().getUserName());
            send.setDocumentId(sendFlowInstance.getDocumentId());
            send.setSendNumberId(sendFlowInstance.getSendNumberId());
            send.setRedHeadId(sendFlowInstance.getRedHeadId());
            send.setSendTime(new Date());
            send.setState(SendState.sended);

            SystemFormContext sendFormContext = element.getSendFormContext();
            if (sendFormContext.getForm().getComponent(Constants.Flow.MESSAGE) != null)
            {
                send.setMessage(sendFormContext.getFormData().getString(Constants.Flow.MESSAGE));
            }

            element.getDao().add(send);

            element.getExchangeSendService().sendDocument(document);

            try
            {
                exchangeNotifyServiceProvider.get().sendMessageWithSendId(send.getSendId(), null, false);
            }
            catch (Throwable ex)
            {
                //短信发送失败不影响其他逻辑
                Tools.log(ex);
            }


            sendFlowInstance.setSentOut(true);
            element.getDao().update(sendFlowInstance);

            for (FlowExtension extension : context.getFlowPage().getExtensions().values())
            {
                if (extension instanceof SendableFlowExtension)
                {
                    ((SendableFlowExtension) extension).sendDocument(context, document);
                }
            }

            return document.getReceiverList().getReceivers();
        }

        return null;
    }

    public static String getOtherFileName(SendFlowElement element) throws Exception
    {
        if (element.isSended())
        {
            OfficeDocument sendDocument = element.getSendDocument();
            if (sendDocument != null)
            {
                DocumentText text = sendDocument.getText();
                if (text != null)
                    return text.getOtherFileName();
            }
        }

        return null;
    }

    public static void checkSendDocument(SendFlowElement element, FlowComponentContext context) throws Exception
    {
        OfficeDocument document = element.getSendDocument();
        DocumentText text = document.getText();
        if (text == null || (!element.isTextFinal() &&
                (text.getOtherFile() == null || StringUtils.isEmpty(text.getOtherFileName()))))
        {
            throw new NoErrorException("ods.flow.textUnFinal");
        }

        checkReceiver(document);

        for (FlowExtension extension : context.getFlowPage().getExtensions().values())
        {
            if (extension instanceof SendableFlowExtension)
            {
                ((SendableFlowExtension) extension).checkSendDocument(context, document);
            }

        }
    }

    public static void checkReceiver(OfficeDocument document) throws Exception
    {
        DocumentReceiverList documentReceiverList = document.getReceiverList();
        List<ReceiverList> receiverLists = documentReceiverList.getReceivers().getReceiverLists();
        if (receiverLists == null || receiverLists.size() == 0)
        {
            throw new NoErrorException("ods.flow.sendWithoutReceiver");
        }
    }
}
