package com.gzzm.oa.activite;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.view.FieldCell;

/**
 * 年度活动预算管理
 *
 * @author xjz
 * @date 13-3-25
 */
@Service(url = "/oa/activite/budget")
public class ActiviteBudgetCrud extends DeptOwnedNormalCrud<ActiviteBudget, Integer>
{
    /**
     * 年份
     */
    private Integer budgetYears;

    public ActiviteBudgetCrud()
    {
    }

    public Integer getBudgetYears()
    {
        return budgetYears;
    }

    public void setBudgetYears(Integer budgetYears)
    {
        this.budgetYears = budgetYears;
    }

    @Override
    public String getOrderField()
    {
        return "budgetYears";
    }

    @Override
    public int getOrderFieldLength()
    {
        return 4;
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        //保存发起部门
        setDefaultDeptId();
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = getAuthDeptIds() == null || getAuthDeptIds().size() > 1 ?
                new ComplexTableView(new AuthDeptDisplay(), "deptId", true) : new PageTableView(true);

        view.addComponent("年份", "budgetYears");

        view.addColumn("年份(年)", new FieldCell("budgetYears").setUnit("年"));
        view.addColumn("活动总经费(元)", new FieldCell("budgetAmount").setFormat("money"));

        view.defaultInit();

        view.importJs("/oa/activite/activitebudget.js");

        return view;
    }


    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();
        view.addComponent("年份(年)", "budgetYears");
        view.addComponent("活动总经费(元)", "budgetAmount");
        view.addDefaultButtons();
        return view;
    }

}
