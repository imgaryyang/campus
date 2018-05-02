package com.gzzm.platform.dict;

import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

/**
 * 字典表，用于定义一个字典
 *
 * @author camel
 * @date 13-3-26
 */
@Entity(table = "PFDICT", keys = "dictId")
public class Dict
{
    /**
     * 字典ID
     */
    @Generatable(length = 5)
    private Integer dictId;

    /**
     * 字典名称，例如，行业
     */
    @Require
    @Unique
    @ColumnDescription(type = "varchar(50)", nullable = false)
    private String dictName;

    /**
     * 字典编码，用一个简短的英文字母定义字典，用于在程序代码中试用，例如行业industry
     */
    @Require
    @Unique
    @Index(unique = true)
    @ColumnDescription(type = "varchar(20)", nullable = false)
    private String dictCode;

    /**
     * 是否需要编码
     */
    @ColumnDescription(defaultValue = "1", nullable = false)
    private Boolean requireCode;

    public Dict()
    {
    }

    public Integer getDictId()
    {
        return dictId;
    }

    public void setDictId(Integer dictId)
    {
        this.dictId = dictId;
    }

    public String getDictName()
    {
        return dictName;
    }

    public void setDictName(String dictName)
    {
        this.dictName = dictName;
    }

    public String getDictCode()
    {
        return dictCode;
    }

    public void setDictCode(String dictCode)
    {
        this.dictCode = dictCode;
    }

    public Boolean getRequireCode()
    {
        return requireCode;
    }

    public void setRequireCode(Boolean requireCode)
    {
        this.requireCode = requireCode;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof Dict))
            return false;

        Dict dict = (Dict) o;

        return dictId.equals(dict.dictId);
    }

    @Override
    public int hashCode()
    {
        return dictId.hashCode();
    }

    @Override
    public String toString()
    {
        return dictName;
    }
}
