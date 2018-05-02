package com.gzzm.portal.link;

import com.gzzm.portal.commons.ListItem;

/**
 * @author camel
 * @date 2011-7-16
 */
public class LinkInfo implements ListItem
{
    private Link link;

    public LinkInfo(Link link)
    {
        this.link = link;
    }

    public Integer getLinkId()
    {
        return link.getLinkId();
    }

    public String getLinkName()
    {
        return link.getLinkName();
    }

    public String getTitle()
    {
        return link.getLinkName();
    }

    public String getUrl()
    {
        return link.getUrl();
    }

    public String getTarget()
    {
        //友情链接固定打开新页面
        return "_blank";
    }

    public String getPhoto()
    {
        return "/portal/link/link/" + link.getLinkId() + "/photo";
    }

    @Override
    public String toString()
    {
        return getTitle();
    }
}
