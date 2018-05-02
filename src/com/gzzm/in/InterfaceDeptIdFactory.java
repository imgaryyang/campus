package com.gzzm.in;

import com.gzzm.platform.commons.*;
import net.cyan.nest.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.*;

/**
 * @author camel
 * @date 13-12-17
 */
public class InterfaceDeptIdFactory implements InjectAnnotationParser<InterfaceDeptId>, ValueFactory
{
    static final String NAME = "interface_deptId";

    public InterfaceDeptIdFactory()
    {
    }

    public ValueFactory parse(InterfaceDeptId annotation)
    {
        return this;
    }

    public Object getValue(Type fieldType, BeanContainer container, Object bean, Type beanType, String beanName,
                           Member member) throws Exception
    {
        HttpServletRequest request = container.get(HttpServletRequest.class);

        Integer deptId = null;
        try
        {
            deptId = getDeptId(request);
        }
        catch (NoErrorException e)
        {
            //用户没有登录产生的错误
        }

        if (deptId != null)
            Tools.log("get interface deptId：" + deptId);

        return deptId;
    }

    public static Integer getDeptId(HttpServletRequest request) throws Exception
    {
        InterfaceUserCheck.check(request);
        return (Integer) request.getAttribute(NAME);
    }
}
