package com.gzzm.safecampus.campus.account;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.safecampus.campus.base.BaseCrud;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.Align;
import net.cyan.crud.view.components.CButton;
import net.cyan.crud.view.components.ConditionComponent;
import net.cyan.nest.annotation.*;

/**
 * 学年管理
 *
 * @author yuanfang
 * @date 18-03-15 17:16
 */
@Service(url = "/campus/system/schoolyearcrud")
public class SchoolYearCrud extends BaseCrud<SchoolYear, Integer>
{
    @Inject
    SchoolYearDao schoolYearDao;

    private Boolean status;

    public Boolean getStatus()
    {
        return status;
    }

    public void setStatus(Boolean status)
    {
        this.status = status;
    }

    public SchoolYearCrud()
    {
    }

    @NotCondition
    @Override
    public Integer getSchoolYearId()
    {
        return super.getSchoolYearId();
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();
        view.addColumn("学年", "year").setWidth("600").setAlign(Align.center);

        view.addColumn("信息", new ConditionComponent()
                .add("status==true",
                        new CButton("当前学年", "')")
                                .setClass("thisYear"))).setWidth("200");

        view.addColumn("操作", new ConditionComponent()
                        .add("status!=true",
                                new CButton("设为当前学年", "switchSchoolYear_(${schoolYearId},${deptId})")
                                        .setClass("setYear"))

        ).setWidth("200");

        view.defaultInit(false);
        view.importCss("/safecampus/campus/schoolyear/year.css");
        view.importJs("/safecampus/campus/schoolyear/switch.js");
        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();
        view.addComponent("学年", "year");
        view.addDefaultButtons();
        return view;
    }

    @Override
    public boolean beforeSave() throws Exception
    {
        getEntity().setStatus(false);
        return super.beforeSave();
    }

    @Override
    public void afterChange() throws Exception
    {
        SchoolYear.setUpdated();
    }

    /**
     * 切换学年
     */
    @Service
    public void switchSchoolYear(Integer schoolYearId,Integer deptId) throws Exception
    {
        //将当前学年设置为非当前学年
//        schoolYearDao.switchSchoolYearStatus(getSchoolYearId(), 0);
//        schoolYearDao.switchSchoolYearStatus(schoolYearId, 1);
        schoolYearDao.switchSchoolYearStatusOff(deptId);
        schoolYearDao.switchSchoolYearStatusOn(schoolYearId);
        SchoolYear.setUpdated();
    }

    /**
     * 假刪除
     */
    @Override
    public String getDeleteTagField()
    {
        return "deleteTag";
    }
}
