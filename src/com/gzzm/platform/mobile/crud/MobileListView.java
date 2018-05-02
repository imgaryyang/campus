package com.gzzm.platform.mobile.crud;

import com.gzzm.platform.commons.crud.AbstractPageView;
import net.cyan.crud.*;
import net.cyan.crud.view.RecordDisplayView;
import net.cyan.crud.view.components.Component;

/**
 * 移动设备上的列表页面，用于展示一个列表
 *
 * @author camel
 * @date 2010-6-6
 */
public class MobileListView extends AbstractPageView<MobileListView> implements RecordDisplayView
{
    private String title;

    private Component component;

    public MobileListView()
    {
        setPage(Pages.LIST);
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public <T extends Component> T setDisplay(T component)
    {
        this.component = component;

        return component;
    }

    @SuppressWarnings("unchecked")
    public Object display(Object obj) throws Exception
    {
        if (component != null)
            return component.display(obj);

        Crud crud = getCrud();
        if (crud instanceof ToString)
            return ((ToString) crud).toString(obj);

        return obj;
    }
}
