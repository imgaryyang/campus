package com.gzzm.platform.commons.inject;

import com.gzzm.platform.annotation.MenuId;
import com.gzzm.platform.menu.MenuItem;
import net.cyan.nest.*;

import java.lang.reflect.*;

/**
 * 菜单id的注入器
 *
 * @author camel
 * @date 2011-6-9
 */
@SuppressWarnings("UnusedDeclaration")
public class MenuIdInjector implements InjectAnnotationParser<MenuId>, ValueFactory
{
    public MenuIdInjector()
    {
    }

    public ValueFactory parse(MenuId annotation)
    {
        return this;
    }

    public Object getValue(Type fieldType, BeanContainer container, Object bean, Type beanType, String beanName,
                           Member member) throws Exception
    {
        MenuItem menuItem = container.get(MenuItem.class);
        return menuItem == null ? null : menuItem.getMenuId();
    }
}
