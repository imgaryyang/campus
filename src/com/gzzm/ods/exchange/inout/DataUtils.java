package com.gzzm.ods.exchange.inout;

import com.gzzm.ods.document.*;
import com.gzzm.ods.exchange.*;
import com.gzzm.platform.attachment.*;
import com.gzzm.platform.group.Member;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.commons.util.*;
import net.cyan.commons.util.io.Base64;
import net.cyan.nest.annotation.Inject;

import java.io.IOException;
import java.util.*;

/**
 * 公文导入导出数据处理工具类
 *
 * @author LDP
 * @date 2017/4/27
 */
public class DataUtils
{
    @Inject
    private static Provider<AttachmentService> attachmentServiceProvider;

    public DataUtils()
    {
    }

    /**
     * 保存文件到附件表中，返回附件表ID
     */
    public static Long saveAttachment(Inputable inputable, String fileName, UserOnlineInfo userOnlineInfo, String tag,
                                      String remark) throws Exception
    {
        Attachment attachment = new Attachment();
        attachment.setInputable(inputable);
        attachment.setAttachmentName(fileName);
        attachment.setFileName(fileName);
        attachment.setUserId(userOnlineInfo.getUserId());
        attachment.setDeptId(userOnlineInfo.getDeptId());
        attachment.setTag(tag);
        attachment.setRemark(remark);

        List<Attachment> attachments = new ArrayList<Attachment>(1);
        attachments.add(attachment);
        return attachmentServiceProvider.get().save(attachments);
    }

    /**
     * 封装document对象数据
     */
    public static Map<String, Object> createDocumentDataMap(OfficeDocument document)
    {
        Map<String, Object> docMap = new HashMap<String, Object>();
        docMap.put("title", document.getTitle());
        docMap.put("sourceDept", document.getSourceDept());
        docMap.put("sourceDeptCode", document.getSourceDeptCode());
        docMap.put("sendNumber", document.getSendNumber());
        docMap.put("subject", document.getSubject());
        docMap.put("secret", document.getSecret());
        docMap.put("priority", document.getPriority());
        docMap.put("sendCount", document.getSendCount());
        docMap.put("lastTime", document.getLastTime());
        docMap.put("noticeType", document.getNoticeType());
        docMap.put("createDeptId", document.getCreateDeptId());
        docMap.put("signer", document.getSigner());
        docMap.put("signTime", document.getSignTime());
        docMap.put("finalTime", document.getFinalTime());
        docMap.put("uuid", document.getUuid());
        return docMap;
    }

    /**
     * 封装正文数据
     */
    public static Map<String, Object> createTextDataMap(DocumentText text) throws IOException
    {
        if(text == null) return null;

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("textBody", text.getTextBody() == null ? null :
                Base64.byteArrayToBase64(new Inputable.StreamInput(text.getTextBody()).getBytes()));
        map.put("type", text.getType());
        if(text.getFileSize() != null) map.put("fileSize", text.getFileSize());
        map.put("otherFileName", text.getOtherFileName());
        map.put("otherFile", text.getOtherFile() == null ? null :
                Base64.byteArrayToBase64(new Inputable.StreamInput(text.getOtherFile()).getBytes()));
        if(text.getOtherFileSize() != null) map.put("otherFileSize", text.getOtherFileSize());

        return map;
    }

    /**
     * 封装附件列表数据
     */
    public static List<Map<String, Object>> createAttachmentList(Collection<Attachment> attachments)
            throws IOException
    {
        if(attachments == null || attachments.size() == 0) return null;

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(attachments.size());
        for (Attachment attachment : attachments)
        {
            Map<String, Object> map = new HashMap<String, Object>();

            map.put("attachmentName", attachment.getAttachmentName());
            map.put("fileName", attachment.getFileName());
            map.put("content", Base64.byteArrayToBase64(attachment.getBytes()));
            map.put("fileSize", attachment.getFileSize());
            map.put("uploadTime", attachment.getUploadTime());
            map.put("fileType", attachment.getFileType());
            map.put("uuid", attachment.getUuid());

            list.add(map);
        }
        return list;
    }

    /**
     * 封装接收者列表
     */
    public static List<Map<String, Object>> createReceiverList(DocumentReceiverList receiverList)
    {
        if(receiverList == null || receiverList.getReceivers() == null) return null;

        List<ReceiverList> receivers = receiverList.getReceivers().getReceiverLists();
        if(receivers == null || receivers.size() == 0) return null;

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(receivers.size());
        for (ReceiverList rl : receivers)
        {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("sendType", rl.getSendType());

            List<Member> members = rl.getReceivers();
            if(members != null && members.size() > 0)
            {
                List<Map<String, Object>> memberList = new ArrayList<Map<String, Object>>(members.size());
                for (Member m : members)
                {
                    Map<String, Object> map1 = new HashMap<String, Object>();
                    map1.put("type", m.getType());
                    map1.put("id", m.getId());
                    map1.put("name", m.getName());
                    memberList.add(map1);
                }
                map.put("receivers", memberList);
            }

            list.add(map);
        }

        return list;
    }

    /**
     * 封装ReceiveBase对象数据
     */
    public static Map<String, Object> createReceiveBaseMap(ReceiveBase receiveBase)
    {
        if(receiveBase == null) return null;

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("deptId", receiveBase.getDeptId());
        map.put("sendTime", receiveBase.getSendTime());
        map.put("state", receiveBase.getState());
        map.put("type", receiveBase.getType());
        map.put("method", receiveBase.getMethod());
        map.put("sendType", receiveBase.getSendType());
//        map.put("notified", receiveBase.getNotified());
        map.put("deadline", receiveBase.getDeadline());
        return map;
    }

    /**
     * 封装Send对象数据
     */
    public static Map<String, Object> createSendMap(Send send)
    {
        if(send == null) return null;

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("sendTime", send.getSendTime());
        map.put("deptId", send.getDeptId());
        map.put("state", send.getState());
        map.put("message", send.getMessage());

        if(send.getSendNumberId() != null)
        {
            map.put("sendNumberId", send.getSendNumberId());
            if(send.getSendNumber() != null)
                map.put("sendNumberName", send.getSendNumber().getSendNumberName());
        }

        if(send.getRedHeadId() != null)
        {
            map.put("redHeadId", send.getRedHeadId());
            if(send.getRedHead() != null)
                map.put("redHeadName", send.getRedHead().getRedHeadName());
        }

        return map;
    }
}
