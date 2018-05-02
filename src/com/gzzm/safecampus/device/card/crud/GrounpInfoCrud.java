package com.gzzm.safecampus.device.card.crud;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.safecampus.device.card.entity.GroupInfo;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;

/**
 * @author liyabin
 * @date 2018/3/28
 */
@Service(url = "/device/crud/GrounpInfoCrud")
public class GrounpInfoCrud extends BaseNormalCrud<GroupInfo, Integer>
{
    @Like
    private String groupName;

    @Like
    private String groupNo;

    public GrounpInfoCrud()
    {
    }

    public String getGroupName()
    {
        return groupName;
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

    public String getGroupNo()
    {
        return groupNo;
    }

    public void setGroupNo(String groupNo)
    {
        this.groupNo = groupNo;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(true);
        view.addComponent("分组编号", "groupNo");
        view.addComponent("分组名称", "groupName");
        view.addColumn("分组编号", "groupNo").setWidth("120");
        view.addColumn("分组名称", "groupName");
        view.addColumn("分组值", "groupInfo");
        view.addDefaultButtons();
        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();
        view.addComponent("分组编号", "groupNo");
        view.addComponent("分组名称", "groupName");
        view.addComponent("分组值", "groupInfo");
        view.addDefaultButtons();
        return view;
    }
}
