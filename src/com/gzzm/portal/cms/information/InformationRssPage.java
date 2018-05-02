package com.gzzm.portal.cms.information;

import com.gzzm.platform.commons.crud.BaseOQLQueryCrud;
import net.cyan.arachne.RequestContext;
import net.cyan.arachne.annotation.Service;
import net.cyan.arachne.exts.rss.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * rss列表，由子类通过OQL定义一个信息列表，根据这个信息列表生成rss
 *
 * @author camel
 * @date 14-2-24
 */
@Service
public abstract class InformationRssPage extends BaseOQLQueryCrud<Information>
{
    @Inject
    protected InformationDao dao;

    public InformationRssPage()
    {
        setPageSize(20);
    }

    protected String getServer()
    {
        return RequestContext.getContext().getServer();
    }

    @SuppressWarnings("UnusedDeclaration")
    protected void initRss(Rss rss) throws Exception
    {
    }

    protected RssItem toRssItem(Information information) throws Exception
    {
        RssItem item = new RssItem();

        item.setTitle(information.getTitle());
        item.setCategory(information.getChannel().getChannelName());
        item.setAuthor(information.getSource());
        item.setLink(getServer() + InformationInfo.getUrl(information));
        item.setPubDate(information.getUpdateTime());

        String description = information.getSummary();
        if (StringUtils.isEmpty(description))
        {
            description = "";
            switch (information.getType())
            {
                case information:
                    for (InformationContent content : information.getContents())
                    {
                        if (content.getContent() != null)
                        {
                            String s = new String(content.getContent());

                            description += s;
                            break;
                        }
                    }
                    break;

                case url:

                    description = information.getLinkUrl();
                    break;

                case file:

                    String s = information.getContentText();
                    if (s.length() > 1000)
                        s = s.substring(0, 1000);

                    description = s;

                    break;

                case images:
                    break;
            }
        }

        if (information.getPhoto() != null)
        {
            description = "<img src=\"" + getServer() + InformationInfo.getPhoto(information) + "\"/>" + description;
        }

        item.setDescription(description);


        return item;
    }

    @Service
    public Rss showRss() throws Exception
    {
        List<Information> informations = getList();

        Rss rss = new Rss();

        initRss(rss);

        for (Information information : informations)
        {
            rss.addItem(toRssItem(information));
        }

        return rss;
    }
}
