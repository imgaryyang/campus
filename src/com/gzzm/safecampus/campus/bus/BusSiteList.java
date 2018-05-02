package com.gzzm.safecampus.campus.bus;

import com.gzzm.platform.commons.components.EntityPageListModel;
import net.cyan.arachne.annotation.Service;

/**
 * @author czy
 */
@Service
public class BusSiteList extends EntityPageListModel<BusSite, Integer> {

    private Integer routeId;

    public BusSiteList()
    {
        addOrderBy("siteName");
    }

    public BusSiteList(Integer routeId) {
        this.routeId = routeId;
    }

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    @Override
    protected String getTextField() throws Exception
    {
        return "siteName";
    }

    @Override
    protected String getSearchCondition() throws Exception
    {
        return "routeId = "+this.routeId+" and siteName like ?text";
    }
}
