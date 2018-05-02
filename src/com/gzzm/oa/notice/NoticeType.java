package com.gzzm.oa.notice;

import com.gzzm.platform.organ.Dept;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;


/**
 * 内部信息类型实体类，对应数据库的内部信息类型维护表
 *
 * @author czf
 * @date 2010-3-16
 */
@Entity(table = "OANOTICETYPE", keys = "typeId")
public class NoticeType
{
    /**
     * 类型ID，长度7位，系统生成
     */
    @Generatable(length = 7)
    private Integer typeId;

    /**
     * 类型名称
     */
    @Require
    @Unique(with = "deptId")
    @ColumnDescription(type = "varchar(250)", nullable = true)
    private String typeName;

    /**
     * 模板ID
     */
    @Require
    @ColumnDescription(nullable = false)
    private Integer templateId;

    /**
     * 模板
     */
    @NotSerialized
    private NoticeTemplate template;

    /**
     * 部门ID
     */
    @ColumnDescription(type = "number(7)", nullable = false)
    private Integer deptId;

    /**
     * 部门
     */
    private Dept dept;

    private Integer sortId;

    private NoticeSort sort;

    /**
     * 排序ID
     */
    @ColumnDescription(type = "number(4)", nullable = false)
    private Integer orderId;

    public NoticeType()
    {

    }

    public NoticeType(Integer typeId, String typeName)
    {
        this.typeId = typeId;
        this.typeName = typeName;
    }

    public Integer getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Integer typeId)
    {
        this.typeId = typeId;
    }

    public String getTypeName()
    {
        return typeName;
    }

    public void setTypeName(String typeName)
    {
        this.typeName = typeName;
    }

    public Integer getTemplateId()
    {
        return templateId;
    }

    public void setTemplateId(Integer templateId)
    {
        this.templateId = templateId;
    }

    public NoticeTemplate getTemplate()
    {
        return template;
    }

    public void setTemplate(NoticeTemplate template)
    {
        this.template = template;
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

    public Integer getSortId()
    {
        return sortId;
    }

    public void setSortId(Integer sortId)
    {
        this.sortId = sortId;
    }

    public NoticeSort getSort()
    {
        return sort;
    }

    public void setSort(NoticeSort sort)
    {
        this.sort = sort;
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

        if (!(o instanceof NoticeType))
            return false;

        NoticeType that = (NoticeType) o;

        return typeId.equals(that.typeId);

    }

    @Override
    public int hashCode()
    {
        return typeId.hashCode();
    }

    @Override
    public String toString()
    {
        return typeName;
    }
}
