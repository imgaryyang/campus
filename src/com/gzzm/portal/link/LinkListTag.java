package com.gzzm.portal.link;

import com.gzzm.portal.annotation.Tag;
import com.gzzm.portal.tag.OQLTag;

import java.util.Map;

/**
 * 友情链接列表标签，显示某个组的友情链接
 *
 * @author camel
 * @date 2011-6-16
 */
@Tag(name = "link", singleton = true)
public class LinkListTag extends OQLTag<Link>
{
    public LinkListTag()
    {
    }

    @Override
    protected int getPageSize(Map<String, Object> context) throws Exception
    {
        //不支持分页
        return -1;
    }

    @Override
    protected String getQueryString(Map<String, Object> parameters) throws Exception
    {
        return "select l from Link l where stationId=:stationId and type=:type order by orderId";
    }

    @Override
    protected Object transform(Link link) throws Exception
    {
        return new LinkInfo(link);
    }
}