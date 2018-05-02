package com.gzzm.platform.form;

import com.gzzm.platform.attachment.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.*;
import net.cyan.valmiki.form.FormContext;
import net.cyan.valmiki.form.components.*;

import java.util.*;

/**
 * 将表单附件保存在PFATTACHMENT表,每个附件控件可关联多个附件列表，但是只有一个是可写，其他只读
 * 当关联多个附件列表时，多个列表ID用都会隔开，只读的列表前面用@表示
 *
 * @author camel
 * @date 11-9-21
 */
@Injectable(singleton = true)
public class SystemFileListService implements FileListService
{
    @Inject
    private static Provider<AttachmentSaver> saverProvider;

    @Inject
    private static Provider<AttachmentDao> daoProvider;

    public SystemFileListService()
    {
    }

    public List<FileItem> loadFileList(String fileListId, FFileList fileList, FormContext context) throws Exception
    {
        AttachmentDao attachmentDao = daoProvider.get();

        List<FileItem> items = null;

        //可以
        for (String listId : fileListId.split(","))
        {
            boolean readOnly = false;
            if (listId.startsWith("@"))
            {
                //前面有@，表示这是个只读列表
                listId = listId.substring(1);
                readOnly = true;
            }

            if (listId.length() == 0)
                continue;

            List<Attachment> attachments = attachmentDao.getAttachments(Long.valueOf(listId));

            if (attachments != null && attachments.size() > 0)
            {
                String encodedId = null;

                boolean isDept = ((SystemFormContext) context).isOperatorDept(fileList);

                if (items == null)
                    items = new ArrayList<FileItem>(attachments.size());

                for (Attachment attachment : attachments)
                {
                    if (encodedId == null)
                        encodedId = attachment.getEncodedId();

                    FileItem item = new FileItem();

                    String no = attachment.getAttachmentNo().toString();

                    if (readOnly)
                        item.setId(listId + "_" + no);
                    else
                        item.setId(no);

                    item.setFileName(attachment.getFileName());

                    if (readOnly)
                    {
                        item.setOperator("");
                    }
                    else
                    {
                        Object id;
                        String name = null;
                        if (isDept)
                        {
                            id = attachment.getDeptId();
                            if (id != null)
                                name = attachment.getDept().getDeptName();
                        }
                        else
                        {
                            id = attachment.getUserId();
                            if (id != null)
                                name = attachment.getUser().getUserName();
                        }
                        item.setOperator(id == null ? "" : id.toString());
                        item.setOperatorName(name == null ? "" : name);
                    }

                    item.setTime(attachment.getUploadTime());

                    item.setUrl("/attachment/" + encodedId + "/" + no);
                    item.setFileSize(attachment.getFileSize());

                    items.add(item);
                }
            }
        }

        return items;
    }

    public String save(FileListData data, FFileList fileList, FormContext context) throws Exception
    {
        AttachmentSaver saver = saverProvider.get();

        Long listId = null;
        if (data.getFileListId() != null)
        {
            for (String id : data.getFileListId().split(","))
            {
                //找到一个非只读的列表
                if (!id.startsWith("@") && id.length() > 0)
                    listId = Long.valueOf(id);
            }
        }

        saver.setAttachmentId(listId);

        if (data.getDeleteds() != null && data.getDeleteds().length > 0)
        {
            //设置要删除的文件
            saver.setDeleteds(DataConvert.<Integer[]>convertArray(Integer.class, data.getDeleteds()));
        }

        InputFile[] uploads = data.getUploads();
        if (uploads != null && uploads.length > 0)
        {
            //创建要上传的文件

            //获得当前用户和部门
            SystemFormContext systemFormContext = (SystemFormContext) context;
            Integer userId = systemFormContext.getUserId();
            Integer deptId = systemFormContext.isOperatorDept(fileList) ? Integer.valueOf(
                    systemFormContext.getOperator(fileList)) : systemFormContext.getDeptId();

            List<Attachment> attachments = null;
            for (InputFile file : data.getUploads())
            {
                if (file != null)
                {
                    if (attachments == null)
                        attachments = new ArrayList<Attachment>(uploads.length);

                    Attachment attachment = new Attachment();
                    attachment.setAttachmentName(file.getName());
                    attachment.setInputable(file.getInputable());
                    attachment.setUserId(userId);
                    attachment.setDeptId(deptId);
                    attachment.setTag("form");

                    attachments.add(attachment);
                }
            }

            if (attachments != null)
                saver.setAttachments(attachments);
        }

        saver.save();

        if (saver.getAttachmentId() != null)
        {
            if (StringUtils.isEmpty(data.getFileListId()))
                return saver.getAttachmentId().toString();

            //原来没有可写列表，将新的附件列表id加到后面
            if (listId == null)
                return data.getFileListId() + "," + saver.getAttachmentId().toString();
        }

        return data.getFileListId();
    }
}
