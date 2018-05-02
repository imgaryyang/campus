package com.gzzm.ods.document;

import com.gzzm.platform.attachment.AttachmentService;
import com.gzzm.platform.commons.Tools;
import net.cyan.commons.transaction.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.*;

/**
 * @author camel
 * @date 11-9-23
 */
public abstract class OdDao extends GeneralDao
{
    @Inject
    private static Provider<AttachmentService> attachmentServiceProvider;

    public OdDao()
    {
    }

    public OfficeDocument getDocument(Long documentId) throws Exception
    {
        return load(OfficeDocument.class, documentId);
    }

    public DocumentText getDocumentText(Long textId) throws Exception
    {
        return load(DocumentText.class, textId);
    }

    public DocumentReceiverList getDocumentReceiverList(Long receiverListId) throws Exception
    {
        return load(DocumentReceiverList.class, receiverListId);
    }

    @OQL("select a from DocumentAttribute a")
    public abstract List<DocumentAttribute> getDocumentAttributes() throws Exception;

    @LoadByKey
    public abstract DocumentAttribute getDocumentAttribute(Integer attributeId) throws Exception;

    /**
     * 复制公文
     *
     * @param document 要复制的公文
     * @return 复制后的公文
     * @throws Exception 复制公文错误，一般由数据库错误引起
     */
    @Transactional
    public OfficeDocument copyDocument(OfficeDocument document) throws Exception
    {
        //复制公文基本信息
        OfficeDocument document2 = new OfficeDocument();
        document2.setTitle(document.getTitle());
        document2.setSubject(document.getSubject());
        document2.setSendNumber(document.getSendNumber());
        document2.setPriority(document.getPriority());
        document2.setSecret(document.getSecret());
        document2.setSendCount(document.getSendCount());
        document2.setSourceDept(document.getSourceDept());
        document2.setSourceDeptCode(document.getSourceDeptCode());
        document2.setNoticeType(document.getNoticeType());
        document2.setAttributes(new HashMap<String, String>(document.getAttributes()));
        document2.setCreateDeptId(document.getCreateDeptId());
        document2.setFinalTime(document.getFinalTime());

        //复制附件列表
        if (document.getAttachmentId() != null)
            document2.setAttachmentId(attachmentServiceProvider.get().clone(document.getAttachmentId()));

        //复制正文
        if (document.getTextId() != null)
        {
            DocumentText documentText = new DocumentText();
            documentText.setTextBody(document.getText().getTextBody());
            documentText.setFileSize(document.getText().getFileSize());
            documentText.setType(document.getText().getType());
            documentText.setOtherFileName(document.getText().getOtherFileName());
            documentText.setOtherFileSize(document.getText().getOtherFileSize());
            documentText.setOtherFile(document.getText().getOtherFile());

            add(documentText);
            document2.setTextId(documentText.getTextId());
        }

        //复制其他正文
        List<DocumentText> texts = new ArrayList<DocumentText>();
        for (DocumentText text : document.getTexts())
        {
            DocumentText documentText = new DocumentText();
            documentText.setTextBody(text.getTextBody());
            documentText.setFileSize(text.getFileSize());
            documentText.setType(text.getType());
            documentText.setOtherFileName(text.getOtherFileName());
            documentText.setOtherFileSize(document.getText().getOtherFileSize());
            documentText.setOtherFile(text.getOtherFile());

            texts.add(documentText);
        }
        document2.setTexts(texts);

        //复制接收者列表
        if (document.getReceiverListId() != null)
        {
            DocumentReceiverList documentReceiverList = new DocumentReceiverList();
            documentReceiverList.setReceivers(document.getReceiverList().getReceivers());

            add(documentReceiverList);
            document2.setReceiverListId(documentReceiverList.getReceiverListId());
        }

        document2.setLastTime(new Date());

        //保存公文
        add(document2);

        return document2;
    }

    @Transactional(mode = TransactionMode.required_new)
    public String getDocumentUUID(Long documentId) throws Exception
    {
        lock(OfficeDocument.class, documentId);

        OfficeDocument document = load(OfficeDocument.class, documentId);

        if (document == null)
            return null;

        String uuid = document.getUuid();
        if (StringUtils.isEmpty(uuid))
        {
            uuid = Tools.getUUID();
            document.setUuid(uuid);
            update(document);
        }

        return uuid;
    }

    @GetByField("uuid")
    public abstract OfficeDocument getDocumentByUUID(String uuid) throws Exception;
}
