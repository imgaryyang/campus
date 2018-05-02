package com.gzzm.oa.activite;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;

/**
 * 日常活动类型维护
 *
 * @author fwj
 * @date 11-9-30
 */
@Service(url = "/oa/active/type")
public class ActiviteTypeCrud extends BaseNormalCrud<ActiviteType, Integer>
{
    /**
     * 类型名称查询
     */
    @Like
    private String typeName;

    public ActiviteTypeCrud()
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
    public int getOrderFieldLength()
    {
        return 4;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(true);
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
