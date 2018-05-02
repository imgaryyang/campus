package com.gzzm.mo;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;

/**
 * 客户端类型管理
 *
 * @author camel
 * @date 2014/5/13
 */
@Service(url = "/mo/clientType")
public class MoClientTypeCrud extends BaseNormalCrud<MoClientType, Integer>
{
    @Like
    private String typeName;

    public MoClientTypeCrud()
    {
        setLog(true);
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
    public String getOrderField()
    {
        return "orderId";
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("类型名称", "typeName");
        view.addComponent("类型编号", "typeCode");
        view.addComponent("设备类型", "deviceType");

        view.addDefaultButtons();

        return view;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addComponent("类型名称", "typeName");

        view.addColumn("类型名称", "typeName");
        view.addColumn("类型编号", "typeCode");
        view.addColumn("设备类型", "deviceType");

        view.defaultInit();
        view.addButton(Buttons.sort());

        return view;
    }
}
