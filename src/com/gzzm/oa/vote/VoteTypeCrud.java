package com.gzzm.oa.vote;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;

/**
 * @author camel
 * @date 12-3-26
 */
@Service(url = "/oa/vote/VoteType")
public class VoteTypeCrud extends BaseNormalCrud<VoteType, Integer>
{
    public VoteTypeCrud()
    {
        addOrderBy("typeId");
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addColumn("类型名称", "typeName");
        view.addColumn("动作名称", "actionName");
        view.addColumn("显示页面", "showPage").setAutoExpand(true);

        view.defaultInit();

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("类型名称", "typeName");
        view.addComponent("动作名称", "actionName");
        view.addComponent("显示页面", "showPage");

        view.addDefaultButtons();

        return view;
    }
}
