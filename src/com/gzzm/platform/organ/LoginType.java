package com.gzzm.platform.organ;

/**
 * 登录方式
 * @author camel
 * @date 2009-7-18
 */
public enum LoginType
{
    /**
     * 如果绑定了证书则用证书登录，否则用用户名密码登录
     */
    auto,

    /**
     * 只允许用证书登录
     */
    cert,

    /**
     * 只允许用用户名密码登录
     */
    password,

    /**
     * 两种登录方式可同时使用
     */
    all
}
