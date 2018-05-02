package com.gzzm.platform.attachment;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.storage.CommonFileColumn;

import java.util.Date;

/**
 * 当一个附件有多人修改时保存备份
 *
 * @author camel
 * @date 13-11-17
 */
@Entity(table = "PFATTACHMENTBAK", keys = {"bakId"})
@Indexes({@Index(columns = {"ATTACHMENTID", "ATTACHMENTNO"})})
public class AttachmentBak
{
    @Inject
    private static Provider<GlobalConfig> configProvider;

    @Inject
    private static Provider<FileUploadService> uploadServiceProvider;

    /**
     * 备份ID
     */
    @Generatable(length = 13)
    private Long bakId;

    /**
     * 附件ID，一组附件有一个唯一的ID
     */
    @Generatable(name = "PFATTACHMENTID", length = 12)
    private Long attachmentId;

    /**
     * 附件号，同一组附件中区分每个附件，从1开始编号
     */
    @ColumnDescription(type = "number(3)")
    private Integer attachmentNo;

    /**
     * 附件内容，以流的形式读取
     */
    @NotSerialized
    @CommonFileColumn(pathColumn = "filePath", target = "{target0}", condition = "file",
            path = "bak/attachment/{yyyyMM}/{yyyyMMdd}/{attachmentId}_{attachmentNo}_{bakId}", clear = true)
    private Inputable content;

    /**
     * 文件路径，如果附件是以文件格式保存在硬盘中，则指向硬盘中的路径，filePath和content只能有一个为空
     */
    @ColumnDescription(type = "varchar(250)")
    private String filePath;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件类型，是映射到磁盘上的文件还是数据库的大字段中
     */
    private AttachmentType type;

    /**
     * 用途，表示文件用于做何用处，为一个字符串，可由各功能模块自己定义，
     * 和PFATTACHMENT表冗余，冗余此字段主要是因为当附件存放在磁盘上时，需要根据此字段确定存放目录
     */
    @ColumnDescription(type = "varchar(30)")
    private String tag;

    /**
     * 文件名称，文件的原始名称
     */
    @ColumnDescription(type = "varchar(250)")
    private String fileName;

    /**
     * 保存文档的用户
     */
    private Integer userId;

    @NotSerialized
    private User user;

    /**
     * 保存的时间
     */
    private Date saveTime;

    @ColumnDescription(type = "varchar(50)")
    private String target;

    public AttachmentBak()
    {
    }

    public Long getBakId()
    {
        return bakId;
    }

    public void setBakId(Long bakId)
    {
        this.bakId = bakId;
    }

    public Long getAttachmentId()
    {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId)
    {
        this.attachmentId = attachmentId;
    }

    public Integer getAttachmentNo()
    {
        return attachmentNo;
    }

    public void setAttachmentNo(Integer attachmentNo)
    {
        this.attachmentNo = attachmentNo;
    }

    public Inputable getContent()
    {
        return content;
    }

    public void setContent(Inputable content)
    {
        this.content = content;
    }

    public String getFilePath()
    {
        return filePath;
    }

    public void setFilePath(String filePath)
    {
        this.filePath = filePath;
    }

    public Long getFileSize()
    {
        return fileSize;
    }

    public void setFileSize(Long fileSize)
    {
        this.fileSize = fileSize;
    }

    public AttachmentType getType()
    {
        return type;
    }

    public void setType(AttachmentType type)
    {
        this.type = type;
    }

    public String getTag()
    {
        return tag;
    }

    public void setTag(String tag)
    {
        this.tag = tag;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public Date getSaveTime()
    {
        return saveTime;
    }

    public void setSaveTime(Date saveTime)
    {
        this.saveTime = saveTime;
    }

    public String getTarget()
    {
        return target;
    }

    public void setTarget(String target)
    {
        this.target = target;
    }

    @NotSerialized
    public String getTarget0()
    {
        if (!StringUtils.isEmpty(target))
            return target;
        else if (!StringUtils.isEmpty(tag))
            return tag;
        else
            return "attachment";
    }

    @NotSerialized
    public boolean isBlob()
    {
        return type == AttachmentType.blob;
    }

    @NotSerialized
    public boolean isFile()
    {
        return type == null && type == AttachmentType.file;
    }

    public String getEncodedId()
    {
        return Attachment.encodeId(attachmentId);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof AttachmentBak))
            return false;

        AttachmentBak that = (AttachmentBak) o;

        return bakId.equals(that.bakId);
    }

    @Override
    public int hashCode()
    {
        return bakId.hashCode();
    }
}
