package com.gzzm.ods.exchange;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;

/**
 * @author camel
 * @date 11-11-5
 */
@Service(url = "/od/receive_attribute")
public class ReceiveAttributeCrud extends BaseNormalCrud<ReceiveAttribute, Integer>
{
    @Like
    private String attributeName;

    public ReceiveAttributeCrud()
    {
        setLog(true);
    }

    public String getAttributeName()
    {
        return attributeName;
    }

    public void setAttributeName(String attributeName)
    {
        this.attributeName = attributeName;
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

        view.addComponent("属性名称", "attributeName");

        view.addColumn("属性名称", "attributeName");

        view.defaultInit();
        view.addButton(Buttons.sort());

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("属性名称", "attributeName");

        view.addDefaultButtons();

        return view;
    }
}
