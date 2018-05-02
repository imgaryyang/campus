package com.gzzm.portal.cms.channel;

import com.gzzm.platform.annotation.AuthDeptIds;
import com.gzzm.platform.appauth.AppAuthService;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.portal.cms.information.EditType;
import net.cyan.arachne.components.*;
import net.cyan.commons.util.Filter;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author camel
 * @date 12-12-25
 */
public class AuthChannelTreeModel implements LazyPageTreeModel<ChannelItem>, SearchablePageTreeModel<ChannelItem>,
        CheckBoxTreeModel<ChannelItem>, Filter<ChannelCache>
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

    private boolean hasCheckBox;

    private Integer deptId;

    private Integer rootId;

    public AuthChannelTreeModel()
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

    public boolean isHasCheckBox()
    {
        return hasCheckBox;
    }

    public void setHasCheckBox(boolean hasCheckBox)
    {
        this.hasCheckBox = hasCheckBox;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Integer getRootId()
    {
        return rootId;
    }

    public void setRootId(Integer rootId)
    {
        this.rootId = rootId;
    }

    private Collection<Integer> getAuthChannelIds() throws Exception
    {
        if (authDeptIds == null && deptId == null)
            return null;

        if (authChannelIds == null)
        {
            Collection<Integer> authDeptIds = this.authDeptIds;

            if (deptId != null)
                authDeptIds = Collections.singleton(deptId);

            authChannelIds = appAuthService.getAppIds(userOnlineInfo.getUserId(), userOnlineInfo.getDeptId(),
                    authDeptIds, editType == EditType.edit ? ChannelAuthCrud.PORTAL_CHANNEL_EDIT :
                            ChannelAuthCrud.PORTAL_CHANNEL_PUBLISH
            );
        }

        return authChannelIds;
    }

    public ChannelItem getRoot() throws Exception
    {
        if (rootId == null)
            return ChannelItem.getRoot();

        ChannelCache channel = channelTree.getChannel(rootId);

        return new ChannelItem(channel);
    }

    public boolean isLazyLoad(ChannelItem channelItem) throws Exception
    {
        if (rootId == null)
            return channelItem.getChannelId() != 0;
        else
            return !rootId.equals(channelItem.getChannelId());
    }

    public void beforeLazyLoad(String id) throws Exception
    {
    }

    public ChannelItem getParent(ChannelItem channel) throws Exception
    {
        if (channel.getChannelId() == 0)
            return null;

        Collection<Integer> authChannelIds = getAuthChannelIds();

        Integer parentNodeId = channel.getParentNodeId();
        ChannelCache parentChannel = channelTree.getChannel(parentNodeId);

        while (parentNodeId != 0 && parentNodeId != null)
        {
            if (authChannelIds == null || authChannelIds.contains(parentNodeId))
                return new ChannelItem(parentChannel);

            ChannelCache parentParentChannel = parentChannel.getParentChannel();
            for (ChannelCache channel1 : parentParentChannel.getChildren())
            {
                if (!channel1.getChannelId().equals(parentNodeId))
                {
                    if (authChannelIds.contains(channel1.getChannelId()))
                    {
                        return new ChannelItem(parentChannel);
                    }
                    else if (channel1.containsChildChannel(this))
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

    public boolean isLeaf(ChannelItem channelItem) throws Exception
    {
        return channelItem.isLeaf();
    }

    public boolean isSearchable() throws Exception
    {
        return true;
    }

    public int getChildCount(ChannelItem parent) throws Exception
    {
        return getChildren(parent).size();
    }

    public ChannelItem getChild(ChannelItem parent, int index) throws Exception
    {
        return getChildren(parent).get(index);
    }

    private List<ChannelItem> getChildren(ChannelItem parent) throws Exception
    {
        if (parent.getChildren() == null)
        {
            ChannelCache parentChannel = channelTree.getChannel(parent.getChannelId());

            List<ChannelItem> children = new ArrayList<ChannelItem>();
            Collection<Integer> authChannelIds = getAuthChannelIds();

            for (ChannelCache channel : parentChannel.getChildren())
            {
                boolean hasChildren = channel.containsChildChannel(this);
                if ((authChannelIds == null || authChannelIds.contains(channel.getChannelId())) &&
                        channel.getType() == ChannelType.information)
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
                    children = getChildren(channelItem);
                }
            }

            parent.setChildren(children);
        }

        return parent.getChildren();
    }

    public String getId(ChannelItem channelItem) throws Exception
    {
        return channelItem.getChannelId().toString();
    }

    public ChannelItem getNode(String id) throws Exception
    {
        return new ChannelItem(channelTree.getChannel(Integer.valueOf(id)));
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

    public String toString(ChannelItem channel) throws Exception
    {
        return channel.getChannelName();
    }

    public boolean hasCheckBox(ChannelItem channelItem) throws Exception
    {
        return hasCheckBox;
    }

    public Boolean isChecked(ChannelItem channelItem) throws Exception
    {
        return false;
    }

    public Boolean isRootVisible()
    {
        return rootId != null;
    }

    public boolean accept(ChannelCache channelCache) throws Exception
    {
        return authChannelIds == null || authChannelIds.contains(channelCache.getChannelId());
    }
}
