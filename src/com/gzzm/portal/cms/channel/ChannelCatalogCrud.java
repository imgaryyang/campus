package com.gzzm.portal.cms.channel;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.portal.cms.station.*;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.*;
import net.cyan.crud.annotation.*;

import java.util.*;

/**
 * 栏目分类维护
 *
 * @author sjy
 * @date 2018/2/23
 */

@Service(url = "/portal/channelCatalog")
public class ChannelCatalogCrud extends BaseNormalCrud<ChannelCatalog, Integer>
{

    private Integer stationId;

    @Like
    private String catalogName;

    @NotSerialized
    private List<Integer> channelIds;

    @NotSerialized
    private List<Channel> channels = null;

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new ComplexTableView(Tools.getBean(StationDisplay.class), "stationId");
        view.addComponent("分类名称", "catalogName");
        view.addColumn("分类名称", "catalogName");
        view.makeEditable();
        view.addDefaultButtons();
        view.addButton(Buttons.sort());
        view.importJs("/portal/cms/channel/channel_catalog.js");
        return view;
    }

    @Override
    public void initEntity(ChannelCatalog entity) throws Exception
    {
        super.initEntity(entity);
        entity.setStationId(stationId);
    }

    @Override
    public String getDeleteTagField()
    {
        return "deleteTag";
    }

    @Override
    public String getOrderField()
    {
        return "orderId";
    }

    @Override
    public boolean beforeSave() throws Exception
    {
        List<Channel> relateChannels = new ArrayList<Channel>();
        if (channelIds != null && channelIds.size() > 0)
        {
            for (Integer channelId : channelIds)
            {
                Channel channel = new Channel();
                channel.setChannelId(channelId);
                relateChannels.add(channel);
            }
        }
        getEntity().setRelateChannels(relateChannels);
        return super.beforeSave();
    }

    @Override
    protected String getSortListCondition() throws Exception
    {
        return "stationId=:stationId";
    }

    @Override
    public String show(Integer key, String forward) throws Exception
    {
        super.show(key, forward);
        this.channels = getEntity().getRelateChannels();
        return "/portal/cms/channel/channel_catalog.ptl";
    }

    @Override
    public String add(String forward) throws Exception
    {
        super.add(forward);
        return "/portal/cms/channel/channel_catalog.ptl";
    }

    @NotSerialized
    public ChannelTreeModel getAllChannels() throws Exception
    {
        ChannelTreeModel treeModel = Tools.getBean(ChannelTreeModel.class);
        //只显示当前站点的栏目
        CrudDao dao = getCrudService().getDao();
        Station station = null;
        if (stationId == null)
        {
            station = getEntity().getStation();
        }
        else
        {
            station = dao.get(Station.class, stationId);
        }
        Integer channelId = station.getChannelId();
        if (channelId != null)
        {
            treeModel.setRootId(channelId);
        }
        return treeModel;
    }

    public List<Channel> getChannels()
    {
        return channels;
    }

    public void setChannels(List<Channel> channels)
    {
        this.channels = channels;
    }

    public Integer getStationId()
    {
        return stationId;
    }

    public void setStationId(Integer stationId)
    {
        this.stationId = stationId;
    }

    public String getCatalogName()
    {
        return catalogName;
    }

    public void setCatalogName(String catalogName)
    {
        this.catalogName = catalogName;
    }

    public List<Integer> getChannelIds()
    {
        return channelIds;
    }

    public void setChannelIds(List<Integer> channelIds)
    {
        this.channelIds = channelIds;
    }
}
