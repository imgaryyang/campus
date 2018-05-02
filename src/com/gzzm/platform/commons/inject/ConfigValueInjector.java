package com.gzzm.platform.commons.inject;

import com.gzzm.platform.annotation.ConfigValue;
import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.*;
import net.cyan.nest.*;

import java.lang.reflect.*;

/**
 * ConfigValue注释的注入实现，从数据库读取配置项
 *
 * @author camel
 * @date 2009-7-23
 */
public class ConfigValueInjector implements InjectAnnotationParser<ConfigValue>
{
    public ConfigValueInjector()
    {
    }

    public ValueFactory parse(ConfigValue configValue)
    {
        final String name = configValue.name();
        final String defaultValue = configValue.defaultValue();
        return new ValueFactory()
        {
            @SuppressWarnings("unchecked")
            public Object getValue(Type fieldType, BeanContainer container, Object bean, Type beanType, String beanName,
                                   Member member) throws Exception
            {
                Class c = BeanUtils.toClass(fieldType);
                if (c == Provider.class)
                {
                    final Class realType = BeanUtils.toClass(BeanUtils.getRealType(Provider.class, "T", fieldType));

                    return new Provider()
                    {
                        public Object get()
                        {
                            try
                            {
                                return Tools.getConfig(name, realType, defaultValue);
                            }
                            catch (Exception ex)
                            {
                                Tools.wrapException(ex);
                                return null;
                            }
                        }
                    };
                }
                else
                {
                    return Tools.getConfig(name, c, defaultValue);
                }
            }
        };
    }
}
