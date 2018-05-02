package com.gzzm.in;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.organ.Scopes;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author camel
 * @date 13-12-24
 */
public final class InterfaceUserCheck
{
    @Inject
    private static Provider<InterfaceDao> daoProvider;

    private InterfaceUserCheck()
    {
    }

    public static void check(HttpServletRequest request) throws Exception
    {
        Integer deptId = (Integer) request.getAttribute(InterfaceDeptIdFactory.NAME);

        if (deptId != null)
            return;

        String userName = request.getHeader("userName");
        String passwrod = request.getHeader("password");

        String message;

        if (StringUtils.isEmpty(userName))
        {
            message = "用户名为空，请在http header中设置userName";
        }
        else if (StringUtils.isEmpty(passwrod))
        {
            message = "密码为空，请在http header中设置password";
        }
        else
        {
            InterfaceUser user = daoProvider.get().getUserByLoginName(userName);
            if (user == null)
            {
                Tools.log("not exists interface user:" + userName);

                message = "interface.user_not_exists";
            }
            else
            {
                Tools.log("interface user:" + user.getUserName());
                if (!user.getPassword().equals(passwrod))
                {
                    message = "interface.password_error";
                }
                else
                {
                    request.setAttribute(InterfaceDeptIdFactory.NAME, user.getDeptId());

                    Collection<Integer> deptIds = null;
                    Integer scopeId = user.getScopeId();
                    if (scopeId != null)
                    {
                        if (scopeId >= 0)
                        {
                            Scopes scopes = new Scopes(scopeId, user.getDeptId());
                            deptIds = scopes.getDeptIds();
                        }
                    }
                    else
                    {
                        deptIds = Collections.singleton(deptId);
                    }

                    request.setAttribute(InterfaceDeptIdsFactory.NAME, deptIds);

                    return;
                }
            }
        }

        throw new NoErrorException(message);
    }
}
