package com.gzzm.ods.dict;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;

/**
 * 秘密等级Crud
 *
 * @author db
 * @date 11-12-31
 */
@Service(url = "/ods/dict/Secret")
public class SecretCrud extends BaseNormalCrud<Secret, Integer>
{
    @Like
    private String secretName;

    public SecretCrud()
    {
    }

    public String getSecretName()
    {
        return secretName;
    }

    public void setSecretName(String secretName)
    {
        this.secretName = secretName;
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
        view.addComponent("秘密等级名称", "secretName");

        // 添加显示列
        view.addColumn("秘密等级名称", "secretName");

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
        view.addComponent("等级名称", "secretName");

        // 添加保存和关闭按钮
        view.addDefaultButtons();

        return view;
    }
}
