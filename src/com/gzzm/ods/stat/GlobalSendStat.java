package com.gzzm.ods.stat;

import com.gzzm.ods.exchange.Send;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.util.JoinType;

/**
 * 全局的发文统计，同时统计多个部门
 *
 * @author camel
 * @date 12-12-12
 */
@Service(url = "/od/stat/global/send")
public class GlobalSendStat extends OdGlobalStat
{
    public GlobalSendStat()
    {
    }

    @Override
    protected void initStats() throws Exception
    {
        super.initStats();

        join(Send.class, "send", "send.deptId=dept.deptId and send.sendTime>=?time_start and send.sendTime<?time_end",
                JoinType.left);

        addStat("sendCount", "count(send.state=0)");
    }

    @Override
    protected void initView(PageTreeTableView view) throws Exception
    {
        view.addComponent("发文时间", "time_start", "time_end");
        view.addComponent("部门", "topDeptIds");

        view.addColumn("部门名称", "deptName").setWidth("600");
        view.addColumn("发文数", "sendCount").setWidth("120");

        view.defaultInit();
        view.addButton(Buttons.export("xls"));
    }
}
