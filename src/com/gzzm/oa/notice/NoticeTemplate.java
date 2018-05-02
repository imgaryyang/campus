package com.gzzm.oa.notice;

import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

/**
 * 内部信息模板实体类，对应数据库的内部信息模板信息表
 *
 * @author czf
 * @date 2010-3-16
 */
@Entity(table = "OANOTICETEMPLATE", keys = "templateId")
public class NoticeTemplate
{
    /**
     * 模板ID，长度5位，系统生成
     */
    @Generatable(length = 5)
    private Integer templateId;

    /**
     * 模板名称
     */
    @Require
    @Unique
    @ColumnDescription(type = "varchar(250)", nullable = false)
    private String templateName;

    /**
     * 模板路径
     */
    @ColumnDescription(type = "varchar(250)", nullable = false)
    private String templatePath;

    /**
     * 排序ID
     */
    @ColumnDescription(type = "number(5)", nullable = false)
    private Integer orderId;

    public NoticeTemplate()
    {
    }

    public Integer getTemplateId()
    {
        return templateId;
    }

    public void setTemplateId(Integer templateId)
    {
        this.templateId = templateId;
    }

    public String getTemplateName()
    {
        return templateName;
    }

    public void setTemplateName(String templateName)
    {
        this.templateName = templateName;
    }

    public String getTemplatePath()
    {
        return templatePath;
    }

    public void setTemplatePath(String templatePath)
    {
        this.templatePath = templatePath;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof NoticeTemplate))
            return false;

        NoticeTemplate that = (NoticeTemplate) o;

        return templateId.equals(that.templateId);

    }

    @Override
    public int hashCode()
    {
        return templateId.hashCode();
    }

    @Override
    public String toString()
    {
        return templateName;
    }


}
