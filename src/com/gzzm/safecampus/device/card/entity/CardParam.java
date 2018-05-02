package com.gzzm.safecampus.device.card.entity;

import com.gzzm.safecampus.device.card.enumtype.ParamType;
import net.cyan.thunwind.annotation.*;

/**
 * @author liyabin
 * @date 2018/3/21
 */
@Entity(table = "SCCARDPARAM",keys = "id")
public class CardParam
{
    @Generatable(length = 2)
    private Integer id;

    @ColumnDescription(type = "VARCHAR(9)")
    private String sector;

    @ColumnDescription(type = "blob")
    private String cardParam;

    private ParamType paramType;
    public CardParam()
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

    public String getSector()
    {
        return sector;
    }

    public void setSector(String sector)
    {
        this.sector = sector;
    }

    public String getCardParam()
    {
        return cardParam;
    }

    public void setCardParam(String cardParam)
    {
        this.cardParam = cardParam;
    }

    public ParamType getParamType()
    {
        return paramType;
    }

    public void setParamType(ParamType paramType)
    {
        this.paramType = paramType;
    }
}
