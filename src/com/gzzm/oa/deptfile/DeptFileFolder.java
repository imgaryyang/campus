package com.gzzm.oa.deptfile;

import com.gzzm.platform.organ.Dept;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * @author : ccs
 * @date : 2010-3-10
 */
@Entity(table = "OADEPTFILEFOLDER", keys = "folderId")
public class DeptFileFolder
{
    /**
     * 主键
     */
    @Generatable(length = 9)
    private Integer folderId;

    /**
     * 此字段关联PFDEPT表
     */
    private Integer deptId;

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
    private Dept dept;

    /**
     * 获取上级目录
     */
    @NotSerialized
    @ToOne("PARENTFOLDERID")
    private DeptFileFolder parentFolder;

    /**
     * 获取子目录列表
     */
    @NotSerialized
    @OneToMany("PARENTFOLDERID")
    private List<DeptFileFolder> children;

    /**
     * 文件夹里的文件
     */
    @NotSerialized
    @OneToMany
    private List<DeptFile> files;

    public DeptFileFolder()
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

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
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

    public Dept getDept()
    {
        return dept;
    }

    public void setDept(Dept dept)
    {
        this.dept = dept;
    }

    public DeptFileFolder getParentFolder()
    {
        return parentFolder;
    }

    public void setParentFolder(DeptFileFolder parentFolder)
    {
        this.parentFolder = parentFolder;
    }

    public List<DeptFileFolder> getChildren()
    {
        return children;
    }

    public void setChildren(List<DeptFileFolder> children)
    {
        this.children = children;
    }

    @Override
    public String toString()
    {
        return folderName;
    }

    public List<DeptFile> getFiles()
    {
        return files;
    }

    public void setFiles(List<DeptFile> files)
    {
        this.files = files;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof DeptFileFolder))
            return false;

        DeptFileFolder folder = (DeptFileFolder) o;

        return folderId.equals(folder.folderId);

    }

    @Override
    public int hashCode()
    {
        return folderId.hashCode();
    }
}
