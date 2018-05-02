package com.gzzm.ods.flow;

import com.gzzm.ods.document.*;
import com.gzzm.ods.exchange.ExchangeSendService;
import com.gzzm.platform.form.SystemFormContext;
import com.gzzm.platform.organ.User;
import net.cyan.commons.util.InputFile;

/**
 * @author camel
 * @date 2014/8/28
 */
public interface SendFlowElement extends TextFlowElement, SendFlowActions
{
    public ExchangeSendService getExchangeSendService();

    public Integer getSendNumberId() throws Exception;

    public void setSendNumberId(Integer sendNumberId);

    public Integer getSendDeptId() throws Exception;

    public String getSendFormName() throws Exception;

    public SystemFormContext createSendFormContext() throws Exception;

    /**
     * 获得发文表单
     *
     * @return 发文表单
     * @throws Exception 允许子类抛出异常
     */
    public SystemFormContext getSendFormContext() throws Exception;

    public SendFlowInstance getSendFlowInstance();

    public void putSendFlowInstance(SendFlowInstance sendFlowInstance);

    public SendFlowInstance getSendFlowInstance(boolean create) throws Exception;

    public boolean isSendFlowInstanceLoaded();

    public void setSendFlowInstanceLoaded();

    public OfficeDocument getSendDocument() throws Exception;

    public void putSendDocument(OfficeDocument sendDocument);

    public Long getSendDocumentId() throws Exception;

    public String getEncodedSendDocumentId() throws Exception;

    public OfficeDocument createSendDocument() throws Exception;

    public boolean isSended() throws Exception;

    public boolean isNewSendText() throws Exception;

    public boolean isTextFinal() throws Exception;

    public boolean isSendTextEditable() throws Exception;

    public boolean canFinalizeText() throws Exception;

    public Integer getRedHeadId() throws Exception;

    public ReceiverListList getReceiverListFromForm() throws Exception;

    public String[] getBookmarkTexts(String[] bookmarks) throws Exception;

    public String showBackText();

    public String getBackTextType() throws Exception;

    public InputFile downBackText() throws Exception;

    public DocumentReceiverList getReceiverList() throws Exception;

    public DocumentReceiverList loadReceiverList() throws Exception;

    public boolean hasReceiverListInForm() throws Exception;

    public void extractSendData() throws Exception;

    public void extractSendDocumentData() throws Exception;

    public void extractSendFlowInstance() throws Exception;

    public void extractReceiverList() throws Exception;

    public void saveSendData() throws Exception;

    public ReceiverListList sendDocument() throws Exception;

    public Boolean isSmsNotify() throws Exception;

    public Integer getSendCreator() throws Exception;

    public User getSendCreateUser() throws Exception;

    public Integer getSendCreateDeptId() throws Exception;

    public String getSendOtherFileName() throws Exception;

    public void checkSendDocument() throws Exception;
}
