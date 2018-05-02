package com.gzzm.portal.cms.channel;

import net.cyan.arachne.annotation.NotSerialized;

import java.util.List;

/**
 * @author camel
 * @date 12-12-25
 */
public class ChannelItem
{
    private Integer channelId;

    private Integer parentNodeId;

    private String channelName;

    private boolean leaf;

    private boolean real = true;

    private ChannelType type;

    private boolean cataloged;

    @NotSerialized
    private List<ChannelItem> children;

    public static ChannelItem getRoot()
    {
        ChannelItem root = new ChannelItem();

        root.setChannelId(0);
        root.setChannelName("根节点");
        root.setLeaf(false);
        root.setReal(true);
        root.setType(ChannelType.information);
        root.setCataloged(false);
        return root;
    }

    public ChannelItem(ChannelCache channel)
    {
        this.channelId = channel.getChannelId();
        this.parentNodeId = channel.getParentChannelId();
        this.channelName = channel.getChannelName();
        this.type = channel.getType();
        this.cataloged = channel.getCataloged() != null && channel.getCataloged();
    }

    public ChannelItem()
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

    public Integer getParentNodeId()
    {
        return parentNodeId;
    }

    public void setParentNodeId(Integer parentNodeId)
    {
        this.parentNodeId = parentNodeId;
    }

    public String getChannelName()
    {
        return channelName;
    }

    public void setChannelName(String channelName)
    {
        this.channelName = channelName;
    }

    public boolean isLeaf()
    {
        return leaf;
    }

    public void setLeaf(boolean leaf)
    {
        this.leaf = leaf;
    }

    public boolean isReal()
    {
        return real;
    }

    public void setReal(boolean real)
    {
        this.real = real;
    }

    public ChannelType getType()
    {
        return type;
    }

    public void setType(ChannelType type)
    {
        this.type = type;
    }

    public List<ChannelItem> getChildren()
    {
        return children;
    }

    public void setChildren(List<ChannelItem> children)
    {
        this.children = children;
    }

    public boolean isCataloged()
    {
        return cataloged;
    }

    public void setCataloged(boolean cataloged)
    {
        this.cataloged = cataloged;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof ChannelItem))
            return false;

        ChannelItem that = (ChannelItem) o;

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
