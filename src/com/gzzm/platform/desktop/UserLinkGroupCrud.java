package com.gzzm.platform.desktop;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;

/**
 * @author camel
 * @date 2016/9/24
 */
@Service(url = "/desktop/userLinkgroup")
public class UserLinkGroupCrud extends UserOwnedNormalCrud<UserLinkGroup, Integer>
{
    public UserLinkGroupCrud()
    {
    }

    @Override
    public String getOrderField()
    {
        return "orderId";
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addColumn("分组名称", "groupName");
        view.defaultInit();
        view.addButton(Buttons.sort());

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("分组名称", "groupName");
        view.addDefaultButtons();

        return view;
    }
}
