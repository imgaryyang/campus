package com.gzzm.platform.commons.crud;

import net.cyan.crud.*;
import net.cyan.crud.view.*;
import net.cyan.crud.view.components.*;

import java.util.*;

/**
 * 简单的列表页面，用于展示一个列表
 *
 * @author camel
 * @date 2010-6-6
 */
public class SimplePageListView extends AbstractPageView<SimplePageListView> implements RecordDisplayView
{
    private String title;

    private Component component;

    private List<Action> actions;

    public SimplePageListView()
    {
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

    public void setDisplay(String title, Object action)
    {
        setDisplay(new CLabel(new HrefCell(title).setAction(action)).setClass("title title_long"));
    }

    public void setDisplay(String title, String time, Object action)
    {
        setDisplay(new CUnion(new CLabel(new HrefCell(title).setAction(action)).setClass("title"),
                new CLabel(new FieldCell(time)).setClass("time")));
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

    public List<Action> getActions()
    {
        return actions;
    }

    public SimplePageListView addAction(String title, Object action, Object icon)
    {
        return addAction(Action.getAction(title, action, icon));
    }

    public SimplePageListView addAction(Action action)
    {
        if (action != null)
        {
            if (actions == null)
                actions = new ArrayList<Action>();

            actions.add(action);
        }

        return this;
    }

    public SimplePageListView addDefaultActions()
    {
        return addDefaultActions(DEFAULTEDITFORWARD);
    }


    public SimplePageListView addDefaultActions(String editForward)
    {
        Crud crud = CrudConfig.getContext().getCrud();
        //crud可编辑
        if (crud instanceof EntityCrud)
        {
            EntityCrud entityCrud = (EntityCrud) crud;
            addAction(Action.add(editForward));
            addAction(Action.edit(editForward));
            addAction(Action.delete());

            if (entityCrud.getOrderField() != null)
            {
                addAction(Action.up());
                addAction(Action.down());
            }
        }

        return this;
    }
}
