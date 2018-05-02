package com.gzzm.platform.organ;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;

/**
 * 用户统计
 *
 * @author camel
 * @date 11-8-30
 */
@Service(url = "/UserStat")
public class BaseUserStat extends UserStat
{
    public BaseUserStat()
    {
    }

    @Override
    protected void initStats() throws Exception
    {
        super.initStats();

        addStat("roleCount", "count(select 1 from UserRole r where r.userId=u.userId and role.type=0)");
        addStat("roleGroupCount", "count(select 1 from UserRole r where r.userId=u.userId and role.type=1)");
        addStat("stationCount", "count(u.stations)");
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addComponent("用户名", "name");
        view.addComponent("所属部门", "topDeptIds");

        view.addColumn("用户名", "u.userName").setWidth("140");
        view.addColumn("所属部门", "u.allSimpleDeptName()");
        view.addColumn("权限数", "roleCount");
        view.addColumn("角色数", "roleGroupCount");
        view.addColumn("岗位数", "stationCount");

        view.defaultInit();

        return view;
    }
}
