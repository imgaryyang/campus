package com.gzzm.platform.organ;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;

/**
 * @author camel
 * @date 14-3-19
 */
@Service(url = "/UserLevelCrud")
public class UserLevelCrud extends BaseNormalCrud<UserLevel, Integer>
{
    @Like
    private String levelName;

    public UserLevelCrud()
    {
        setLog(true);
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
    public String getOrderField()
    {
        return "orderId";
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("级别名称", "levelName");

        view.addDefaultButtons();

        return view;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addComponent("级别名称", "levelName");

        view.addColumn("级别名称", "levelName");

        view.defaultInit();
        view.addButton(Buttons.sort());

        return view;
    }
}
