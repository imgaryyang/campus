package com.gzzm.platform.organ;

import com.gzzm.platform.commons.BaseConfig;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.exporters.ExportParameters;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * 系统一些基本信息分部门统计
 *
 * @author camel
 * @date 2010-7-13
 */
@Service(url = "/DeptStat")
public class BaseDeptStat extends DeptTreeStat
{
    @Inject
    private BaseConfig config;

    public BaseDeptStat()
    {
    }

    protected void initStats() throws Exception
    {
        super.initStats();

        addStat("deptCount", "count(*)");

        List<String> levels = config.getDeptLevels();
        if (levels != null)
        {
            int n = levels.size();
            for (int i = n - 1; i >= 0; i--)
            {
                addStat("deptCount_" + i, "count(deptLevel=" + i + ")");
            }
        }

        if (getCrudService().getDao().getDialect().startsWith("oracle"))
        {
            addStat("userCount", "min(count(select 1 from UserDept u where u.deptId=:key))",
                    "min(count(select distinct u.userId from UserDept u where u.deptId in :keys))");
        }
        else
        {
            addStat("userCount", "count(select 1 from UserDept u where u.deptId=:key)",
                    "count(select distinct u.userId from UserDept u where u.deptId in :keys)");
        }
    }

    protected void initView(PageTreeTableView view)
    {
        view.addComponent("部门", "topDeptIds");

        view.addColumn("部门名称", "deptName").setWidth("400");
        view.addColumn("部门数", "deptCount").setWidth("120");

        List<String> levels = config.getDeptLevels();
        if (levels != null)
        {
            int n = levels.size();
            for (int i = n - 1; i >= 0; i--)
            {
                view.addColumn(levels.get(i) + "数", "deptCount_" + i).setWidth("120");
            }
        }

        view.addColumn("用户数", "userCount").setWidth("120");

        view.defaultInit();
        view.addButton(Buttons.export("xls"));
    }

    @Override
    protected ExportParameters getExportParameters() throws Exception
    {
        return new ExportParameters("部门统计");
    }
}
