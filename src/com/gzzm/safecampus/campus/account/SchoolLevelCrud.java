package com.gzzm.safecampus.campus.account;

import com.gzzm.platform.commons.crud.BaseNormalCrud;
import com.gzzm.platform.commons.crud.Buttons;
import com.gzzm.platform.commons.crud.PageTableView;
import com.gzzm.platform.commons.crud.SimpleDialogView;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;

/**
 * 学校等级管理
 *
 * @author Neo
 * @date 2018/4/9 9:45
 */
@Service(url = "/campus/account/schoollevel")
public class SchoolLevelCrud extends BaseNormalCrud<SchoolLevel, Integer>
{
    @Like
    private String levelName;

    public SchoolLevelCrud()
    {
    }

    public String getLevelName()
    {
        return levelName;
    }

    public void setLevelName(String levelName)
    {
        this.levelName = levelName;
    }

    @Override
    public String getDeleteTagField()
    {
        return "deleteTag";
    }

    @Override
    public String getOrderField()
    {
        return "orderId";
    }

    @Override
    public int getOrderFieldLength()
    {
        return 3;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();
        view.addComponent("学校级别", "levelName");
        view.addColumn("学校级别", "levelName");
        view.defaultInit(false);
        view.addButton(Buttons.sort());
        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();
        view.addComponent("学校级别", "levelName", true);
        view.addDefaultButtons();
        return view;
    }
}
