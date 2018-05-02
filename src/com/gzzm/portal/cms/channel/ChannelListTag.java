package com.gzzm.portal.cms.channel;

import com.gzzm.platform.appauth.AppAuthService;
import com.gzzm.portal.annotation.Tag;
import com.gzzm.portal.cms.commons.PublishPeriod;
import com.gzzm.portal.tag.PortalTag;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 栏目列表标签
 *
 * @author camel
 * @date 2011-6-15
 */
@Tag(name = "channel")
public class ChannelListTag implements PortalTag
{
    @Inject
    private Provider<AppAuthService> appAuthServiceProvider;

    @Inject
    private ChannelDao dao;

    private Integer channelId;

    /**
     * 过滤栏目的属性，当栏目的此属性为true时才显示，如果没有定义表示不过滤，显示所有栏目
     */
    private String property;

    /**
     * 是否包括子栏目的子栏目
     */
    private boolean descendant;

    /**
     * 是否只加载叶子节点，true表示只加载叶子节点，false表示只加载非叶子节点，空表示都加载
     */
    private Boolean leaf;

    /**
     * 栏目列表
     */
    private String channelCodes;

    /**
     * 只显示多少条记录，如果为0表示显示全部
     */
    private int size;

    private Integer channelDeptId;


    private PublishPeriod period;


    public ChannelListTag()
    {
    }

    public Integer getChannelId()
    {
        return channelId;
    }

    public void setChannelId(Integer channelId)
    {
        this.channelId = channelId;
    }

    public String getProperty()
    {
        return property;
    }

    public void setProperty(String property)
    {
        this.property = property;
    }

    public boolean isDescendant()
    {
        return descendant;
    }

    public void setDescendant(boolean descendant)
    {
        this.descendant = descendant;
    }

    public Boolean getLeaf()
    {
        return leaf;
    }

    public void setLeaf(Boolean leaf)
    {
        this.leaf = leaf;
    }

    public String getChannelCodes()
    {
        return channelCodes;
    }

    public void setChannelCodes(String channelCodes)
    {
        this.channelCodes = channelCodes;
    }

    public int getSize()
    {
        return size;
    }

    public void setSize(int size)
    {
        this.size = size;
    }

    public Integer getChannelDeptId()
    {
        return channelDeptId;
    }

    public void setChannelDeptId(Integer channelDeptId)
    {
        this.channelDeptId = channelDeptId;
    }

    public PublishPeriod getPeriod()
    {
        return period;
    }

    public void setPeriod(PublishPeriod period)
    {
        this.period = period;
    }

    public Object getValue(Map<String, Object> context) throws Exception
    {
        Collection<Integer> authChannelIds = null;
        if (channelDeptId != null && channelDeptId > 1)
        {
            authChannelIds =
                    appAuthServiceProvider.get().getAppIds(null, channelDeptId, Collections.singleton(channelDeptId),
                            ChannelAuthCrud.PORTAL_CHANNEL_EDIT);
        }

        if (channelCodes == null)
        {
            List<Channel> channels =
                    descendant ? dao.getDescendantChannels(channelId, period) : dao.getChildChannels(channelId, period);

            int size = this.size;
            if (size == 0)
                size = channels.size();
            else if (size > channels.size())
                size = channels.size();

            List<ChannelInfo> result = new ArrayList<ChannelInfo>(size);
            for (Channel channel : channels)
            {
                if (property == null || "true".equals(channel.getProperties().get(property)))
                {
                    if (leaf != null)
                    {
                        if (leaf && !channel.getChildChannels().isEmpty())
                            continue;

                        if (!leaf && channel.getChildChannels().isEmpty())
                            continue;
                    }

                    if (authChannelIds == null ||
                            authChannelIds.contains(channel.getChannelId()) ||
                            channel.containsChildChannel(authChannelIds))
                        result.add(new ChannelInfo(channel));

                    if (result.size() == size)
                        break;
                }
            }

            return result;
        }
        else
        {
            String[] channelCodes = this.channelCodes.split(",");

            List<ChannelInfo> result = new ArrayList<ChannelInfo>(channelCodes.length);

            for (String channelCode : channelCodes)
            {
                Channel channel = dao.getChannelByCode(channelCode);
                if (channel != null)
                {
                    if (authChannelIds == null  ||
                            authChannelIds.contains(channel.getChannelId()) ||
                            channel.containsChildChannel(authChannelIds))
                        result.add(new ChannelInfo(channel));
                }
            }

            return result;
        }
    }
}
