package com.gzzm.safecampus.device.card.entity;

import net.cyan.thunwind.annotation.*;

/**
 * @author liyabin
 * @date 2018/3/28
 */
@Entity(table = "SCIDTYPE", keys = "id")
public class IDType
{

    @Generatable(length = 8)
    private Integer id;
    @ColumnDescription(type = "NUMBER(9)")
    private Integer typeNo;
    @ColumnDescription(type = "VARCHAR(30)")
    private String name;

    public IDType()
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

    public Integer getTypeNo()
    {
        return typeNo;
    }

    public void setTypeNo(Integer typeNo)
    {
        this.typeNo = typeNo;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
