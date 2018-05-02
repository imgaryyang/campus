package com.gzzm.safecampus.campus.base;

import com.gzzm.platform.commons.crud.BaseQueryCrud;
import com.gzzm.platform.commons.crud.SelectableListView;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

/**
 * 与部门相关的列表控件，用于左侧过滤数据
 *
 * @author Neo
 * @date 2018/3/26 18:07
 */
public abstract class BaseDeptQueryCrud<E extends BaseBean, K> extends BaseQueryCrud<E, K>
{
    @Inject
    protected static Provider<UserOnlineInfo> userOnlineInfoProvider;

    public BaseDeptQueryCrud()
    {
        addOrderBy("orderId");
    }

    public Integer getDeptId()
    {
        return userOnlineInfoProvider.get().getDeptId();
    }

    @Override
    protected void afterQuery() throws Exception
    {
        E root = getRoot();
        if (root != null)
            getList().add(0, root);
    }

    @Override
    protected Object createListView() throws Exception
    {
        return new SelectableListView();
    }

    protected abstract E getRoot() throws Exception;

}
