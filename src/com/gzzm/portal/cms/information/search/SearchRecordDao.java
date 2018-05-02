package com.gzzm.portal.cms.information.search;

import com.gzzm.portal.cms.channel.*;
import com.gzzm.portal.cms.information.*;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.*;

import java.util.*;

/**
 * @author sjy
 * @date 2018/3/6
 */
public abstract class SearchRecordDao extends GeneralDao
{
    @OQL("select t.keyword as keyword,count(t.recordId) as searchCount from com.gzzm.portal.cms.information.search.SearchRecord t where t.stationId=?1 and t.searchTime>:2 group by t.keyword order by count(t.recordId) desc")
    public abstract List<QueryHotWordBean> queryHotWord(Integer stationId, Date startTime) throws Exception;

    @OQL("select t.stationId from com.gzzm.portal.cms.station.Station t where t.valid=1")
    public abstract List<Integer> queryStationIds() throws Exception;

    @OQLUpdate("delete from com.gzzm.portal.cms.information.search.InfoHotWord where stationId=:1 and source=1")
    public abstract void deleteStationHotWord(Integer stationId) throws Exception;

    @OQL("select max(t.orderId) from com.gzzm.portal.cms.information.search.InfoHotWord t where t.stationId=:1")
    public abstract Integer queryMaxOrderId(Integer stationId) throws Exception;

    @OQL("select t from ChannelCatalog t where t.stationId=:1")
    public abstract List<ChannelCatalog> queryChannelCatalogs(Integer stationId) throws Exception;



    @OQL("select i from Information i where i.valid=1 and (i.validTime is null or i.validTime>sysdate()) and contains(i.text,?1) " +
            " and( i.channelId in ?2 or i.channelId in ?3 ) order by case when i.channelId in ?3 then 0 else 1 end , i.topmost desc,i.orderId desc,i.publishTime desc,i.informationId desc")
    public abstract List<Information> queryInformationList(String text,List<Integer> channelIds,List<Integer> catalogChannelIds) throws Exception;

    @OQL("select i from Information i where i.valid=1 and (i.validTime is null or i.validTime>sysdate()) and contains(i.text,?1) and i.channelId in ?2 " +
            " order by i.topmost desc,i.orderId desc,i.publishTime desc,i.informationId desc")
    public abstract List<Information> queryInformationList(String text,List<Integer> catalogChannelIds) throws Exception;
}
