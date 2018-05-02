package com.gzzm.safecampus.campus.wx.menu;

import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * @author Neo
 * @date 2018/4/8 11:53
 */
public abstract class MenuDao extends GeneralDao
{
    public MenuDao()
    {
    }

    public Menu getRoot() throws Exception
    {
        Menu root = load(Menu.class, 0);
        if (root == null)
        {
            root = new Menu();
            root.setMenuId(0);
            root.setMenuName("根节点");
            add(root);
        }
        return root;
    }

    @OQL("select s from com.gzzm.safecampus.campus.wx.menu.Menu s where s.groupId=:1 and s.parentMenuId=:2 order by s.orderId")
    public abstract List<Menu> getChildMenu(Integer groupId, Integer parentMenuId) throws Exception;
}
