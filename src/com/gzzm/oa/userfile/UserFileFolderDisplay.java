package com.gzzm.oa.userfile;

import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.commons.crud.*;
import net.cyan.crud.*;
import net.cyan.nest.annotation.Inject;

/**
 * @author wmy
 */
public class UserFileFolderDisplay extends BaseTreeDisplay<UserFileFolder, Integer>
{
    @Inject
    private UserFileDao dao;

    /**
     * 用户ID
     */
    @UserId
    private Integer userId;

    public UserFileFolderDisplay()
    {
        addOrderBy(new OrderBy("orderId", OrderType.asc));
    }

    @Override
    public UserFileFolder getRoot() throws Exception
    {
        return dao.getRootFolder();
    }

    @Override
    protected Object createTreeView() throws Exception
    {
        return new SelectableTreeView();
    }

    public Integer getUserId()
    {
        return userId;
    }

    @Override
    public boolean supportSearch() throws Exception
    {
        return true;
    }
}
