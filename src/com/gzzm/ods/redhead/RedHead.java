package com.gzzm.ods.redhead;

import com.gzzm.platform.organ.Dept;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

import java.io.InputStream;
import java.util.Date;

/**
 * 红头模板
 *
 * @author camel
 * @date 2010-6-28
 */
@Entity(table = "ODREDHEAD", keys = "redHeadId")
public class RedHead
{
    /**
     * 红头模板ID，由序列号生成
     */
    @Generatable(length = 8)
    private Integer redHeadId;

    /**
     * 红头名称
     */
    @ColumnDescription(type = "varchar(250)")
    @Require
    @Unique(with = "deptId")
    private String redHeadName;

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
    private InputStream redHead;

    /**
     * 最后修改时间
     */
    private Date lastModified;

    /**
     * 红头类型ID
     */
    @Require
    private Integer typeId;

    /**
     * 通过关联RedHeadType对象
     *
     * @see #typeId
     */
    @NotSerialized
    private RedHeadType type;

    /**
     * 二维码左上角位置
     */
    private Integer qrLeft;

    /**
     * 二维码左上角位置
     */
    private Integer qrTop;

    public RedHead()
    {
    }

    public Integer getRedHeadId()
    {
        return redHeadId;
    }

    public void setRedHeadId(Integer redHeadId)
    {
        this.redHeadId = redHeadId;
    }

    public String getRedHeadName()
    {
        return redHeadName;
    }

    public void setRedHeadName(String redHeadName)
    {
        this.redHeadName = redHeadName;
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

    public Integer getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Integer typeId)
    {
        this.typeId = typeId;
    }

    public RedHeadType getType()
    {
        return type;
    }

    public void setType(RedHeadType type)
    {
        this.type = type;
    }

    public InputStream getRedHead()
    {
        return redHead;
    }

    public void setRedHead(InputStream redHead)
    {
        this.redHead = redHead;
    }

    public Date getLastModified()
    {
        return lastModified;
    }

    public void setLastModified(Date lastModified)
    {
        this.lastModified = lastModified;
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

        if (!(o instanceof RedHead))
            return false;

        RedHead redHead = (RedHead) o;

        return redHeadId.equals(redHead.redHeadId);
    }

    @Override
    public int hashCode()
    {
        return redHeadId.hashCode();
    }

    @Override
    public String toString()
    {
        return redHeadName;
    }
}