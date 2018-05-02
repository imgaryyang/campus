package com.gzzm.portal.cms.station;

import com.gzzm.portal.cms.channel.Channel;
import com.gzzm.portal.cms.commons.CmsPage;
import com.gzzm.portal.cms.template.PageTemplate;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.StringUtils;
import net.cyan.nest.annotation.Inject;

/**
 * 访问某个站点的入口，转向站点的主页
 *
 * @author camel
 * @date 2011-6-9
 */
@Service(url = "/web/otherpage")
public class OtherPage extends CmsPage
{
    @Inject
    private StationDao dao;

    private Integer stationId;


    /**
     * 站点对象，临时变量，用于获取其他信息
     */
    private Station station;

    /**
     * 站点主页对应的模版对象，临时变量，避免主页对象重复加载
     */
    private PageTemplate template;

    public OtherPage()
    {
    }

    public Integer getStationId()
    {
        return stationId;
    }

    public void setStationId(Integer stationId)
    {
        this.stationId = stationId;
    }

    @Override
    protected Station getStationObject() throws Exception
    {
        if (station == null)
            station = dao.getStation(stationId);

        return station;
    }


    @Override
    protected Channel getChannelObject() throws Exception
    {
        return getStationObject().getChannel();
    }

    @Override
    protected PageTemplate getPageTemplate() throws Exception
    {
        if (template == null)
            template = dao.getMainPage(stationId);

        return template;
    }

    @Service(url = "/web/otherpages/{$0}")
    public String goToPage(String pageName) throws Exception
    {
        Station station = getStationObject();
        String path = station.getTemplate().getPath() + "/otherpage/";

        if (!StringUtils.isEmpty(pageName))
        {
            return path + pageName + (pageName.indexOf(".ptl") != -1 ? "" : ".ptl");
        }

        return getForward();
    }


    @Override
    protected String getForward() throws Exception
    {


        //如果没有定义主页，默认主页为网站路径下的index.html
        PageTemplate template = getPageTemplate();
        Station station = getStationObject();
        if (template != null) return template.getPath();
        if (station.getTemplate() != null && StringUtils.isNotBlank(station.getTemplate().getHomePath()))
        {
            return station.getTemplate().getHomePath();
        }
        else
        {
            return getStationObject().getPath() + "/index.html";
        }
    }

    @Override
    protected int getVisitType()
    {
        return STATION;
    }

    @Override
    protected Integer getVisitId()
    {
        return stationId;
    }
}
