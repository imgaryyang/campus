package com.gzzm.platform.commons.crud;

import net.cyan.commons.util.BeanUtils;
import net.cyan.crud.view.CrudViewUtils;
import net.cyan.crud.view.components.*;

/**
 * 在两个组件前面加一个标签构成一个新的组件，表示这个标签象征的变量的取值在这两个组件的值
 *
 * @author camel
 * @date 2009-10-21
 */
public class CBetween extends AbstractComponent<CBetween>
{
    private Component start;

    private Component end;

    public CBetween(Component start, Component end)
    {
        this.start = start;
        this.end = end;
    }

    public CBetween(String expression_start, String expression_end)
    {
        this(new CData(expression_start), new CData(expression_end));
    }

    public Component getStart()
    {
        return start;
    }

    public Component getEnd()
    {
        return end;
    }

    public String getLabel(Object obj)
    {
        if (start instanceof ValueComponent)
        {
            String expression = ((ValueComponent) start).getExpression();

            if (BeanUtils.isVariable(expression))
            {
                //expression是简单变量，用expression做标题

                int index = expression.lastIndexOf('_');
                if (index > 0)
                    expression = expression.substring(0, index);

                return CrudViewUtils.getValueWithLanguage(expression, obj);
            }
        }

        return null;
    }
}
