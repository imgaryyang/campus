package com.gzzm.portal.cms.information.search;

import com.gzzm.portal.cms.channel.*;
import com.gzzm.portal.cms.information.*;

/**
 * @author sjy
 * @date 2018/3/13
 */
public class SearchInformationInfo extends InformationInfo
{
    private ChannelCatalog channelCatalog;

    public SearchInformationInfo(InformationBase<?, ?> information, ChannelData channelObject)
    {
        super(information, channelObject);
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
