package com.gzzm.ods.flow;

import com.gzzm.ods.document.*;
import com.gzzm.ods.documenttemplate.*;
import com.gzzm.platform.commons.*;
import com.gzzm.platform.weboffice.OfficeEditType;
import net.cyan.activex.OfficeUtils;
import net.cyan.arachne.RequestContext;
import net.cyan.arachne.fileupload.FileLoader;
import net.cyan.commons.ext.TextExtractors;
import net.cyan.commons.util.*;
import net.cyan.commons.util.io.CacheData;
import net.cyan.nest.annotation.Inject;

import java.io.InputStream;
import java.util.*;

/**
 * @author camel
 * @date 2014/8/28
 */
public final class TextFlowUtils
{
    @Inject
    protected static Provider<DocumentTemplateService> documentTemplateServiceProvider;

    private TextFlowUtils()
    {
    }

    public static void showText(TextFlowElement element) throws Exception
    {
        String textType = element.getTextType();
        OfficeDocument textDocument = element.getDocument(textType);
        boolean textEditable = element.isTextEditable();

        OfficeEditType textEditType =
                textEditable ? element.getEditType(textDocument, textType) : OfficeEditType.readOnly;

        if (textEditType == OfficeEditType.readOnly)
            textEditable = false;

        DocumentText documentText = textDocument.getText();

        if (textEditable)
        {
            if (documentText != null)
            {
                Integer userId = element.getUserId();
                if (documentText.getEditor() != null && !documentText.getEditor().equals(userId) &&
                        (System.currentTimeMillis() - documentText.getEditTime().getTime()) < 1000 * 60 * 10)
                {
                    //有人在编辑此文，不允许编辑
                    textEditable = false;
                    textEditType = OfficeEditType.readOnly;
                    element.putEditUserName(documentText.getEditorUser().getUserName());
                }
                else
                {
                    documentText.setEditor(userId);
                    documentText.setEditTime(new Date());
                    element.getDao().update(documentText);
                }
            }
        }

        if (documentText != null)
            element.setTextFormat(documentText.getType());

        element.putTextDocument(textDocument);
        element.setTextEditable(textEditable);
        element.putTextEditType(textEditType);
    }

    public static String getText(TextFlowElement element, int length) throws Exception
    {
        OfficeDocument document = element.getDocument(element.getTextType());

        DocumentText documentText = document.getText();

        if (documentText == null)
            return null;

        InputStream text = documentText.getUnsealedText();
        if (text == null)
            text = documentText.getTextBody();

        String s = null;
        if (text != null)
        {
            try
            {
                s = TextExtractors.extract(text, documentText.getType());

                s = s.trim();
            }
            catch (Throwable ex)
            {
                //提取正文失败不影响逻辑，仅记录日志
                Tools.log(ex);
            }
        }

        if (StringUtils.isEmpty(s))
            s = document.getTitle();

        if (StringUtils.isEmpty(s))
            s = "";

        return Chinese.truncate(s, length);
    }

    public static boolean isTextNew(TextFlowElement element)
    {
        OfficeDocument textDocument = element.getTextDocument();

        if (textDocument == null)
            return false;

        DocumentText documentText = textDocument.getText();
        return documentText == null || StringUtils.isEmpty(documentText.getType()) ||
                documentText.getFileSize() == null || documentText.getFileSize() == 0;
    }

    public static void saveText(TextFlowElement element) throws Exception
    {
        String textType = element.getTextType();
        OfficeDocument document = element.getDocument(textType);

        OfficeEditType editType = element.getEditType(document, textType);

        //只读状态不允许保存
        if (editType == OfficeEditType.readOnly)
            throw new SystemException("save readonly document");

        DocumentText documentText = document.getText();

        OdFlowDao dao = element.getDao();

        InputFile text = element.getText();
        String textFormat = element.getTextFormat();
        Integer userId = element.getUserId();

        if (text == null)
        {
            RequestContext context = RequestContext.getContext();
            if (context != null)
            {
                FileLoader fileLoader = context.getFileLoader();
                if (fileLoader != null)
                {
                    Set<String> fileFields = fileLoader.getFileFields();
                    if (!fileFields.isEmpty())
                    {
                        text = fileLoader.getFile(fileFields.iterator().next());
                    }
                }
                else
                {
                    CacheData cacheData = new CacheData();
                    IOUtils.copyStream(context.getOriginalRequest().getInputStream(),
                            cacheData);

                    text = new InputFile(cacheData, "");
                }
            }
        }

        if (documentText == null)
        {
            documentText = new DocumentText();

            if (text != null)
            {
                documentText.setTextBody(text.getInputStream());
                documentText.setFileSize(text.size());
            }
            documentText.setType(StringUtils.isEmpty(textFormat) ? "doc" : textFormat);
            documentText.setEditor(userId);
            documentText.setEditTime(new Date());
            documentText.setStepId(element.getStepId());
            dao.add(documentText);

            document.setTextId(documentText.getTextId());
            document.setLastTime(new Date());
            dao.update(document);
        }
        else
        {
            if (text != null && text.size() > 0)
            {
                documentText.setTextBody(text.getInputStream());
                documentText.setFileSize(text.size());
            }
            if (!StringUtils.isEmpty(textFormat))
                documentText.setType(textFormat);
            documentText.setEditor(userId);
            documentText.setEditTime(new Date());
            dao.update(documentText);

            document.setLastTime(new Date());
            dao.update(document);
        }

        //保存文档备份
        DocumentTextBak bak = new DocumentTextBak();
        bak.setTextId(documentText.getTextId());
        bak.setUserId(userId);
        bak.setSaveTime(new Date());
        if (text != null)
            bak.setTextBody(text.getInputStream());
        bak.setFileSize(documentText.getFileSize());
        dao.add(bak);
    }

    public static void saveOtherFile(TextFlowElement element) throws Exception
    {
        String textType = element.getTextType();

        OfficeDocument document = element.getDocument(textType);

        OfficeEditType editType = element.getEditType(document, textType);

        //只读状态不允许保存
        if (editType == OfficeEditType.readOnly)
            throw new SystemException("save readonly document");

        DocumentText documentText = document.getText();

        OdFlowDao dao = element.getDao();

        InputFile text = element.getText();

        if (documentText == null)
        {
            documentText = new DocumentText();
            if (text != null)
            {
                documentText.setOtherFile(text.getInputStream());
                documentText.setOtherFileSize(text.size());
                documentText.setOtherFileName(text.getName());
            }
            dao.add(documentText);

            document.setTextId(documentText.getTextId());
            document.setLastTime(new Date());
            dao.update(document);
        }
        else
        {
            if (text != null && text.size() > 0)
            {
                documentText.setOtherFile(text.getInputStream());
                documentText.setOtherFileSize(text.size());
                documentText.setOtherFileName(text.getName());
            }
            dao.update(documentText);

            document.setLastTime(new Date());
            dao.update(document);
        }
    }

    public static void deleteOtherFile(TextFlowElement element) throws Exception
    {
        String textType = element.getTextType();

        OfficeDocument document = element.getDocument(textType);

        OfficeEditType editType = element.getEditType(document, textType);

        //只读状态不允许保存
        if (editType == OfficeEditType.readOnly)
            throw new SystemException("save readonly document");

        DocumentText documentText = document.getText();

        OdFlowDao dao = element.getDao();
        if (documentText != null)
        {
            documentText.setOtherFile(Null.Stream);
            documentText.setOtherFileSize(Null.Long);
            documentText.setOtherFileName(Null.String);

            dao.update(documentText);

            document.setLastTime(new Date());
            dao.update(document);
        }
    }

    public static void releaseText(TextFlowElement element) throws Exception
    {
        OfficeDocument document = element.getDocument(element.getTextType());
        if (document != null)
        {
            DocumentText documentText = document.getText();

            OdFlowDao dao = element.getDao();

            if (documentText != null)
            {
                if (documentText.getEditor() != null && documentText.getEditor().equals(element.getUserId()))
                {
                    documentText.setEditor(Null.Integer);
                    documentText.setEditTime(Null.Timestamp);
                    dao.update(documentText);
                }
            }
        }
    }

    public static String getTextTitle(TextFlowElement element)
    {
        OfficeDocument textDocument = element.getTextDocument();
        return textDocument == null ? null : textDocument.getTitleText();
    }

    public static void convertToPdf(TextFlowElement element) throws Exception
    {
        InputFile text = element.getText();
        CacheData cacheData = new CacheData();
        text.getInputable().writeTo(cacheData);
        cacheData.flushData();

        OfficeDocument document = element.getDocument(element.getTextType());
        DocumentText documentText = document.getText();
        String type = documentText.getType();

        DocumentText documentText1 = null;
        List<DocumentText> texts = document.getTexts();
        for (DocumentText documentText2 : texts)
        {
            if (documentText2.getType().equals(type))
            {
                documentText1 = documentText2;
                break;
            }
        }

        if (documentText1 == null)
        {
            documentText1 = new DocumentText();
            documentText1.setType(type);
        }

        documentText1.setTextBody(cacheData.getInputStream());
        documentText1.setFileSize(cacheData.size());

        if (documentText1.getTextId() == null)
        {
            texts.add(documentText1);
        }
        else
        {
            element.getDao().update(documentText1);
        }

        Inputable inputable = OfficeUtils.wordToPdf(cacheData, element.getTextFormat());
        element.setText(new InputFile(inputable, "aa.pdf"));
        element.setTextFormat("pdf");

        element.saveText();
    }

    public static List<DocumentTemplate> getDocumentTemplates(TextFlowElement element) throws Exception
    {
        return documentTemplateServiceProvider.get()
                .getDocumentTemplates(element.getBusinessDeptId(), element.getUserInfo());
    }
}
