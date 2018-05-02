package com.gzzm.portal.cms.channel;

import com.gzzm.portal.cms.commons.PublishPeriod;

import java.util.*;

/**
 * @author camel
 * @date 2017/7/20
 */
public interface ChannelData<T extends ChannelData>
{
    public Integer getChannelId();

    public String getChannelName();

    public String getChannelCode();

    public ChannelType getType();

    public Integer getLinkChannelId();

    public List<T> getChildren();

    public T getParentChannel();

    public String getUrl();

    public String getLinkUrl();

    public byte[] getPhoto();

    public PublishPeriod getPeriod();

    public Map<String, String> getProperties();

    public List<InfoProperty> getAllInfoProperties() throws Exception;

    public List<T> getLinkChannels();

    public Boolean getCataloged();

    public String getKeywords();

    public String getRemark();

    public Byte getDeleteTag();
}