package com.gzzm.safecampus.campus.bus;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.PageTableView;
import com.gzzm.platform.commons.crud.SimpleDialogView;
import com.gzzm.safecampus.campus.base.BaseCrud;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.arachne.annotation.Select;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;
import net.cyan.nest.annotation.Inject;

/**
 * 校巴信息CRUD
 *
 * @author czy
 */
@Service(url = "/campus/bus/info")
public class BusInfoCrud extends BaseCrud<BusInfo, Integer>
{

    @Inject
    private BusRouteDao busRouteDao;

    /**
     * 校巴名称
     */
    @Like
    private String busName;

    /**
     * 校巴车牌
     */
    @Like
    private String busLicense;

    /**
     * 路线选择控件
     */
    private BusRouteList busRouteList;

    public BusInfoCrud()
    {
    }

    public String getBusName()
    {
        return busName;
    }

    public void setBusName(String busName)
    {
        this.busName = busName;
    }

    public String getBusLicense()
    {
        return busLicense;
    }

    public void setBusLicense(String busLicense)
    {
        this.busLicense = busLicense;
    }

    @NotSerialized
    @Select(field = "entity.routeId",text = "routeName")
    public BusRouteList getBusRouteList() throws Exception
    {
        if (busRouteList == null)
            busRouteList = Tools.getBean(BusRouteList.class);
        return busRouteList;
    }

    @Override
    public String getDeleteTagField()
    {
        return "deleteTag";
    }
    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();
        view.addComponent("校巴名称", "busName");
        view.addComponent("校巴车牌", "busLicense");

        view.addColumn("校巴名称", "busName").setWidth("150px");
        view.addColumn("校巴车牌", "busLicense").setWidth("150px");
        view.addColumn("路线名称", "busRoute.routeName");
        view.addColumn("司机名称", "driverName").setWidth("150px");
        view.addColumn("司机电话", "driverPhone").setWidth("150px");
        view.defaultInit(false);
        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();
        view.addComponent("校巴名称", "busName", true);
        view.addComponent("校巴车牌", "busLicense", true);
        view.addComponent("司机名称", "driverName", true);
        view.addComponent("司机电话", "driverPhone", true);
        view.addComponent("路线名称", "routeId", true).setProperty("text", "${busRoute == null ? '' : busRoute.routeName}");
        view.addDefaultButtons();
        return view;
    }
}