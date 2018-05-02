package com.gzzm.safecampus.campus.service;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;

/**
 * 服务类型crud
 * @author zy
 * @date 2018/3/12 17:20
 */
@Service(url = "/campus/service/servicetypecrud")
public class ServiceTypeCrud extends BaseNormalCrud<ServiceType,Integer>
{
    /**
     * 类型名称
     */
    @Like
    private String typeName;

    public ServiceTypeCrud()
    {
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
    protected Object createListView() throws Exception
    {
        PageTableView view=new PageTableView();
        view.addComponent("类型名称","typeName");
        view.addColumn("类型名称","typeName");
        view.defaultInit(false);
        view.addButton(Buttons.sort());
        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view=new SimpleDialogView();
        view.addComponent("类型名称","typeName");
        view.addDefaultButtons();
        return view;
    }
}
