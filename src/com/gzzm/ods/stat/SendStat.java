package com.gzzm.ods.stat;

import com.gzzm.ods.exchange.Send;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.menu.MenuItem;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.exporters.ExportParameters;
import net.cyan.crud.util.JoinType;
import net.cyan.crud.view.chart.*;
import net.cyan.crud.view.components.CCombox;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.Collection;

/**
 * 发文统计
 *
 * @author camel
 * @date 12-2-22
 */
@Service(url = "/od/stat/send")
public class SendStat extends OdStat<Send>
{
    @Inject
    private MenuItem menuItem;

    private SendStatType statType;

    public SendStat()
    {
    }

    public SendStatType getStatType()
    {
        return statType;
    }

    public void setStatType(SendStatType statType)
    {
        this.statType = statType;
    }

    @Override
    protected java.util.Date getDefaultYearStartTime() throws Exception
    {
        return dao.getMinSendTime(getDeptId());
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        if (statType == SendStatType.month && time_start == null)
        {
            time_start = new Date(getDefaultMonthStartTime().getTime());
        }
    }

    @Override
    protected void initStats() throws Exception
    {
        if (getViewName() == null || "exp".equals(getAction()))
            setLoadTotal(true);

        switch (statType)
        {
            case redHead:
                setGroupField("redHeadId");

                addStat("redHeadId", "nvl(redHeadId,0)", "-1");
                addStat("redHeadName", "nvl(min(redHead.redHeadName),'其他')", "'合计'");

                break;
            case sendNumber:
                setGroupField("sendNumberId");

                addStat("sendNumberId", "nvl(sendNumberId,0)", "-1");
                addStat("sendNumberName", "nvl(min(sendNumber.sendNumberName),'其他')", "'合计'");

                break;
            case dept:
                setGroupField("createDeptId");

                addStat("deptId", "nvl(createDeptId,0)", "-1");
                addStat("deptName", "min(createDept.deptName)", "'合计'");

                break;
            case month:
                join(":months", "m", "sendTime>=m.startTime and sendTime<m.endTime and deptId=:deptId and state=0",
                        JoinType.right).setTotal(false);

                setGroupField("m.startTime");

                addStat("startTime", "m.startTime", "null");
                addStat("monthName", "min(m.monthName)", "'合计'");
                addStat("simpleName", "min(m.simpleName)", "''");
                break;

            case year:
                join(":years", "y", "sendTime>=y.startTime and sendTime<y.endTime and deptId=:deptId and state=0",
                        JoinType.right).setTotal(false);

                setGroupField("y.startTime");

                addStat("startTime", "y.startTime", "null");
                addStat("yearName", "min(y.yearName)", "'合计'");
                addStat("simpleName", "min(y.simpleName)", "''");
                break;
        }

        addStat("count", "count(sendId)");

        switch (statType)
        {
            case redHead:
                addOrderBy("min(redHead.orderId)");
                break;
            case sendNumber:
                addOrderBy("min(sendNumber.orderId)");
                break;
            case dept:
                addOrderBy("min(createDept.leftValue)");
                break;
            case month:
                addOrderBy("m.startTime");
                break;
            case year:
                addOrderBy("y.startTime");
                break;
        }

        if ("chart".equals(getViewName()) && !"exp".equals(getAction()))
        {
            setOrderBy(null);
        }
    }

    @Override
    protected String getComplexCondition(boolean total) throws Exception
    {
        if (total || statType != SendStatType.month && statType != SendStatType.year)
        {
            return "sendTime>=?time_start and sendTime<?time_end and deptId=:deptId and state=0";
        }

        return null;
    }

    @Override
    protected Object createListView() throws Exception
    {
        if ("chart".equals(getViewName()) && !"exp".equals(getAction()))
        {
            ChartView view = new ChartView();

            switch (statType)
            {
                case redHead:
                {
                    view.setType(ChartType.pie);
                    view.setKey("redHeadId");
                    view.setName("redHeadName");

                    break;
                }
                case sendNumber:
                {
                    view.setType(ChartType.pie);
                    view.setKey("sendNumberId");
                    view.setName("sendNumberName");

                    break;
                }
                case dept:
                {
                    view.setType(ChartType.pie);
                    view.setKey("deptId");
                    view.setName("deptName");

                    break;
                }
                case month:
                {
                    view.setType(ChartType.column);
                    view.setKey("startTime");
                    view.setName("simpleName");

                    Axis axis = new Axis();
                    axis.setInterval(10);
                    view.setYAxis(axis);

                    break;
                }
                case year:
                {
                    view.setType(ChartType.column);
                    view.setKey("startTime");
                    view.setName("simpleName");

                    Axis axis = new Axis();
                    axis.setInterval(400);
                    view.setYAxis(axis);

                    break;
                }
            }
            view.addSerie("数量", "count");

            return view;
        }
        else
        {
            Collection<Integer> authDeptIds = getAuthDeptIds();
            boolean showDeptTree = authDeptIds == null || authDeptIds.size() > 3;

            PageTableView view = showDeptTree ? new ComplexTableView(new AuthDeptDisplay(), "deptId", false) :
                    new PageTableView(false);

            view.addComponent("发文时间", "time_start", "time_end");

            if (!showDeptTree && authDeptIds.size() > 1)
                view.addComponent("发文部门", new CCombox("deptId").setNullable(false));

            switch (statType)
            {
                case redHead:
                    view.addColumn("红头", "redHeadName").setWidth("250");
                    break;

                case sendNumber:
                    view.addColumn("发文字号", "sendNumberName").setWidth("250");
                    break;

                case dept:
                    view.addColumn("拟稿科室", "deptName").setWidth("250");
                    break;

                case month:
                    view.addColumn("月度", "monthName").setOrderFiled("m.startTime").setWidth("250");
                    break;

                case year:
                    view.addColumn("年度", "yearName").setOrderFiled("y.startTime").setWidth("250");
                    break;
            }


            view.addColumn("发文数量", "count");
            view.setSubView("chart");

            view.defaultInit();
            view.addButton(Buttons.export("xls"));

            if (statType == SendStatType.month || statType == SendStatType.year)
                view.addButton(Buttons.showBarChart());
            else
                view.addButton(Buttons.showPieChart());

            return view;
        }
    }

    @Override
    protected ExportParameters getExportParameters() throws Exception
    {
        return new ExportParameters(menuItem == null ? "发文统计" : menuItem.getTitle());
    }
}
