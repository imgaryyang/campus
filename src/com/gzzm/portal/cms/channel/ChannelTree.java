package com.gzzm.portal.cms.channel;

import com.gzzm.platform.annotation.CacheInstance;
import net.cyan.commons.collections.tree.TreeLoader;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 在内存中缓存栏目树
 *
 * @author camel
 * @date 12-12-2
 */
@CacheInstance("cms.channel")
public class ChannelTree
{
    @Inject
    private static Provider<ChannelDao> daoProvider;

    private ChannelCache root;

    private Map<Integer, ChannelCache> channels = new HashMap<Integer, ChannelCache>();

    private Map<String, ChannelCache> channelsByCode = new HashMap<String, ChannelCache>();

    private ChannelTree() throws Exception
    {
        load();
    }

    private void load() throws Exception
    {
        List<Channel> channels = daoProvider.get().getAllChannels();

        new TreeLoader<Channel, ChannelCache, Integer>()
        {
            protected Integer getKey(Channel channel)
            {
                return channel.getChannelId();
            }

            protected Integer getParentKey(Channel channel)
            {
                return channel.getParentChannelId();
            }

            protected ChannelCache create(Integer channelId)
            {
                return new ChannelCache(ChannelTree.this, channelId);
            }

            protected void copy(Channel channel, ChannelCache cache)
            {
                cache.setChannelName(channel.getChannelName());
                cache.setParentChannelId(channel.getParentChannelId());
                cache.setChannelCode(channel.getChannelCode());
                cache.setUrl(channel.getUrl());
                cache.setLinkUrl(channel.getLinkUrl());
                cache.setPeriod(channel.getPeriod());
                cache.setType(channel.getType());
                cache.setLinkChannelId(channel.getLinkChannelId());
                cache.setProperties(channel.getProperties());
                cache.setCataloged(channel.getCataloged());

                channelsByCode.put(cache.getChannelCode(), cache);
            }

            protected void addChild(ChannelCache parent, ChannelCache child)
            {
                parent.addChild(child);
            }

            protected void addRoot(ChannelCache root)
            {
                if (root.getChannelId() == 0)
                    ChannelTree.this.root = root;
            }
        }.load(channels, this.channels);
    }

    public ChannelCache getRoot()
    {
        return root;
    }

    public ChannelCache getChannel(Integer channelId)
    {
        return channels.get(channelId);
    }

    public ChannelCache getChannelByCode(String channelCode)
    {
        return channelsByCode.get(channelCode);
    }

    public ChannelDao getDao()
    {
        return daoProvider.get();
    }
}
