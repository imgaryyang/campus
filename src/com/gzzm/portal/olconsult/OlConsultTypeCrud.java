package com.gzzm.portal.olconsult;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;

/**
 * User: wym
 * Date: 13-5-31
 * Time: 上午11:26
 * 咨询类型crud
 */
@Service(url = "/portal/olconsult/type")
public class OlConsultTypeCrud extends BaseNormalCrud<OlConsultType, Integer>
{
    @Like
    private String typeName;

    public OlConsultTypeCrud()
    {
        setLog(true);
        addOrderBy("typeName");
    }

    public String getTypeName()
    {
        return typeName;
    }

    public void setTypeName(String typeName)
    {
        this.typeName = typeName;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addComponent("类型名称", "typeName");
        view.addColumn("类型名称", "typeName");

        view.defaultInit();
        view.addButton(Buttons.sort());

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("类型名称", "typeName");
        view.addDefaultButtons();

        return view;
    }
}
