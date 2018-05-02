package com.gzzm.safecampus.campus.bus;

import com.gzzm.platform.commons.crud.SimpleDialogView;
import com.gzzm.safecampus.campus.base.CHourMinute;
import com.gzzm.safecampus.campus.common.ScSubListCrud;
import com.gzzm.safecampus.campus.common.ScSubListView;
import net.cyan.arachne.annotation.Service;

/**
 * 校巴路线途经地点维护
 */
@Service
public class BusSiteSubList extends ScSubListCrud<BusSite, Integer>
{
    private Integer routeId;

    public BusSiteSubList()
    {
    }

    public Integer getRouteId()
    {
        return routeId;
    }

    public void setRouteId(Integer routeId)
    {
        this.routeId = routeId;
    }

    @Override
    protected String getParentField()
    {
        return "routeId";
    }

    @Override
    protected void initListView(ScSubListView view) throws Exception
    {
        view.addColumn("途经地点", "siteName");
        view.addColumn("早班途经时间", "morningTime").setWidth("100px");
        view.addColumn("晚班途经时间", "nightTime").setWidth("100px");
    }

    @Override
    protected void initShowView(SimpleDialogView view) throws Exception
    {
        view.addComponent("途经地点", "siteName");
        view.addComponent("早班途经时间", new CHourMinute("morningViaHour", "morningViaMinute").setFormat("00", "00").setWidth("117px", "117px"));
        view.addComponent("晚班途经时间", new CHourMinute("nightViaHour", "nightViaMinute").setFormat("00", "00").setWidth("117px", "117px"));
        //重写默认的label样式：label文字过长导致下跌
        view.importCss("/safecampus/campus/bus/bussite.css");
    }
}
