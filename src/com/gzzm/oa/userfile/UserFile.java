package com.gzzm.oa.userfile;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.organ.User;
import net.cyan.activex.OfficeUtils;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.ext.*;
import net.cyan.commons.util.*;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

import java.io.InputStream;
import java.util.*;

/**
 * @author : wmy
 * @date : 2010-3-10
 */
@Entity(table = "OAUSERFILE", keys = "fileId")
public class UserFile
{
    /**
     * 主键
     */
    @Generatable(length = 9)
    private Integer fileId;

    /**
     * 关联文件夹表
     */
    @ColumnDescription(nullable = false)
    private Integer folderId;

    /**
     * 自定义文件名(如果上传的时候没有自定义文件名则使用原有文件名，否则使用自定义文件名)
     */
    @Require
    @ColumnDescription(type = "varchar(250)", nullable = false)
    private String fileName;

    /**
     * 文件大小
     */
    @ColumnDescription(type = "number(18)", nullable = false)
    private Long fileSize;

    /**
     * 文件类型
     */
    @ColumnDescription(type = "varchar(50)")
    private String fileType;

    /**
     * 上传时间
     */
    @ColumnDescription(nullable = false)
    private Date uploadTime;

    /**
     * 关联用户表
     */
    private Integer userId;

    /**
     * 如果是上传的文件，这个字段设置为true；如果是自己编写的文件，这个字段设置为false
     * 修改字段名，字段名不要以is或者get开头
     */
    @ColumnDescription(nullable = false, defaultValue = "0")
    private Boolean editFile;

    /**
     * 被共享者，与用户的多对多关系，表示有权限查看此文件的用户的集合
     */
    @OneToMany
    @NotSerialized
    private List<UserFileShare> shares;

    /**
     * 文件内容表
     */
    @NotSerialized
    private UserFileContent fileContent;

    /**
     * 用户表
     */
    @NotSerialized
    private User user;

    /**
     * 文件夹表
     */
    @ToOne("FOLDERID")
    @NotSerialized
    private UserFileFolder fileFolder;

    /**
     * 来源
     */
    @ColumnDescription(type = "varchar(250)")
    private String source;

    /**
     * 说明
     */
    @ColumnDescription(type = "varchar(4000)")
    private String remark;

    /**
     * 摘要
     */
    @ColumnDescription(type = "varchar(4000)")
    private String summary;

    /**
     * 更新时间
     */
    @IndexTimestamp
    @Index
    private Date updateTime;


    /**
     * 删除标志
     */
    @ColumnDescription(defaultValue = "0")
    private Boolean deleteTag;

    public UserFile()
    {
    }

    public Integer getFileId()
    {
        return fileId;
    }

    public void setFileId(Integer fileId)
    {
        this.fileId = fileId;
    }

    public Integer getFolderId()
    {
        return folderId;
    }

    public void setFolderId(Integer folderId)
    {
        this.folderId = folderId;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public Long getFileSize()
    {
        return fileSize;
    }

    public void setFileSize(Long fileSize)
    {
        this.fileSize = fileSize;
    }

    public String getFileType()
    {
        return fileType;
    }

    public void setFileType(String fileType)
    {
        this.fileType = fileType;
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

    public Boolean isEditFile()
    {
        return editFile;
    }

    public void setEditFile(Boolean editFile)
    {
        this.editFile = editFile;
    }

    public List<UserFileShare> getShares()
    {
        return shares;
    }

    public void setShares(List<UserFileShare> shares)
    {
        this.shares = shares;
    }

    public UserFileContent getFileContent()
    {
        return fileContent;
    }

    public void setFileContent(UserFileContent fileContent)
    {
        this.fileContent = fileContent;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public UserFileFolder getFileFolder()
    {
        return fileFolder;
    }

    public void setFileFolder(UserFileFolder fileFolder)
    {
        this.fileFolder = fileFolder;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getSummary()
    {
        return summary;
    }

    public void setSummary(String summary)
    {
        this.summary = summary;
    }

    public Date getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }

    public Boolean getDeleteTag()
    {
        return deleteTag;
    }

    public void setDeleteTag(Boolean deleteTag)
    {
        this.deleteTag = deleteTag;
    }

    @NotSerialized
    public boolean isShared()
    {
        return getShares().size() > 0;
    }

    @NotSerialized
    public String getFullName(){
        return fileName+"."+fileType;
    }

    /**
     * 获得文本格式的文件内容，用于全文检索
     *
     * @return 文本格式的文件内容
     * @throws Exception 获得文件内容异常
     */
    @NotSerialized
    @FullText
    public String getText() throws Exception
    {
        TextExtractor extractor = TextExtractors.getExtractor(fileType);

        if (extractor == null)
            return "";

        Inputable content = getFileContent().getContent();

        if (content != null)
        {
            InputStream in = content.getInputStream();

            try
            {
                return extractor.extract(fileType, in);
            }
            finally
            {
                IOUtils.close(in);
            }
        }

        return "";
    }

    @NotSerialized
    public String getSourceName()
    {
        return Tools.getMessage("oa.userfile.source." + (StringUtils.isEmpty(source) ?
                (editFile != null && editFile ? "edit" : "upload") : source));
    }

    public boolean canShow()
    {
        return OfficeUtils.canChangeToHtml(fileType) || IOUtils.isImage(fileType) || (fileType != null &&
                ("pdf".equalsIgnoreCase(fileType) || "zip".equalsIgnoreCase(fileType) || "rar".equalsIgnoreCase(
                        fileType)));
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof UserFile))
            return false;

        UserFile file = (UserFile) o;

        return fileId.equals(file.fileId);
    }

    @Override
    public int hashCode()
    {
        return fileId.hashCode();
    }

    @Override
    public String toString()
    {
        return fileName;
    }
}
