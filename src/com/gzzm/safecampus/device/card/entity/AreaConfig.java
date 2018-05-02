package com.gzzm.safecampus.device.card.entity;

import com.gzzm.safecampus.device.card.enumtype.Default;
import net.cyan.thunwind.annotation.*;

/**
 * @author liyabin
 * @date 2018/3/21
 */
@Entity(table = "SCAREACONFIG",keys = "id")
public class AreaConfig
{
    @Generatable(length = 9)
    private Integer id;
    @ColumnDescription(type = "VARCHAR2(16)")
    private String name;
    @ColumnDescription(type = "VARCHAR2(4)")
    private String code;

    @ColumnDescription(type = "VARCHAR2(4)",defaultValue="0")
    private Default isDefault;
    @ColumnDescription(type = "VARCHAR2(32)")
    private String note;

    public AreaConfig()
    {
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public Default getIsDefault()
    {
        return isDefault;
    }

    public void setIsDefault(Default isDefault)
    {
        this.isDefault = isDefault;
    }

    public String getNote()
    {
        return note;
    }

    public void setNote(String note)
    {
        this.note = note;
    }
}
