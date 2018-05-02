package com.gzzm.ods.exchange.inout;

import com.gzzm.ods.document.*;
import com.gzzm.ods.exchange.*;
import com.gzzm.ods.redhead.RedHead;
import com.gzzm.ods.sendnumber.SendNumber;
import com.gzzm.platform.attachment.*;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.group.*;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.commons.ext.CompressUtils;
import net.cyan.commons.util.*;
import net.cyan.commons.util.io.Base64;
import net.cyan.commons.util.json.*;
import net.cyan.nest.annotation.Inject;

import java.io.*;
import java.util.*;

/**
 * 公文导入服务
 * 用于内部对接，通过JSON格式解析
 *
 * @author LDP
 * @date 2017/4/26
 */
public class DocumentImportService
{
    @Inject
    private UserOnlineInfo userOnlineInfo;

    @Inject
    private DocumentDao documentDao;

    @Inject
    private AttachmentService attachmentService;

    public DocumentImportService()
    {
    }

    /**
     * 导入发文
     */
    public void importSend(InputFile file, final DocumentImportPage.DocumentImportProgressInfo progressInfo)
            throws Exception
    {
        CompressUtils.decompress("zip", file.getInputStream(), new DocumentJsonDecompresser(0, progressInfo));
    }

    /**
     * 导入收文
     * 收文数据导入系统后，都变为待接收状态，不管原来导出的时候是否为已接收
     */
    public void importReceive(InputFile file, final DocumentImportPage.DocumentImportProgressInfo progressInfo)
            throws Exception
    {
        CompressUtils.decompress("zip", file.getInputStream(), new DocumentJsonDecompresser(1, progressInfo));
    }

    /**
     * 保存发文
     */
    private void saveSend(String jsonStr) throws Exception
    {
        JsonObject json = new JsonParser(jsonStr).parse();

        OfficeDocument document = saveDocument(json.getJsonObject("document"));

        JsonArray array = json.getJsonArray("receiveBaseList");
        if(array != null && array.size() > 0)
        {
            for (int i = 0; i < array.size(); i++)
            {
                saveReceiveBase(document, array.getJsonObject(i));
            }
        }

        JsonObject sendJson = json.getJsonObject("send");
        Send send = new Send();
        send.setDocumentId(document.getDocumentId());

        //设置为当前用户
        send.setCreator(userOnlineInfo.getUserId());
        send.setCreateDeptId(userOnlineInfo.getDeptId());
        send.setSender(userOnlineInfo.getUserId());

        send.setSendTime(sendJson.getDate("sendTime"));
        send.setDeptId(sendJson.getInt("deptId"));
        send.setState(SendState.valueOf(sendJson.getString("state")));
        send.setMessage(sendJson.getString("message"));

        SendNumber sendNumber = null;
        if(sendJson.contains("sendNumberId"))
        {
            Integer sendNumberId = sendJson.getInt("sendNumberId");
            sendNumber = documentDao.getSendNumber(sendNumberId);
        }
        if(sendNumber == null && sendJson.contains("sendNumberName"))
        {
            sendNumber = documentDao.getSendNumber(userOnlineInfo.getBureauId(), sendJson.getString("sendNumberName"));
        }
        if(sendNumber != null) send.setSendNumberId(sendNumber.getSendNumberId());

        RedHead redHead = null;
        if(sendJson.contains("redHeadId"))
        {
            Integer redHeadId = sendJson.getInt("redHeadId");
            redHead = documentDao.getRedHead(redHeadId);
        }
        if(redHead == null && sendJson.contains("redHeadName"))
        {
            redHead = documentDao.getRedHead(userOnlineInfo.getBureauId(), sendJson.getString("redHeadName"));
        }
        if(redHead != null) send.setRedHeadId(redHead.getRedHeadId());

        documentDao.save(send);
    }

    /**
     * 保存收文
     */
    private void saveReceive(String jsonStr) throws Exception
    {
        JsonObject json = new JsonParser(jsonStr).parse();
        OfficeDocument document = saveDocument(json.getJsonObject("document"));
        saveReceiveBase(document, json.getJsonObject("receiveBase"));
    }

    /**
     * 保存收文信息表数据
     */
    private void saveReceiveBase(OfficeDocument document, JsonObject json) throws Exception
    {
        if(json == null || document == null) return;

        ReceiveBase receiveBase = new ReceiveBase();
        receiveBase.setState(ReceiveState.noAccepted);
        receiveBase.setDocumentId(document.getDocumentId());
        receiveBase.setDeptId(json.getInt("deptId"));
        receiveBase.setSendTime(json.getDate("sendTime"));
        receiveBase.setType(ReceiveType.valueOf(json.getString("type")));
        receiveBase.setMethod(ReceiveMethod.valueOf(json.getString("method")));
        receiveBase.setSendType(json.getString("sendType"));
        receiveBase.setDeadline(json.getDate("deadline"));
        documentDao.save(receiveBase);

        Receive r = new Receive();
        r.setReceiveId(receiveBase.getReceiveId());
        documentDao.save(r);
    }

    /**
     * 保存公文对象
     */
    private OfficeDocument saveDocument(JsonObject docJson) throws Exception
    {
        OfficeDocument document = new OfficeDocument();
        document.setTitle(docJson.getString("title"));
        document.setSourceDept(docJson.getString("sourceDept"));
        document.setSourceDeptCode(docJson.getString("sourceDeptCode"));
        document.setSendNumber(docJson.getString("sendNumber"));
        document.setSubject(docJson.getString("subject"));
        document.setSecret(docJson.getString("secret"));
        document.setPriority(docJson.getString("priority"));
        document.setSendCount(docJson.getInt("sendCount"));
        document.setLastTime(docJson.getDate("lastTime"));
        document.setNoticeType(NoticeType.valueOf(docJson.getString("noticeType")));
        document.setCreateDeptId(docJson.getInt("createDeptId"));
        document.setSigner(docJson.getString("signer"));
        document.setSignTime(docJson.getDate("signTime"));
        document.setFinalTime(docJson.getDate("finalTime"));
        document.setUuid(docJson.getString("uuid"));

        document.setTextId(saveText(docJson.getJsonObject("text")));
        document.setAttachmentId(saveAttachments(docJson.getJsonArray("attachments")));
        document.setReceiverListId(saveReceiverList(docJson.getJsonArray("receiverList")));

        JsonObject attrJson = docJson.getJsonObject("attributes");
        if(attrJson != null)
        {
            Map<String, String> attributes = new HashMap<String, String>();
            for (String key : attrJson.keySet())
            {
                attributes.put(key, attrJson.getString(key));
            }
            document.setAttributes(attributes);
        }

        documentDao.save(document);
        return document;
    }

    /**
     * 保存公文正文
     */
    private Long saveText(JsonObject json) throws Exception
    {
        if(json == null) return null;

        DocumentText text = new DocumentText();
        String testStr = json.getString("textBody");
        if(StringUtils.isNotBlank(testStr))
        {
            text.setTextBody(IOUtils.byteArrayInputStream(Base64.base64ToByteArray(testStr)));
        }
        text.setType(json.getString("type"));
        text.setFileSize((json.contains("fileSize") ? json.getLong("fileSize") : null));

        String otherFileStr = json.getString("otherFile");
        if(StringUtils.isNotBlank(otherFileStr))
        {
            text.setOtherFile(IOUtils.byteArrayInputStream(Base64.base64ToByteArray(otherFileStr)));
            text.setOtherFileName(json.getString("otherFileName"));
            text.setOtherFileSize(json.getLong("otherFileSize"));
        }

        documentDao.save(text);
        return text.getTextId();
    }

    /**
     * 保存附件
     */
    private Long saveAttachments(JsonArray array) throws Exception
    {
        if(array == null || array.size() == 0) return null;

        List<Attachment> attachments = new ArrayList<Attachment>(array.size());
        for (int i = 0; i < array.size(); i++)
        {
            JsonObject json = array.getJsonObject(i);
            Attachment attachment = new Attachment();
            attachment.setAttachmentName(json.getString("attachmentName"));
            attachment.setFileName(json.getString("fileName"));
            attachment.setInputable(new Inputable.ByteInput(Base64.base64ToByteArray(json.getString("content"))));
            attachment.setFileSize(json.getLong("fileSize"));
            attachment.setUploadTime(json.getDate("uploadTime"));
            String typeStr = json.getString("fileType");
            if(StringUtils.isNotBlank(typeStr) && !"null".equalsIgnoreCase(typeStr)) {
                attachment.setFileType(FileType.valueOf(typeStr));
            }

            attachments.add(attachment);
        }

        return attachmentService.save(attachments);
    }

    /**
     * 保存接受者列表
     */
    private Long saveReceiverList(JsonArray array) throws Exception
    {
        if(array == null || array.size() == 0) return null;

        ReceiverListList list = new ReceiverListList();

        for (int i = 0; i < array.size(); i++)
        {
            JsonObject json = array.getJsonObject(i);
            ReceiverList receiverList = new ReceiverList();
            receiverList.setSendType(json.getString("sendType"));

            JsonArray array1 = json.getJsonArray("receivers");
            if(array1 == null || array1.size() == 0) continue;

            for (int j = 0; j < array1.size(); j++)
            {
                JsonObject json1 = array1.getJsonObject(j);
                Member member = new Member();
                member.setType(MemberType.valueOf(json1.getString("type")));
                member.setId(json1.getInt("id"));
                member.setName(json1.getString("name"));
                receiverList.addReceiver(member);
            }

            list.addReceiverList(receiverList);
        }

        DocumentReceiverList receiverList = new DocumentReceiverList();
        receiverList.setReceivers(list);
        documentDao.save(receiverList);
        return receiverList.getReceiverListId();
    }

    /**
     * 公文解压器
     */
    private class DocumentJsonDecompresser implements CompressUtils.Decompresser
    {
        /**
         * 0=发文；1=收文
         */
        private Integer type;

        private DocumentImportPage.DocumentImportProgressInfo progressInfo;

        public DocumentJsonDecompresser(Integer type, DocumentImportPage.DocumentImportProgressInfo progressInfo)
        {
            this.type = type;
            this.progressInfo = progressInfo;
        }

        @Override
        public void decompressDirectory(String name, long time, String comment) throws Exception
        {
        }

        @Override
        public void decompressFile(String fileName, InputStream in, long time, String comment) throws IOException
        {
            int index = fileName.lastIndexOf('.');
            if(index > 0)
            {
                String ext = fileName.substring(index + 1);
                //必须是json文件
                if(ext.equalsIgnoreCase("json"))
                {
                    if(progressInfo != null) progressInfo.setFileName(fileName);

                    String jsonStr = new String(IOUtils.streamToBytes(in, false), "UTF-8");
                    try
                    {
                        if(type != null)
                        {
                            if(type == 0) saveSend(jsonStr);
                            else if(type == 1) saveReceive(jsonStr);
                        }

                    }
                    catch (Exception e)
                    {
                        Tools.log("json文件解析错误", e);
                        throw new IOException("文件解析错误");
                    }
                }
            }
        }
    }
}
