package com.gzzm.safecampus.campus.account;

import com.gzzm.platform.commons.crud.Buttons;
import com.gzzm.platform.commons.crud.ComplexTableView;
import com.gzzm.platform.commons.crud.PageTableView;
import com.gzzm.platform.commons.crud.SimpleDialogView;
import com.gzzm.safecampus.campus.base.BaseCrud;
import com.gzzm.safecampus.campus.classes.Grade;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.Null;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.annotation.NotCondition;

/**
 * 各个等级的学校的年级管理
 *
 * @author Neo
 * @date 2018/4/9 10:27
 */
@Service(url = "/campus/account/grade")
public class GradeCrud extends BaseCrud<Grade, Integer>
{
    @NotCondition
    private Integer levelId;

    /**
     * 年级查询条件
     */
    @Like
    private String gradeName;

    public GradeCrud()
    {
        addOrderBy("orderId");
    }

    public Integer getLevelId()
    {
        return levelId;
    }

    public void setLevelId(Integer levelId)
    {
        this.levelId = levelId;
    }

    public String getGradeName()
    {
        return gradeName;
    }

    public void setGradeName(String gradeName)
    {
        this.gradeName = gradeName;
    }

    @Override
    public void initEntity(Grade entity) throws Exception
    {
        super.initEntity(entity);
        entity.setLevelId(levelId);
    }

    @Override
    public String getOrderField()
    {
        return "orderId";
    }

    @Override
    public String getDeleteTagField()
    {
        return "deleteTag";
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        if (Null.isNull(levelId) || levelId == 0)
        {
            return null;
        }
        return "levelId=?levelId";
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new ComplexTableView(new SchoolLevelDisplay(), "levelId");
        view.addComponent("年级", "gradeName");
        view.addColumn("年级", "gradeName");
        view.defaultInit(false);
        view.addButton(Buttons.sort());
        view.importJs("/safecampus/campus/account/grade.js");
        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();
        view.addComponent("年级", "gradeName", true);
        view.addDefaultButtons();
        return view;
    }
}
