package com.gzzm.platform.commons.crud;

import net.cyan.commons.util.language.Language;
import net.cyan.crud.Crud;
import net.cyan.crud.view.*;
import net.cyan.crud.view.components.*;

import java.util.*;

/**
 * 主页面视图，主页面是指那些非模态，非弹出窗口的页面
 *
 * @author camel
 * @date 2009-10-21
 */
public abstract class MainPageView<V extends AbstractPropertied<?>, T extends Propertied> extends AbstractPageView<T>
        implements Propertied<T>
{
    protected String title;

    protected String remark;

    protected V mainBody;

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

    private String subViewName;

    private Object subView;

    protected MainPageView(V mainBody)
    {
        this.mainBody = mainBody;
    }

    public V getMainBody()
    {
        return mainBody;
    }

    public void setCrud(Crud crud)
    {
        super.setCrud(crud);
        ((CrudView) getMainBody()).setCrud(crud);
    }

    public String getTitle() throws Exception
    {
        return title == null ? null : Language.getLanguage().getWord(title, title, getCrud());
    }

    @SuppressWarnings("unchecked")
    public T setTitle(String title)
    {
        this.title = title;
        return (T) this;
    }

    public String getRemark() throws Exception
    {
        return remark == null ? null : Language.getLanguage().getWord(remark, remark, getCrud());
    }

    @SuppressWarnings("unchecked")
    public T setRemark(String remark)
    {
        this.remark = remark;
        return (T) this;
    }

    public <C extends Component> C addComponent(C component)
    {
        if (components == null)
            components = new ArrayList<Component>();

        components.add(component);

        return component;
    }

    public <C extends Component> C addComponent(String label, C component)
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

    public <C extends Component> C addMoreComponent(C component)
    {
        if (moreComponents == null)
            moreComponents = new ArrayList<Component>();

        moreComponents.add(component);

        return component;
    }

    public <C extends Component> C addMoreComponent(String label, C component)
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

    public <C extends Component> C addButton(C component)
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

    @SuppressWarnings("unchecked")
    public T setProperty(String name, Object value)
    {
        mainBody.setProperty(name, value);
        return (T) this;
    }

    public Object getProperty(Object obj, String name)
            throws Exception
    {
        return mainBody.getProperty(obj, name);
    }

    public boolean containsProperty(String name)
    {
        return mainBody.containsProperty(name);
    }

    public String getPropertyWithLanguage(Object obj, String name)
            throws Exception
    {
        return mainBody.getPropertyWithLanguage(obj, name);
    }

    public Set<String> getProperties()
    {
        return mainBody.getProperties();
    }

    @SuppressWarnings("unchecked")
    public T setClass(String name)
    {
        mainBody.setClass(name);
        return (T) this;
    }

    public String getClass(Object obj) throws Exception
    {
        return mainBody.getClass(obj);
    }

    @SuppressWarnings("unchecked")
    public T setFontName(String name)
    {
        mainBody.setFontName(name);
        return (T) this;
    }

    public String getFontName(Object obj) throws Exception
    {
        return mainBody.getFontName(obj);
    }

    @SuppressWarnings("unchecked")
    public T setFontSize(String size)
    {
        mainBody.setFontSize(size);
        return (T) this;
    }

    public Object getFontSize(Object obj) throws Exception
    {
        return mainBody.getFontSize(obj);
    }

    @SuppressWarnings("unchecked")
    public T setColor(Object color)
    {
        mainBody.setColor(color);
        return (T) this;
    }

    public Object getColor(Object obj) throws Exception
    {
        return mainBody.getColor(obj);
    }

    @SuppressWarnings("unchecked")
    public T setPrompt(String prompt)
    {
        mainBody.setPrompt(prompt);
        return (T) this;
    }

    public Object getPrompt(Object obj) throws Exception
    {
        return mainBody.getPrompt(obj);
    }

    @SuppressWarnings("unchecked")
    public T setBold(Object bold)
    {
        mainBody.setBold(bold);
        return (T) this;
    }

    public boolean isBold(Object obj) throws Exception
    {
        return mainBody.isBold(obj);
    }

    @SuppressWarnings("unchecked")
    public T setItalic(Object italic)
    {
        mainBody.setItalic(italic);
        return (T) this;
    }

    public boolean isItalic(Object obj) throws Exception
    {
        return mainBody.isItalic(obj);
    }

    @SuppressWarnings("unchecked")
    public T setUnderline(Object underline)
    {
        mainBody.setUnderline(underline);
        return (T) this;
    }

    public boolean isUnderline(Object obj) throws Exception
    {
        return mainBody.isUnderline(obj);
    }

    @SuppressWarnings("unchecked")
    public T setBackgroundColor(Object backgroundColor)
    {
        mainBody.setBackgroundColor(backgroundColor);
        return (T) this;
    }

    public Object getBackgroundColor(Object obj) throws Exception
    {
        return mainBody.getBackgroundColor(obj);
    }

    public String getSubViewName()
    {
        return subViewName;
    }

    public void setSubViewName(String subViewName)
    {
        this.subViewName = subViewName;
    }

    public void setSubView(String subViewName)
    {
        this.subViewName = subViewName;
    }

    public Object getSubView() throws Exception
    {
        if (subView == null && subViewName != null)
        {
            Crud crud = getCrud();

            if (crud != null)
            {
                String viewName = crud.getViewName();

                crud.setViewName(subViewName);
                subView = crud.getView();

                crud.setViewName(viewName);
            }
        }
        return subView;
    }
}