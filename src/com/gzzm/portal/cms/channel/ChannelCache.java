package com.gzzm.portal.cms.channel;

import com.gzzm.platform.commons.Tools;
import com.gzzm.portal.cms.commons.PublishPeriod;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.*;

import java.util.*;

/**
 * @author camel
 * @date 12-12-2
 */
public class ChannelCache implements ChannelData<ChannelCache>
{
    @NotSerialized
    private ChannelTree tree;

    private Integer channelId;

    private String channelName;

    private Integer parentChannelId;

    private String channelCode;

    private ChannelType type;

    private PublishPeriod period;

    private String url;

    private String linkUrl;

    private Integer linkChannelId;

    private Boolean cataloged;

    @NotSerialized
    private String keywords;

    @NotSerialized
    private String remark;

    private Map<String, String> properties;

    @NotSerialized
    private List<ChannelCache> children;

    /**
     * 让子栏目列表不允许修改
     */
    private List<ChannelCache> unmodifiableChildren;

    @NotSerialized
    private List<Integer> linkChannelIds;

    public ChannelCache(ChannelTree tree, Integer channelId)
    {
        this.tree = tree;
        this.channelId = channelId;
    }

    @Override
    public String getKeywords()
    {
        return keywords;
    }

    public void setKeywords(String keywords)
    {
        this.keywords = keywords;
    }

    @Override
    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public Boolean getCataloged()
    {
        return cataloged;
    }

    public void setCataloged(Boolean cataloged)
    {
        this.cataloged = cataloged;
    }

    public ChannelTree getTree()
    {
        return tree;
    }

    public Integer getChannelId()
    {
        return channelId;
    }

    public String getChannelName()
    {
        return channelName;
    }

    void setChannelName(String channelName)
    {
        this.channelName = channelName;
    }

    public Integer getParentChannelId()
    {
        return parentChannelId;
    }

    void setParentChannelId(Integer parentChannelId)
    {
        this.parentChannelId = parentChannelId;
    }

    public String getChannelCode()
    {
        return channelCode;
    }

    void setChannelCode(String channelCode)
    {
        this.channelCode = channelCode;
    }

    public ChannelType getType()
    {
        return type;
    }

    void setType(ChannelType type)
    {
        this.type = type;
    }

    @Override
    public String getUrl()
    {
        return url;
    }

    void setUrl(String url)
    {
        this.url = url;
    }

    @Override
    public String getLinkUrl()
    {
        return linkUrl;
    }

    void setLinkUrl(String linkUrl)
    {
        this.linkUrl = linkUrl;
    }

    public PublishPeriod getPeriod()
    {
        return period;
    }

    void setPeriod(PublishPeriod period)
    {
        this.period = period;
    }

    @Override
    @NotSerialized
    public Byte getDeleteTag()
    {
        return 0;
    }

    public Integer getLinkChannelId()
    {
        return linkChannelId;
    }

    void setLinkChannelId(Integer linkChannelId)
    {
        this.linkChannelId = linkChannelId;
    }

    public String getProperty(String name)
    {
        return properties == null ? null : properties.get(name);
    }

    void setProperties(Map<String, String> properties)
    {
        this.properties = Collections.unmodifiableMap(new HashMap<String, String>(properties));
    }

    @Override
    public Map<String, String> getProperties()
    {
        return properties;
    }

    void setLinkChannelIds(List<Integer> linkChannelIds)
    {
        this.linkChannelIds = linkChannelIds;
    }

    private synchronized List<Integer> getLinkChannelIds()
    {
        if (linkChannelIds == null)
        {
            try
            {
                List<Channel> linkChannels = tree.getDao().getChannel(channelId).getLinkChannels();

                linkChannelIds = new ArrayList<Integer>();
                for (Channel linkChannel : linkChannels)
                {
                    if (linkChannel.getDeleteTag() == null || linkChannel.getDeleteTag() == 0)
                        linkChannelIds.add(linkChannel.getChannelId());
                }
            }
            catch (Exception ex)
            {
                Tools.wrapException(ex);
            }
        }
        return linkChannelIds;
    }

    @Override
    public List<ChannelCache> getLinkChannels()
    {
        List<Integer> linkChannelIds = getLinkChannelIds();
        if (linkChannelIds != null && linkChannelIds.size() > 0)
        {
            List<ChannelCache> linkChannels = new ArrayList<ChannelCache>(linkChannelIds.size());
            for (Integer linkChannlId : linkChannelIds)
            {
                ChannelCache channel = tree.getChannel(linkChannlId);
                if (channel != null)
                    linkChannels.add(channel);
            }

            return linkChannels;
        }
        else
        {
            return Collections.emptyList();
        }
    }

    @SuppressWarnings("unchecked")
    public List<ChannelCache> getChildren()
    {
        return unmodifiableChildren == null ?
                (List<ChannelCache>) (children == null ? Collections.emptyList() : children) :
                unmodifiableChildren;
    }

    void addChild(ChannelCache child)
    {
        if (children == null)
        {
            children = new ArrayList<ChannelCache>();
            unmodifiableChildren = Collections.unmodifiableList(children);
        }

        children.add(child);
    }

    @NotSerialized
    public ChannelCache getParentChannel()
    {
        return parentChannelId == null ? null : tree.getChannel(parentChannelId);
    }

    public boolean containsChildChannel(Filter<ChannelCache> filter) throws Exception
    {
        if (children != null)
        {
            for (ChannelCache channel : children)
            {
                if (filter == null || filter.accept(channel) || channel.containsChildChannel(filter))
                    return true;
            }
        }

        return false;
    }

    public boolean containsChildChannel(Integer channelId) throws Exception
    {
        if (children != null)
        {
            for (ChannelCache channel : children)
            {
                if (channel.getChannelId().equals(channelId) || channel.containsChildChannel(channelId))
                    return true;
            }
        }

        return false;
    }

    public List<Integer> getAllChildrenChannelIds() throws Exception
    {
        List<Integer> channelIds = new ArrayList<Integer>();

        getAllChildrenChannelIds(channelIds);

        return channelIds;
    }

    private void getAllChildrenChannelIds(List<Integer> channelIds) throws Exception
    {
        channelIds.add(channelId);

        if (children != null)
        {
            for (ChannelCache channel : children)
            {
                channel.getAllChildrenChannelIds(channelIds);
            }
        }
    }

    public List<ChannelCache> search(String text, Collection<Integer> channelIds, int size)
    {
        List<ChannelCache> channels = new ArrayList<ChannelCache>();

        search(text, channels, channelIds, size);

        return channels;
    }

    public void search(String text, List<ChannelCache> channels, Collection<Integer> channelIds, int size)
    {
        if ((channelIds == null || channelIds.contains(channelId)) &&
                (Tools.matchText(getChannelName(),text) || text.equals(getChannelCode())))
        {
            channels.add(this);

            if (size > 0 && channels.size() >= size)
                return;
        }

        if (children != null)
        {
            for (ChannelCache channel : children)
            {
                channel.search(text, channels, channelIds, size);

                if (size > 0 && channels.size() >= size)
                    return;
            }
        }
    }

    @Override
    public byte[] getPhoto()
    {
        try
        {
            Channel channel = tree.getDao().getChannel(channelId);
            if (channel != null)
                return channel.getPhoto();
            else
                return null;
        }
        catch (Exception ex)
        {
            Tools.wrapException(ex);
            return null;
        }
    }

    @Override
    @NotSerialized
    public List<InfoProperty> getAllInfoProperties() throws Exception
    {
        Channel channel = tree.getDao().getChannel(channelId);
        if (channel != null)
            return channel.getAllInfoProperties();
        else
            return Collections.emptyList();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof ChannelCache))
            return false;

        ChannelCache that = (ChannelCache) o;

        return channelId.equals(that.channelId);
    }

    @Override
    public int hashCode()
    {
        return channelId.hashCode();
    }

    @Override
    public String toString()
    {
        return channelName;
    }
}
