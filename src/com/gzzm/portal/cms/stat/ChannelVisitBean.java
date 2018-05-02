package com.gzzm.portal.cms.stat;

/**
 * @author sjy
 * @date 2017/5/23
 */
public class ChannelVisitBean
{
    private Integer channelId;

    private String channelName;

    private String channelCode;

    private Long visitCount;

    public String getChannelCode()
    {
        return channelCode;
    }

    public void setChannelCode(String channelCode)
    {
        this.channelCode = channelCode;
    }

    public Integer getChannelId()
    {
        return channelId;
    }

    public void setChannelId(Integer channelId)
    {
        this.channelId = channelId;
    }

    public String getChannelName()
    {
        return channelName;
    }

    public void setChannelName(String channelName)
    {
        this.channelName = channelName;
    }

    public Long getVisitCount()
    {
        return visitCount;
    }

    public void setVisitCount(Long visitCount)
    {
        this.visitCount = visitCount;
    }
}
