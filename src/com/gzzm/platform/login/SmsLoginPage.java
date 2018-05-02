package com.gzzm.platform.login;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.message.sms.SmsVerifier;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.StringUtils;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * @author camel
 * @date 11-11-30
 */
@Service
public class SmsLoginPage
{
    @Inject
    private OrganDao dao;

    private String loginName;

    private String userName;

    private String phone;

    public SmsLoginPage()
    {
    }

    public String getLoginName()
    {
        return loginName;
    }

    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    @Service(url = "/login/sms/send")
    @ObjectResult
    public String sendSms() throws Exception
    {
        User user = null;

        if (userName != null && phone != null)
        {
            List<User> users = dao.getUserByNameAndPhone(userName, phone);

            if (users.size() == 1)
                user = users.get(0);
            else
                return Tools.getMessage("smspwd.userphoneerror");
        }
        else if (loginName != null)
        {
            user = dao.getUserByLoginName(loginName, UserType.in);
        }

        if (user == null)
            return Tools.getMessage("smspwd.usernotexists");

        String phone = user.getPhone();
        if (StringUtils.isEmpty(phone))
            return Tools.getMessage("smspwd.phoneempty");

        SmsVerifier.sendSms(phone, "smspwd.password");

        return null;
    }
}
