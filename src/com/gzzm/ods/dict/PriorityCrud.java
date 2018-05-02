package com.gzzm.ods.dict;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;

/**
 * 紧急程度Crud
 *
 * @author db
 * @date 11-12-31
 */
@Service(url = "/ods/dict/Priority")
public class PriorityCrud extends BaseNormalCrud<Priority, Integer>
{
    @Like
    private String priorityName;

    public PriorityCrud()
    {
    }

    public String getPriorityName()
    {
        return priorityName;
    }

    public void setPriorityName(String priorityName)
    {
        this.priorityName = priorityName;
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

        // 添加查询
        view.addComponent("紧急程度名称", "priorityName");

        // 添加显示列
        view.addColumn("紧急程度名称", "priorityName");

        // 添加增删查改、复制及排序按钮
        view.defaultInit();
        view.addButton(Buttons.sort());

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        // 添加录入列
        view.addComponent("程度名称", "priorityName");

        // 添加保存和关闭按钮
        view.addDefaultButtons();

        return view;
    }
}
