package com.gzzm.safecampus.device.card.crud;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.safecampus.device.card.entity.CardAppType;
import net.cyan.arachne.annotation.Service;

/**
 * @author liyabin
 * @date 2018/3/21
 */
@Service(url = "/device/crud/CardAppTypeCrud")
public class CardAppTypeCrud extends BaseNormalCrud<CardAppType, Integer>
{
    public CardAppTypeCrud()
    {
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(true);
        view.addComponent("索引号", "id");
        view.addComponent("应用名称", "appName");
        view.addColumn("索引号", "AppIdx");
        view.addColumn("应用名称", "appName");
        view.addColumn("钱包类型", "code");
        view.addColumn("钱包余额类型", "IsDefault");
        view.addColumn("钱包透支限额(分)", "IsDefault");
        view.addColumn("联机借贷标记", "IsDefault");
        view.addColumn("钱包余额有效期", "balanceValidity");
        view.addDefaultButtons();
        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();
        view.addComponent("索引号", "AppIdx");
        view.addComponent("应用名称", "appName");
        view.addComponent("有效期", "expireDate");
        view.addComponent("应用类型", "cardAppType");
        view.addComponent("钱包类型", "walletType");
        view.addComponent("钱包余额类型", "walletBalType");
        view.addComponent("钱包透支限额(分)", "walletOverLimit");
        view.addComponent("联机借贷标记", "debitFlag");
        view.addComponent("贷记最高额(分)", "maxDebit");
        view.addComponent("钱包余额有效期", "balanceValidity");
        view.addDefaultButtons();
        return view;
    }
}
