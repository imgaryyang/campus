package com.gzzm.portal.cms.channel;

import com.gzzm.platform.annotation.AuthDeptIds;
import com.gzzm.platform.appauth.AppAuthService;
import com.gzzm.platform.commons.crud.SelectableTreeView;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.portal.cms.commons.PublishPeriod;
import com.gzzm.portal.cms.information.EditType;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.Filter;
import net.cyan.crud.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 栏目树展示
 *
 * @author camel
 * @date 2011-5-17
 */
@Service
public class ChannelDisplay extends TreeCrud<ChannelItem, Integer>
        implements SearchTreeCrud<ChannelItem, Integer>, Filter<ChannelCache>
{

    @Inject
    private ChannelTree channelTree;

    @Inject
    private AppAuthService appAuthService;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    @AuthDeptIds
    private Collection<Integer> authDeptIds;

    /**
     * 拥有权限的栏目的id
     */
    private Collection<Integer> authChannelIds;

    /**
     * 编辑类型，采编或者发布
     */
    private EditType editType;

    private Integer topChannelId;

    private PublishPeriod period;

    public ChannelDisplay()
    {
    }

    public EditType getEditType()
    {
        return editType;
    }

    public void setEditType(EditType editType)
    {
        this.editType = editType;
    }

    public Integer getTopChannelId()
    {
        return topChannelId;
    }

    public void setTopChannelId(Integer topChannelId)
    {
        this.topChannelId = topChannelId;
    }

    public PublishPeriod getPeriod()
    {
        return period;
    }

    public void setPeriod(PublishPeriod period)
    {
        this.period = period;
    }

    public boolean accept(ChannelCache channel) throws Exception
    {
        Collection<Integer> authChannelIds = getAuthChannelIds();

        return (authChannelIds == null || authChannelIds.contains(channel.getChannelId())) &&
                channel.getType() == ChannelType.information && (period == null || channel.getPeriod() == period);
    }

    private Collection<Integer> getAuthChannelIds() throws Exception
    {
        if (authDeptIds == null)
            return null;

        if (authChannelIds == null)
        {
            authChannelIds = appAuthService.getAppIds(userOnlineInfo.getUserId(), userOnlineInfo.getDeptId(),
                    authDeptIds, editType == EditType.edit ? ChannelAuthCrud.PORTAL_CHANNEL_EDIT :
                            ChannelAuthCrud.PORTAL_CHANNEL_PUBLISH
            );
        }

        return authChannelIds;
    }

    @Override
    public ChannelItem getRoot() throws Exception
    {
        if (topChannelId == null)
            return ChannelItem.getRoot();

        return new ChannelItem(channelTree.getChannel(topChannelId));
    }

    public Collection<ChannelItem> search(String text) throws Exception
    {
        List<ChannelCache> channels = channelTree.getRoot().search(text, getAuthChannelIds(), 20);
        List<ChannelItem> result = new ArrayList<ChannelItem>(channels.size());

        for (ChannelCache channel : channels)
        {
            result.add(new ChannelItem(channel));
        }

        return result;
    }

    public ChannelItem getParent(ChannelItem channel) throws Exception
    {
        if (channel.getChannelId() == 0)
            return null;

        if (topChannelId != null && channel.getChannelId().equals(topChannelId))
            return null;

        Integer parentNodeId = channel.getParentNodeId();
        ChannelCache parentChannel = channelTree.getChannel(parentNodeId);

        while (parentNodeId != null && parentNodeId != 0)
        {
            if (topChannelId != null && topChannelId.equals(parentNodeId) || accept(parentChannel))
                return new ChannelItem(parentChannel);

            ChannelCache parentParentChannel = parentChannel.getParentChannel();
            for (ChannelCache channel1 : parentParentChannel.getChildren())
            {
                if (!channel1.getChannelId().equals(parentNodeId))
                {
                    if (accept(channel1) || channel1.containsChildChannel(this))
                    {
                        return new ChannelItem(parentChannel);
                    }
                }
            }

            parentNodeId = parentParentChannel.getParentChannelId();
            parentChannel = parentParentChannel;
        }

        return ChannelItem.getRoot();
    }

    public boolean supportSearch() throws Exception
    {
        return true;
    }

    @Override
    public List<ChannelItem> getChildren(Integer parentChannelId) throws Exception
    {
        ChannelCache parentChannel = channelTree.getChannel(parentChannelId);

        List<ChannelItem> children = new ArrayList<ChannelItem>();

        for (ChannelCache channel : parentChannel.getChildren())
        {
            boolean hasChildren = channel.containsChildChannel(this);
            if (accept(channel))
            {
                ChannelItem item = new ChannelItem(channel);
                item.setReal(true);
                item.setLeaf(!hasChildren);
                children.add(item);
            }
            else if (hasChildren)
            {
                ChannelItem item = new ChannelItem(channel);
                item.setReal(false);
                item.setLeaf(false);
                children.add(item);
            }
        }

        if (children.size() == 1)
        {
            ChannelItem channelItem = children.get(0);
            if (!channelItem.isReal())
            {
                children = getChildren(channelItem.getChannelId());
            }
        }

        return children;
    }

    @Override
    public boolean hasChildren(ChannelItem channel) throws Exception
    {
        return !channel.isLeaf();
    }

    @Override
    public ChannelItem getNode(Integer channelId) throws Exception
    {
        return new ChannelItem(channelTree.getChannel(channelId));
    }

    public Integer getKey(ChannelItem channel) throws Exception
    {
        return channel.getChannelId();
    }

    @Override
    protected Object createTreeView() throws Exception
    {
        SelectableTreeView view = new SelectableTreeView();

        if (topChannelId == null)
            view.setRootVisible(false);
        else
            view.setRootVisible(true);

        return view;
    }
}