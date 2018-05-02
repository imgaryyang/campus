package com.gzzm.ods.documenttemplate;

import com.gzzm.platform.organ.Dept;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

import java.io.InputStream;
import java.util.Date;

/**
 * 文件模板
 *
 * @author camel
 * @date 2010-6-28
 */
@Entity(table = "ODDOCUMENTTEMPLATE", keys = "templateId")
public class DocumentTemplate
{
    /**
     * 文件模板ID，由序列号生成
     */
    @Generatable(length = 8)
    private Integer templateId;

    /**
     * 红头名称
     */
    @ColumnDescription(type = "varchar(250)")
    @Require
    @Unique(with = "deptId")
    private String templateName;

    /**
     * 红头模板所属部门
     */
    @Index
    private Integer deptId;

    /**
     * 关联Dept对象
     */
    private Dept dept;

    /**
     * 排序ID，排序范围为部门内
     */
    private Integer orderId;

    /**
     * 红头模板的内容
     */
    private InputStream template;

    /**
     * 最后修改时间
     */
    private Date lastModified;

    public DocumentTemplate()
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

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Dept getDept()
    {
        return dept;
    }

    public void setDept(Dept dept)
    {
        this.dept = dept;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    public InputStream getTemplate()
    {
        return template;
    }

    public void setTemplate(InputStream template)
    {
        this.template = template;
    }

    public Date getLastModified()
    {
        return lastModified;
    }

    public void setLastModified(Date lastModified)
    {
        this.lastModified = lastModified;
    }

    @BeforeAdd
    @BeforeUpdate
    public void beforeModified()
    {
        setLastModified(new Date());
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof DocumentTemplate))
            return false;

        DocumentTemplate documentTemplate = (DocumentTemplate) o;

        return templateId.equals(documentTemplate.templateId);
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