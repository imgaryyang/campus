package com.gzzm.portal.cms.channel;

import com.gzzm.portal.cms.station.*;
import net.cyan.arachne.annotation.*;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * 栏目分类表
 *
 * @author sjy
 * @date 2018/2/23
 */
@Entity(table = "PLCHANNELCATALOG", keys = "catalogId")
public class ChannelCatalog
{
    @Generatable(length = 5)
    private Integer catalogId;

    /**
     * 分类名称
     */
    @ColumnDescription(type = "varchar(50)", nullable = false)
    private String catalogName;

    /**
     * 所属站点
     */
    private Integer stationId;

    @ToOne("STATIONID")
    private Station station;

    /**
     * 是否删除
     */
    @ColumnDescription(defaultValue = "0")
    private Boolean deleteTag;

    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    @ManyToMany(table = "PLCHANNELCATALOGRELATED")
    @OrderBy(column = "CHANNELORDER")
    @NotSerialized
    private List<Channel> relateChannels;

    public Integer getCatalogId()
    {
        return catalogId;
    }

    public void setCatalogId(Integer catalogId)
    {
        this.catalogId = catalogId;
    }

    public String getCatalogName()
    {
        return catalogName;
    }

    public void setCatalogName(String catalogName)
    {
        this.catalogName = catalogName;
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

    public Boolean getDeleteTag()
    {
        return deleteTag;
    }

    public void setDeleteTag(Boolean deleteTag)
    {
        this.deleteTag = deleteTag;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    public List<Channel> getRelateChannels()
    {
        return relateChannels;
    }

    public void setRelateChannels(List<Channel> relateChannels)
    {
        this.relateChannels = relateChannels;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof ChannelCatalog))
        {
            return false;
        }

        ChannelCatalog that = (ChannelCatalog) o;

        return catalogId.equals(that.catalogId);

    }

    @Override
    public int hashCode()
    {
        return catalogId.hashCode();
    }

    @Override
    public String toString()
    {
        return catalogName;
    }
}
