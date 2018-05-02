package com.gzzm.mo;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

import java.io.InputStream;
import java.util.Date;

/**
 * 移动办公客户端
 *
 * @author camel
 * @date 2014/5/13
 */
@Entity(table = "MOCLIENT", keys = "clientId")
public class MoClient
{
    @Generatable(length = 6)
    private Integer clientId;

    @Require
    @ColumnDescription(type = "varchar(250)")
    private String clientName;

    @Require
    private Integer typeId;

    @NotSerialized
    private MoClientType type;

    @Require
    @Unique(with = "typeId")
    @ColumnDescription(type = "varchar(20)")
    private String versionNo;

    /**
     * 发布时间
     */
    private Date publishTime;

    /**
     * 文件名
     */
    @ColumnDescription(type = "varchar(50)")
    private String fileName;

    /**
     * 客户端文件
     */
    @NotSerialized
    private InputStream clientFile;

    /**
     * 文件大小
     */
    private Long fileSize;

    private char[] remark;

    /**
     * 是否强制更新
     */
    @ColumnDescription(defaultValue = "0")
    private Boolean forceUpdate;

    public MoClient()
    {
    }

    public char[] getRemark() {
        return remark;
    }

    public void setRemark(char[] remark) {
        this.remark = remark;
    }

    public Integer getClientId()
    {
        return clientId;
    }

    public void setClientId(Integer clientId)
    {
        this.clientId = clientId;
    }

    public String getClientName()
    {
        return clientName;
    }

    public void setClientName(String clientName)
    {
        this.clientName = clientName;
    }

    public Integer getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Integer typeId)
    {
        this.typeId = typeId;
    }

    public MoClientType getType()
    {
        return type;
    }

    public void setType(MoClientType type)
    {
        this.type = type;
    }

    public String getVersionNo()
    {
        return versionNo;
    }

    public void setVersionNo(String versionNo)
    {
        this.versionNo = versionNo;
    }

    public Date getPublishTime()
    {
        return publishTime;
    }

    public void setPublishTime(Date publishTime)
    {
        this.publishTime = publishTime;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public InputStream getClientFile()
    {
        return clientFile;
    }

    public void setClientFile(InputStream clientFile)
    {
        this.clientFile = clientFile;
    }

    public Long getFileSize()
    {
        return fileSize;
    }

    public void setFileSize(Long fileSize)
    {
        this.fileSize = fileSize;
    }

    public Boolean getForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(Boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof MoClient))
            return false;

        MoClient moClient = (MoClient) o;

        return clientId.equals(moClient.clientId);
    }

    @Override
    public int hashCode()
    {
        return clientId.hashCode();
    }

    @Override
    public String toString()
    {
        return clientName;
    }
}
