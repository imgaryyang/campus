package com.gzzm.portal.cms.channel;

import com.gzzm.platform.commons.components.EntityPageTreeModel;
import net.cyan.arachne.components.*;

import java.util.*;

/**
 * 栏目树
 *
 * @author camel
 * @date 2011-5-11
 */
public class ChannelTreeModel extends EntityPageTreeModel<Channel>
        implements CheckBoxTreeModel<Channel>, SelectableModel<Channel>
{
    private Integer rootId = 0;

    private boolean showBox;

    private List<Integer> excludedChannels;

    public ChannelTreeModel()
    {
    }

    public Integer getRootId()
    {
        return rootId;
    }

    public void setRootId(Integer rootId)
    {
        this.rootId = rootId;
    }

    public boolean isShowBox()
    {
        return showBox;
    }

    public void setShowBox(boolean showBox)
    {
        this.showBox = showBox;
    }

    @Override
    protected Object getRootKey()
    {
        return rootId;
    }

    public Boolean isRootVisible()
    {
        return false;
    }

    @Override
    protected String getTextField() throws Exception
    {
        return "channelName";
    }

    @Override
    protected String getParentField() throws Exception
    {
        return "parentChannelId";
    }

    @Override
    protected boolean accept(Channel channel) throws Exception
    {
        return (excludedChannels == null || !excludedChannels.contains(channel.getChannelId())) &&
                (channel.getDeleteTag() == null || channel.getDeleteTag() == 0);
    }

    public boolean hasCheckBox(Channel channel) throws Exception
    {
        return showBox;
    }

    public Boolean isChecked(Channel channel) throws Exception
    {
        if (channel.getType() != null && channel.getType() != ChannelType.information)
            return null;

        return false;
    }

    @Override
    public boolean isSelectable(Channel channel) throws Exception
    {
        return channel.getType() == null || channel.getType() == ChannelType.information;
    }

    public void addExcludedChannel(Integer channelId)
    {
        if (excludedChannels == null)
            excludedChannels = new ArrayList<Integer>();

        excludedChannels.add(channelId);
    }
}
