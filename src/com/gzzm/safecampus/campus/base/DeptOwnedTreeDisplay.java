package com.gzzm.safecampus.campus.base;

import com.gzzm.platform.commons.crud.BaseTreeDisplay;
import com.gzzm.platform.commons.crud.SelectableTreeView;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

/**
 * @author Neo
 * @date 2018/3/26 19:27
 */
public abstract class DeptOwnedTreeDisplay<E, K> extends BaseTreeDisplay<E, K>
{
    @Inject
    protected static Provider<UserOnlineInfo> userOnlineInfoProvider;

    public DeptOwnedTreeDisplay()
    {
    }

    public Integer getDeptId()
    {
        return userOnlineInfoProvider.get().getDeptId();
    }

    @Override
    protected Object createTreeView() throws Exception
    {
        return new SelectableTreeView();
    }
}
