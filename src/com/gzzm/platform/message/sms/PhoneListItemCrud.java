package com.gzzm.platform.message.sms;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.importers.CrudEntityImportor;

/**
 * @author camel
 * @date 12-4-28
 */
@Service(url = "/phonelistitem")
public class PhoneListItemCrud extends BaseNormalCrud<PhoneListItem, Long>
{
    private Integer listId;

    @Like
    private String itemName;

    @Like
    private String phone;

    public PhoneListItemCrud()
    {
        addOrderBy("phone");
    }

    public Integer getListId()
    {
        return listId;
    }

    public void setListId(Integer listId)
    {
        this.listId = listId;
    }

    public String getItemName()
    {
        return itemName;
    }

    public void setItemName(String itemName)
    {
        this.itemName = itemName;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    @Override
    public void initEntity(PhoneListItem entity) throws Exception
    {
        if (entity.getListId() == null)
            entity.setListId(listId);

        super.initEntity(entity);
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addComponent("名称", "itemName");
        view.addComponent("手机号码", "phone");

        view.addColumn("名称", "itemName");
        view.addColumn("手机号码", "phone");

        view.defaultInit();
        view.addButton(Buttons.export("xls"));
        view.addButton(Buttons.imp());

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("名称", "itemName");
        view.addComponent("手机号码", "phone");

        view.addDefaultButtons();

        return view;
    }

    @Override
    protected void initImportor(CrudEntityImportor<PhoneListItem, Long> importor)
            throws Exception
    {
        super.initImportor(importor);

        importor.addColumnMap("名称", "itemName");
        importor.addColumnMap("手机号码", "phone");
    }
}
