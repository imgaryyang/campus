package com.gzzm.platform.login;

import net.cyan.arachne.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.validate.annotation.*;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 2011-3-25
 */
@Service
public class PasswordPage
{
    @Inject
    private LoginService service;

    @Inject
    private PasswordRule passwordRule;

    /**
     * 当前用户
     */
    @Inject
    private UserOnlineInfo userOnlineInfo;

    /**
     * 旧密码
     */
    @Require
    private String oldPassword;

    /**
     * 新密码
     */
    @Require
    private String password;

    public PasswordPage()
    {
    }

    public String getOldPassword()
    {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword)
    {
        this.oldPassword = oldPassword;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    @FieldValidator("password")
    public String validatePassword(String password) throws Exception
    {
        if (passwordRule == null)
            return null;

        return passwordRule.checkPassword(userOnlineInfo, password);
    }

    /**
     * 显示修改密码的页面
     *
     * @return 返回null，表示转到Forward定义的页面
     */
    @Service(url = "/user/password")
    @Forward(page = "/platform/login/password.ptl")
    public String show()
    {
        return null;
    }

    @Service(url = "/user/lock")
    @Forward(page = "/platform/login/lock.ptl")
    public String lock()
    {
        return null;
    }

    /**
     * 保存密码修改
     *
     * @throws Exception 数据库查询或者修改错误
     */
    @Service(url = "/changePassword", method = HttpMethod.post, validateType = ValidateType.server)
    @ObjectResult
    public void save() throws Exception
    {
        service.changePassword(userOnlineInfo.getUserId(), oldPassword, password);
    }

    @Service
    @ObjectResult
    public boolean checkPassword() throws Exception
    {
        return service.checkPassword(userOnlineInfo.getUserId(), password);
    }
}
