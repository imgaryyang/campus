package com.gzzm.safecampus.device.card.crud;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.safecampus.device.card.entity.IDType;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;

/**
 * @author liyabin
 * @date 2018/3/28
 */
@Service(url = "/device/crud/IDTypeCrud")
public class IDTypeCrud extends BaseNormalCrud<IDType, Integer>
{
    private Integer typeNo;
    @Like
    private String name;

    public IDTypeCrud()
    {
    }

    public Integer getTypeNo()
    {
        return typeNo;
    }

    public void setTypeNo(Integer typeNo)
    {
        this.typeNo = typeNo;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(true);
        view.addComponent("证件类型编号", "typeNo");
        view.addComponent("证件类型名称", "name");
        view.addColumn("名称", "name");
        view.addColumn("编号", "typeNo");
        view.addDefaultButtons();
        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();
        view.addComponent("证件类型编号", "typeNo");
        view.addComponent("证件类型名称", "name");
        view.addDefaultButtons();
        return view;
    }
}
