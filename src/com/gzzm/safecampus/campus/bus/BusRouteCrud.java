package com.gzzm.safecampus.campus.bus;

import com.gzzm.platform.commons.crud.PageTableView;
import com.gzzm.safecampus.campus.base.BaseCrud;
import com.gzzm.safecampus.campus.base.NumberSelect;
import net.cyan.arachne.annotation.Forward;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.arachne.annotation.Select;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.KeyValue;
import net.cyan.crud.annotation.Like;

import java.util.List;

/**
 * 路线管理
 *
 * @author czy
 */
@Service(url = "/campus/bus/route")
public class BusRouteCrud extends BaseCrud<BusRoute, Integer>
{
    /**
     * 路线名称
     */
    @Like
    private String routeName;

    /**
     * 小时选择控件
     */
    @NotSerialized
    private List<KeyValue<String>> hourSelect;

    /**
     * 分钟选择控件
     */
    @NotSerialized
    private List<KeyValue<String>> minuteSelect;

    public BusRouteCrud()
    {
    }

    public String getRouteName()
    {
        return routeName;
    }

    public void setRouteName(String routeName)
    {
        this.routeName = routeName;
    }

    @Override
    public String getDeleteTagField()
    {
        return "deleteTag";
    }

    @Override
    @Forward(page = "/safecampus/campus/bus/busroute.ptl")
    public String add(String forward) throws Exception
    {
        return super.add(forward);
    }

    @Override
    @Forward(page = "/safecampus/campus/bus/busroute.ptl")
    public String show(Integer key, String forward) throws Exception
    {
        return super.show(key, forward);
    }

    /**
     * 小时选择控件
     *
     * @return 小时选择控件
     */
    @Select(field = {"entity.morningBusHour", "entity.nightBusHour"})
    public List<KeyValue<String>> getHourSelect()
    {
        if (hourSelect == null)
            hourSelect = NumberSelect.createHourSelect("00");
        return hourSelect;
    }

    /**
     * 分钟选择控件
     *
     * @return 分钟选择控件
     */
    @Select(field = {"entity.morningBusMinute", "entity.nightBusMinute"})
    public List<KeyValue<String>> getMinuteSelect()
    {
        if (minuteSelect == null)
            minuteSelect = NumberSelect.createMinuteSelect("00");
        return minuteSelect;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();
        view.addComponent("路线名称", "routeName");

        view.addColumn("路线名称", "routeName").setWidth("450px");
        view.addColumn("早班发车时间", "morningTime").setWidth("200px");
        view.addColumn("晚班发车时间", "nightTime").setWidth("200px");
        view.addColumn("预计行程", "routeTravelTime");
        view.defaultInit(false);
        return view;
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        return "routeName is not null";
    }
}
