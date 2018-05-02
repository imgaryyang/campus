package com.gzzm.ods.document;

import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.storage.CommonFileColumn;

import java.io.InputStream;
import java.util.Date;

/**
 * 文档文本备份
 *
 * @author camel
 * @date 11-10-20
 */
@Entity(table = "ODDOCUMENTTEXTBAK", keys = "bakId")
public class DocumentTextBak
{
    /**
     * 备份ID
     */
    @Generatable(length = 13)
    private Long bakId;

    /**
     * 文档ID
     */
    @Index
    @ColumnDescription(type = "number(12)")
    private Long textId;

    /**
     * 正文内容
     */
    @NotSerialized
    @CommonFileColumn(pathColumn = "filePath", target = "{target}", defaultTarget = "od",
            path = "bak/od/{yyyyMM}/{yyyyMMdd}/text/{textId}_{bakId}", clear = true)
    private InputStream textBody;

    /**
     * 正文内容保存的路径
     */
    @ColumnDescription(type = "varchar(250)")
    private String filePath;

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

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件保存在哪个文件服务上，调用哪个文件服务读取文件
     */
    @ColumnDescription(type = "varchar(50)")
    private String target;

    public DocumentTextBak()
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

    public Long getTextId()
    {
        return textId;
    }

    public void setTextId(Long textId)
    {
        this.textId = textId;
    }

    public InputStream getTextBody()
    {
        return textBody;
    }

    public void setTextBody(InputStream textBody)
    {
        this.textBody = textBody;
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

    public Long getFileSize()
    {
        return fileSize;
    }

    public void setFileSize(Long fileSize)
    {
        this.fileSize = fileSize;
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

    public String getEncodedId()
    {
        return OfficeDocument.encodeId(bakId);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof DocumentTextBak))
            return false;

        DocumentTextBak that = (DocumentTextBak) o;

        return bakId.equals(that.bakId);
    }

    @Override
    public int hashCode()
    {
        return bakId.hashCode();
    }
}
