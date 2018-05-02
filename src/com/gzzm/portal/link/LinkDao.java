package com.gzzm.portal.link;

import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * @author camel
 * @date 2011-5-30
 */
public abstract class LinkDao extends GeneralDao
{
    public LinkDao()
    {
    }

    /**
     * 获得友情链接类别的列表
     *
     * @param stationId 站点ID，只查此站点的友情链接列表
     * @return 友情链接类别列表
     * @throws Exception 数据库查询错误
     */
    @OQL("select distinct type from Link where stationId=?1 and notempty(type) order by type")
    public abstract List<String> getLinkTypes(Integer stationId) throws Exception;   
}
