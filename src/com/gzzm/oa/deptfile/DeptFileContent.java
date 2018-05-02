package com.gzzm.oa.deptfile;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.Inputable;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.storage.CommonFileColumn;

import java.io.InputStream;

/**
 * 文件内容，关联DeptFile对象，将文件的内容单独保存，以加快对OADEPTFILE表的扫描速度
 *
 * @author : ccs
 * @date : 2010-3-10
 */
@Entity(table = "OADEPTFILECONTENT", keys = "fileId")
public class DeptFileContent
{
    /**
     * 关联OADEPTFILE表
     */
    @ColumnDescription(type = "number(9)")
    private Integer fileId;

    /**
     * 文件内容，定义为inputstream而不使用byte[]，防止文件太大时占用太大内存
     */
    @NotSerialized
    @CommonFileColumn(pathColumn = "filePath", target = "{target}", defaultTarget = "deptfile",
            path = "deptfile/{yyyyMM}/{fileId}", clear = true)
    private Inputable content;

    /**
     * 文件路径，如果附件是以文件格式保存在硬盘中，则指向硬盘中的路径，filePath和content只能有一个为空
     */
    @ColumnDescription(type = "varchar(250)")
    private String filePath;

    /**
     * 文件保存在哪个文件服务上，调用哪个文件服务读取文件
     */
    @ColumnDescription(type = "varchar(50)")
    private String target;

    /**
     * 图片的缩略图
     */
    @NotSerialized
    private InputStream thumb;

    public DeptFileContent()
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

    public String getTarget()
    {
        return target;
    }

    public void setTarget(String target)
    {
        this.target = target;
    }

    public InputStream getThumb()
    {
        return thumb;
    }

    public void setThumb(InputStream thumb)
    {
        this.thumb = thumb;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof DeptFileContent))
            return false;

        DeptFileContent fileContent = (DeptFileContent) o;

        return fileId.equals(fileContent.fileId);

    }

    @Override
    public int hashCode()
    {
        return fileId.hashCode();
    }
}
