package com.gzzm.portal.cms.station;

import com.gzzm.platform.annotation.*;
import com.gzzm.platform.commons.components.EntityPageListModel;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.annotation.In;
import net.cyan.nest.annotation.Inject;

import java.util.Collection;

/**
 * @author camel
 * @date 13-9-24
 */
@Service
public class StationList extends EntityPageListModel<Station, Integer>
{
    @NotSerialized
    @AuthDeptIds
    @In("deptId")
    private Collection<Integer> authDeptIds;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    @MenuId
    private String menuId;

    public StationList()
    {
        addOrderBy("type");
        addOrderBy("orderId");
    }

    @NotSerialized
    public Collection<Integer> getAuthDeptIds()
    {
        return authDeptIds;
    }

    @NotSerialized
    public Integer getCreator() throws Exception
    {
        if (userOnlineInfo.isSelf(menuId))
            return userOnlineInfo.getUserId();

        return null;
    }

    @Override
    protected String getTextField() throws Exception
    {
        return "stationName";
    }
}
