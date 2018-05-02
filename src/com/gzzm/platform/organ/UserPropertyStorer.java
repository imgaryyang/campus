package com.gzzm.platform.organ;

import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.PropertyStorer;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 2016/9/22
 */
public class UserPropertyStorer implements PropertyStorer
{
    @Inject
    private static Provider<OrganDao> daoProvider;

    @Inject
    private static Provider<UserOnlineInfo> userOnlineInfoProvider;

    public UserPropertyStorer()
    {
    }

    @Override
    public void save(String name, Object value) throws Exception
    {
        UserOnlineInfo userOnlineInfo = userOnlineInfoProvider.get();
        if (userOnlineInfo != null)
            daoProvider.get().saveUserPropertyValue(userOnlineInfo.getUserId(), name, DataConvert.toString(value));
    }

    @Override
    public <T> T load(String name, Class<T> type) throws Exception
    {
        UserOnlineInfo userOnlineInfo = userOnlineInfoProvider.get();
        if (userOnlineInfo != null)
        {
            String s = daoProvider.get().getUserPropertyValue(userOnlineInfo.getUserId(), name);

            if (s != null)
                return DataConvert.convertValue(type, s);
        }

        return null;
    }
}
