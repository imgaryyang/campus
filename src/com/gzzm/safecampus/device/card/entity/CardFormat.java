package com.gzzm.safecampus.device.card.entity;

import com.gzzm.safecampus.device.card.enumtype.*;
import net.cyan.thunwind.annotation.*;

/**
 * @author liyabin
 * @date 2018/3/22
 */
@Entity(table = "SCCARDFORMAT", keys = "cardType")
public class CardFormat
{
    @Generatable
    private Integer cardType;

    @ColumnDescription(type = "VARCHAR2(20)")
    private String cardName;

    /**
     * 是否支持考勤
     */
    private AttendFlag attendFlag;
    /**
     * 是否支持门禁
     */
    private DoorFlag doorFlag;
    /**
     * 是否支持脱机消费
     */
    private OffLineMealFlag offLineMealFlag;
    /**
     * 是否启用
     */
    private UseFlag useFlag;
    /**
     * 是否默认
     */
    private Default cardFlag;

    public CardFormat()
    {
    }

    public Integer getCardType()
    {
        return cardType;
    }

    public void setCardType(Integer cardType)
    {
        this.cardType = cardType;
    }

    public String getCardName()
    {
        return cardName;
    }

    public void setCardName(String cardName)
    {
        this.cardName = cardName;
    }

    public AttendFlag getAttendFlag()
    {
        return attendFlag;
    }

    public void setAttendFlag(AttendFlag attendFlag)
    {
        this.attendFlag = attendFlag;
    }

    public DoorFlag getDoorFlag()
    {
        return doorFlag;
    }

    public void setDoorFlag(DoorFlag doorFlag)
    {
        this.doorFlag = doorFlag;
    }

    public OffLineMealFlag getOffLineMealFlag()
    {
        return offLineMealFlag;
    }

    public void setOffLineMealFlag(OffLineMealFlag offLineMealFlag)
    {
        this.offLineMealFlag = offLineMealFlag;
    }

    public UseFlag getUseFlag()
    {
        return useFlag;
    }

    public void setUseFlag(UseFlag useFlag)
    {
        this.useFlag = useFlag;
    }

    public Default getCardFlag()
    {
        return cardFlag;
    }

    public void setCardFlag(Default cardFlag)
    {
        this.cardFlag = cardFlag;
    }
}
