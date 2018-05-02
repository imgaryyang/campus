package com.gzzm.safecampus.campus.account;

import net.cyan.thunwind.annotation.ColumnDescription;
import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.annotation.Generatable;

/**
 * 学校级别
 *
 * @author yuanfang
 * @date 18-03-06 17:35
 */
@Entity(table = "SCSCHOOLLEVEL", keys = "levelId")
public class SchoolLevel
{
    @Generatable(length = 3)
    private Integer levelId;

    /**
     * 学校级别
     */
    @ColumnDescription(type = "VARCHAR2(50)")
    private String levelName;

    /**
     * 删除标识
     */
    @ColumnDescription(type = "NUMBER(1)", defaultValue = "0")
    private Integer deleteTag;

    @ColumnDescription(type = "NUMBER(3)")
    private Integer orderId;

    public SchoolLevel()
    {
    }

    public SchoolLevel(Integer levelId, String levelName)
    {
        this.levelId = levelId;
        this.levelName = levelName;
    }

    public Integer getLevelId()
    {
        return levelId;
    }

    public void setLevelId(Integer levelId)
    {
        this.levelId = levelId;
    }

    public String getLevelName()
    {
        return levelName;
    }

    public void setLevelName(String levelName)
    {
        this.levelName = levelName;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    public Integer getDeleteTag()
    {
        return deleteTag;
    }

    public void setDeleteTag(Integer deleteTag)
    {
        this.deleteTag = deleteTag;
    }

    @Override
    public String toString()
    {
        return levelName;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof SchoolLevel)) return false;

        SchoolLevel that = (SchoolLevel) o;

        return levelId.equals(that.levelId);

    }

    @Override
    public int hashCode()
    {
        return levelId.hashCode();
    }
}
