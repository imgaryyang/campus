package com.gzzm.portal.cms.channel;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;

/**
 * 栏目扩展属性维护
 *
 * @author camel
 * @date 2011-5-11
 */
@Service(url = "/portal/infoproperty")
public class InfoPropertyCrud extends SubListCrud<InfoProperty, Integer>
{
    private Integer channelId;

    public InfoPropertyCrud()
    {
    }

    public Integer getChannelId()
    {
        return channelId;
    }

    public void setChannelId(Integer channelId)
    {
        this.channelId = channelId;
    }

    @Override
    protected String getParentField()
    {
        return "channelId";
    }

    @Override
    public int getOrderFieldLength()
    {
        return 4;
    }

    @Override
    public void initEntity(InfoProperty entity) throws Exception
    {
        super.initEntity(entity);

        entity.setNullable(false);
    }

    @Override
    protected void initListView(SubListView view) throws Exception
    {
        view.setName("栏目扩展属性");
        view.addColumn("属性名称", "propertyName");
        view.addColumn("显示名称", "showName");
        view.addColumn("数据类型", "dataType");
        view.addColumn("可为空", "nullable");
        view.addColumn("可选值", "enumValues");
        view.addColumn("多选", "multiple");
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("属性名称", "propertyName");
        view.addComponent("显示名称", "showName");
        view.addComponent("数据类型", "dataType");
        view.addComponent("可为空", "nullable");
        view.addComponent("可选值", "enumValues");
        view.addComponent("多选", "multiple");
        view.addComponent("选择器", "selectable");

        view.addDefaultButtons();

        return view;
    }
}
