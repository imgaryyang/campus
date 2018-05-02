package com.gzzm.safecampus.campus.base;

import com.gzzm.platform.commons.components.EntityPageListModel;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

/**
 * @author Neo
 * @date 2018/3/26 20:36
 */
public class DeptOwnedEntityPageList<E extends BaseBean, K> extends EntityPageListModel<E, K>
{
    @Inject
    protected static Provider<UserOnlineInfo> userOnlineInfoProvider;

    public DeptOwnedEntityPageList()
    {
        addOrderBy("orderId");
    }

    public Integer getDeptId()
    {
        return userOnlineInfoProvider.get().getDeptId();
    }
}
