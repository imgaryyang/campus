package com.gzzm.platform.commons.inject;

import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.organ.UserType;
import net.cyan.commons.util.BeanUtils;
import net.cyan.nest.*;

import java.lang.reflect.*;
import java.util.*;

/**
 * 注入当前部门id的值
 *
 * @author camel
 * @date 2009-7-29
 */
public class DeptIdFactory implements ValueFactory
{
    private static DeptIdFactory INSTANCE;

    private static List<DeptIdFactory> factories;

    /**
     * 部门id的级别
     */
    private int level;

    private DeptIdFactory(int level)
    {
        this.level = level;
    }

    @SuppressWarnings("unchecked")
    public Object getValue(Type fieldType, BeanContainer container, Object bean, Type beanType, String beanName,
                           Member member) throws Exception
    {
        UserOnlineInfo userOnlineInfo = container.get(UserOnlineInfo.class);
        if (userOnlineInfo != null && (userOnlineInfo.getUserType() == UserType.in || userOnlineInfo.isAdmin()))
        {
            Class type = BeanUtils.toClass(fieldType);
            if (type.isAssignableFrom(List.class))
                return level >= 0 ? userOnlineInfo.getDeptIds(level) : userOnlineInfo.getDeptIds();
            else
                return level >= 0 ? userOnlineInfo.getDept(level).getDeptId() : userOnlineInfo.getDeptId();
        }

        return null;
    }

    public static synchronized DeptIdFactory getFactory(int level)
    {
        if (level >= 0)
        {
            if (factories == null)
                factories = new ArrayList<DeptIdFactory>();

            DeptIdFactory factory = null;
            if (factories.size() > level)
                factory = factories.get(level);

            if (factory == null)
            {
                while (factories.size() <= level)
                    factories.add(null);

                factories.set(level, factory = new DeptIdFactory(level));
            }

            return factory;
        }
        else
        {
            if (INSTANCE == null)
                INSTANCE = new DeptIdFactory(level);
            return INSTANCE;
        }
    }
}
