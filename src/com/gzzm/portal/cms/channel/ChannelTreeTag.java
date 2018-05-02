package com.gzzm.portal.cms.channel;

import com.gzzm.platform.appauth.AppAuthService;
import com.gzzm.portal.annotation.Tag;
import com.gzzm.portal.cms.commons.PublishPeriod;
import com.gzzm.portal.tag.PortalTag;
import net.cyan.commons.util.Provider;
import net.cyan.commons.util.json.JsonSerializer;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 栏目树标签
 *
 * @author camel
 * @date 2011-7-12
 */
@Tag(name = "channelTree")
public class ChannelTreeTag implements PortalTag
{
    @Inject
    private Provider<AppAuthService> appAuthServiceProvider;

    @Inject
    private ChannelDao dao;

    private Integer channelId;

    /**
     * 属性过滤，一个栏目的属性名称，仅当此属性值为true的栏目显示，如果为空，表示不过滤
     */
    private String property;

    /**
     * 层数，如果为0表示无限层
     */
    private int level;

    /**
     * 初始化栏目树的函数名
     */
    private String functionName = "initChannels";

    private Integer channelDeptId;

    private PublishPeriod period;


    public ChannelTreeTag()
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

    public int getLevel()
    {
        return level;
    }

    public void setLevel(int level)
    {
        this.level = level;
    }

    public String getFunctionName()
    {
        return functionName;
    }

    public void setFunctionName(String functionName)
    {
        this.functionName = functionName;
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
        List<Channel> channels = dao.getChildChannels(channelId, period);

        StringBuilder buffer = new StringBuilder("<script>\n");

        List<ChannelNode> nodes = getChannelTree(channels, 1);

        buffer.append(functionName).append("(");

        new JsonSerializer(buffer).serialize(nodes);

        buffer.append(");\n</script>");

        return buffer.toString();
    }

    private List<ChannelNode> getChannelTree(List<Channel> channels, int level) throws Exception
    {
        Collection<Integer> authChannelIds = null;
        if (channelDeptId != null && channelDeptId > 1)
        {
            authChannelIds =
                    appAuthServiceProvider.get().getAppIds(null, channelDeptId, Collections.singleton(channelDeptId),
                            ChannelAuthCrud.PORTAL_CHANNEL_EDIT);
        }

        List<ChannelNode> nodes = new ArrayList<ChannelNode>();

        for (Channel channel : channels)
        {
            if (channel.getDeleteTag() == null || channel.getDeleteTag() == 0)
            {
                if (property == null || "true".equals(channel.getProperties().get(property)))
                {
                    ChannelNode node = new ChannelNode(channel);

                    if (authChannelIds == null || channel.getType() != ChannelType.information ||
                            authChannelIds.contains(channel.getChannelId()) ||
                            channel.containsChildChannel(authChannelIds))
                    {
                        nodes.add(node);
                    }

                    if (this.level == 0 || level < this.level)
                    {
                        node.setChildren(getChannelTree(channel.getChildChannels(), level + 1));
                    }
                }
            }
        }

        return nodes;
    }
}
