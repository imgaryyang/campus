package com.gzzm.in;

import com.gzzm.platform.commons.NoErrorException;
import net.cyan.nest.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.*;
import java.util.Collection;

/**
 * @author camel
 * @date 13-12-17
 */
public class InterfaceDeptIdsFactory implements InjectAnnotationParser<InterfaceDeptIds>, ValueFactory
{
    static final String NAME = "interface_deptIds";

    public InterfaceDeptIdsFactory()
    {
    }

    public ValueFactory parse(InterfaceDeptIds annotation)
    {
        return this;
    }

    public Object getValue(Type fieldType, BeanContainer container, Object bean, Type beanType, String beanName,
                           Member member) throws Exception
    {
        HttpServletRequest request = container.get(HttpServletRequest.class);

        Collection<Integer> deptIds = null;
        try
        {
            deptIds = getDeptIds(request);
        }
        catch (NoErrorException e)
        {
            //用户没有登录产生的错误
        }

        return deptIds;
    }

    @SuppressWarnings("unchecked")
    public static Collection<Integer> getDeptIds(HttpServletRequest request) throws Exception
    {
        InterfaceUserCheck.check(request);
        return (Collection<Integer>) request.getAttribute(NAME);
    }
}
