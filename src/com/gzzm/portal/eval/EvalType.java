package com.gzzm.portal.eval;

import com.gzzm.portal.cms.station.*;
import net.cyan.thunwind.annotation.*;

/**
 * 门户评价类型表
 *
 * @author sjy
 * @date 2018/2/23
 */
@Entity(table = "PLEVALTYPE", keys = "typeId")
public class EvalType
{
    @Generatable(length = 6)
    private Integer typeId;

    /**
     * 类型名称
     */
    @ColumnDescription(type = "varchar(50)", nullable = false)
    private String typeName;

    private Integer stationId;

    @ToOne("STATIONID")
    private Station station;

    /**
     * 类型
     */
    private TargetType type;

    private EvalCatalog catalog;

    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    @ColumnDescription(defaultValue = "0")
    private Boolean deleteTag;

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

    public TargetType getType()
    {
        return type;
    }

    public void setType(TargetType type)
    {
        this.type = type;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    public Boolean getDeleteTag()
    {
        return deleteTag;
    }

    public void setDeleteTag(Boolean deleteTag)
    {
        this.deleteTag = deleteTag;
    }

    public EvalCatalog getCatalog()
    {
        return catalog;
    }

    public void setCatalog(EvalCatalog catalog)
    {
        this.catalog = catalog;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof EvalType))
        {
            return false;
        }

        EvalType evalType = (EvalType) o;

        return typeId.equals(evalType.typeId);

    }

    @Override
    public int hashCode()
    {
        return typeId.hashCode();
    }

    @Override
    public String toString()
    {
        return typeName;
    }
}
