package com.gzzm.platform.commons.crud;

import net.cyan.arachne.annotation.Service;

/**
 * @author camel
 * @date 2018/3/28
 */
@Service
public abstract class SimpleNormalCrud<E> extends BaseNormalCrud<E, Integer>
{
    private String nameField;

    private String nameTitle;

    public SimpleNormalCrud(String nameField, String nameTitle)
    {
        this.nameField = nameField;
        this.nameTitle = nameTitle;
        setLog(true);
    }

    @Override
    public String getDeleteTagField()
    {
        return "deleteTag";
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

        view.addComponent(nameTitle, nameField);
        view.addColumn(nameTitle, nameField);

        view.defaultInit();
        view.addButton(Buttons.sort());
        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();
        view.addComponent(nameTitle, nameField);
        view.addDefaultButtons();
        return view;
    }
}
