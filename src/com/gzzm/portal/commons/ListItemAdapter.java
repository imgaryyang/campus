package com.gzzm.portal.commons;

import net.cyan.commons.util.CollectionUtils;

/**
 * 将一个对象封装成ListItem，并通过Map的方式能够保留远对象的属性
 *
 * @author camel
 * @date 12-9-1
 */
public class ListItemAdapter extends CollectionUtils.EvalMap implements ListItem
{
    private Object obj;

    private String title;

    private String url;

    private String target;

    private String photo;

    public ListItemAdapter(Object obj)
    {
        super(obj);
        this.obj = obj;
    }

    @Override
    public Object get(Object key)
    {
        if ("title".equals(key))
            return getTitle();
        else if ("url".equals(key))
            return getUrl();
        else if ("target".equals(key))
            return getTarget();
        else if ("photo".equals(key))
            return getPhoto();

        return super.get(key);
    }

    public String getTitle()
    {
        if (title == null)
            return obj.toString();

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
