package com.gzzm.ods.document;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.Null;
import net.cyan.crud.annotation.Like;

/**
 * @author camel
 * @date 11-11-5
 */
@Service(url = "/od/document/attribute")
public class DocumentAttributeCrud extends BaseNormalCrud<DocumentAttribute, Integer>
{
    @Like
    private String attributeName;

    public DocumentAttributeCrud()
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
        view.addColumn("查询条件", "query");
        view.addColumn("更多查询", "moreQuery");
        view.addColumn("枚举值", "enumValues").setWidth("300");

        view.defaultInit();
        view.addButton(Buttons.sort());

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("属性名称", "attributeName");
        view.addComponent("查询条件", "query").setProperty("onchange", "queryChange()");
        view.addComponent("更多查询", "moreQuery");
        view.addComponent("枚举值", "enumValues");

        view.importJs("/ods/document/attribute.js");

        view.addDefaultButtons();

        return view;
    }

    @Override
    public boolean beforeSave() throws Exception
    {
        if(!getEntity().getQuery()) {
            getEntity().setMoreQuery(Null.Boolean);
        }
        return super.beforeSave();
    }
}
