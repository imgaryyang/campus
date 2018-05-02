package com.gzzm.safecampus.device.card.crud;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.safecampus.device.card.entity.CardType;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.components.CTextArea;

/**
 * @author liyabin
 * @date 2018/3/21
 */
@Service(url = "/device/crud/CardTypeCrud")
public class CardTypeCrud extends BaseNormalCrud<CardType, Integer>
{
    @Like
    private String name;

    private Integer code;

    public CardTypeCrud()
    {
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Integer getCode()
    {
        return code;
    }

    public void setCode(Integer code)
    {
        this.code = code;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(true);
        view.addComponent("名称", "name");
        view.addComponent("编码", "code");
        view.addColumn("名称", "name");
        view.addColumn("卡种编码", "code");
        view.addColumn("是否默认", "isDefault");
        view.addColumn("备注", "note");
        view.addButton(Buttons.query());
        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();
        view.addComponent("名称", "name");
        view.addComponent("卡种编码", "code");
        view.addComponent("备注", new CTextArea("note"));
        view.addDefaultButtons();
        return view;
    }
}
