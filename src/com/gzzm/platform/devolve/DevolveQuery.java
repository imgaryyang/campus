package com.gzzm.platform.devolve;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.FieldCell;

import java.sql.Date;

/**
 * @author camel
 * @date 13-1-8
 */
@Service(url = "/platform/devolve/query")
public class DevolveQuery extends DeptOwnedQuery<Devolve, Integer>
{
    @Lower(column = "devolveTime")
    private Date time_start;

    @Upper(column = "devolveTime")
    private Date time_end;

    @Like("fromUser.userName")
    private String fromUser;

    @Like("toUser.userName")
    private String toUser;

    public DevolveQuery()
    {
        addOrderBy("devolveTime", OrderType.desc);
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

    public String getFromUser()
    {
        return fromUser;
    }

    public void setFromUser(String fromUser)
    {
        this.fromUser = fromUser;
    }

    public String getToUser()
    {
        return toUser;
    }

    public void setToUser(String toUser)
    {
        this.toUser = toUser;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(false);

        view.addComponent("移交时间", "time_start", "time_end");
        view.addComponent("移交人", "fromUser");
        view.addComponent("接收人", "toUser");

        view.addColumn("移交时间", "devolveTime");
        view.addColumn("移交人", "fromUser.userName");
        view.addColumn("接收人", "toUser.userName");
        view.addColumn("移交内容", new FieldCell("scopeNames").setOrderable(false)).setAutoExpand(true);

        view.addColumn("开始时间", "startTime");
        view.addColumn("结束时间", "endTime");
        view.addColumn("操作者", "user.userName");

        view.defaultInit();

        return view;
    }
}
