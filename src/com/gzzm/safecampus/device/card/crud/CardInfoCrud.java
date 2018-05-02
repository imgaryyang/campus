package com.gzzm.safecampus.device.card.crud;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.safecampus.device.card.entity.CardInfo;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.view.components.CTextArea;

/**
 * @author liyabin
 * @date 2018/3/24
 */
@Service
public class CardInfoCrud extends BaseNormalCrud<CardInfo,Integer>
{
    public CardInfoCrud()
    {
    }
    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(true);
        view.addComponent("地区名称", "name");
        view.addComponent("地区代码", "code");
        view.addColumn("编号", "id");
        view.addColumn("地区名称", "name");
        view.addColumn("地区代码", "code");
        view.addColumn("是否默认", "IsDefault");
        view.addColumn("备注", "note");
        view.addDefaultButtons();
        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();
        view.addComponent("地区名称", "name");
        view.addComponent("地区代码", "code");
        view.addComponent("备注", new CTextArea("note"));
        view.addDefaultButtons();
        return view;
    }

    public String getStateText()
    {
        return "";
    }
}
