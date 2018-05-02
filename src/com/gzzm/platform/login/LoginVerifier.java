package com.gzzm.platform.login;

import com.gzzm.platform.organ.User;

import javax.servlet.http.HttpServletRequest;

/**
 * 登录校验
 *
 * @author camel
 * @date 11-11-30
 */
public interface LoginVerifier
{
    /**
     * 是否要求输入密码
     *
     * @return 要求输入密码返回true，否则返回false
     */
    public boolean isRequirePassword();

    /**
     * 从request中确定当前用户登录，比如可以通过用户姓名和手机号码来确定一个用户
     *
     * @param request request
     * @return 返回用户登录信息
     * @throws Exception 允许实现类抛出异常
     */
    public User getUser(HttpServletRequest request) throws Exception;

    /**
     * 校验合法性
     *
     * @param user    要校验的用户
     * @param request http请求
     * @return 合法返回null，否则返回错误信息
     * @throws Exception 允许实现类抛出异常
     */
    public String verify(User user, HttpServletRequest request) throws Exception;
}