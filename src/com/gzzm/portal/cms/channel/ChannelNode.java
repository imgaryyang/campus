package com.gzzm.portal.cms.channel;

import com.gzzm.portal.cms.commons.PublishPeriod;

import java.util.*;

/**
 * 栏目节点，用于序列化成js的栏目信息
 *
 * @author camel
 * @date 2011-7-12
 */
public class ChannelNode
{
    private Integer channelId;

    private String channelCode;

    private String channelName;

    private ChannelType type;

    private String url;

    private String photo;

    private PublishPeriod period;

    private List<ChannelNode> children;

    public ChannelNode(Channel channel)
    {
        channelId = channel.getChannelId();
        channelCode = channel.getChannelCode();
        channelName = channel.getChannelName();
        type = channel.getType();
        url = ChannelInfo.getUrl(channel);
        photo = ChannelInfo.getPhoto(channel);
        period = channel.getPeriod();
    }

    public Integer getChannelId()
    {
        return channelId;
    }

    public void setChannelId(Integer channelId)
    {
        this.channelId = channelId;
    }

    public String getChannelCode()
    {
        return channelCode;
    }

    public void setChannelCode(String channelCode)
    {
        this.channelCode = channelCode;
    }

    public String getChannelName()
    {
        return channelName;
    }

    public void setChannelName(String channelName)
    {
        this.channelName = channelName;
    }

    public ChannelType getType()
    {
        return type;
    }

    public void setType(ChannelType type)
    {
        this.type = type;
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
        ChannelType type = getType();
        return type == ChannelType.information || type == ChannelType.link ? "_self" : "_blank";
    }

    public String getPhoto()
    {
        return photo;
    }

    public void setPhoto(String photo)
    {
        this.photo = photo;
    }

    public PublishPeriod getPeriod()
    {
        return period;
    }

    public void setPeriod(PublishPeriod period)
    {
        this.period = period;
    }

    public List<ChannelNode> getChildren()
    {
        return children;
    }

    public void setChildren(List<ChannelNode> children)
    {
        this.children = children;
    }

    public void addChild(ChannelNode node)
    {
        if (children == null)
            children = new ArrayList<ChannelNode>();

        children.add(node);
    }
}
