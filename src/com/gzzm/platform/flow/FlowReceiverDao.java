package com.gzzm.platform.flow;

import com.gzzm.platform.organ.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;
import net.cyan.thunwind.dao.GeneralDao;

/**
 * @author camel
 * @date 2014/8/21
 */
public abstract class FlowReceiverDao extends GeneralDao
{
    @Inject
    private static Provider<FlowReceiverDao> daoProvider;

    public static FlowReceiverDao getInstance()
    {
        return daoProvider.get();
    }

    public FlowReceiverDao()
    {
    }

    public String getReceiverName(String receiver) throws Exception
    {
        if (StringUtils.isEmpty(receiver))
            return "";

        if (receiver.startsWith("$"))
        {
            return receiver.substring(1);
        }

        int index = receiver.indexOf('@');

        if (index == 0)
        {
            return getDeptName(Integer.valueOf(receiver.substring(1)));
        }
        else if (index > 0)
        {
            return getStationName(Integer.valueOf(receiver.substring(0, index)));
        }
        else
        {
            index = receiver.indexOf('#');

            if (index > 0)
                return receiver.substring(0, index);

            return getUserName(Integer.valueOf(receiver));
        }
    }

    public User getUser(Integer userId) throws Exception
    {
        return load(User.class, userId);
    }

    protected String getUserName(Integer userId) throws Exception
    {
        User user = getUser(userId);

        if (user == null)
            return null;
        else
            return user.getUserName();
    }

    protected String getDeptName(Integer deptId) throws Exception
    {
        Dept dept = load(Dept.class, deptId);

        if (dept == null)
            return null;
        else
            return dept.getDeptName();
    }

    protected String getStationName(Integer stationId) throws Exception
    {
        Station station = load(Station.class, stationId);

        if (station == null)
            return null;
        else
            return station.getStationName();
    }
}
