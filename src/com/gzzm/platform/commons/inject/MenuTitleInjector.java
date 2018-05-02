package com.gzzm.platform.commons.inject;

import com.gzzm.platform.annotation.MenuTitle;
import com.gzzm.platform.menu.MenuItem;
import net.cyan.nest.*;

import java.lang.reflect.*;

/**
 * MenuTitle注释的注入实现
 *
 * @author camel
 * @date 2011-5-24
 */
@SuppressWarnings("UnusedDeclaration")
public class MenuTitleInjector implements InjectAnnotationParser<MenuTitle>, ValueFactory
{
    public MenuTitleInjector()
    {
    }

    public ValueFactory parse(MenuTitle menuTitle)
    {
        return this;
    }

    public Object getValue(Type fieldType, BeanContainer container, Object bean, Type beanType, String beanName,
                           Member member) throws Exception
    {
        MenuItem menuItem = container.get(MenuItem.class);
        return menuItem == null ? null : menuItem.getTitle();
    }
}
