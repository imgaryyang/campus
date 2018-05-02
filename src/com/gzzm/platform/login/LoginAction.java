package com.gzzm.platform.login;

/**
 * 登录动作
 *
 * @author camel
 * @date 2009-7-24
 */
public enum LoginAction
{
    /**
     * 登录
     */
    login,

    /**
     * 注销
     */
    logout,

    /**
     * 超时
     */
    expire,

    /**
     * 重复登录退出
     */
    kickout,

    /**
     * 密码错误
     */
    password_error,

    /**
     * 登录恢复，从cookie中恢复
     */
    recover,
}
