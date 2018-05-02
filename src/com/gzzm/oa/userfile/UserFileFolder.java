package com.gzzm.oa.userfile;

import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * @author : wmy
 * @date : 2010-3-10
 */
@Entity(table = "OAUSERFILEFOLDER", keys = "folderId")
public class UserFileFolder
{
    /**
     * 主键
     */
    @Generatable(length = 9)
    private Integer folderId;

    /**
     * 此字段关联PFUSER表
     */
    private Integer userId;

    /**
     * 上级目录ID
     */
    private Integer parentFolderId;

    /**
     * 文件夹名称
     */
    @Require
    @Unique(with = {"parentFolderId", "userId"})
    @ColumnDescription(type = "varchar(250)", nullable = false)
    private String folderName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 排序ID
     */
    private Integer orderId;

    /**
     * 关联用户表
     */
    private User user;

    /**
     * 获取上级目录
     */
    @NotSerialized
    @ToOne("PARENTFOLDERID")
    private UserFileFolder parentFolder;

    /**
     * 获取子目录列表
     */
    @NotSerialized
    @OneToMany("PARENTFOLDERID")
    private List<UserFileFolder> children;

    /**
     * 被共享者，与用户的多对多关系，表示有权限查看此文件夹的用户的集合
     */
    @OneToMany
    @NotSerialized
    private List<UserFileFolderShare> shares;

    /**
     * 文件夹里的文件
     */
    @NotSerialized
    @OneToMany
    private List<UserFile> files;

    /**
     * 删除标志
     */
    @ColumnDescription(type = "number(2)",defaultValue = "0")
    private Boolean deleteTag;

    public UserFileFolder()
    {
    }

    public Integer getFolderId()
    {
        return folderId;
    }

    public void setFolderId(Integer folderId)
    {
        this.folderId = folderId;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public Integer getParentFolderId()
    {
        return parentFolderId;
    }

    public void setParentFolderId(Integer parentFolderId)
    {
        this.parentFolderId = parentFolderId;
    }

    public String getFolderName()
    {
        return folderName;
    }

    public void setFolderName(String folderName)
    {
        this.folderName = folderName;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public UserFileFolder getParentFolder()
    {
        return parentFolder;
    }

    public void setParentFolder(UserFileFolder parentFolder)
    {
        this.parentFolder = parentFolder;
    }

    public List<UserFileFolder> getChildren()
    {
        return children;
    }

    public void setChildren(List<UserFileFolder> children)
    {
        this.children = children;
    }

    @Override
    public String toString()
    {
        return folderName;
    }

    public List<UserFileFolderShare> getShares()
    {
        return shares;
    }

    public void setShares(List<UserFileFolderShare> shares)
    {
        this.shares = shares;
    }

    public List<UserFile> getFiles()
    {
        return files;
    }

    public void setFiles(List<UserFile> files)
    {
        this.files = files;
    }

    public Boolean getDeleteTag() {
        return deleteTag;
    }

    public void setDeleteTag(Boolean deleteTag) {
        this.deleteTag = deleteTag;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof UserFileFolder))
            return false;

        UserFileFolder folder = (UserFileFolder) o;

        return folderId.equals(folder.folderId);

    }

    @Override
    public int hashCode()
    {
        return folderId.hashCode();
    }
}
