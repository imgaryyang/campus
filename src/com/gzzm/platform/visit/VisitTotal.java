package com.gzzm.platform.visit;

import net.cyan.thunwind.annotation.*;

/**
 * 统计每个对象的访问总数
 *
 * @author camel
 * @date 2011-6-21
 */
@Entity(table = "PFVISITTOTAL", keys = {"type", "objectId"})
public class VisitTotal
{
    /**
     * 访问对象的类型，为一个整数，由各功能模块定义，由于此表数据量大，不允许用字符串，
     * 必须用整数,必须保证各功能模块所使用的数字不会冲突
     */
    @ColumnDescription(type = "number(3)", nullable = false)
    private Integer type;

    /**
     * 访问的对象的ID，即相关表的主键，要求必须是整数
     */
    private Integer objectId;

    /**
     * 访问次数
     */
    @ColumnDescription(type = "number(9)")
    private Integer visitCount;

    public VisitTotal()
    {
    }

    public Integer getType()
    {
        return type;
    }

    public void setType(Integer type)
    {
        this.type = type;
    }

    public Integer getObjectId()
    {
        return objectId;
    }

    public void setObjectId(Integer objectId)
    {
        this.objectId = objectId;
    }

    public Integer getVisitCount()
    {
        return visitCount;
    }

    public void setVisitCount(Integer visitCount)
    {
        this.visitCount = visitCount;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof VisitTotal))
            return false;

        VisitTotal that = (VisitTotal) o;

        return objectId.equals(that.objectId) && type.equals(that.type) && visitCount.equals(that.visitCount);
    }

    @Override
    public int hashCode()
    {
        int result = type.hashCode();
        result = 31 * result + objectId.hashCode();
        result = 31 * result + visitCount.hashCode();
        return result;
    }
}
