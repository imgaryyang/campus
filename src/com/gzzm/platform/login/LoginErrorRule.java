package com.gzzm.platform.login;

/**
 * 登录密码错误的规则，定义限制错误多少次不允许多长时间登录
 *
 * @author camel
 * @date 2018/1/30
 */
public interface LoginErrorRule
{
    public String check(String loginName, LoginErrorService service) throws Exception;
}