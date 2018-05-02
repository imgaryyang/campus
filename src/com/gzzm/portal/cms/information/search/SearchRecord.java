package com.gzzm.portal.cms.information.search;

import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * 信息采编在网站上的搜索记录表
 *
 * @author sjy
 * @date 2018/2/23
 */
@Entity(table = "PLSEARCHRECORD", keys = "recordId")
public class SearchRecord
{
    @Generatable(length = 11)
    private Long recordId;

    /**
     * 搜索关键字
     */
    @ColumnDescription(type = "varchar(250)")
    private String keyword;

    /**
     * 搜索时间
     */
    private Date searchTime;

    /**
     * 搜索人ip
     */
    @ColumnDescription(type = "varchar(25)")
    private String ip;

    @ColumnDescription(type = "number(6)")
    private Integer stationId;

    public Integer getStationId()
    {
        return stationId;
    }

    public void setStationId(Integer stationId)
    {
        this.stationId = stationId;
    }

    public Long getRecordId()
    {
        return recordId;
    }

    public void setRecordId(Long recordId)
    {
        this.recordId = recordId;
    }

    public String getKeyword()
    {
        return keyword;
    }

    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }

    public Date getSearchTime()
    {
        return searchTime;
    }

    public void setSearchTime(Date searchTime)
    {
        this.searchTime = searchTime;
    }

    public String getIp()
    {
        return ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof SearchRecord))
        {
            return false;
        }

        SearchRecord that = (SearchRecord) o;

        return recordId.equals(that.recordId);

    }

    @Override
    public int hashCode()
    {
        return recordId.hashCode();
    }
}
