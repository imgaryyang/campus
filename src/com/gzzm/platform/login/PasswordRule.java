package com.gzzm.platform.login;

/**
 * @author camel
 * @date 2015/8/11
 */
public interface PasswordRule
{
    public String checkPassword(UserOnlineInfo user, String password) throws Exception;
}