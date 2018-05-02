package com.gzzm.portal.cms.commons;

import com.gzzm.platform.visit.VisitService;
import com.gzzm.portal.annotation.Tag;
import com.gzzm.portal.commons.PortalUtils;
import com.gzzm.portal.tag.PortalTag;
import net.cyan.arachne.RequestContext;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.Map;

/**
 * 访问次数
 *
 * @author camel
 * @date 2011-6-22
 */
@Tag(name = "visit")
public class VisitTag implements PortalTag
{
    @Inject
    private static Provider<VisitService> visitServiceProvider;

    private String name;

    private Integer type;

    public VisitTag()
    {
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Integer getType()
    {
        return type;
    }

    public void setType(Integer type)
    {
        this.type = type;
    }

    public Object getValue(Map<String, Object> context) throws Exception
    {
        VisitService service = visitServiceProvider.get();

        int visitType = -1;
        Integer visitId = null;

        if (!StringUtils.isEmpty(name))
        {
            visitType = 799;
            visitId = service.getVisit(name).getVisitId();
            service.visit(visitType, visitId, null, PortalUtils.getIp(RequestContext.getContext().getRequest()));
        }
        else
        {
            CmsPage page = CmsPage.getPage();

            if (type != null)
            {
                visitType = type;

                if (visitType == CmsPage.STATION)
                {
                    visitId = page.getStation().getId();
                }
                else if (visitType == CmsPage.CHANNEL)
                {
                    visitId = page.getChannel().getChannelId();
                }
            }
            else if (page != null)
            {
                visitType = page.getVisitType();
                visitId = page.getVisitId();
            }
        }

        if (visitType >= 0)
        {
            return service.getVisitCount(visitType, visitId);
        }

        return null;
    }
}
