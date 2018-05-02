package com.gzzm.platform.attachment;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.organ.*;
import net.cyan.activex.OfficeUtils;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.ext.*;
import net.cyan.commons.util.*;
import net.cyan.commons.util.io.DownloadFile;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.storage.CommonFileColumn;

import java.io.*;
import java.util.Date;

/**
 * 附件，统一管理系统的所有附件，附件可以存在硬盘中也可以保存在数据库中
 * 如果附件保存在数据库中则保存在content字段中，如果保存在硬盘中则通过filePath字段指向文件路径
 *
 * @author camel
 * @date 2010-3-15
 * @see com.gzzm.platform.commons.GlobalConfig
 */
@Entity(table = "PFATTACHMENT", keys = {"attachmentId", "attachmentNo"})
public class Attachment implements Comparable<Attachment>, Inputable
{
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
     * 附件名称
     */
    @ColumnDescription(type = "varchar(250)", nullable = false)
    private String attachmentName;

    /**
     * 文件名称，文件的原始名称
     */
    @ColumnDescription(type = "varchar(800)")
    private String fileName;

    /**
     * 附件内容，以流的形式读取
     */
    @NotSerialized
    @CommonFileColumn(pathColumn = "filePath", target = "{target0}", condition = "file",
            path = "{yyyyMM}/{yyyyMMdd}/attachment/{tag}/{attachmentId}_{attachmentNo}", clear = true)
    private Inputable content;

    /**
     * 文件路径，如果附件是以文件格式保存在硬盘中，则指向硬盘中的路径，filePath和content只能有一个为空
     */
    @ColumnDescription(type = "varchar(800)")
    private String filePath;

    /**
     * 文件说明，不必要
     */
    @ColumnDescription(type = "varchar(800)")
    private String remark;

    /**
     * 文件大小
     */
    @ColumnDescription(type = "number(18)")
    private Long fileSize;

    /**
     * 上传时间
     */
    private Date uploadTime;

    /**
     * 上传文件的用户,可以为空
     */
    private Integer userId;

    /**
     * 关联用户对象
     */
    @NotSerialized
    private User user;

    /**
     * 上传文件的部门，可以为空
     */
    private Integer deptId;

    /**
     * 关联部门对象
     */
    @NotSerialized
    private Dept dept;

    /**
     * 用途，表示文件用于做何用处，为一个字符串，可由各功能模块自己定义
     */
    @ColumnDescription(type = "varchar(30)")
    private String tag;

    /**
     * 文件类型，是映射到磁盘上的文件还是数据库的大字段中
     */
    @ColumnDescription(defaultValue = "1", nullable = false)
    private AttachmentType type;

    private FileType fileType;

    /**
     * 排序号
     */
    @ColumnDescription(type = "number(6)", nullable = false, defaultValue = "999999")
    private Integer orderId;

    /**
     * 缩略图
     */
    @NotSerialized
    private InputStream thumb;

    @Index
    @ColumnDescription(type = "varchar(32)")
    private String uuid;

    @ColumnDescription(type = "varchar(200)")
    private String contentType;

    /**
     * 文件保存在哪个文件服务上，调用哪个文件服务读取文件
     */
    @ColumnDescription(type = "varchar(50)")
    private String target;

    public Attachment()
    {
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

    public String getAttachmentName()
    {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName)
    {
        this.attachmentName = attachmentName;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
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

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public Long getFileSize()
    {
        return fileSize;
    }

    public void setFileSize(Long fileSize)
    {
        this.fileSize = fileSize;
    }

    public Date getUploadTime()
    {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime)
    {
        this.uploadTime = uploadTime;
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

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Dept getDept()
    {
        return dept;
    }

    public void setDept(Dept dept)
    {
        this.dept = dept;
    }

    public String getTag()
    {
        return tag;
    }

    public void setTag(String tag)
    {
        this.tag = tag;
    }

    public AttachmentType getType()
    {
        return type;
    }

    public void setType(AttachmentType type)
    {
        this.type = type;
    }

    public FileType getFileType()
    {
        return fileType;
    }

    public void setFileType(FileType fileType)
    {
        this.fileType = fileType;
    }

    public long getSize() throws Exception
    {
        if (fileSize != null)
            return fileSize;

        Inputable inputable = getInputable();
        if (inputable != null)
            return inputable.size();

        return 0;
    }

    @NotSerialized
    public String getExtName()
    {
        return IOUtils.getExtName(fileName);
    }

    @NotSerialized
    public boolean isImage()
    {
        return IOUtils.isImage(getExtName());
    }

    @NotSerialized
    public boolean canChangeToHtml()
    {
        String extName = getExtName();
        return OfficeUtils.canChangeToHtml(extName) || (extName != null && "pdf".equalsIgnoreCase(extName));
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
        return type == null || type == AttachmentType.file;
    }

    @NotSerialized
    @Transient
    public Inputable getInputable() throws IOException
    {
        return getContent();
    }

    public void setInputable(Inputable inputable)
    {
        setContent(inputable);
    }

    public InputFile getInputFile() throws IOException
    {
        return getInputFile(null, null);
    }

    public InputFile getInputFile(String contentType, Boolean attachment) throws IOException
    {
        DownloadFile file = new DownloadFile(getInputable(), fileName);

        if (contentType == null)
            contentType = getContentType1();

        if (contentType != null)
            file.setType(contentType);

        if (attachment != null)
            file.setAttachment(attachment);
        else
            file.setAttachment(type == AttachmentType.file || type == null);

        return file;
    }

    @NotSerialized
    public String getContentType1()
    {
        String contentType = getContentType();
        if (StringUtils.isEmpty(contentType))
        {
            if (fileType == FileType.image)
            {
                return "image/" + IOUtils.getExtName(fileName);
            }
            else if (fileType == FileType.video)
            {
                return "video/" + IOUtils.getExtName(fileName);
            }
        }

        return contentType;
    }


    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    public InputStream getThumb()
    {
        return thumb;
    }

    public void setThumb(InputStream thumb)
    {
        this.thumb = thumb;
    }

    public String getUuid()
    {
        return uuid;
    }

    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }

    public String getContentType()
    {
        return contentType;
    }

    public void setContentType(String contentType)
    {
        this.contentType = contentType;
    }

    public String getTarget()
    {
        return target;
    }

    public void setTarget(String target)
    {
        this.target = target;
    }

    public void save() throws Exception
    {
    }

    public void delete() throws Exception
    {
    }

    public int compareTo(Attachment o)
    {
        if (attachmentId == null)
            return 1;

        if (o.attachmentId == null)
            return -1;

        int c = attachmentId.compareTo(o.attachmentId);
        if (c != 0)
            return c;

        if (orderId != null && o.orderId != null)
        {
            c = orderId.compareTo(o.orderId);
            if (c != 0)
                return c;
        }

        return attachmentNo.compareTo(o.attachmentNo);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof Attachment))
            return false;

        Attachment that = (Attachment) o;

        return attachmentId.equals(that.attachmentId) && attachmentNo.equals(that.attachmentNo);
    }

    @Override
    public int hashCode()
    {
        int result = attachmentId.hashCode();
        result = 31 * result + attachmentNo.hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return attachmentName;
    }

    public InputStream getInputStream() throws IOException
    {
        Inputable inputable = getInputable();
        return inputable == null ? null : inputable.getInputStream();
    }

    public void writeTo(OutputStream out) throws IOException
    {
        Inputable inputable = getInputable();
        if (inputable != null)
            inputable.writeTo(out);
    }

    public void saveAs(String path) throws IOException
    {
        Inputable inputable = getInputable();
        if (inputable != null)
            inputable.saveAs(path);
    }

    public byte[] getBytes() throws IOException
    {
        Inputable inputable = getInputable();
        return inputable == null ? null : inputable.getBytes();
    }

    public long size() throws IOException
    {
        Inputable inputable = getInputable();
        return inputable == null ? 0 : getInputable().size();
    }

    @NotSerialized
    public String getIconPath()
    {
        return Tools.getFileTypeIcon(getFileName());
    }

    public Attachment cloneAttachment() throws IOException
    {
        Attachment c = new Attachment();
        c.setAttachmentName(getAttachmentName());
        c.setFileName(getFileName());
        c.setFileSize(getFileSize());
        c.setType(getType());
        c.setTag(getTag());
        c.setUserId(getUserId());
        c.setDeptId(getDeptId());
        c.setInputable(cloneInputable());

        return c;
    }

    public Inputable cloneInputable() throws IOException
    {
        return getContent();
    }

    @NotSerialized
    public String getText() throws Exception
    {
        String extName = IOUtils.getExtName(getFileName());
        TextExtractor extractor = TextExtractors.getExtractor(extName);

        if (extractor == null)
            return "";

        return extractor.extract(extName, getInputStream());
    }

    public String getEncodedId()
    {
        return encodeId(attachmentId);
    }

    public static String encodeId(Long attachmentId)
    {
        if (attachmentId == null)
            return null;

        return IDEncoder.encode(attachmentId);
    }

    public static Long decodeId(String s) throws Exception
    {
        if (s == null)
            return null;

        return IDEncoder.decode(s);
    }
}
