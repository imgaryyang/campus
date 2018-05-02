package com.gzzm.portal.floatimg;

import com.gzzm.portal.cms.station.Station;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

/**
 * 漂移在一个网站上的图片
 *
 * @author camel
 * @date 2011-7-14
 */
@Entity(table = "PLFLOATIMG", keys = "imgId")
public class FloatImg
{
    @Generatable(length = 6)
    private Integer imgId;

    /**
     * 图片名称
     */
    @Require
    @ColumnDescription(type = "varchar(250)")
    private String imgName;

    /**
     * 图片链接的url
     */
    @ColumnDescription(type = "varchar(250)")
    private String url;

    /**
     * 图片的内容
     */
    private byte[] img;

    /**
     * 所属站点的ID
     */
    @Index
    private Integer stationId;

    /**
     * 关联所属站点对象
     */
    @NotSerialized
    private Station station;

    /**
     * 是否有效，true表示有效，false表示无效，仅当有效时在主页上显示此图片
     */
    @Require
    private Boolean valid;

    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    public FloatImg()
    {
    }

    public Integer getImgId()
    {
        return imgId;
    }

    public void setImgId(Integer imgId)
    {
        this.imgId = imgId;
    }

    public String getImgName()
    {
        return imgName;
    }

    public void setImgName(String imgName)
    {
        this.imgName = imgName;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public byte[] getImg()
    {
        return img;
    }

    public void setImg(byte[] img)
    {
        this.img = img;
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

    public Boolean getValid()
    {
        return valid;
    }

    public void setValid(Boolean valid)
    {
        this.valid = valid;
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
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof FloatImg))
            return false;

        FloatImg floatImg = (FloatImg) o;

        return imgId.equals(floatImg.imgId);
    }

    @Override
    public int hashCode()
    {
        return imgId.hashCode();
    }
}
