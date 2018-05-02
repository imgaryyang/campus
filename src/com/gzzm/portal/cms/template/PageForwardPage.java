package com.gzzm.portal.cms.template;

import com.gzzm.portal.cms.channel.Channel;
import com.gzzm.portal.cms.commons.CmsPage;
import com.gzzm.portal.cms.station.Station;
import net.cyan.arachne.annotation.Service;
import net.cyan.nest.annotation.Inject;

/**
 * 转向页面的service
 *
 * @author camel
 * @date 2011-5-31
 */
@Service(url = "/web/page")
public class PageForwardPage extends CmsPage
{
    @Inject
    private PageTemplateDao dao;

    /**
     * 页面模版ID
     */
    private Integer templateId;

    /**
     * 模版对象，临时变量，用于取得其他数据
     */
    private PageTemplate template;

    private Integer stationId;

    public PageForwardPage()
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

    public Integer getTemplateId()
    {
        return templateId;
    }

    public void setTemplateId(Integer templateId)
    {
        this.templateId = templateId;
    }

    @Override
    protected Station getStationObject() throws Exception
    {
        if (stationId != null)
            return dao.getStation(stationId);

        return getPageTemplate().getStation();
    }

    @Override
    protected Channel getChannelObject() throws Exception
    {
        return getPageTemplate().getStation().getChannel();
    }

    @Override
    public PageTemplate getPageTemplate() throws Exception
    {
        if (template == null)
            template = dao.getPageTemplate(templateId);

        return template;
    }

    @Override
    protected int getVisitType() throws Exception
    {
        return getPageTemplate().getType() == PageTemplateType.MAIN ? STATION : PAGE;
    }

    @Override
    protected Integer getVisitId() throws Exception
    {
        PageTemplate template = getPageTemplate();

        return getPageTemplate().getType() == PageTemplateType.MAIN ? template.getStationId() :
                template.getTemplateId();
    }
}