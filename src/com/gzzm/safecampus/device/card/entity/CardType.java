package com.gzzm.safecampus.device.card.entity;

import com.gzzm.safecampus.device.card.enumtype.Default;
import net.cyan.thunwind.annotation.*;

/**
 * @author liyabin
 * @date 2018/3/21
 */
@Entity(table = "SCCARDTYPECONFIG", keys = "code")
public class CardType
{
    @ColumnDescription(type = "VARCHAR2(16)")
    private String name;

    @ColumnDescription(type = "NUMBER(4)")
    @Generatable(length = 4)
    private Integer code;

    private Default isDefault;
    @ColumnDescription(type = "VARCHAR2(32)")
    private String note;

    public CardType()
    {
    }


    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Integer getCode()
    {
        return code;
    }

    public void setCode(Integer code)
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
