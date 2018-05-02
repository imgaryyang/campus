package com.gzzm.platform.menu;

import java.util.List;

/**
 * 动态加载菜单
 *
 * @author camel
 * @date 2016/6/12
 */
public interface MenuLoader
{
    public List<MenuItem> load(MenuItem menuItem) throws Exception;

    public MenuItem load(MenuItem menuItem, String subMenuId) throws Exception;

    public List<MenuAuth> getAuths() throws Exception;
}