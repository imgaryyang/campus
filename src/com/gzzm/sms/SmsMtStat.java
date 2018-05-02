package com.gzzm.sms;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.*;
import net.cyan.crud.exporters.ExportParameters;

import java.sql.Date;

/**
 * @author camel
 * @date 12-11-1
 */
@Service(url = "/sms/stat/mt")
public class SmsMtStat extends BaseStatCrud<SmsMt>
{
    @Lower(column = "sendTime")
    private Date time_start;

    @Upper(column = "sendTime")
    private Date time_end;

    @Like
    private String userName;

    public SmsMtStat()
    {
        setLoadTotal(true);
    }

    public Date getTime_start()
    {
        return time_start;
    }

    public void setTime_start(Date time_start)
    {
        this.time_start = time_start;
    }

    public Date getTime_end()
    {
        return time_end;
    }

    public void setTime_end(Date time_end)
    {
        this.time_end = time_end;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    @Override
    protected void initStats() throws Exception
    {
        setGroupField("userId");
        addOrderBy("userId");

        addStat("userName", "min(user.userName)", "'合计'");

        addStat("total", "count(gatewayType is not null)");

        for (GatewayType gatewayType : GatewayType.values())
        {
            if (gatewayType != GatewayType.common)
            {
                addStat("count_" + gatewayType.name(), "count(gatewayType=" + gatewayType.ordinal() + ")");
            }
        }
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(false);

        view.addComponent("发送时间", "time_start", "time_end");
        view.addComponent("用户", "userName");

        view.addColumn("用户", "userName");

        for (GatewayType gatewayType : GatewayType.values())
        {
            if (gatewayType != GatewayType.common)
            {
                view.addColumn(gatewayType.toString(), "count_" + gatewayType.name());
            }
        }

        view.addColumn("合计", "total");

        view.defaultInit();
        view.addButton(Buttons.export("xls"));

        return view;
    }

    @Override
    protected ExportParameters getExportParameters() throws Exception
    {
        return new ExportParameters("短信发送统计");
    }
}
