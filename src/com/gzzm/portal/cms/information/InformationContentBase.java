package com.gzzm.portal.cms.information;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

/**
 * @author camel
 * @date 2014/8/6
 */
public abstract class InformationContentBase<I extends InformationBase0>
{
    private Long informationId;

    /**
     * 关联信息对象
     */
    @NotSerialized
    private I information;

    /**
     * 页码，第一页为0，以此类推
     */
    @ColumnDescription(type = "number(3)")
    private Integer pageNo;

    /**
     * 内容
     */
    private char[] content;

    /**
     * 以字符串格式保存内容
     */
    @Transient
    private String contentString;

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

    public InformationContentBase()
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

    public I getInformation()
    {
        return information;
    }

    public void setInformation(I information)
    {
        this.information = information;
    }

    public Integer getPageNo()
    {
        return pageNo;
    }

    public void setPageNo(Integer pageNo)
    {
        this.pageNo = pageNo;
    }

    public char[] getContent()
    {
        return content;
    }

    public void setContent(char[] content)
    {
        this.content = content;
    }

    public String getContentString()
    {
        if (contentString == null)
        {
            char[] content = getContent();
            contentString = content == null ? "" : new String(content);
        }

        return contentString;
    }

    public void setContentString(String contentString)
    {
        this.contentString = contentString;
        setContent(contentString.toCharArray());
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

    @Override
    public int hashCode()
    {
        int result = informationId.hashCode();
        result = 31 * result + pageNo.hashCode();
        return result;
    }
}
