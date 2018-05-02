package com.gzzm.portal.user.crud;

import com.gzzm.platform.commons.crud.*;
import net.cyan.commons.util.language.Language;
import net.cyan.crud.view.components.*;

import java.util.*;

/**
 * 门户列表页面对象
 *
 * @author camel
 * @date 2011-8-9
 */
public class ProtalListView extends AbstractPageView
{
    protected String title;

    protected String remark;

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

    public ProtalListView()
    {
    }

    public String getTitle() throws Exception
    {
        return title == null ? null : Language.getLanguage().getWord(title, title, getCrud());
    }

    public ProtalListView setTitle(String title)
    {
        this.title = title;
        return this;
    }

    public String getRemark() throws Exception
    {
        return remark == null ? null : Language.getLanguage().getWord(remark, remark, getCrud());
    }

    public ProtalListView setRemark(String remark)
    {
        this.remark = remark;
        return this;
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
}
