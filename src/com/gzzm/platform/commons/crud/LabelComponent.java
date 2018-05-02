package com.gzzm.platform.commons.crud;

import net.cyan.commons.util.BeanUtils;
import net.cyan.crud.view.CrudViewUtils;
import net.cyan.crud.view.components.*;

/**
 * 在一个组件前面加一个标签构成一个新的组件
 *
 * @author camel
 * @date 2009-10-21
 */
public class LabelComponent extends AbstractComponent<LabelComponent>
{
    private Component component;

    private String label;

    public LabelComponent(String label, Component component)
    {
        this.component = component;
        setLabel(label);
    }

    public LabelComponent(String label, String expression)
    {
        this(label, new CData(expression));
    }

    public Component getComponent()
    {
        return component;
    }

    public LabelComponent setLabel(String label)
    {
        this.label = label;
        return this;
    }

    public String getLabel(Object obj)
    {
        String label = this.label;

        if (label == null)
        {
            if (component instanceof ValueComponent)
            {
                String expression = ((ValueComponent) component).getExpression();

                if (BeanUtils.isVariable(expression))
                {
                    //expression是简单变量，用expression做标题
                    return CrudViewUtils.getValueWithLanguage(expression, obj);
                }
            }
            else if (component instanceof CBetween)
            {
                return ((CBetween) component).getLabel(obj);
            }
        }

        return CrudViewUtils.getValueWithLanguage(label, obj);
    }
}