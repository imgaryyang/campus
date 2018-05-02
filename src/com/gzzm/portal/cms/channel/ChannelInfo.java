package com.gzzm.portal.cms.channel;

import com.gzzm.portal.cms.commons.*;
import com.gzzm.portal.commons.ListItem;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 栏目信息，用于在页面上显示
 *
 * @author camel
 * @date 2011-6-9
 */
public class ChannelInfo implements ListItem
{
    @Inject
    private static Provider<ChannelDao> channelDaoProvider;

    private ChannelData<?> channel;

    private String url;

    private List<ChannelInfo> channels;

    private Map<String, String> properties;

    public ChannelInfo(ChannelData channel)
    {
        this.channel = channel;
    }

    public static ChannelInfo get(Integer channelId) throws Exception
    {
        return new ChannelInfo(channelDaoProvider.get().getChannel(channelId));
    }

    public static Integer getChannelId(String channelCode) throws Exception
    {
        return channelDaoProvider.get().getChannelIdByCode(channelCode);
    }

    public Integer getChannelId()
    {
        return channel.getChannelId();
    }

    public String getChannelCode()
    {
        return channel.getChannelCode();
    }

    public String getChannelName()
    {
        return channel.getChannelName();
    }

    public String getKeywords()
    {
        return channel.getKeywords();
    }

    public String getRemark()
    {
        return channel.getRemark();
    }

    public Boolean getCataloged()
    {
        return channel.getCataloged();
    }

    @NotSerialized
    public Map<String, String> getProperties()
    {
        if (properties == null)
        {
            properties = new Map<String, String>()
            {
                public int size()
                {
                    throw new UnsupportedOperationException("Method not implemented.");
                }

                public boolean isEmpty()
                {
                    return false;
                }

                public boolean containsKey(Object key)
                {
                    return true;
                }

                public boolean containsValue(Object value)
                {
                    return channel.getProperties().containsValue(value);
                }

                public String get(Object key)
                {
                    return channel.getProperties().get(key);
                }

                public String put(String key, String value)
                {
                    throw new UnsupportedOperationException("Method not implemented.");
                }

                public String remove(Object key)
                {
                    throw new UnsupportedOperationException("Method not implemented.");
                }

                public void putAll(Map<? extends String, ? extends String> m)
                {
                    throw new UnsupportedOperationException("Method not implemented.");
                }

                public void clear()
                {
                    throw new UnsupportedOperationException("Method not implemented.");
                }

                public Set<String> keySet()
                {
                    throw new UnsupportedOperationException("Method not implemented.");
                }

                public Collection<String> values()
                {
                    throw new UnsupportedOperationException("Method not implemented.");
                }

                public Set<Entry<String, String>> entrySet()
                {
                    throw new UnsupportedOperationException("Method not implemented.");
                }
            };
        }

        return properties;
    }

    @NotSerialized
    public List<InfoProperty> getInfoProperties() throws Exception
    {
        return channel.getAllInfoProperties();
    }

    public String getType()
    {
        return channel.getType().name();
    }

    public Integer getLinkChannelId()
    {
        return channel.getLinkChannelId();
    }

    public String getUrl()
    {
        if (url != null)
            return url;

        return getUrl(channel);
    }

    public static String getUrl(ChannelData<?> channel)
    {
        String url = channel.getUrl();

        if (StringUtils.isEmpty(url))
        {
            if (channel.getType() == ChannelType.url)
                url = channel.getLinkUrl();
            else
                url = "/channel/" + channel.getChannelCode();
        }

        return url;
    }

    /**
     * 栏目图片的url
     *
     * @return 栏目图片的url
     */
    public String getPhoto()
    {
        return getPhoto(channel);
    }

    public static String getPhoto(ChannelData channel)
    {
        if (channel.getPhoto() == null)
            return null;

        return "/channel/" + channel.getChannelCode() + "/photo";
    }

    /**
     * 父栏目信息
     *
     * @return 父栏目信息，如果没有父栏目，返回null
     */
    @NotSerialized
    public ChannelInfo getParent()
    {
        ChannelData parentChannel = channel.getParentChannel();
        if (parentChannel != null)
            return new ChannelInfo(parentChannel);

        return null;
    }

    /**
     * 得到从网站根栏目到到当前栏目的栏目路径
     *
     * @return 栏目路径
     * @throws Exception 读取网站根栏目id错误
     */
    @NotSerialized
    public List<ChannelInfo> getChannels() throws Exception
    {
        if (channels == null)
        {
            Integer stationChannelId = CmsPage.getPage().getStationChannelId();

            channels = new ArrayList<ChannelInfo>();

            ChannelData channel = this.channel;
            while (channel.getChannelId() != 0)
            {
                ChannelInfo channelInfo = new ChannelInfo(channel);

                channels.add(0, channelInfo);

                if (stationChannelId != null && channel.getChannelId().equals(stationChannelId))
                {
                    //设置根栏目的url为网站的url
                    channelInfo.url = CmsPage.getPage().getStation().getIndexPath();
                    break;
                }

                channel = channel.getParentChannel();
            }
        }

        return channels;
    }

    @NotSerialized
    public List<ChannelInfo> getChildren()
    {
        List<ChannelInfo> channels = new ArrayList<ChannelInfo>();

        for (ChannelData channel : this.channel.getChildren())
        {
            Byte deleteTag = null;
            if (channel instanceof Channel)
                deleteTag = ((Channel) channel).getDeleteTag();
            if (deleteTag == null || deleteTag == 0)
                channels.add(new ChannelInfo(channel));
        }

        return channels;
    }

    public String getTitle()
    {
        return getChannelName();
    }

    public String getTarget()
    {
        ChannelType type = channel.getType();
        return type == ChannelType.information || type == ChannelType.link ? "_self" : "_blank";
    }

    public PublishPeriod getPeriod()
    {
        return channel.getPeriod();
    }
}
