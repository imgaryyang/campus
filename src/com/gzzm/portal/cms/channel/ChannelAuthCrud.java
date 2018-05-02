package com.gzzm.portal.cms.channel;

import com.gzzm.platform.appauth.*;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * 栏目权限维护
 *
 * @author camel
 * @date 2011-5-13
 */
@Service(url = "/portal/channelauth")
public class ChannelAuthCrud extends AppAuthCrud
{
    public static final String PORTAL_CHANNEL_EDIT = "portal_channel_edit";

    public static final String PORTAL_CHANNEL_PUBLISH = "portal_channel_publish";

    @Inject
    private ChannelDao dao;

    private Integer channelId;

    public ChannelAuthCrud()
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

    @NotSerialized
    public String getChannelName() throws Exception
    {
        return dao.getChannel(channelId).getChannelName();
    }

    @Override
    protected App[] createApps()
    {
        return new App[]{
                new App(PORTAL_CHANNEL_EDIT, channelId, "采编"),
                new App(PORTAL_CHANNEL_PUBLISH, channelId, "发布")
        };
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = (PageTableView) super.createListView();

        view.setTitle("栏目赋权-{channelName}");

        view.addButton("将所有权限赋给子栏目", "copyAllAuthsToDescendant()");
        view.addButton("将选择的权限赋给子栏目", "copySelectedAuthsToDescendant()");
        view.addButton(Buttons.getButton("从子栏目一起删除", "removeAllWithDescendant()", "delete"));

        view.importJs("/portal/cms/channel/auth.js");

        return view;
    }

    @Service
    @Transactional
    public void copyAllAuthsToDescendant() throws Exception
    {
        for (Integer descendantChannelId : dao.getDescendantChannelIds(channelId))
        {
            if (!descendantChannelId.equals(channelId))
            {
                //复制采编权限
                service.copyAuths(PORTAL_CHANNEL_EDIT, channelId, PORTAL_CHANNEL_EDIT, descendantChannelId);

                //复制发布权限
                service.copyAuths(PORTAL_CHANNEL_PUBLISH, channelId, PORTAL_CHANNEL_PUBLISH, descendantChannelId);
            }
        }
    }

    @Service(method = HttpMethod.post)
    @Transactional
    public void copySelectedAuthsToDescendant() throws Exception
    {
        List<AuthObject> authObjects = getSelectedAuthObjects();
        for (Integer descendantChannelId : dao.getDescendantChannelIds(channelId))
        {
            if (!descendantChannelId.equals(channelId))
            {
                for (AuthObject authObject : authObjects)
                {
                    //复制采编权限
                    service.copyAuths(PORTAL_CHANNEL_EDIT, channelId, authObject.getType(), authObject.getObjectId(),
                            PORTAL_CHANNEL_EDIT, descendantChannelId);

                    //复制发布权限
                    service.copyAuths(PORTAL_CHANNEL_PUBLISH, channelId, authObject.getType(), authObject.getObjectId(),
                            PORTAL_CHANNEL_PUBLISH, descendantChannelId);
                }
            }
        }

        service.clearTables(PORTAL_CHANNEL_EDIT);
        service.clearTables(PORTAL_CHANNEL_PUBLISH);
    }

    @Service(method = HttpMethod.post)
    @Transactional
    public void removeAllWithDescendant() throws Exception
    {
        List<AuthObject> authObjects = getSelectedAuthObjects();
        for (Integer descendantChannelId : dao.getDescendantChannelIds(channelId))
        {
            for (AuthObject authObject : authObjects)
            {
                //复制采编权限
                service.setAuth(PORTAL_CHANNEL_EDIT, descendantChannelId, authObject.getType(),
                        authObject.getObjectId(), false);

                //复制发布权限
                service.setAuth(PORTAL_CHANNEL_PUBLISH, descendantChannelId, authObject.getType(),
                        authObject.getObjectId(), false);
            }
        }

        service.clearTables(PORTAL_CHANNEL_EDIT);
        service.clearTables(PORTAL_CHANNEL_PUBLISH);
    }
}
