package com.gzzm.portal.commons;

/**
 * @author camel
 * @date 2011-7-14
 */
public class SimpleListItem implements ListItem
{
    private String title;

    private String url;

    private String target;

    private String photo;

    public SimpleListItem()
    {
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getTarget()
    {
        return target;
    }

    public void setTarget(String target)
    {
        this.target = target;
    }

    public String getPhoto()
    {
        return photo;
    }

    public void setPhoto(String photo)
    {
        this.photo = photo;
    }
}
