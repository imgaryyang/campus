package com.gzzm.portal.user.crud;

import com.gzzm.platform.commons.crud.*;
import net.cyan.commons.util.language.Language;
import net.cyan.crud.*;
import net.cyan.crud.util.ValueGetter;
import net.cyan.crud.view.RecordDisplayView;
import net.cyan.crud.view.components.*;

import java.util.*;

/**
 * 门户列表页面对象
 *
 * @author camel
 * @date 2011-8-9
 */
public class PortalListView extends AbstractPageView implements RecordDisplayView
{
    public static final String DEFAULTEDITFORWARD = "";

    /**
     * 页面标题
     */
    protected String title;

    /**
     * 页面说明
     */
    protected String remark;

    /**
     * 前面是否有checkbox框
     */
    private boolean checkable;

    /**
     * 组件列表
     */
    protected List<Component> components;

    /**
     * 更多组件列表
     */
    protected List<Component> moreComponents;

    /**
     * 按钮列表
     */
    protected List<Component> buttons;

    /**
     * 动作列表
     */
    protected List<Action> actions;

    /**
     * 排序字段
     */
    protected List<OrderItem> orders;

    public PortalListView(boolean checkable)
    {
        setPage(Pages.LIST);
        this.checkable = checkable;
    }

    public PortalListView()
    {
        this(false);
    }

    public String getTitle() throws Exception
    {
        return title == null ? null : Language.getLanguage().getWord(title, title, getCrud());
    }

    public PortalListView setTitle(String title)
    {
        this.title = title;
        return this;
    }

    public String getRemark() throws Exception
    {
        return remark == null ? null : Language.getLanguage().getWord(remark, remark, getCrud());
    }

    public PortalListView setRemark(String remark)
    {
        this.remark = remark;
        return this;
    }

    public boolean isCheckable()
    {
        return checkable;
    }

    public void setCheckable(boolean checkable)
    {
        this.checkable = checkable;
    }

    public void addOrder(OrderItem order)
    {
        if (orders == null)
            orders = new ArrayList<OrderItem>();

        orders.add(order);
    }

    public void addOrder(String field, String title)
    {
        addOrder(new OrderItem(field, title));
    }

    public List<OrderItem> getOrders()
    {
        return orders;
    }

    public <T extends Component> T addComponent(T component)
    {
        if (components == null)
            components = new ArrayList<Component>();

        components.add(component);

        return component;
    }

    public <T extends Component> T addComponent(String label, T component)
    {
        addComponent(new LabelComponent(label, component));
        return component;
    }

    public CData addComponent(String label, String expression)
    {
        return addComponent(label, new CData(expression));
    }

    public void addComponent(String label, Component start, Component end)
    {
        addComponent(label, new CBetween(start, end));
    }

    public void addComponent(String label, String start, String end)
    {
        addComponent(label, new CData(start), new CData(end));
    }

    public List<Component> getComponents()
    {
        return components;
    }

    public <T extends Component> T addMoreComponent(T component)
    {
        if (moreComponents == null)
            moreComponents = new ArrayList<Component>();

        moreComponents.add(component);

        return component;
    }

    public <T extends Component> T addMoreComponent(String label, T component)
    {
        addMoreComponent(new LabelComponent(label, component));
        return component;
    }

    public CData addMoreComponent(String label, String expression)
    {
        return addMoreComponent(label, new CData(expression));
    }

    public void addMoreComponent(String label, Component start, Component end)
    {
        addMoreComponent(label, new CBetween(start, end));
    }

    public void addMoreComponent(String label, String start, String end)
    {
        addMoreComponent(label, new CData(start), new CData(end));
    }

    public List<Component> getMoreComponents()
    {
        return moreComponents;
    }

    public <T extends Component> T addButton(T component)
    {
        if (component != null)
        {
            if (buttons == null)
                buttons = new ArrayList<Component>();

            buttons.add(component);
        }

        return component;
    }

    public CButton addButton(String text, Object action)
    {
        return action == null ? null : addButton(Buttons.getButton(text, action));
    }

    public List<Component> getButtons()
    {
        return buttons;
    }

    public List<Action> getActions()
    {
        return actions;
    }

    public PortalListView addAction(String title, Object icon, Object action)
    {
        return addAction(Action.getAction(title, icon, action));
    }

    public PortalListView addAction(Action action)
    {
        if (action != null)
        {
            if (actions == null)
                actions = new ArrayList<Action>();

            actions.add(action);
        }

        return this;
    }

    @SuppressWarnings("unchecked")
    public Object display(Object record) throws Exception
    {
        PortalListItem item = new PortalListItem();
        item.setRecord(record);

        Crud crud = CrudConfig.getContext().getCrud();

        if (crud instanceof KeyBaseCrud)
        {
            item.setKey(((KeyBaseCrud) crud).getKey(record));
        }

        if (actions != null)
        {
            for (Action action : actions)
            {
                if (action.getAction() instanceof ValueGetter)
                {
                    item.addAction(new Action(action.getTitle(), ((ValueGetter) action.getAction()).get(record),
                            action.getIcon()));
                }
                else
                {
                    item.addAction(action);
                }
            }
        }

        return item;
    }

    @SuppressWarnings("unchecked")
    public PortalListView addDefaultButtons(String edtiForward)
    {
        Crud crud = CrudConfig.getContext().getCrud();

        if (components != null || moreComponents != null)
            addButton(crud instanceof StatCrud ? Buttons.stat() : Buttons.query());

        //crud可编辑
        if (crud instanceof EntityCrud)
        {
            addButton(Buttons.add(edtiForward));

            //前面有checkbox才能删除
            if (checkable)
                addButton(Buttons.delete());
        }

        return this;
    }

    public PortalListView addDefaultButtons()
    {
        return addDefaultButtons(DEFAULTEDITFORWARD);
    }

    public PortalListView addDefaultActions(String edtiForward, boolean duplicate)
    {
        Crud crud = CrudConfig.getContext().getCrud();

        //crud可编辑
        if (crud instanceof EntityCrud)
        {
            addAction(Action.edit(edtiForward));

            if (duplicate)
            {
                //添加复制按钮
            }

            addAction(Action.delete());
        }

        return this;
    }

    public PortalListView addDefaultActions(String edtiForward)
    {
        return addDefaultActions(edtiForward, false);
    }

    public PortalListView addDefaultActions(boolean duplicate)
    {
        return addDefaultActions(DEFAULTEDITFORWARD, duplicate);
    }

    public PortalListView defaultInit(String editForward)
    {
        return defaultInit(editForward, true);
    }

    @SuppressWarnings("unchecked")
    public PortalListView defaultInit(String editForward, boolean duplicate)
    {
        addDefaultButtons(editForward);
        addDefaultActions(editForward, duplicate);

        return this;
    }

    public PortalListView defaultInit()
    {
        return defaultInit(DEFAULTEDITFORWARD, false);
    }

    public PortalListView defaultInit(boolean duplicate)
    {
        return defaultInit(DEFAULTEDITFORWARD, duplicate);
    }
}
