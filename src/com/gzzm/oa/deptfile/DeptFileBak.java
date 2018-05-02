package com.gzzm.oa.deptfile;

import com.gzzm.platform.commons.*;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.storage.CommonFileColumn;

import java.util.Date;

/**
 * 文件内容备份，多次修改保存历史记录
 *
 * @author : ccs
 * @date : 2010-3-10
 */
@Entity(table = "OADEPTFILEBAK", keys = "bakId")
public class DeptFileBak
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
     * 关联OADEPTFILE表
     */
    @Index
    private Integer fileId;

    @Cascade
    @NotSerialized
    private DeptFile deptFile;

    /**
     * 文件内容，定义为inputstream而不使用byte[]，防止文件太大时占用太大内存
     */
    @NotSerialized
    @CommonFileColumn(pathColumn = "filePath", target = "{target}", defaultTarget = "deptfile",
            path = "deptfile/{yyyyMM}/bak/{fileId}/{bakId}")
    private Inputable content;

    /**
     * 文件路径，如果附件是以文件格式保存在硬盘中，则指向硬盘中的路径，filePath和content只能有一个为空
     */
    @ColumnDescription(type = "varchar(250)")
    private String filePath;

    /**
     * 文件类型
     */
    @ColumnDescription(type = "varchar(50)")
    private String fileType;

    /**
     * 文件大小
     */
    @ColumnDescription(type = "number(18)", nullable = false)
    private Long fileSize;

    /**
     * 上传时间
     */
    @ColumnDescription(nullable = false)
    private Date uploadTime;

    /**
     * 文件保存在哪个文件服务上，调用哪个文件服务读取文件
     */
    @ColumnDescription(type = "varchar(50)")
    private String target;

    public DeptFileBak()
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

    public Integer getFileId()
    {
        return fileId;
    }

    public void setFileId(Integer fileId)
    {
        this.fileId = fileId;
    }

    public DeptFile getDeptFile()
    {
        return deptFile;
    }

    public void setDeptFile(DeptFile deptFile)
    {
        this.deptFile = deptFile;
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

    public Long getFileSize()
    {
        return fileSize;
    }

    public void setFileSize(Long fileSize)
    {
        this.fileSize = fileSize;
    }

    public String getTarget()
    {
        return target;
    }

    public void setTarget(String target)
    {
        this.target = target;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof DeptFileBak))
            return false;

        DeptFileBak deptFileBak = (DeptFileBak) o;

        return fileId.equals(deptFileBak.fileId);

    }

    @Override
    public int hashCode()
    {
        return fileId.hashCode();
    }
}
