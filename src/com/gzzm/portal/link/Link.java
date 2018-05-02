package com.gzzm.portal.link;

import com.gzzm.portal.cms.station.Station;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

/**
 * 友情链接
 *
 * @author camel
 * @date 2011-5-30
 */
@Entity(table = "PLLINK", keys = "linkId")
@Indexes({@Index(columns = {"STATIONID", "TYPE"})})
public class Link
{
    /**
     * 链接ID
     */
    @Generatable(length = 6)
    private Integer linkId;

    /**
     * 链接名称
     */
    @Require
    private String linkName;

    /**
     * url
     */
    @Require
    private String url;

    /**
     * 所属网站
     */
    @Require
    private Integer stationId;

    /**
     * 关联站点对象
     */
    @NotSerialized
    private Station station;

    /**
     * 友情链接分类，可以对同一个站点的友情链接分成若干类，在不同的地方显示
     */
    @ColumnDescription(type = "varchar(50)")
    private String type;

    /**
     * 排序ID，必须在某个类型内排序
     */
    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    /**
     * 栏目图片
     */
    @NotSerialized
    private byte[] photo;

    public Link()
    {
    }

    public Integer getLinkId()
    {
        return linkId;
    }

    public void setLinkId(Integer linkId)
    {
        this.linkId = linkId;
    }

    public String getLinkName()
    {
        return linkName;
    }

    public void setLinkName(String linkName)
    {
        this.linkName = linkName;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
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

    public String getType()
    {
        return type;
    }

    public void setType(String type)
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

    public byte[] getPhoto()
    {
        return photo;
    }

    public void setPhoto(byte[] photo)
    {
        this.photo = photo;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof Link))
            return false;

        Link link = (Link) o;

        return linkId.equals(link.linkId);
    }

    @Override
    public int hashCode()
    {
        return linkId.hashCode();
    }

    @Override
    public String toString()
    {
        return linkName;
    }
}
