package com.gzzm.platform.login;

import com.gzzm.platform.commons.NoErrorException;
import com.gzzm.platform.message.sms.SmsVerifier;
import com.gzzm.platform.organ.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author camel
 * @date 11-11-30
 */
public class SmsLoginVerifier implements LoginVerifier
{
    @Inject
    private static Provider<OrganDao> daoProvider;

    public SmsLoginVerifier()
    {
    }

    public boolean isRequirePassword()
    {
        return false;
    }

    public User getUser(HttpServletRequest request) throws Exception
    {
        String userName = request.getParameter("userName");
        String phone = request.getParameter("phone");
        String loginName = request.getParameter("loginName");

        User user = null;
        if (userName != null && phone != null)
        {
            List<User> users = daoProvider.get().getUserByNameAndPhone(userName, phone);

            if (users.size() == 1)
                user = users.get(0);
            else
                throw new PasswordErrorException("smspwd.userphoneerror");
        }
        else if (loginName != null)
        {
            user = daoProvider.get().getUserByLoginName(loginName, UserType.in);
        }

        if (user == null)
            throw new PasswordErrorException("smspwd.usernotexists");

        return user;
    }

    public String verify(User user, HttpServletRequest request)
    {
        String sms = request.getParameter("sms");
        if (StringUtils.isEmpty(sms))
            return "smspwd.empty";

        try
        {
            SmsVerifier.check(user.getPhone(), sms);
        }
        catch (NoErrorException ex)
        {
            String message = ex.getOriginalMessage();
            if ("sms_verifier.timeout".equals(message))
                return "smspwd.timeout";
            else if ("sms_verifier.errorphone".equals(message))
                return "smspwd.erroruser";
            else if ("sms_verifier.smserror".equals(message))
                return "smspwd.error";
            else
                return message;
        }

        return null;
    }
}
