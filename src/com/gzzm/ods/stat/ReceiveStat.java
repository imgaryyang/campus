package com.gzzm.ods.stat;

import com.gzzm.ods.exchange.Receive;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.menu.MenuItem;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.KeyValue;
import net.cyan.crud.OrderType;
import net.cyan.crud.exporters.ExportParameters;
import net.cyan.crud.util.JoinType;
import net.cyan.crud.view.HrefCell;
import net.cyan.crud.view.chart.*;
import net.cyan.crud.view.components.CCombox;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.Collection;

/**
 * 收文统计
 *
 * @author camel
 * @date 11-11-18
 */
@Service(url = "/od/stat/receive")
public class ReceiveStat extends OdStat<Receive>
{
    @Inject
    private MenuItem menuItem;

    private ReceiveStatType statType;

    /**
     * 有无发文号
     * 1：有
     * 2：无
     */
    private Integer sendNumber;

    public ReceiveStat()
    {
    }

    public ReceiveStatType getStatType()
    {
        return statType;
    }

    public void setStatType(ReceiveStatType statType)
    {
        this.statType = statType;
    }

    public Integer getSendNumber() {
        return sendNumber;
    }

    public void setSendNumber(Integer sendNumber) {
        this.sendNumber = sendNumber;
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        if(statType == ReceiveStatType.month && time_start == null)
        {
            time_start = new Date(getDefaultMonthStartTime().getTime());
        }
    }

    @Override
    protected java.util.Date getDefaultYearStartTime() throws Exception
    {
        return dao.getMinReceiveTime(getDeptId());
    }

    @Override
    protected void initStats() throws Exception
    {
        if(getViewName() == null || "exp".equals(getAction()))
            setLoadTotal(true);

        switch (statType)
        {
            case type:
                setGroupField("receiveTypeId");

                addStat("typeId", "nvl(receiveTypeId,-1)", "0");
                addStat("typeName", "nvl(min(receiveType.receiveTypeName),'其他')", "'合计'");

                break;
            case month:
                join(":months", "m", "receiveBase.sendTime>=m.startTime and receiveBase.sendTime<m.endTime and" +
                        " receiveBase.deptId=:deptId and receiveBase.state<4", JoinType.right).setTotal(false);

                setGroupField("m.startTime");

                addStat("startTime", "m.startTime", "null");
                addStat("monthName", "min(m.monthName)", "'合计'");
                addStat("simpleName", "min(m.simpleName)", "''");

                break;

            case year:
                join(":years", "y", "receiveBase.sendTime>=y.startTime and receiveBase.sendTime<y.endTime and" +
                        " receiveBase.deptId=:deptId and receiveBase.state<4", JoinType.right).setTotal(false);

                setGroupField("y.startTime");

                addStat("startTime", "y.startTime", "null");
                addStat("yearName", "min(y.yearName)", "'合计'");
                addStat("simpleName", "min(y.simpleName)", "''");

                break;

            case dept:
                setGroupField("dealDeptId");

                addStat("deptId", "nvl(dealDeptId,0)", "-1");
                addStat("deptName", "min(dealDept.deptName)", "'合计'");

                break;
        }

        addStat("count", "count(receiveId)");

        switch (statType)
        {
            case type:
                addOrderBy("min(receiveType.dept.leftValue)", OrderType.desc);
                addOrderBy("min(receiveType.orderId)");
                break;
            case month:
                addOrderBy("m.startTime");
                break;
            case year:
                addOrderBy("y.startTime");
                break;
            case dept:
                addOrderBy("min(dealDept.leftValue)");
                break;
        }

        if("chart".equals(getViewName()) && !"exp".equals(getAction()))
        {
            setOrderBy(null);
        }
    }

    @Override
    protected String getComplexCondition(boolean total) throws Exception
    {
        if(total || statType == ReceiveStatType.type) {
            String oql="";
            if(sendNumber!=null){
                switch (sendNumber){
                    case 1:
                        oql+="receiveBase.document.sendNumber is not null and ";
                        break;
                    case 2:
                        oql+="receiveBase.document.sendNumber is null and ";
                        break;
                }
            }
             oql+="receiveBase.sendTime>=?time_start and receiveBase.sendTime<?time_end and receiveBase.deptId=:deptId and receiveBase.state<4";
            return oql;
        }

        return null;
    }

    @Override
    protected Object createListView() throws Exception
    {
        if("chart".equals(getViewName()) && !"exp".equals(getAction()))
        {
            ChartView view = new ChartView();

            switch (statType)
            {
                case type:
                {
                    view.setType(ChartType.pie);
                    view.setKey("typeId");
                    view.setName("typeName");

                    break;
                }
                case month:
                {
                    view.setType(ChartType.column);
                    view.setKey("startTime");
                    view.setName("simpleName");

                    Axis axis = new Axis();
                    axis.setInterval(40);
                    view.setYAxis(axis);

                    break;
                }
                case year:
                {
                    view.setType(ChartType.column);
                    view.setKey("startTime");
                    view.setName("simpleName");

                    break;
                }
                case dept:
                {
                    view.setType(ChartType.pie);
                    view.setKey("deptId");
                    view.setName("deptName");

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

            view.addComponent("来文时间", "time_start", "time_end");
            view.addComponent("发文编号", new CCombox("sendNumber", stateKeyValue()));

            if(!showDeptTree && authDeptIds.size() > 1)
                view.addComponent("收文部门", new CCombox("deptId").setNullable(false));

            switch (statType)
            {
                case type:
                    view.addColumn("收文类型", new HrefCell("typeName").setAction("queryReceive(${typeId})"))
                            .setWidth("250");
                    break;

                case month:
                    view.addColumn("月度", new HrefCell("monthName").setAction("queryReceive('${startTime}')"))
                            .setWidth("250").setOrderFiled("m.startTime");
                    break;

                case year:
                    view.addColumn("年度", new HrefCell("yearName").setAction("queryReceive('${startTime}')"))
                            .setWidth("250").setOrderFiled("year.startTime");
                    break;

                case dept:
                    view.addColumn("承办科室", new HrefCell("deptName").setAction("queryReceive(${deptId})"))
                            .setWidth("250");
                    break;
            }


            view.addColumn("收文数量", "count");
            view.setSubView("chart");

            view.defaultInit();
            view.addButton(Buttons.export("xls"));

            if(statType == ReceiveStatType.type)
                view.addButton(Buttons.showPieChart());
            else
                view.addButton(Buttons.showBarChart());

            view.importJs("/ods/stat/receive.js");

            return view;
        }
    }

    @Override
    protected ExportParameters getExportParameters() throws Exception
    {
        return new ExportParameters(menuItem == null ? "收文统计" : menuItem.getTitle());
    }

    private Object[] stateKeyValue() {
        return new Object[]{new KeyValue<String>("1", "有"), new KeyValue<String>("2", "无")};
    }
}