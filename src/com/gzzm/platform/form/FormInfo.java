package com.gzzm.platform.form;

import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 表单信息表，每个表单的每个版本一条记录
 *
 * @author camel
 * @date 2010-9-14
 */
@Entity(table = "PFFORMINFO", keys = "formId")
public class FormInfo
{
    /**
     * 流程ID，主键，每个版本的FLOWID都不一样
     */
    @Generatable(length = 7)
    private Integer formId;

    /**
     * 表单名称
     */
    @ColumnDescription(type = "varchar(250)")
    @Require
    private String formName;

    /**
     * 忽略版本号的表单ID，当IEFORMID相同时表示这是同一个表单的多个版本
     */
    @Generatable(length = 7)
    private Integer ieFormId;

    /**
     * 表单类型，按表单的用途对表单进行分类，其值为一个预定义的字符串，如send，receive等，具体的业务功能模块可定义自己的表单类型
     */
    @Require
    @ColumnDescription(type = "varchar(50)", nullable = false)
    private FormType type;

    /**
     * 流程版本号，从1开始，发布之后才产生版本号，没发布之前版本号为空
     */
    @ColumnDescription(type = "number(4)")
    private Integer version;

    /**
     * 表单定义
     */
    @NotSerialized
    private char[] form;

    /**
     * 表单设计，表单发布之前的设计对象，用于可视化表单设计
     */
    @NotSerialized
    private byte[] design;

    /**
     * 表单创建人
     */
    private Integer creator;

    @NotSerialized
    @ToOne("CREATOR")
    private User createUser;

    /**
     * 创建时间
     */
    private Date publishTime;

    /**
     * 是否已发布，true表示已发布，false表示还处于设计阶段，只有未发布的表单才能修改
     */
    @ColumnDescription(defaultValue = "0")
    private Boolean published;

    /**
     * 是否被使用，如果被使用则发布时创建一个新的版本
     */
    @ColumnDescription(defaultValue = "0")
    private Boolean used;

    /**
     * 所属部门的ID
     */
    @Index
    private Integer deptId;

    @NotSerialized
    private Dept dept;

    /**
     * 更新时间，每次修改时置为当前时间
     *
     * @see #beforeModify();
     */
    private Date updateTime;

    private Integer orderId;

    public FormInfo()
    {
    }

    public Integer getFormId()
    {
        return formId;
    }

    public void setFormId(Integer formId)
    {
        this.formId = formId;
    }

    public String getFormName()
    {
        return formName;
    }

    public void setFormName(String formName)
    {
        this.formName = formName;
    }

    public Integer getIeFormId()
    {
        return ieFormId;
    }

    public void setIeFormId(Integer ieFormId)
    {
        this.ieFormId = ieFormId;
    }

    public FormType getType()
    {
        return type;
    }

    public void setType(FormType type)
    {
        this.type = type;
    }

    public char[] getForm()
    {
        return form;
    }

    public void setForm(char[] form)
    {
        this.form = form;
    }

    public byte[] getDesign()
    {
        return design;
    }

    public void setDesign(byte[] design)
    {
        this.design = design;
    }

    public Integer getVersion()
    {
        return version;
    }

    public void setVersion(Integer version)
    {
        this.version = version;
    }

    public Integer getCreator()
    {
        return creator;
    }

    public void setCreator(Integer creator)
    {
        this.creator = creator;
    }

    public User getCreateUser()
    {
        return createUser;
    }

    public void setCreateUser(User createUser)
    {
        this.createUser = createUser;
    }

    public Date getPublishTime()
    {
        return publishTime;
    }

    public void setPublishTime(Date publishTime)
    {
        this.publishTime = publishTime;
    }

    public Boolean isPublished()
    {
        return published;
    }

    public void setPublished(Boolean published)
    {
        this.published = published;
    }

    public Boolean isUsed()
    {
        return used;
    }

    public void setUsed(Boolean used)
    {
        this.used = used;
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

    public Date getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
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

        if (!(o instanceof FormInfo))
            return false;

        FormInfo formInfo = (FormInfo) o;

        return formId.equals(formInfo.formId);
    }

    @Override
    public int hashCode()
    {
        return formId.hashCode();
    }

    @Override
    public String toString()
    {
        return formName;
    }

    @BeforeAdd
    @BeforeUpdate
    public void beforeModify()
    {
        setUpdateTime(new Date());
    }
}
