package com.gzzm.ods.print;

import com.gzzm.ods.business.*;
import com.gzzm.platform.organ.Dept;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

import java.io.InputStream;
import java.util.Date;

/**
 * 打印模版
 *
 * @author camel
 * @date 11-11-9
 */
@Entity(table = "ODPRINTTEMPLATE", keys = "templateId")
public class PrintTemplate
{
    /**
     * 打印模版ID
     */
    @Generatable(length = 7)
    private Integer templateId;

    /**
     * 模板名称
     */
    @Require
    @Unique(with = "deptId")
    @ColumnDescription(type = "varchar(250)")
    private String templateName;

    /**
     * 显示在办文页面上的名称
     */
    @Require
    @ColumnDescription(type = "varchar(40)")
    private String showName;

    @Index
    private Integer deptId;

    /**
     * 所属部门的ID
     */
    @NotSerialized
    private Dept dept;

    /**
     * 绑定的业务ID，可以为空
     */
    private Integer businessId;

    /**
     * 关联的业务模型
     */
    @NotSerialized
    private BusinessModel businessModel;

    /**
     * 关联的业务类型
     *
     * @see com.gzzm.ods.flow.OdFlowInstance#type
     */
    @Require
    @ColumnDescription(type = "varchar(50)")
    private BusinessType businessType;

    /**
     * 关联表单
     */
    private FormType formType;

    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    /**
     * 模板内容
     */
    private InputStream content;

    /**
     * 最后修改时间
     */
    private Date lastModified;

    /**
     * 二维码左上角位置
     */
    private Integer qrLeft;

    /**
     * 二维码左上角位置
     */
    private Integer qrTop;

    public PrintTemplate()
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

    public String getShowName()
    {
        return showName;
    }

    public void setShowName(String showName)
    {
        this.showName = showName;
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

    public BusinessType getBusinessType()
    {
        return businessType;
    }

    public void setBusinessType(BusinessType businessType)
    {
        this.businessType = businessType;
    }

    public Integer getBusinessId()
    {
        return businessId;
    }

    public void setBusinessId(Integer businessId)
    {
        this.businessId = businessId;
    }

    public BusinessModel getBusinessModel()
    {
        return businessModel;
    }

    public void setBusinessModel(BusinessModel businessModel)
    {
        this.businessModel = businessModel;
    }

    public FormType getFormType()
    {
        return formType;
    }

    public void setFormType(FormType formType)
    {
        this.formType = formType;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    public InputStream getContent()
    {
        return content;
    }

    public void setContent(InputStream content)
    {
        this.content = content;
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

    public Integer getQrLeft()
    {
        return qrLeft;
    }

    public void setQrLeft(Integer qrLeft)
    {
        this.qrLeft = qrLeft;
    }

    public Integer getQrTop()
    {
        return qrTop;
    }

    public void setQrTop(Integer qrTop)
    {
        this.qrTop = qrTop;
    }

    @Override
    public String toString()
    {
        return templateName;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof PrintTemplate))
            return false;

        PrintTemplate that = (PrintTemplate) o;

        return templateId.equals(that.templateId);
    }

    @Override
    public int hashCode()
    {
        return templateId.hashCode();
    }
}
