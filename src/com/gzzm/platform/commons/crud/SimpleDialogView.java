package com.gzzm.platform.commons.crud;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.language.Language;
import net.cyan.crud.*;
import net.cyan.crud.view.Propertied;
import net.cyan.crud.view.components.*;

import java.util.*;

/**
 * 简单的对话框视图，包括并主要用于模态窗口
 *
 * @author camel
 * @date 2009-11-8
 */
public class SimpleDialogView extends AbstractPageView<SimpleDialogView>
{
    public class Item
    {
        private String label;

        private Component component;

        private String unit;

        private boolean duple;

        private boolean require;

        public Item(String label, Component component)
        {
            this.label = label;
            this.component = component;
        }

        public Item(String label, Component component, String unit)
        {
            this.label = label;
            this.component = component;
            this.unit = unit;
        }

        public Item(String label, Component component, String unit, boolean require)
        {
            this.label = label;
            this.component = component;
            this.unit = unit;
            this.require = require;

            if (require && component instanceof Propertied)
                ((Propertied) component).setProperty("require", null);
        }

        public Item(String label, Component component, boolean require)
        {
            this.label = label;
            this.component = component;
            this.require = require;

            if (require && component instanceof Propertied)
                ((Propertied) component).setProperty("require", null);
        }

        public String getLabel()
        {
            return label == null ? null : Tools.getMessage(label, getCrud());
        }

        public Component getComponent()
        {
            return component;
        }

        public String getUnit()
        {
            return unit;
        }

        public void setUnit(String unit)
        {
            this.unit = unit;
        }

        public boolean isDuple()
        {
            return duple;
        }

        public void setDuple(boolean duple)
        {
            this.duple = duple;
        }

        public boolean isRequire()
        {
            return require;
        }

        public void setRequire(boolean require)
        {
            this.require = require;

            if (require && component instanceof Propertied)
                ((Propertied) component).setProperty("require", null);
        }
    }

    private String title;

    private List<String> hiddens;

    /**
     * 页面上的控件列表
     */
    private List<Item> components = new ArrayList<Item>();

    /**
     * 页面上的按钮
     */
    private List<Component> buttons = new ArrayList<Component>();

    public SimpleDialogView()
    {
    }

    public String getTitle() throws Exception
    {
        return title == null ? null : Language.getLanguage().getWord(title, title, getCrud());
    }

    public SimpleDialogView setTitle(String title)
    {
        this.title = title;
        return this;
    }

    public Item item(String label, Component component)
    {
        return new Item(label, component);
    }

    public SimpleDialogView addComponent(Item item)
    {
        components.add(item);
        return this;
    }

    public <T extends Component> T addComponent(String label, T component)
    {
        components.add(new Item(label, component));
        return component;
    }

    public <T extends Component> T addComponent(String label, T component, String unit)
    {
        components.add(new Item(label, component, unit));
        return component;
    }

    public <T extends Component> T addComponent(String label, T component, String unit, boolean require)
    {
        components.add(new Item(label, component, unit, require));
        return component;
    }

    public <T extends Component> T addComponent(String label, T component, boolean require)
    {
        components.add(new Item(label, component, require));
        return component;
    }

    public CData addComponent(String label, String field)
    {
        CData component = new CData(field);
        components.add(new Item(label, component));

        return component;
    }

    public CData addComponent(String label, String field, String unit)
    {
        CData component = new CData(field);
        components.add(new Item(label, component, unit));

        return component;
    }

    public CData addComponent(String label, String field, String unit, boolean require)
    {
        CData component = new CData(field);
        components.add(new Item(label, component, unit, require));

        return component;
    }

    public CData addComponent(String label, String field, boolean require)
    {
        CData component = new CData(field);
        components.add(new Item(label, component, require));

        return component;
    }

    public <T extends Component> T addDoubleComponent(String label, T component)
    {
        Item item = new Item(label, component);
        item.duple = true;
        components.add(item);
        return component;
    }

    public <T extends Component> T addDoubleComponent(String label, T component, String unit)
    {
        Item item = new Item(label, component, unit);
        item.duple = true;
        components.add(item);
        return component;
    }

    public <T extends Component> T addDoubleComponent(String label, T component, String unit, boolean require)
    {
        Item item = new Item(label, component, unit, require);
        item.duple = true;
        components.add(item);
        return component;
    }

    public <T extends Component> T addDoubleComponent(String label, T component, boolean require)
    {
        Item item = new Item(label, component, require);
        item.duple = true;
        components.add(item);
        return component;
    }

    public CData addDoubleComponent(String label, String field)
    {
        CData component = new CData(field);
        Item item = new Item(label, component);
        item.duple = true;
        components.add(item);

        return component;
    }

    public CData addDoubleComponent(String label, String field, String unit)
    {
        CData component = new CData(field);
        Item item = new Item(label, component, unit);
        item.duple = true;
        components.add(item);

        return component;
    }

    public CData addDoubleComponent(String label, String field, String unit, boolean require)
    {
        CData component = new CData(field);
        Item item = new Item(label, component, unit, require);
        item.duple = true;
        components.add(item);

        return component;
    }

    public CData addDoubleComponent(String label, String field, boolean require)
    {
        CData component = new CData(field);
        Item item = new Item(label, component, require);
        item.duple = true;
        components.add(item);

        return component;
    }

    public List<Item> getComponents()
    {
        return components;
    }

    public <T extends Component> T addButton(T component)
    {
        if (component != null)
            buttons.add(component);
        return component;
    }

    public Component addButton(String text, Object action)
    {
        return action == null ? null : addButton(Buttons.getButton(text, action));
    }

    public List<Component> getButtons()
    {
        return buttons;
    }

    public SimpleDialogView addHidden(String expression)
    {
        if (hiddens == null)
            hiddens = new ArrayList<String>();
        hiddens.add(expression);

        return this;
    }

    public List<String> getHiddens()
    {
        return hiddens;
    }

    public SimpleDialogView addDefaultButtons()
    {
        Crud crud = CrudConfig.getContext().getCrud();

        if ("forward".equals(crud.getAction()))
        {
            addButton(Buttons.ok());
            addButton(Buttons.cancel());
        }
        else
        {
            if (crud instanceof EntityCrud)
            {
                //crud可编辑
                addButton(Buttons.save());
            }

            addButton(Buttons.close());
        }

        return this;
    }
}
