package com.gzzm.portal.cms.information;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.ColumnDescription;

import java.io.InputStream;

/**
 * @author camel
 * @date 2014/8/6
 */
public class InformationFileBase<I extends InformationBase>
{
    private Long informationId;

    private InputStream content;

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

    @ColumnDescription(type = "varchar(250)")
    private String fileName;

    @NotSerialized
    private I information;

    public InformationFileBase()
    {
    }

    public Long getInformationId()
    {
        return informationId;
    }

    public void setInformationId(Long informationId)
    {
        this.informationId = informationId;
    }

    public InputStream getContent()
    {
        return content;
    }

    public void setContent(InputStream content)
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

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public I getInformation()
    {
        return information;
    }

    public void setInformation(I information)
    {
        this.information = information;
    }

    @Override
    public int hashCode()
    {
        return informationId.hashCode();
    }
}
