package com.gzzm.platform.commons;

import com.gzzm.platform.menu.Menu;
import com.gzzm.platform.organ.*;
import net.cyan.commons.test.*;
import net.cyan.commons.util.Null;
import net.cyan.thunwind.dao.GeneralDao;

/**
 * 系统数据初始化
 *
 * @author camel
 * @date 2010-4-27
 */
public abstract class SysDataInitDao extends GeneralDao
{
    public SysDataInitDao()
    {
    }

    @TestCase
    public void init() throws Exception
    {
        Dept dept = load(Dept.class, 1);
        if (dept == null)
        {
            dept = new Dept();
            dept.setDeptId(1);
            dept.setDeptName("平台管理");
            dept.setState((byte) 0);
            dept.setDeptLevel((byte) 0);
            dept.setLeftValue(0);
            dept.setRightValue(1);
            add(dept);
        }
        else if (dept.getParentDeptId() != null)
        {
            dept.setParentDeptId(Null.Integer);
            update(dept);
        }

        User user = load(User.class, 1);
        if (user == null)
        {
            user = new User();
            user.setUserId(1);
            user.setLoginName("admin");
            user.setUserName("平台管理员");
            user.setPassword("123456");
            user.setAdminUser(true);
            user.setLoginType(LoginType.auto);
            user.setState((byte) 0);
            user.setDeptDataType(0);
            add(user);

            UserDept userDept = new UserDept(user.getUserId(), dept.getDeptId());
            userDept.setOrderId(0);
            add(userDept);
        }

        Menu root = load(Menu.class, "oa");

        if (root == null)
        {
            root = new Menu();
            root.setParentMenuId(null);
            root.setMenuId("oa");
            root.setMenuTitle("OA系统");
            add(root);

            Menu menu = new Menu();
            menu.setParentMenuId("oa");
            menu.setMenuTitle("系统管理");
            add(menu);

            Menu menu1 = new Menu();
            menu1.setParentMenuId(menu.getMenuId());
            menu1.setUrl("/MenuCrud?group=oa");
            menu1.setMenuTitle("OA菜单管理");
            add(menu1);
        }
    }

    public static void main(String[] args)
    {
        TestRunner.run();
    }
}
