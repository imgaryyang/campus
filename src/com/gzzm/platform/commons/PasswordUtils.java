package com.gzzm.platform.commons;

import net.cyan.commons.security.SecurityUtils;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

/**
 * 密码校验的基础类
 *
 * @author camel
 * @date 2016/7/18
 */
public final class PasswordUtils
{
    @Inject
    private static Provider<GlobalConfig> configProvider;

    private PasswordUtils()
    {
    }

    public static boolean isMd5(String s)
    {
        int length = s.length();
        if (length == 32)
        {
            for (int i = 0; i < length; i++)
            {
                char c = s.charAt(i);
                if (!(c >= 'A' && c <= 'Z' || c >= '0' && c <= '9'))
                    return false;
            }

            return true;
        }

        return false;
    }

    public static String hash(String password)
    {
        return SecurityUtils.md5(password);
    }

    public static boolean checkPassword(String inputPassword, String realPassword, Integer userId)
    {
        return !isMd5(inputPassword) && (checkPassword0(inputPassword, realPassword, userId) ||
                checkPassword0(hash(inputPassword), realPassword, userId));
    }

    private static boolean checkPassword0(String inputPassword, String realPassword, Integer userId)
    {
        return inputPassword.equals(realPassword) ||
                hashPassword0(inputPassword, userId).equals(realPassword);
    }

    public static String hashPassword(String password, Integer userId)
    {
        GlobalConfig config = configProvider.get();
        if (config.isSaltPassword())
        {
            return hashPassword0(hash(password), userId);
        }
        else
        {
            return hash(password);
        }
    }

    private static String hashPassword0(String password, Integer userId)
    {
        return hash(salt(password, userId));
    }

    public static String salt(String password, Integer userId)
    {
        return password + "#" + userId;
    }

    public static void main(String[] args)
    {
        System.out.println(hash("dfd"));
    }
}
