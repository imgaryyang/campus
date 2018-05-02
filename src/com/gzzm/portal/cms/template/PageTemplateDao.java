package com.gzzm.portal.cms.template;

import com.gzzm.portal.cms.station.Station;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * 和页面模版相关的dao
 *
 * @author camel
 * @date 2011-5-10
 */
public abstract class PageTemplateDao extends GeneralDao
{
    public PageTemplateDao()
    {
    }

    public PageTemplate getPageTemplate(Integer templateId) throws Exception
    {
        return load(PageTemplate.class, templateId);
    }

    @GetByField("path")
    public abstract PageTemplate getPageTemplateByPath(String path);

    public Station getStation(Integer stationId) throws Exception
    {
        return load(Station.class, stationId);
    }

    /**
     * 获得和某个站点相关的模版
     *
     * @param stationId 站点ID
     * @param type      模版类型
     * @return 和此站点相关的模版列表，
     * @throws Exception 数据库查询错误
     */
    @OQL("select p from PageTemplate p where (stationId=?1 or stationId is null) and type=:2 order by p.templateName")
    public abstract List<PageTemplate> getPageTemplates(Integer stationId, PageTemplateType type) throws Exception;


    /**
     * 查询所有需要url映射的页面
     *
     * @return 页面列表
     * @throws Exception 数据库查询错误
     */
    @OQL("select p from PageTemplate p where p.type>1 and notempty(url)")
    public abstract List<PageTemplate> getPageTemplatesForUrlMap() throws Exception;
}
