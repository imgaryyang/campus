package com.gzzm.ods.dict;

import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.*;

/**
 * 公文标记Crud
 *
 * @author db
 * @date 11-12-31
 */
@Service(url = "/ods/dict/Tag")
public class TagCrud extends BaseNormalCrud<Tag, Integer>
{
    @Like("tagName")
    private String name;

    @UserId
    @NotCondition
    private Integer userId;

    private boolean self;

    public TagCrud()
    {
    }

    public boolean isSelf()
    {
        return self;
    }

    public void setSelf(boolean self)
    {
        this.self = self;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Integer getUserId()
    {
        return userId;
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        if (self)
            return "userId=:userId";
        else
            return "userId is null";
    }

    @Override
    protected String getSortListCondition() throws Exception
    {
        if (self)
            return "userId=:userId";
        else
            return "userId is null";
    }

    @Override
    public boolean beforeSave() throws Exception
    {
        super.beforeSave();

        if (self)
            getEntity().setUserId(userId);

        return true;
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
        view.addComponent("标记名称", "name");

        // 添加显示列
        view.addColumn("标记名称", "tagName");

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
        view.addComponent("标记名称", "tagName");

        // 添加保存和关闭按钮
        view.addDefaultButtons();

        return view;
    }
}
