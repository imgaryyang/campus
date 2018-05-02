package com.gzzm.ods.stat;

import com.gzzm.ods.exchange.*;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.util.JoinType;

/**
 * 全局的收文统计，同时统计多个部门
 *
 * @author camel
 * @date 12-12-12
 */
@Service(url = "/od/stat/global/receive")
public class GlobalReceiveStat extends OdGlobalStat
{
    private ReceiveType type;

    public GlobalReceiveStat()
    {
    }

    public ReceiveType getType()
    {
        return type;
    }

    public void setType(ReceiveType type)
    {
        this.type = type;
    }

    @Override
    protected void initStats() throws Exception
    {
        super.initStats();

        join(ReceiveBase.class, "receive", "receive.deptId=dept.deptId and receive.type=?type and " +
                "receive.sendTime>=?time_start and receive.sendTime<?time_end", JoinType.left);

        addStat("receiveCount", "count(receive.state<4)");
        addStat("noAcceptedCount", "count(receive.state=0)");
        addStat("acceptedCount", "count(receive.state>0 and receive.state<4)");
    }

    @Override
    protected void initView(PageTreeTableView view) throws Exception
    {
        view.addComponent("来文时间", "time_start", "time_end");
        view.addComponent("部门", "topDeptIds");

        view.addColumn("部门名称", "deptName").setWidth("400");
        view.addColumn("收文数", "receiveCount").setWidth("120");
        view.addColumn("未接收数", "noAcceptedCount").setWidth("120");
        view.addColumn("已接收数", "acceptedCount").setWidth("120");

        view.defaultInit();
        view.addButton(Buttons.export("xls"));
    }
}
