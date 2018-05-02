package com.gzzm.platform.attachment;

import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.InputFile;
import net.cyan.crud.annotation.NotCondition;
import net.cyan.crud.view.FieldCell;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author camel
 * @date 12-2-6
 */
@Service(url = "/attachment/crud")
public class AttachmentCrud extends BaseNormalCrud<Attachment, Integer>
{
    @Inject
    protected AttachmentDao dao;

    @UserId
    private Integer userId;

    private Long attachmentId;

    private String encodedId;

    private InputFile file;

    @NotCondition
    private String tag;

    private Integer deptId;

    private boolean readOnly;

    private boolean dialog = true;

    public AttachmentCrud()
    {
        setPageSize(-1);
        initOrders();
    }

    protected void initOrders()
    {
        addOrderBy("orderId");
        addOrderBy("attachmentNo");
    }

    public Long getAttachmentId() throws Exception
    {
        if (attachmentId == null && encodedId != null)
            attachmentId = Attachment.decodeId(encodedId);

        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId)
    {
        this.attachmentId = attachmentId;
    }

    public String getEncodedId()
    {
        if (encodedId == null && attachmentId != null)
            encodedId = Attachment.encodeId(attachmentId);

        return encodedId;
    }

    public void setEncodedId(String encodedId)
    {
        this.encodedId = encodedId;
    }

    public InputFile getFile()
    {
        return file;
    }

    public void setFile(InputFile file)
    {
        this.file = file;
    }

    public String getTag()
    {
        return tag;
    }

    public void setTag(String tag)
    {
        this.tag = tag;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public boolean isReadOnly()
    {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly)
    {
        this.readOnly = readOnly;
    }

    public boolean isDialog()
    {
        return dialog;
    }

    public void setDialog(boolean dialog)
    {
        this.dialog = dialog;
    }

    @Override
    public Integer getKey(Attachment entity) throws Exception
    {
        return entity.getAttachmentNo();
    }

    @Override
    public void setKey(Attachment entity, Integer key) throws Exception
    {
        entity.setAttachmentNo(key);
        entity.setAttachmentId(attachmentId);
    }

    @Override
    public String getOrderField()
    {
        return "orderId";
    }

    @Override
    protected String[] getOrderWithFields()
    {
        if (deptId == null)
            return new String[]{"attachmentId"};
        else
            return new String[]{"attachmentId", "deptId"};
    }

    @Override
    public int deleteAll() throws Exception
    {
        Integer[] keys = getKeys();

        if (keys != null)
        {
            AttachmentSaver saver = Tools.getBean(AttachmentSaver.class);

            saver.setAttachmentId(attachmentId);
            saver.setDeleteds(keys);
            saver.delete();

            return keys.length;
        }
        else
        {
            return 0;
        }
    }

    @Service(method = HttpMethod.post)
    public void upload() throws Exception
    {
        if (file != null)
        {
            AttachmentSaver saver = Tools.getBean(AttachmentSaver.class);

            Attachment attachment = new Attachment();
            attachment.setAttachmentName(file.getName());
            attachment.setFileName(file.getName());
            attachment.setInputable(file.getInputable());
            attachment.setUserId(userId);
            attachment.setDeptId(deptId);
            attachment.setTag(tag);

            saver.setAttachmentId(attachmentId);
            saver.setAttachments(Collections.singleton(attachment));
            saver.save();
        }
    }

    @Service(url = "/attachments/{attachmentId}/img/first")
    public Integer getFirstImgNo() throws Exception
    {
        List<Attachment> attachments = dao.getAttachments(attachmentId);

        for (Attachment attachment : attachments)
        {
            if (attachment.isImage())
            {
                return attachment.getAttachmentNo();
            }
        }

        return null;
    }

    @Service(url = "/attachments/{attachmentId}/{$0}/img/next")
    public Integer getNextImgNo(Integer attachmentNo) throws Exception
    {
        List<Attachment> attachments = dao.getAttachments(attachmentId);

        boolean b = false;
        for (Attachment attachment : attachments)
        {
            if (!b)
            {
                if (attachment.getAttachmentNo().equals(attachmentNo))
                {
                    b = true;
                }
            }
            else
            {
                if (attachment.isImage())
                {
                    return attachment.getAttachmentNo();
                }
            }
        }

        return null;
    }

    @Service(url = "/attachments/{attachmentId}/{$0}/img/pre")
    public Integer getPreImgNo(Integer attachmentNo) throws Exception
    {
        List<Attachment> attachments = dao.getAttachments(attachmentId);

        Integer preAttachmentNo = null;
        for (Attachment attachment : attachments)
        {
            if (attachment.getAttachmentNo().equals(attachmentNo))
            {
                break;
            }

            if (attachment.isImage())
            {
                preAttachmentNo = attachment.getAttachmentNo();
            }
        }

        return preAttachmentNo;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(!readOnly);

        view.setTitle("附件列表");

        if (dialog)
            view.setPage(Pages.DIALOG_LIST);

        view.addColumn("文件名称", "attachmentName");
        view.addColumn("文件大小", new FieldCell("fileSize").setFormat("bytesize"));

        if (!dialog)
        {
            view.addColumn("上传时间", "uploadTime");
            view.addColumn("上传用户", "user.userName");
            view.addColumn("上传部门", "dept.deptName").setWidth("150");
        }

        view.addColumn("下载", new CHref("下载", "/attachment/${encodedId}/${attachmentNo}").setTarget("_blank"))
                .setWidth("50");
        view.addColumn("查看", new ConditionComponent()
                .add("image", new CHref("查看").setAction("display(${attachmentNo})"))
                .add("canChangeToHtml()", new CHref("查看").setAction("showHtml(${attachmentNo},'${extName}')")))
                .setWidth("50");

        if (!readOnly)
        {
            view.addButton(Buttons.getButton("crud.add", "", "add")).setProperty("id", "add");
            view.addButton(Buttons.delete());
            if (deptId == null)
                view.addButton(Buttons.sort());
        }

        view.importJs("/platform/attachment/list.js");

        return view;
    }
}
