package com.gzzm.oa.activite;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.view.FieldCell;
import net.cyan.nest.annotation.Inject;

import java.text.DecimalFormat;
import java.util.*;

/**
 * 活动统计
 *
 * @author xjz
 * @date 13-3-27
 */
@Service(url = "/oa/activite/statistics")
public class ActiviteStatisticsCrud extends BaseListCrud<ActiviteStatisticsCrud.Detail>
{
    @Inject
    private ActiviteService service;

    private Integer activiteBudgetId;

    private Integer year;

    public ActiviteStatisticsCrud()
    {
    }

    public Integer getActiviteBudgetId()
    {
        return activiteBudgetId;
    }

    public void setActiviteBudgetId(Integer activiteBudgetId)
    {
        this.activiteBudgetId = activiteBudgetId;
    }

    public Integer getYear()
    {
        return year;
    }

    public void setYear(Integer year)
    {
        this.year = year;
    }

    @Override
    protected void loadList() throws Exception
    {
        List<Activite> activites = new ArrayList<Activite>();
        //该年度已经发生活动的金额合计
        Double amountSum = 0.0;
        //该年度的资金余额
        Double amountBalance = 0.0;

        if (getYear() == null || getYear() == 0)
        {
            year = null;
        }
        activites = service.getDao().getActivitiesByYear(year);

        List<Detail> details = new ArrayList<Detail>();
        for (Activite activite : activites)
        {
            Detail detail = new Detail();
            detail.setActiviteId(activite.getActiviteId());
            detail.setTitle(activite.getTitle());
            detail.setAmount(activite.getActualAmount() == null ? 0 : activite.getActualAmount());
            detail.setActiviteBudgetId(activite.getActiviteBudgetId());
            detail.setBudgetYears(activite.getActiviteBudget().getBudgetYears());
            detail.setBudgetAmount(activite.getAmount());
            details.add(detail);
        }

        //该年度已经发生活动的金额合计
        for (Detail tmp : details)
        {
            amountSum += tmp.getAmount();
        }
        //该年度的资金余额
        amountBalance = activites.get(0).getActiviteBudget().getBudgetAmount() - amountSum;

        Detail detail1 = new Detail();
        detail1.setTitle("活动金额合计");
        detail1.setAmount(amountSum);
        if (getActiviteBudgetId() == null)
        {
            detail1.setBudgetYears(null);
        }
        else
        {
            detail1.setBudgetYears(activites.get(0).getActiviteBudget().getBudgetYears());
        }
        details.add(detail1);

        Detail detail2 = new Detail();
        detail2.setTitle("年度活动资金余额");
        if (getActiviteBudgetId() == null)
        {
            detail2.setAmount(service.getDao().sumActiviteAmount() - amountSum);
            detail2.setBudgetYears(null);
        }
        else
        {
            detail2.setAmount(amountBalance);
            detail2.setBudgetYears(activites.get(0).getActiviteBudget().getBudgetYears());
        }

        details.add(detail2);

        setList(details);
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addComponent("年度", "year");

        view.addColumn("活动标题", "title==null?'-':title").setWidth("600");
        view.addColumn("活动预算(元)", new FieldCell("budgetAmount==null?0:budgetAmount").
                setFormat("money")).setWidth("120");
        view.addColumn("实际活动经费(元)", new FieldCell("amount==null?0:amount").
                setFormat("money")).setWidth("130");
        view.addColumn("年度(年)", new FieldCell("budgetYears==null?'-':budgetYears").
                setUnit("年")).setWidth("110");

        view.defaultInit();
        return view;
    }

    //格式化
    @NotSerialized
    public String DoubleFormat(Double value)
    {
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        return df.format(value == null ? 0.0 : value);
    }

    public static class Detail
    {
        private Integer activiteId;
        private String title;
        //实际活动经费
        private Double amount;
        private Integer activiteBudgetId;
        private Integer budgetYears;
        //活动预算
        private Double budgetAmount;

        public Integer getActiviteId()
        {
            return activiteId;
        }

        public void setActiviteId(Integer activiteId)
        {
            this.activiteId = activiteId;
        }

        public String getTitle()
        {
            return title;
        }

        public void setTitle(String title)
        {
            this.title = title;
        }

        public Double getAmount()
        {
            return amount;
        }

        public void setAmount(Double amount)
        {
            this.amount = amount;
        }

        public Integer getActiviteBudgetId()
        {
            return activiteBudgetId;
        }

        public void setActiviteBudgetId(Integer activiteBudgetId)
        {
            this.activiteBudgetId = activiteBudgetId;
        }

        public Integer getBudgetYears()
        {
            return budgetYears;
        }

        public void setBudgetYears(Integer budgetYears)
        {
            this.budgetYears = budgetYears;
        }

        public Double getBudgetAmount()
        {
            return budgetAmount;
        }

        public void setBudgetAmount(Double budgetAmount)
        {
            this.budgetAmount = budgetAmount;
        }
    }
}
