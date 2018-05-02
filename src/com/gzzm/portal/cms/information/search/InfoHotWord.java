package com.gzzm.portal.cms.information.search;

import com.gzzm.portal.cms.station.*;
import net.cyan.arachne.annotation.*;
import net.cyan.thunwind.annotation.*;

/**
 * 信息采编在网站上的热门搜索词
 *
 * @author sjy
 * @date 2018/2/23
 */
@Entity(table = "PLINFOHOTWORD", keys = "wordId")
public class InfoHotWord
{
    @Generatable(length = 7)
    private Integer wordId;

    /**
     * 搜索关键字
     */
    @ColumnDescription(type = "varchar(250)")
    private String keyword;

    private Integer stationId;

    @ToOne("STATIONID")
    @NotSerialized
    private Station station;

    /**
     * 来源
     */
    private HotWordSource source;

    @ColumnDescription(type = "number(3)")
    private Integer orderId;

    public HotWordSource getSource()
    {
        return source;
    }

    public void setSource(HotWordSource source)
    {
        this.source = source;
    }

    public Integer getWordId()
    {
        return wordId;
    }

    public void setWordId(Integer wordId)
    {
        this.wordId = wordId;
    }

    public String getKeyword()
    {
        return keyword;
    }

    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
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

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof InfoHotWord))
        {
            return false;
        }

        InfoHotWord that = (InfoHotWord) o;

        return wordId.equals(that.wordId);

    }

    @Override
    public int hashCode()
    {
        return wordId.hashCode();
    }

    @Override
    public String toString()
    {
        return keyword;
    }
}
