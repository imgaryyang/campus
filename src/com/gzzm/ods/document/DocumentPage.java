package com.gzzm.ods.document;

import com.gzzm.platform.attachment.*;
import com.gzzm.platform.barcode.*;
import com.gzzm.platform.commons.IDEncoder;
import net.cyan.arachne.*;
import net.cyan.arachne.annotation.*;
import net.cyan.arachne.fileupload.FileLoader;
import net.cyan.commons.ext.CompressUtils;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.*;
import net.cyan.commons.util.io.*;
import net.cyan.nest.annotation.Inject;

import java.io.*;
import java.util.*;

/**
 * @author camel
 * @date 11-10-10
 */
@Service
public class DocumentPage
{
    @Inject
    private static Provider<AttachmentService> attachmentServiceProvider;

    @Inject
    private OdDao dao;

    private Long documentId;

    @NotSerialized
    private OfficeDocument document;

    public DocumentPage()
    {
    }

    public Long getDocumentId()
    {
        return documentId;
    }

    public void setDocumentId(Long documentId)
    {
        this.documentId = documentId;
    }

    public String getEncodedDocumentId()
    {
        return OfficeDocument.encodeId(documentId);
    }

    public OfficeDocument getDocument() throws Exception
    {
        if (document == null)
            document = dao.getDocument(documentId);
        return document;
    }

    public String getTitle() throws Exception
    {
        return getDocument().getTitle();
    }

    public String getType() throws Exception
    {
        DocumentText text = getDocument().getText();

        return text == null ? null : text.getType();
    }

    @Service(url = "/ods/document/{id}/attachment")
    public InputFile zipAttachments(String id) throws Exception
    {
        documentId = IDEncoder.decode(id);

        OfficeDocument document = getDocument();

        if (document.getAttachmentId() == null)
            return null;

        return attachmentServiceProvider.get().zip(document.getAttachmentId(), document.getTitle());
    }

    @Service(url = "/ods/document/{id}/text/show")
    public String showText(String id) throws Exception
    {
        documentId = IDEncoder.decode(id);

        return "text";
    }

    @Service(url = "/ods/document/{id}/text/down")
    public InputFile downText(String id) throws Exception
    {
        documentId = IDEncoder.decode(id);

        OfficeDocument document = getDocument();

        DocumentText documentText = document.getText();

        if (documentText == null || documentText.getTextBody() == null)
            return null;

        return new InputFile(documentText.getTextBody(), document.getTitleText() + "." + documentText.getType());
    }

    @Service(url = "/ods/document/{id}/text/unsealed")
    public InputFile downUnsealedText(String id) throws Exception
    {
        documentId = IDEncoder.decode(id);

        OfficeDocument document = getDocument();

        DocumentText documentText = document.getText();

        if (documentText == null)
            return null;

        InputStream text = documentText.getUnsealedText();
        if (text == null)
            text = documentText.getTextBody();

        if (text == null)
            return null;

        return new InputFile(text, document.getTitleText() + "." + documentText.getType());
    }

    @Service(url = "/ods/document/{id}/otherFile/down")
    public InputFile downOtherFile(String id) throws Exception
    {
        documentId = IDEncoder.decode(id);

        OfficeDocument document = getDocument();

        DocumentText documentText = document.getText();

        if (documentText == null || documentText.getOtherFile() == null ||
                StringUtils.isEmpty(documentText.getOtherFileName()))
        {
            return null;
        }

        return new InputFile(documentText.getOtherFile(), documentText.getOtherFileName());
    }

    @Service(url = "/ods/document/{documentId}/texts/{type}/save", method = HttpMethod.post)
    @ObjectResult
    @Transactional
    public void saveTexts(String type, String base64) throws Exception
    {
        OfficeDocument document = dao.getDocument(documentId);

        List<DocumentText> texts = document.getTexts();

        DocumentText text = null;
        for (DocumentText text1 : texts)
        {
            if (text1.getType().equals(type))
                text = text1;
        }

        if (text == null)
        {
            text = new DocumentText();
            text.setType(type);
        }

        InputStream in;
        long size;
        if (base64 != null)
        {
            byte[] bytes = CommonUtils.base64ToByteArray(base64.replaceAll("\\s", ""));
            in = new ByteArrayInputStream(bytes);
            size = bytes.length;
        }
        else
        {
            FileLoader fileLoader = RequestContext.getContext().getFileLoader();

            InputFile file = fileLoader.getFile(fileLoader.getFileFields().iterator().next());
            in = file.getInputStream();
            size = file.size();
        }

        text.setTextBody(in);
        text.setFileSize(size);

        if (text.getTextId() == null)
        {
            texts.add(text);
        }
        else
        {
            dao.update(text);
        }
    }

    @Service(url = "/ods/document/{id}/texts/{type}/down")
    public InputFile downTexts(String id, String type) throws Exception
    {
        documentId = IDEncoder.decode(id);

        OfficeDocument document = dao.getDocument(documentId);

        List<DocumentText> texts = document.getTexts();

        for (DocumentText text : texts)
        {
            if (text.getType().equals(type))
            {
                return new InputFile(text.getTextBody(), document.getTitle() + "." + type);
            }
        }

        return null;
    }

    @Service(url = "/ods/document/{documentId}/texts/{type}/exists")
    public boolean existsTexts(String type) throws Exception
    {
        OfficeDocument document = dao.getDocument(documentId);

        List<DocumentText> texts = document.getTexts();

        for (DocumentText text : texts)
        {
            if (text.getType().equals(type))
            {
                return true;
            }
        }

        return false;
    }

    @SuppressWarnings("StringBufferReplaceableByString")
    @Service(url = "/ods/document/{documentId}/qr")
    public InputFile getQRCode(int width) throws Exception
    {
        String uuid = dao.getDocumentUUID(documentId);

        if (StringUtils.isEmpty(uuid))
            return null;

        String link = "/ods/document/" + uuid;

        String content;
        List<BarCode> barCodes = BarCodeUtils.getBarCodeByLinkContent(link);
        if (barCodes != null && barCodes.size() > 1)
        {
            BarCode barCode = barCodes.get(0);
            content = barCode.getContent();
        }
        else
        {
            OfficeDocument document = dao.getDocument(documentId);

            StringBuilder buffer = new StringBuilder();

            buffer.append("文件标题:").append(document.getTitle());

            buffer.append("\r\n").append(link);

            content = buffer.toString();
        }

        if (width <= 0)
            width = 150;

        return new InputFile(BarCodeUtils.qr(content, width), uuid + ".png", Mime.PNG);
    }

    /**
     * 所有文件打包下载(正文及附件)
     *
     * @return 压缩后的文件
     * @throws Exception
     */
    @Service(url = "/ods/document/{id}/zipall")
    public InputFile zipAll(String id) throws Exception
    {
        documentId = IDEncoder.decode(id);

        OfficeDocument document = getDocument();

        if (document.getAttachmentId() == null)
            return null;

        DocumentText documentText = document.getText();

        Collection<Attachment> attachments = document.getAttachments();

        CacheData cache = new CacheData();

        try
        {
            CompressUtils.Compress zip = CompressUtils.createCompress("zip", cache);

            for (Attachment attachment : attachments)
            {
                zip.addFile(attachment.getFileName(), attachment.getInputStream(), attachment.getUploadTime().getTime(),
                        attachment.getRemark());
            }

            if (documentText != null)
            {
                if (documentText.getTextBody() != null)
                    zip.addFile(document.getTitleText() + "." + documentText.getType(), documentText.getTextBody());

                if (!StringUtils.isEmpty(documentText.getOtherFileName()) && documentText.getOtherFile() != null)
                    zip.addFile(documentText.getOtherFileName(), documentText.getOtherFile());
            }

            zip.close();

            return new InputFile(cache, document.getTitle() + ".zip");
        }
        catch (Exception ex)
        {
            //出错的时候清除缓存的文件
            try
            {
                cache.clear();
            }
            catch (Exception ex1)
            {
                //释放资源
            }
            throw ex;
        }
    }
}
