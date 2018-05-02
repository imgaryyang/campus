package com.gzzm.portal.cms.commons;

import com.gzzm.portal.cms.channel.ChannelUrlMapper;
import com.gzzm.portal.cms.station.StationPathDomainMapper;
import com.gzzm.portal.cms.template.PageUrlMapper;
import net.cyan.arachne.urlrewrite.*;
import net.cyan.commons.util.*;
import net.cyan.commons.util.io.WebUtils;
import net.cyan.nest.annotation.Inject;

import javax.servlet.http.*;

/**
 * cmsurl重写器，根据url转向页面，栏目，站点
 *
 * @author camel
 * @date 2011-6-9
 */
public class CmsUrlRewriter implements UrlRewriter
{
    @Inject
    private static Provider<PageUrlMapper> pageUrlMapperProvider;

    @Inject
    private static Provider<ChannelUrlMapper> channelUrlMapperProvider;

    @Inject
    private static Provider<StationPathDomainMapper> stationPathDomainMapperProvider;

    @Inject
    private static Provider<CmsConfig> configProvider;

    public CmsUrlRewriter()
    {
    }

    public UrlRewriterResult rewrite(HttpServletRequest request, HttpServletResponse response)
    {
        String method = request.getMethod();
        if (method == null)
            return null;

        if (!"GET".equalsIgnoreCase(method))
            return null;

        String url = getUrl(request);

        if (url != null)
        {
            if (StringUtils.isEmpty(request.getQueryString()) && configProvider.get().isCache())
            {
                return new CmsRewriterResult(url);
            }
            else
            {
                return new BaseRewriterResult(url);
            }
        }

        return null;
    }

    public static String getUrl(HttpServletRequest request)
    {
        String uri = WebUtils.getRequestURI(request);

        String domain = request.getHeader("Host");
        if (StringUtils.isEmpty(domain))
            domain = request.getServerName();

        StationPathDomainMapper stationPathDomainMapper = stationPathDomainMapperProvider.get();

        if (uri == null || "/".equals(uri) || "/index.jsp".equalsIgnoreCase(uri))
        {
            Integer stationId = stationPathDomainMapper.getStationIdByDomain(domain);

            //域名为某个站点的域名，直接转向此站点的主页
            return stationId == null ? null : "/web/station.page?stationId=" + stationId;
        }
        else
        {
            Integer stationId = stationPathDomainMapper.getStationIdByPath(uri);
            if (stationId != null)
                return "/web/station.page?stationId=" + stationId;

            //先带域名做url匹配
            ChannelUrlMapper channelUrlMapper = channelUrlMapperProvider.get();

            //路径为某个栏目的url，转向此栏目
            String url = domain + uri;
            Integer channelId = channelUrlMapper.getChannelId(url);
            if (channelId != null)
                return "/web/channel.page?channelId=" + channelId;

            PageUrlMapper pageUrlMapper = pageUrlMapperProvider.get();

            //路径为某个页面的url，转向此页面
            Integer templateId = pageUrlMapper.getTemplateId(url);
            if (templateId != null)
                return "/web/page.page?templateId=" + templateId;

            //匹配栏目和文章
            int index = url.lastIndexOf('/');
            String s = url.substring(0, index);
            channelId = channelUrlMapper.getChannelId(s);
            if (channelId != null)
            {
                String s1 = url.substring(index + 1);
                try
                {
                    int informationId = Integer.parseInt(s1);
                    return "/web/info.page?informationId=" + informationId + "&channelId=" + channelId;
                }
                catch (NumberFormatException ex)
                {
                    //url中最后一段不是整数，不是合法的文章id
                }
            }

            //不带域名做url匹配

            //路径为某个栏目的url，转向此栏目
            channelId = channelUrlMapper.getChannelId(uri);
            if (channelId != null)
                return "/web/channel.page?channelId=" + channelId;


            //路径为某个页面的url，转向此页面
            templateId = pageUrlMapper.getTemplateId(uri);
            if (templateId != null)
                return "/web/page.page?templateId=" + templateId;

            //匹配栏目和文章
            index = uri.lastIndexOf('/');
            s = uri.substring(0, index);
            channelId = channelUrlMapper.getChannelId(s);
            if (channelId != null)
            {
                String s1 = url.substring(index + 1);
                try
                {
                    int informationId = Integer.parseInt(s1);
                    return "/web/info.page?informationId=" + informationId + "&channelId=" + channelId;
                }
                catch (NumberFormatException ex)
                {
                    //url中最后一段不是整数，不是合法的文章id
                }
            }

        }

        return null;
    }
}
