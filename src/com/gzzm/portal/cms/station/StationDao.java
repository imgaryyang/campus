package com.gzzm.portal.cms.station;

import com.gzzm.portal.cms.template.PageTemplate;
import net.cyan.thunwind.annotation.GetByField;
import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * 站点管理相关的dao
 *
 * @author camel
 * @date 2011-5-3
 */
public abstract class StationDao extends GeneralDao
{
    public StationDao()
    {
    }

    public Station getStation(Integer stationId) throws Exception
    {
        return load(Station.class, stationId);
    }

    public StationTemplate getStationTemplate(Integer templateId) throws Exception
    {
        return load(StationTemplate.class, templateId);
    }

    @GetByField("channelId")
    public abstract Station getStationByChannelId(Integer channelId) throws Exception;

    /**
     * 获得站点类别的列表
     *
     * @return 站点类别列表
     * @throws Exception 数据库查询错误
     */
    @OQL("select distinct type from Station where notempty(type) order by type")
    public abstract List<String> getStationTypes() throws Exception;

    /**
     * 获得站点列表
     *
     * @return 站点列表
     * @throws Exception 数据库查询错误
     */
    @OQL("select s from Station s order by s.stationName")
    public abstract List<Station> getAllStations() throws Exception;

    /**
     * 获得网站模版列表
     *
     * @return 网站模版列表
     * @throws Exception 数据库查询错误
     */
    @OQL("select t from StationTemplate t order by t.orderId")
    public abstract List<StationTemplate> getAllStationTemplates() throws Exception;

    /**
     * 获得某个站点的主页
     *
     * @param stationId 站点ID
     * @return 主页ID
     * @throws Exception 数据库查询错误
     */
    @OQL("select p from PageTemplate p where p.stationId=:1 and p.type=2")
    public abstract PageTemplate getMainPage(Integer stationId) throws Exception;

    /**
     * 获得某个类型的站点的列表
     *
     * @param type 站点类型
     * @return 站点列表
     * @throws Exception 数据查询错误
     */
    @OQL("select s from Station s where type=:1 order by orderId")
    public abstract List<Station> getStations(String type) throws Exception;
}
