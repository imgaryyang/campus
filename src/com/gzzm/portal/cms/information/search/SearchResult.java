package com.gzzm.portal.cms.information.search;

import com.gzzm.portal.cms.channel.*;
import com.gzzm.portal.cms.information.*;

/**
 * @author sjy
 * @date 2018/3/12
 */
public class SearchResult extends Information
{
    private ChannelCatalog channelCatalog;

    private Information information;

    public Information getInformation()
    {
        return information;
    }

    public void setInformation(Information information)
    {
        this.information = information;
    }

    public ChannelCatalog getChannelCatalog()
    {
        return channelCatalog;
    }

    public void setChannelCatalog(ChannelCatalog channelCatalog)
    {
        this.channelCatalog = channelCatalog;
    }
}
