package com.gzzm.platform.attachment;

import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.*;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.arachne.components.AbstractPageComponent;
import net.cyan.commons.util.*;
import net.cyan.commons.util.json.JsonSerializer;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 附件列表控件
 *
 * @author camel
 * @date 2010-4-20
 */
public class PageAttachmentList extends AbstractPageComponent<Object>
{
    public class Item
    {
        private int no;

        private long id;

        private String name;

        /**
         * 能否删除
         */
        private boolean deletable = true;

        private Item(Attachment attachment)
        {
            this.no = attachment.getAttachmentNo();
            this.name = attachment.getFileName();
            this.id = attachmentId;
            this.deletable = editable &&
                    (authority == null || authority.isDeletable(attachment, getUserOnlineInfo()));
        }

        public int getNo()
        {
            return no;
        }

        public void setNo(int no)
        {
            this.no = no;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public boolean isDeletable()
        {
            return deletable;
        }

        public void setDeletable(boolean deletable)
        {
            this.deletable = deletable;
        }

        public long getId()
        {
            return id;
        }

        public void setId(long id)
        {
            this.id = id;
        }

        public String getEncodedId()
        {
            return PageAttachmentList.this.getEncodedId();
        }
    }

    @Inject
    private static Provider<AttachmentSaver> saverProvider;

    @Inject
    private static Provider<AttachmentDao> daoProvider;

    @Inject
    private static Provider<UserOnlineInfo> userOnlineInfoProvider;

    /**
     * 接收上传的文件
     */
    private InputFile[] files;

    /*
     * 附件列表id
     */
    private Long attachmentId;

    private String encodedId;

    /**
     * 被删除的附件的编号
     */
    private String deleteds;

    @NotSerialized
    private AttachmentAuthority authority;

    /**
     * 是否可编辑
     */
    private boolean editable = true;

    /**
     * 当前用户在线信息
     */
    private UserOnlineInfo userOnlineInfo;

    public PageAttachmentList()
    {
    }

    @NotSerialized
    public List<Item> getItems() throws Exception
    {
        if (attachmentId != null)
        {
            List<Attachment> attachments = daoProvider.get().getAttachments(attachmentId);
            List<Item> items = new ArrayList<Item>(attachments.size());

            for (Attachment attachment : attachments)
            {
                if (authority == null || authority.isVisible(attachment, getUserOnlineInfo()))
                    items.add(new Item(attachment));
            }

            return items;
        }
        else
        {
            return null;
        }
    }

    public String createHtml(String tagName, Map<String, Object> attributes) throws Exception
    {
        ForwardContext context = RequestContext.getContext().getForwardContext();
        context.importJs("/platform/attachment/attachment.js");

        String id = StringUtils.toString(attributes.get("id"));
        if (id == null)
            id = expression;

        //输出标签本身
        StringBuilder buffer = new StringBuilder("<").append(tagName);
        buffer.append(" id=\"").append(HtmlUtils.escapeString(id)).append("\"");

        for (Map.Entry<String, Object> entry : attributes.entrySet())
        {
            String name = entry.getKey();
            if (!"id".equals(name) && !INIT.equals(name))
                buffer.append(" ").append(name).append("=\"").append(DataConvert.toString(entry.getValue()))
                        .append("\"");
        }

        buffer.append(">\n");

        buffer.append("<script>\n");


        List<Item> items = getItems();

        //定义树的js
        int index = expression.indexOf(".");
        if (index > 0)
            buffer.append("Cyan.requireObj(\"").append(expression.substring(0, index)).append("\");\n");
        else
            buffer.append("var ");

        buffer.append(expression).append("=new System.AttachmentList(\"").append(expression).append("\",")
                .append(attachmentId).append(",");

        if (attachmentId != null)
        {
            buffer.append("\"").append(getEncodedId()).append("\"");
        }
        else
        {
            buffer.append("null");
        }

        buffer.append(",");
        if (items != null)
        {
            new JsonSerializer(buffer).serialize(items);
        }
        else
        {
            buffer.append("null");
        }

        buffer.append(",").append(editable);

        buffer.append(");\n");

        if (!"false".equals(StringUtils.toString(attributes.get(INIT))))
            buffer.append(expression).append(".").append("init(\"").append(HtmlUtils.escapeString(id)).append("\");\n");

        buffer.append("</script>\n");

        buffer.append("\n</").append(tagName).append(">");


        return buffer.toString();
    }

    public InputFile[] getFiles()
    {
        return files;
    }

    public void setFiles(InputFile[] files)
    {
        this.files = files;
    }

    public Long getAttachmentId()
    {
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

    public AttachmentAuthority getAuthority()
    {
        return authority;
    }

    public void setAuthority(AttachmentAuthority authority)
    {
        this.authority = authority;
    }

    public boolean isEditable()
    {
        return editable;
    }

    public void setEditable(boolean editable)
    {
        this.editable = editable;
    }

    public String getDeleteds()
    {
        return deleteds;
    }

    public void setDeleteds(String deleteds)
    {
        this.deleteds = deleteds;
    }

    private UserOnlineInfo getUserOnlineInfo()
    {
        if (userOnlineInfo == null)
            userOnlineInfo = userOnlineInfoProvider.get();
        return userOnlineInfo;
    }

    public Long save(Integer userId, Integer deptId, String tag) throws Exception
    {
        return save(userId, deptId, tag, null);
    }

    public Long save(Integer userId, Integer deptId, String tag, AttachmentType type) throws Exception
    {
        if ((files != null && files.length > 0) || (attachmentId != null && deleteds != null && deleteds.length() > 0))
        {
            AttachmentSaver saver = saverProvider.get();

            saver.setAttachmentId(attachmentId);

            if (deleteds != null && deleteds.length() > 0)
            {
                String[] ss = deleteds.split(",");
                Integer[] deleteds = new Integer[ss.length];
                for (int i = 0; i < ss.length; i++)
                    deleteds[i] = Integer.valueOf(ss[i]);

                saver.setDeleteds(deleteds);
            }

            if (files != null && files.length > 0)
            {
                List<Attachment> attachments = new ArrayList<Attachment>(files.length);
                for (InputFile file : files)
                {
                    if (file != null)
                    {
                        Attachment attachment = new Attachment();
                        attachment.setAttachmentName(file.getName());
                        attachment.setFileName(file.getName());
                        attachment.setInputable(file.getInputable());
                        attachment.setTag(tag);
                        attachment.setType(type);
                        attachment.setUserId(userId);
                        attachment.setDeptId(deptId);

                        attachments.add(attachment);
                    }
                }

                saver.setAttachments(attachments);
            }

            saver.save();

            if (attachmentId == null)
                attachmentId = saver.getAttachmentId();

            return attachmentId;
        }

        return null;
    }
}
