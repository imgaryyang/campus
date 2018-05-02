package com.gzzm.ods.flow;

import com.gzzm.ods.document.*;
import com.gzzm.ods.documenttemplate.DocumentTemplate;
import com.gzzm.ods.exchange.ExchangeSendService;
import com.gzzm.platform.flow.*;
import com.gzzm.platform.form.SystemFormContext;
import com.gzzm.platform.organ.*;
import com.gzzm.platform.weboffice.OfficeEditType;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;
import net.cyan.valmiki.flow.*;

import java.util.List;

/**
 * @author camel
 * @date 2014/8/27
 */
public class SendFlowExtension extends AbstractFlowExtension implements SendFlowElement
{
    @Inject
    @NotSerialized
    private OdFlowDao dao;

    @Inject
    protected ExchangeSendService exchangeSendService;

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
     * 正在显示其正文的文档
     */
    @NotSerialized
    private OfficeDocument textDocument;

    private String textType;

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

    private boolean textEditable;

    /**
     * 发文字号ID
     */
    private Integer sendNumberId;

    /**
     * 发文的接收者
     */
    @NotSerialized
    private DocumentReceiverList receiverList;

    public SendFlowExtension()
    {
    }

    public OdFlowDao getDao()
    {
        return dao;
    }

    @Override
    public void init(FlowComponentContext context) throws Exception
    {
        super.init(context);

        SendFlowUtils.initBusinessContext(this, context.getBusinessContext());
    }

    @NotSerialized
    public Boolean isSmsNotify() throws Exception
    {
        return getFlowPage().isSmsNotify();
    }

    public String getTextType()
    {
        return textType;
    }

    public void setTextType(String textType)
    {
        this.textType = textType;
    }

    public OfficeEditType getTextEditType()
    {
        return textEditType;
    }

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

    public String getEditUserName()
    {
        return editUserName;
    }

    @Override
    public void putEditUserName(String editUserName)
    {
        this.editUserName = editUserName;
    }

    public boolean isTextEditable()
    {
        return textEditable;
    }

    public void setTextEditable(boolean textEditable)
    {
        this.textEditable = textEditable;
    }

    public OfficeDocument getTextDocument()
    {
        return textDocument;
    }

    @Override
    public void putTextDocument(OfficeDocument textDocument)
    {
        this.textDocument = textDocument;
    }

    @Override
    public Long getTextDocumentId()
    {
        return textDocument == null ? null : textDocument.getDocumentId();
    }

    @Override
    public String getEncodedTextDocumentId()
    {
        return textDocument != null ? textDocument.getEncodedId() : null;
    }

    public String getTextTitle()
    {
        return TextFlowUtils.getTextTitle(this);
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

    @Override
    public Integer getSendDeptId() throws Exception
    {
        return null;
    }

    @Override
    public String getSendFormName() throws Exception
    {
        return "";
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

    @Override
    public SystemFormContext createSendFormContext() throws Exception
    {
        return getFormContext();
    }

    @Override
    @NotSerialized
    public SystemFormContext getSendFormContext() throws Exception
    {
        return getFormContext();
    }

    public SendFlowInstance getSendFlowInstance(boolean create) throws Exception
    {
        return SendFlowUtils.getSendFlowInstance(this, create);
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

        document.setCreateDeptId(getBusinessDeptId());

        getDao().add(document);

        return document;
    }

    public OfficeDocument getSendDocument() throws Exception
    {
        if (sendDocument == null)
        {
            SendFlowInstance sendFlowInstance = getSendFlowInstance(true);
            if (sendDocument == null)
                sendDocument = sendFlowInstance.getDocument();
        }

        return sendDocument;
    }

    public void putSendDocument(OfficeDocument sendDocument)
    {
        this.sendDocument = sendDocument;
    }

    @Override
    public Long getSendDocumentId() throws Exception
    {
        OfficeDocument sendDocument = getSendDocument();
        return sendDocument == null ? null : sendDocument.getDocumentId();
    }

    @Override
    public String getEncodedSendDocumentId() throws Exception
    {
        OfficeDocument sendDocument = getSendDocument();
        return sendDocument == null ? null : sendDocument.getEncodedId();
    }

    public boolean isSended() throws Exception
    {
        return SendFlowUtils.isSended(this);
    }

    public String getBackTextType() throws Exception
    {
        return SendFlowUtils.getBackTextType(this);
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

    @NotSerialized
    public boolean isSendTextEditable() throws Exception
    {
        return isSended() && isEditable() && SendFlowUtils.hasEditSendTextAction(this);
    }

    public boolean canFinalizeText() throws Exception
    {
        return isSended() && SendFlowUtils.hasFinalizeTextAction(this);
    }

    @NotSerialized
    public Action[] getTextActions() throws Exception
    {
        return getTextActions(textType, textEditable);
    }

    public Action[] getTextActions(String type, boolean editable) throws Exception
    {
        return SendFlowUtils.getTextActions(this, type, editable);
    }

    @Override
    public void initActions(List<Action> actions, FlowComponentContext context) throws Exception
    {
        super.initActions(actions, context);

        SendFlowUtils.initActions(this, actions);
    }

    public OfficeDocument getDocument(String type) throws Exception
    {
        if ("send".equals(type) || "final".equals(type))
            return getSendDocument();

        return null;
    }

    @SuppressWarnings("UnusedDeclaration")
    public OfficeEditType getEditType(OfficeDocument document, String type) throws Exception
    {
        return SendFlowUtils.getEditType(this, document, type);
    }

    public boolean isNewSendText() throws Exception
    {
        SendFlowInstance sendFlowInstance = getSendFlowInstance(false);

        return sendFlowInstance != null && (sendFlowInstance.getSendStepId() == null ||
                sendFlowInstance.getSendStepId().equals(getStepId())) &&
                (getState() == FlowStep.NODEAL || getState() == FlowStep.DRAFT);
    }

    public String showText() throws Exception
    {
        TextFlowUtils.showText(this);

        return "/ods/flow/text_ex.ptl";
    }

    @Override
    public String getText(String textType, int length) throws Exception
    {
        setTextType(textType);

        return TextFlowUtils.getText(this, length);
    }

    public boolean isTextNew()
    {
        return TextFlowUtils.isTextNew(this);
    }

    /**
     * 保存正文
     *
     * @throws Exception 写数据库错误
     */
    @Transactional
    public void saveText() throws Exception
    {
        TextFlowUtils.saveText(this);
    }

    @Transactional
    public void saveOtherFile(String textType) throws Exception
    {
        setTextType(textType);
        TextFlowUtils.saveOtherFile(this);
    }

    /**
     * 释放正文，让其他人可以编辑
     *
     * @throws Exception 数据库错误
     */
    public void releaseText() throws Exception
    {
        TextFlowUtils.releaseText(this);
    }

    public void convertToPdf() throws Exception
    {
        TextFlowUtils.convertToPdf(this);
    }

    /**
     * 红头模版ID，从表单里提取
     *
     * @return 红头模版ID
     * @throws Exception 加载表单错误
     */
    @NotSerialized
    public Integer getRedHeadId() throws Exception
    {
        return SendFlowUtils.getRedHeadId(this);
    }

    @Transactional
    public void finalizeText() throws Exception
    {
        SendFlowUtils.finalizeText(this);
    }

    @Transactional
    public String unFinalizeText() throws Exception
    {
        return SendFlowUtils.unFinalizeText(this);
    }

    @NotSerialized
    public List<DocumentTemplate> getDocumentTemplates() throws Exception
    {
        return TextFlowUtils.getDocumentTemplates(this);
    }

    @NotSerialized
    public ReceiverListList getReceiverListFromForm() throws Exception
    {
        return SendFlowUtils.getReceiverListFromForm(this);
    }

    public String[] getBookmarkTexts(String[] bookmarks) throws Exception
    {
        return SendFlowUtils.getBookmarkTexts(this, bookmarks);
    }

    public String showBackText()
    {
        return "/ods/flow/backtext.ptl";
    }

    public InputFile downBackText() throws Exception
    {
        return SendFlowUtils.downBackText(this);
    }

    @Override
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

    public boolean hasReceiverListInForm() throws Exception
    {
        return SendFlowUtils.hasReceiverListInForm(this);
    }

    public void extractReceiverList() throws Exception
    {
        SendFlowUtils.extractReceiverList(this);
    }

    public void extractSendFlowInstance() throws Exception
    {
        SendFlowUtils.extractSendFlowInstance(this);
    }

    public void extractSendDocumentData() throws Exception
    {
        SendFlowUtils.extractSendDocumentData(this);
    }

    @Override
    public void extractSendData() throws Exception
    {
        if (getFlowContext().getInstance().getState() == 0)
        {
            extractSendDocumentData();
            extractReceiverList();
            extractSendFlowInstance();
        }
    }

    @Override
    public void extractData(FlowComponentContext context) throws Exception
    {
        super.extractData(context);

        if (isSended())
            extractSendData();
    }

    @Override
    public void saveSendData() throws Exception
    {
        if (getFlowContext().getInstance().getState() == 0)
            SendFlowUtils.saveSendData(this);
    }

    @Override
    public void saveData(FlowComponentContext context) throws Exception
    {
        super.saveData(context);

        if (isSended())
            saveSendData();
    }

    @Override
    public void checkSendDocument() throws Exception
    {
        SendFlowUtils.checkSendDocument(this, context);
    }

    public ReceiverListList sendDocument() throws Exception
    {
        return SendFlowUtils.sendDocument(this, context);
    }

    @Override
    @NotSerialized
    public ExchangeSendService getExchangeSendService()
    {
        return exchangeSendService;
    }

    @Override
    @NotSerialized
    public Integer getSendCreator() throws Exception
    {
        SendFlowInstance sendFlowInstance = getSendFlowInstance();
        if (sendFlowInstance == null)
            return null;
        return sendFlowInstance.getCreator();
    }

    @Override
    @NotSerialized
    public User getSendCreateUser() throws Exception
    {
        SendFlowInstance sendFlowInstance = getSendFlowInstance();
        if (sendFlowInstance == null)
            return null;
        return sendFlowInstance.getCreateUser();
    }

    @Override
    @NotSerialized
    public Integer getSendCreateDeptId() throws Exception
    {
        SendFlowInstance sendFlowInstance = getSendFlowInstance();
        if (sendFlowInstance == null)
            return null;
        return sendFlowInstance.getCreateDeptId();
    }

    @Override
    public void endFlow(FlowComponentContext context) throws Exception
    {
        super.endFlow(context);

        if (isSended() && hasReceiverListInForm())
        {
            //锁住发文流程实例，避免重复发送
            Long instanceId = getInstanceId();
            getDao().lockSendFlowInstance(instanceId);

            if (getExchangeSendService().getDao().getSendByDocumentId(getSendFlowInstance().getDocumentId()) == null) ;
            {
                sendDocument();
            }
        }

    }

    @Override
    @NotSerialized
    public String getJsFile()
    {
        if (textDocument == null)
            return "/ods/flow/send_ex.js";
        else
            return "/ods/flow/text_ex.js";
    }

    @Override
    public String getSendOtherFileName() throws Exception
    {
        return SendFlowUtils.getOtherFileName(this);
    }
}