package com.gzzm.ods.stat;

import com.gzzm.ods.business.*;
import com.gzzm.ods.flow.OdFlowInstance;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.NotCondition;
import net.cyan.crud.util.JoinType;
import net.cyan.crud.view.chart.*;
import net.cyan.crud.view.components.CCombox;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 内部流程统计
 *
 * @author camel
 * @date 12-2-23
 */
@Service(url = "/od/stat/instance")
public class OdInstanceStat extends OdStat<OdFlowInstance>
{
    @Inject
    private UserOnlineInfo userOnlineInfo;

    @Inject
    private BusinessService businessService;

    /**
     * 对应的业务列表，表示只统计这些业务，由于此id用于接收url参数，所以不使用复数
     */
    @NotCondition
    private Integer[] businessId;

    private String businessType;

    private BusinessTag businessTag;

    private InstanceStatType statType;

    private List<BusinessModel> businesses;

    public OdInstanceStat()
    {
    }

    public Integer[] getBusinessId()
    {
        return businessId;
    }

    public void setBusinessId(Integer[] businessId)
    {
        this.businessId = businessId;
    }

    public String getBusinessType()
    {
        return businessType;
    }

    public void setBusinessType(String businessType)
    {
        this.businessType = businessType;
    }

    public BusinessTag getBusinessTag()
    {
        return businessTag;
    }

    public void setBusinessTag(BusinessTag businessTag)
    {
        this.businessTag = businessTag;
    }

    public InstanceStatType getStatType()
    {
        return statType;
    }

    public void setStatType(InstanceStatType statType)
    {
        this.statType = statType;
    }

    private List<BusinessModel> getBusinesses() throws Exception
    {
        if (businesses == null && (businessId == null || businessId.length > 1))
        {
            if (businessId != null)
            {
                businesses = dao.getBusinesses(businessId);
            }
            else if (businessType != null)
            {
                businesses = businessService.getSelectableBusinesses(getDeptId(), new String[]{businessType},
                        businessTag, userOnlineInfo.getAuthDeptIds(BusinessListPage.OD_BUSINESS_SELECT_APP));

                businessId = new Integer[businesses.size()];

                int index = 0;
                for (BusinessModel business : businesses)
                {
                    businessId[index++] = business.getBusinessId();
                }
            }
        }
        return businesses;
    }

    @Override
    protected java.util.Date getDefaultYearStartTime() throws Exception
    {
        return dao.getMinInstanceTime(getDeptId());
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        if (statType == InstanceStatType.month && time_start == null)
        {
            time_start = new java.sql.Date(getDefaultMonthStartTime().getTime());
        }
    }

    @Override
    protected void initStats() throws Exception
    {
        List<BusinessModel> businesses = getBusinesses();

        if (getViewName() == null || "exp".equals(getAction()))
            setLoadTotal(true);

        switch (statType)
        {
            case user:
                setGroupField("creator");

                addStat("userId", "nvl(creator,0)", "-1");
                addStat("userName", "min(createUser.userName)", "'合计'");

                break;

            case dept:
                setGroupField("createDeptId");

                addStat("deptId", "nvl(createDeptId,0)", "-1");
                addStat("deptName", "min(createDept.deptName)", "'合计'");

                break;
            case month:
                join(":months", "m", "startTime>=m.startTime and startTime<m.endTime and deptId=:deptId" +
                                " and businessId in :businessId",
                        JoinType.right).setTotal(false);

                setGroupField("m.startTime");

                addStat("startTime", "m.startTime", "null");
                addStat("monthName", "min(m.monthName)", "'合计'");
                addStat("simpleName", "min(m.simpleName)", "''");
                break;

            case year:
                join(":years", "y", "startTime>=y.startTime and startTime<y.endTime and deptId=:deptId" +
                                " and businessId in :businessId",
                        JoinType.right).setTotal(false);

                setGroupField("y.startTime");

                addStat("startTime", "y.startTime", "null");
                addStat("yearName", "min(y.yearName)", "'合计'");
                addStat("simpleName", "min(y.simpleName)", "''");
                break;
        }

        if (businesses == null || businesses.size() == 1)
        {
            addStat("count", "count(instanceId)");
        }
        else
        {
            for (BusinessModel business : businesses)
            {
                addStat("count_" + business.getBusinessId(), "count(businessId='" + business.getBusinessId() + "')");
            }
        }

        switch (statType)
        {
            case user:
                addOrderBy(
                        "first(select dept.leftValue from UserDept d where d.userId=createUser.userId order by userOrder limit 1)");
                addOrderBy(
                        "first(select orderId from UserDept d where d.userId=createUser.userId order by userOrder limit 1)");
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
        if (total || statType != InstanceStatType.month && statType != InstanceStatType.year)
        {
            return "startTime>=?time_start and startTime<?time_end and deptId=:deptId and businessId in :businessId and state<2";
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
                case user:
                {
                    view.setType(ChartType.column);
                    view.setKey("userId");
                    view.setName("userName");

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
                    axis.setInterval(10);
                    view.setYAxis(axis);

                    break;
                }
            }
            view.addSerie("数量", "count");

            return view;
        }
        else
        {
            List<BusinessModel> businesses = getBusinesses();

            Collection<Integer> authDeptIds = getAuthDeptIds();
            boolean showDeptTree = authDeptIds == null || authDeptIds.size() > 3;

            PageTableView view = showDeptTree ? new ComplexTableView(new AuthDeptDisplay(), "deptId", false) :
                    new PageTableView(false);

            view.addComponent("发文时间", "time_start", "time_end");

            if (!showDeptTree && authDeptIds.size() > 1)
                view.addComponent("部门", new CCombox("deptId").setNullable(false));

            switch (statType)
            {
                case user:
                    view.addColumn("用户", "userName").setWidth("100");
                    break;

                case dept:
                    view.addColumn("发起科室", "deptName").setWidth("250");
                    break;

                case month:
                    view.addColumn("月度", "monthName").setOrderFiled("m.startTime").setWidth("250");
                    break;

                case year:
                    view.addColumn("年度", "yearName").setOrderFiled("y.startTime").setWidth("250");
                    break;
            }

            if (businesses == null || businesses.size() == 1)
            {
                view.addColumn("数量", "count");

                if (statType != InstanceStatType.user)
                    view.setSubView("chart");
            }
            else
            {
                for (BusinessModel business : businesses)
                {
                    view.addColumn(business.getBusinessName(), "count_" + business.getBusinessId());
                }
            }

            view.defaultInit();
            view.addButton(Buttons.export("xls"));

            if (businesses == null || businesses.size() == 1)
            {
                if (statType == InstanceStatType.dept)
                    view.addButton(Buttons.showPieChart());
                else if (statType == InstanceStatType.month || statType == InstanceStatType.year)
                    view.addButton(Buttons.showBarChart());
            }

            return view;
        }
    }
}
