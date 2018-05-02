package com.gzzm.platform.addivision;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.Chinese;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * 行政区划
 *
 * @author camel
 * @date 2011-4-29
 */
@Entity(table = "PFADDIVISION", keys = "divisionId")
public class AdDivision
{
    /**
     * 行政区划ID
     */
    @Generatable(length = 7)
    private Integer divisionId;

    @ColumnDescription(type = "varchar(50)")
    @Require
    private String divisionName;

    /**
     * 上级行政区划ID
     */
    @Index
    private Integer parentDivisionId;

    /**
     * 上级行政区划
     */
    @NotSerialized
    @ToOne("PARENTDIVISIONID")
    private AdDivision parent;

    @NotSerialized
    @OneToMany("PARENTDIVISIONID")
    @OrderBy(column = "LEFTVALUE")
    private List<AdDivision> children;

    /**
     * 行政区划编码
     */
    @Index
    @ColumnDescription(type = "varchar(20)")
    private String divisionCode;

    /**
     * 拼音
     */
    @ColumnDescription(type = "varchar(250)")
    private String spell;

    /**
     * 姓名简拼
     */
    @ColumnDescription(type = "varchar(20)")
    private String simpleSpell;

    /**
     * 使用nested set结构组织树所使用的左值
     */
    @ColumnDescription(type = "number(5)")
    private Integer leftValue;

    /**
     * 使用nested set结构组织树所使用的右值
     */
    @ColumnDescription(type = "number(5)")
    private Integer rightValue;

    public AdDivision()
    {
    }

    public Integer getDivisionId()
    {
        return divisionId;
    }

    public void setDivisionId(Integer divisionId)
    {
        this.divisionId = divisionId;
    }

    public String getDivisionName()
    {
        return divisionName;
    }

    public void setDivisionName(String divisionName)
    {
        this.divisionName = divisionName;
    }

    public Integer getParentDivisionId()
    {
        return parentDivisionId;
    }

    public void setParentDivisionId(Integer parentDivisionId)
    {
        this.parentDivisionId = parentDivisionId;
    }

    public AdDivision getParent()
    {
        return parent;
    }

    public void setParent(AdDivision parent)
    {
        this.parent = parent;
    }

    public List<AdDivision> getChildren()
    {
        return children;
    }

    public void setChildren(List<AdDivision> children)
    {
        this.children = children;
    }

    public String getDivisionCode()
    {
        return divisionCode;
    }

    public void setDivisionCode(String divisionCode)
    {
        this.divisionCode = divisionCode;
    }

    public Integer getLeftValue()
    {
        return leftValue;
    }

    public void setLeftValue(Integer leftValue)
    {
        this.leftValue = leftValue;
    }

    public Integer getRightValue()
    {
        return rightValue;
    }

    public void setRightValue(Integer rightValue)
    {
        this.rightValue = rightValue;
    }

    public String getSpell()
    {
        return spell;
    }

    public void setSpell(String spell)
    {
        this.spell = spell;
    }

    public String getSimpleSpell()
    {
        return simpleSpell;
    }

    public void setSimpleSpell(String simpleSpell)
    {
        this.simpleSpell = simpleSpell;
    }

    @NotSerialized
    public String getFullName()
    {
        StringBuilder buffer = new StringBuilder();

        for (AdDivision division = this; division.getDivisionId() != 0; division = division.getParent())
        {
            buffer.insert(0, division.getDivisionName());
        }

        return buffer.toString();
    }

    @BeforeAdd
    @BeforeUpdate
    public void beforeModify() throws Exception
    {
        //设置简拼和全拼
        String divisionName = getDivisionName();
        if (divisionName != null)
        {
            setSpell(Chinese.getLetters(divisionName));
            setSimpleSpell(Chinese.getFirstLetters(divisionName));
        }
    }

    @Override
    public String toString()
    {
        return divisionName;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof AdDivision))
            return false;

        AdDivision that = (AdDivision) o;

        return divisionId.equals(that.divisionId);
    }

    @Override
    public int hashCode()
    {
        return divisionId.hashCode();
    }
}
