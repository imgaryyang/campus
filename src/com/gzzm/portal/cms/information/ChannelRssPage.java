package com.gzzm.portal.cms.information;

import com.gzzm.platform.annotation.ConfigValue;
import com.gzzm.portal.cms.channel.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.arachne.exts.rss.Rss;
import net.cyan.commons.util.StringUtils;
import net.cyan.crud.annotation.*;
import net.cyan.nest.annotation.Inject;

import java.util.Date;

/**
 * 某栏目及子栏目下的信息生成rss列表
 *
 * @author camel
 * @date 14-2-25
 */
@Service
public class ChannelRssPage extends InformationRssPage
{
    @Inject
    protected ChannelDao channelDao;

    @ConfigValue(name = "rss_exclude_channels")
    private String excludeChannels;

    private String channelCode;

    private Channel channel;

    private Integer channelId;

    @Lower
    private Date startTime;

    @Upper
    private Date endTime;

    public ChannelRssPage()
    {
    }

    public Date getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public Date getEndTime()
    {
        return endTime;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    @Override
    protected void initRss(Rss rss) throws Exception
    {
        rss.setTitle(getChannel().getChannelName());
    }

    protected Channel getChannel() throws Exception
    {
        if (channel == null)
        {
            if (channelId != null)
                channel = channelDao.getChannel(channelId);
            else if (channelCode != null)
                channel = channelDao.getChannelByCode(channelCode);
        }

        return channel;
    }

    public Integer getChannelId() throws Exception
    {
        if (channelId == null && channelCode != null)
            channelId = getChannel().getChannelId();

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

    @Override
    protected String getQueryString() throws Exception
    {
        return "select i from Information i join Channel c on channel.leftValue>=c.leftValue " +
                "and channel.leftValue<c.rightValue where c.channelId=:channelId and c.deleteTag=0 " +
                "and valid=1 and (validTime is null or validTime>sysdate()) and " +
                "i.updateTime>=?startTime and i.updateTime<?endTime order by updateTime desc," +
                "informationId desc";
    }

    @Override
    @Service(url = "/channel/{channelCode}/rss")
    public Rss showRss() throws Exception
    {
        if (!StringUtils.isEmpty(excludeChannels))
        {
            String s = excludeChannels.trim();
            if ("all".equals(s))
            {
                return null;
            }

            String[] channelCodes = s.split(",");
            for (String pre : channelCodes)
            {
                if (channelCode.startsWith(pre))
                {
                    return null;
                }
            }

        }
        return super.showRss();
    }
}
