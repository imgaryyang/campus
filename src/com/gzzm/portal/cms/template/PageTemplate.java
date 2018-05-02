package com.gzzm.portal.cms.template;

import com.gzzm.platform.commons.UpdateTimeService;
import com.gzzm.platform.organ.User;
import com.gzzm.portal.cms.commons.PageCache;
import com.gzzm.portal.cms.station.*;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.Provider;
import net.cyan.commons.validate.annotation.*;
import net.cyan.crud.annotation.Unique;
import net.cyan.nest.annotation.Inject;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 内容管理模版维护
 *
 * @author camel
 * @date 2011-5-3
 */
@Entity(table = "PLPAGETEMPLATE", keys = "templateId")
public class PageTemplate
{
    @Inject
    private static Provider<StationDao> stationDaoProvider;

    /**
     * ，模板ID
     */
    @Generatable(length = 6)
    private Integer templateId;

    /**
     * 模板名称
     */
    @Require
    @ColumnDescription(type = "varchar(250)")
    private String templateName;

    /**
     * 模板路径
     */
    @Require
    @ColumnDescription(type = "varchar(250)")
    private String path;

    /**
     * 访问此页面的url
     */
    @Unique(with = "stationId")
    @ColumnDescription(type = "varchar(250)")
    private String url;

    /**
     * 模板创建人的ID
     */
    private Integer creator;

    /**
     * 关联模板创建人的User对象
     */
    @NotSerialized
    private User createUser;

    /**
     * 所属站点的ID，可以不属于任何站点
     */
    @Index
    private Integer stationId;

    /**
     * 关联站点
     */
    private Station station;

    /**
     * 页面模版类型
     */
    @Require
    @ColumnDescription(type = "number(3)")
    private PageTemplateType type;

    public PageTemplate()
    {
    }

    public Integer getTemplateId()
    {
        return templateId;
    }

    public void setTemplateId(Integer templateId)
    {
        this.templateId = templateId;
    }

    public String getTemplateName()
    {
        return templateName;
    }

    public void setTemplateName(String templateName)
    {
        this.templateName = templateName;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public Integer getCreator()
    {
        return creator;
    }

    public void setCreator(Integer creator)
    {
        this.creator = creator;
    }

    public User getCreateUser()
    {
        return createUser;
    }

    public void setCreateUser(User createUser)
    {
        this.createUser = createUser;
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

    public PageTemplateType getType()
    {
        return type;
    }

    public void setType(PageTemplateType type)
    {
        this.type = type;
    }

    @FieldValidator("path")
    @Warning("portal.cms.pagepath_error")
    public Station checkForPath() throws Exception
    {
        if (stationId != null && stationId > 0 && getType() != null && getType().getValue() == 3)
        {
            Station station = stationDaoProvider.get().getStation(stationId);
            String stationPath = station.getPath();
            if (!stationPath.endsWith("/"))
                stationPath += "/";

            if (!path.startsWith(stationPath))
                return station;
        }

        return null;
    }


    @Override
    public String toString()
    {
        return templateName;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof PageTemplate))
            return false;

        PageTemplate that = (PageTemplate) o;

        return templateId.equals(that.templateId);
    }

    @Override
    public int hashCode()
    {
        return templateId.hashCode();
    }

    public static void setUpdated() throws Exception
    {
        //数据被修改之后更新页面缓存
        UpdateTimeService.updateLastTime("cms.page", new Date());

        //更新页面缓存
        PageCache.updateCache();
    }
}
