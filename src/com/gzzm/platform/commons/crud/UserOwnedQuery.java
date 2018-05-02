package com.gzzm.platform.commons.crud;

import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 2015/7/15
 */
public class UserOwnedQuery<E, K> extends BaseQueryCrud<E, K>
{
    @Inject
    protected UserOnlineInfo userOnlineInfo;

    public UserOwnedQuery()
    {
    }

    @NotSerialized
    public Integer getUserId()
    {
        if (userOnlineInfo == null)
            return null;
        return userOnlineInfo.getUserId();
    }
}
