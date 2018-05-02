package com.gzzm.portal.cms.station;

import com.gzzm.portal.cms.channel.ChannelInfo;
import com.gzzm.portal.cms.commons.CmsPage;
import com.gzzm.portal.commons.ListItem;
import net.cyan.commons.util.*;

/**
 * 站点信息，用于在页面上显示
 *
 * @author camel
 * @date 2011-6-9
 */
public class StationInfo implements ListItem
{
    /**
     * 站点ID
     */
    private Integer id;

    /**
     * 站点名称
     */
    private String name;

    /**
     * 网站标题
     */
    private String title;

    /**
     * 网站所在的部门的ID
     */
    private Integer deptId;

    /**
     * 网站的域名
     */
    private String domain;

    /**
     * 网站域名，如果有多个的话
     */
    private String[] domains;

    /**
     * 网站的繁体域名
     */
    private String gb5Domain;

    /**
     * 网站的繁体域名，如果有多个
     */
    private String[] gb5Domains;

    /**
     * 网站路径
     */
    private String path;

    /**
     * 网站分类
     */
    private String type;

    /**
     * 网站的模版路径
     */
    private String templatePath;

    /**
     * 主栏目的ID
     */
    private Integer channelId;

    private ChannelInfo channel;

    public StationInfo(Station station)
    {
        id = station.getStationId();
        name = station.getStationName();
        title = station.getTitle();
        deptId = station.getDeptId();

        String domain = station.getDomainName();
        if (!StringUtils.isEmpty(domain))
        {
            domains = domain.split(",");
            this.domain = domains[0];
        }

        String gb5Domain = station.getGb5Domain();
        if (!StringUtils.isEmpty(gb5Domain))
        {
            gb5Domains = gb5Domain.split(",");
            this.gb5Domain = gb5Domains[0];
        }

        path = station.getPath();
        type = station.getType();
        if (station.getTemplate() != null)
            templatePath = station.getTemplate().getPath();
        channelId = station.getChannelId();
    }

    public Integer getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getTitle()
    {
        return title;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public String getDomain()
    {
        return domain;
    }

    public String getGb5Domain()
    {
        return gb5Domain;
    }

    public String[] getDomains()
    {
        return domains;
    }

    public String[] getGb5Domains()
    {
        return gb5Domains;
    }

    public String getPath()
    {
        return path;
    }

    public String getType()
    {
        return type;
    }

    public String getTemplatePath()
    {
        return templatePath;
    }

    public Integer getChannelId()
    {
        return channelId;
    }

    public ChannelInfo getChannel() throws Exception
    {
        if (channel == null)
            channel = ChannelInfo.get(channelId);

        return channel;
    }

    public String getIndexPath()
    {
        //首页路径
        String serverName = CmsPage.getServerName();

        //访问的是当前网站，返回根路径
        if (CollectionUtils.contains(domains, serverName) || CollectionUtils.contains(gb5Domains, serverName))
            return "/";

        if (!StringUtils.isEmpty(domain))
            return "http://" + domain;

        //没有定义域名，获得主页的路径
        return path;
    }

    public String getUrl()
    {
        return getIndexPath();
    }

    public String getTarget()
    {
        //固定用新窗口打开网站
        return "_blank";
    }

    public String getPhoto()
    {
        return null;
    }
}
