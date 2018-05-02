package com.gzzm.platform.desktop;

import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

/**
 * 桌面相关的数据库访问对象
 *
 * @author camel
 * @date 2009-10-5
 */
public abstract class DesktopDao extends GeneralDao
{
    public DesktopDao()
    {
    }

    public UserDesktopConfig getDesktopStyle(Integer userId, String groupId) throws Exception
    {
        return load(UserDesktopConfig.class, userId, groupId);
    }

    @OQL("select u.stylePath from UserDesktopConfig u where u.userId=:1 and (u.groupId='desktop' or u.groupId is null)")
    public abstract String getStylePath(Integer userId) throws Exception;

    @OQL("select u.autoReload from UserDesktopConfig u where u.userId=:1 and (u.groupId='desktop' or u.groupId is null)")
    public abstract Boolean getAutoReload(Integer userId) throws Exception;
}
