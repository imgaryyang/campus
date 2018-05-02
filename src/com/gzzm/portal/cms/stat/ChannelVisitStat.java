package com.gzzm.portal.cms.stat;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.portal.cms.channel.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.Null;
import net.cyan.crud.view.Align;
import net.cyan.nest.annotation.Inject;

import java.util.*;
import java.sql.Date;

/**
 * @author sjy
 * @date 2017/5/23
 */
@Service(url = "/portal/channel/visitStat")
public class ChannelVisitStat extends BaseListCrud<ChannelVisitBean>
{
    private Integer channelId;

    @Inject
    private ChannelVisitDao dao;

    private Date time_start;

    private Date time_end;

    public Date getTime_start()
    {
        return time_start;
    }

    public void setTime_start(Date time_start)
    {
        this.time_start = time_start;
    }

    public Date getTime_end()
    {
        return time_end;
    }

    public void setTime_end(Date time_end)
    {
        this.time_end = time_end;
    }

    @Override
    protected Object createListView() throws Exception
    {
        ChannelDisplay channelDisplay = Tools.getBean(ChannelDisplay.class);
        PageTableView view = new ComplexTableView(channelDisplay, "channelId", false);

        view.addComponent("访问时间", "time_start", "time_end");

        view.addColumn("栏目名称", "channelName");
        view.addColumn("栏目编号", "channelCode");
        view.addColumn("访问量", "visitCount").setWidth("200px").setAlign(Align.left);

        view.addButton(Buttons.query());
        view.addButton(Buttons.export("xls"));
        return view;
    }

    @Override
    protected void loadList() throws Exception
    {
        List<ChannelVisitBean> channelVisitBeen = new ArrayList<ChannelVisitBean>(30);
        dao.setPageSize(30);
        List<Channel> channels = dao.queryChannels(channelId);
        if (channels != null && channels.size() > 0)
        {
            long allVisit = 0L;

            if (!(Null.Date.equals(time_start)) || !(Null.Date.equals(time_end)))
            {
                for (Channel channel : channels)
                {
                    ChannelVisitBean bean = new ChannelVisitBean();
                    channelVisitBeen.add(bean);
                    bean.setChannelId(channel.getChannelId());
                    bean.setChannelCode(channel.getChannelCode());
                    bean.setChannelName(channel.getChannelName());

                    long visitTotal = dao.countChannelsVisitTotalByDate(1, channel.getChannelId(),
                            time_start, time_end);
                    allVisit += visitTotal;
                    bean.setVisitCount(visitTotal);
                }
            }
            else
            {
                for (Channel channel : channels)
                {
                    ChannelVisitBean bean = new ChannelVisitBean();
                    channelVisitBeen.add(bean);
                    bean.setChannelId(channel.getChannelId());
                    bean.setChannelCode(channel.getChannelCode());
                    bean.setChannelName(channel.getChannelName());
                    long visitTotal = dao.countChannelsVisitTotal(channel.getChannelId());
                    allVisit += visitTotal;
                    bean.setVisitCount(visitTotal);
                }
            }

            /*ChannelVisitBean all = new ChannelVisitBean();
            channelVisitBeen.add(0,all);
            all.setChannelId(-1);
            all.setVisitCount(allVisit);
            all.setChannelName("总共");
            all.setChannelCode("");*/
        }
        if (channelId != null)
        {

        }
        setList(channelVisitBeen);
        // super.loadList();
    }

    public Integer getChannelId()
    {
        return channelId;
    }

    public void setChannelId(Integer channelId)
    {
        this.channelId = channelId;
    }
}
