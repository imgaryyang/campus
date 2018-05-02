package com.gzzm.portal.survey;

import com.gzzm.portal.cms.station.Station;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

/**
 * 调查类型
 *
 * @author camel
 * @date 13-11-13
 */
@Entity(table = "PLSURVEYTYPE", keys = "typeId")
public class SurveyType
{
    @Generatable(length = 6)
    private Integer typeId;

    @Require
    @ColumnDescription(type = "varchar(250)", nullable = false)
    private String typeName;

    private Integer stationId;

    private Station station;

    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    public SurveyType()
    {
    }

    public Integer getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Integer typeId)
    {
        this.typeId = typeId;
    }

    public String getTypeName()
    {
        return typeName;
    }

    public void setTypeName(String typeName)
    {
        this.typeName = typeName;
    }

    public Integer getStationId()
    {
        return stationId;
    }

    public void setStationId(Integer stationId)
    {
        this.stationId = stationId;
    }

    public Station getStation()
    {
        return station;
    }

    public void setStation(Station station)
    {
        this.station = station;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    @Override
    public String toString()
    {
        return typeName;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof SurveyType))
            return false;

        SurveyType that = (SurveyType) o;

        return typeId.equals(that.typeId);
    }

    @Override
    public int hashCode()
    {
        return typeId.hashCode();
    }
}
