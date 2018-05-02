package com.gzzm.platform.workday;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.*;
import net.cyan.crud.view.components.CTextArea;

import java.sql.Date;

/**
 * 工作日维护
 *
 * @author camel
 * @date 2010-5-26
 */
@Service(url = "/workday")
public class WorkDayCrud extends BaseNormalCrud<WorkDay, Integer>
{
    @Lower(column = "workDayDate")
    private Date date_start;

    @Upper(column = "workDayDate")
    private Date date_end;

    public WorkDayCrud()
    {
        setLog(true);
        addOrderBy("workDayDate", OrderType.desc);
    }

    public Date getDate_start()
    {
        return date_start;
    }

    public void setDate_start(Date date_start)
    {
        this.date_start = date_start;
    }

    public Date getDate_end()
    {
        return date_end;
    }

    public void setDate_end(Date date_end)
    {
        this.date_end = date_end;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addComponent("日期", "date_start", "date_end");

        view.addColumn("日期", "workDayDate");
        view.addColumn("星期", new FieldCell("workDayDate", "E").setOrderable(false)).setAlign(Align.center);
        view.addColumn("类型", "type");
        view.addColumn("说明", "remark").setAutoExpand(true);

        view.defaultInit(false);

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("日期", "workDayDate");
        view.addComponent("类型", "type");
        view.addComponent("说明", new CTextArea("remark"));

        view.addDefaultButtons();

        return view;
    }

    @Override
    public void afterChange() throws Exception
    {
        WorkDay.setUpdated();
    }
}
