package com.gzzm.im;

import com.gzzm.im.entitys.FriendGroup;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.ToString;
import net.cyan.crud.view.components.CButton;
import net.cyan.nest.annotation.Inject;

/**
 * 好友用户组维护
 *
 * @author camel
 * @date 2011-1-4
 */
@Service(url = "/im/FriendGroupCrud")
public class FriendGroupCrud extends UserOwnedNormalCrud<FriendGroup, Long> implements ToString<FriendGroup>
{
    @Inject
    private ImDao dao;

    /**
     * 是否只展示列表，如果为true，表示只作为左边的列表
     */
    private boolean display;

    public FriendGroupCrud()
    {
    }

    @Override
    public void afterQuery() throws Exception
    {
        if (display)
        {
            getList().add(0, new FriendGroup(0L, "常用联系人"));//原来是我的好友
            getList().add(0, new FriendGroup(-1L, "所有好友"));
        }
    }

    public boolean isDisplay()
    {
        return display;
    }

    public void setDisplay(boolean display)
    {
        this.display = display;
    }

    @Override
    public String getOrderField()
    {
        return "orderId";
    }

    @Override
    protected Object createListView() throws Exception
    {
        if (display)
        {
            return new SelectableListView();
        }
        else
        {
            PageTableView view = new PageTableView();

            view.addColumn("分组名称", "groupName");
            view.addColumn("好友数量", "friendCount");
            view.addColumn("好友管理", new CButton("好友管理", "friendManage(${groupId})"));

            view.defaultInit();
            view.addButton(Buttons.sort());

            view.importJs("/im/friendgroup.js");

            return view;
        }
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("分组名称", "groupName");

        view.addDefaultButtons();

        return view;
    }

    public String toString(FriendGroup group) throws Exception
    {
        if (group.getGroupId() == -1)
            return group.toString() + "(" + dao.getFriendCount(getUserId()) + ")";
        else if (group.getGroupId() == 0)
            return group.toString() + "(" + dao.getFriendCountNotInGroup(getUserId()) + ")";
        else
            return group.toString() + "(" + group.getFriendCount() + ")";
    }
}
