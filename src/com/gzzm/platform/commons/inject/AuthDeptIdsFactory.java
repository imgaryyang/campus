package com.gzzm.platform.commons.inject;

import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.organ.DeptInfo;
import net.cyan.commons.util.*;
import net.cyan.nest.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.*;

/**
 * 注入当前功能拥有权限的部门
 *
 * @author camel
 * @date 2009-7-29
 */
public class AuthDeptIdsFactory implements ValueFactory
{
    private int level;

    private Filter<DeptInfo> filter;

    private String app;

    public AuthDeptIdsFactory(int level, Filter<DeptInfo> filter, String app)
    {
        this.level = level;
        this.filter = filter;
        this.app = app;
    }

    public Object getValue(Type fieldType, BeanContainer container, Object bean, Type beanType, String beanName,
                           Member member) throws Exception
    {
        HttpServletRequest request = container.get(HttpServletRequest.class);

        if (request != null)
        {
            UserOnlineInfo userOnlineInfo = UserOnlineInfo.getUserOnlineInfo(request);
            if (userOnlineInfo != null)
            {
                if (StringUtils.isEmpty(app))
                    return userOnlineInfo.getAuthDeptIds(request, level, filter);
                else
                    return userOnlineInfo.getAuthDeptIds(app, level, filter);
            }
        }

        return null;
    }
}
