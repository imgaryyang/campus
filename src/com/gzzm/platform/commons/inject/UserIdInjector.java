package com.gzzm.platform.commons.inject;

import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.nest.*;

import java.lang.reflect.*;

/**
 * UserId注释的注入实现
 *
 * @author camel
 * @date 2009-7-22
 */
@SuppressWarnings("UnusedDeclaration")
public class UserIdInjector implements InjectAnnotationParser<UserId>, ValueFactory
{
    public UserIdInjector()
    {
    }

    public ValueFactory parse(UserId userId)
    {
        return this;
    }

    public Object getValue(Type type, BeanContainer container, Object o, Type type1, String s, Member member)
            throws Exception
    {
        UserOnlineInfo userOnlineInfo = container.get(UserOnlineInfo.class);
        return userOnlineInfo == null ? null : userOnlineInfo.getUserId();
    }
}
