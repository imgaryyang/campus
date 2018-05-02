package com.gzzm.portal.user;

import com.gzzm.platform.organ.User;
import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

/**
 * 和外网用户登录相关的数据库操纵
 *
 * @author camel
 * @date 2011-5-4
 */
public abstract class UserDao extends GeneralDao
{
    public UserDao()
    {
    }

    /**
     * 根据用户id获得用户名称
     *
     * @param userId 用户id
     * @return 用户名称
     * @throws Exception 数据库异常
     */
    @OQL("select userName from User where userId=:1")
    public abstract String getUserName(Integer userId) throws Exception;

    /**
     * 根据登录名获得用户信息
     *
     * @param loginName 登录名
     * @param userId    不包括此用户ID，用于唯一性校验
     * @return User对象
     * @throws Exception 数据库异常
     */
    @OQL("select u from User u where u.loginName=:1 and u.userId<>?2")
    public abstract User getUserByLoginName(String loginName, Integer userId) throws Exception;

    public User getUserByLoginName(String loginName) throws Exception
    {
        return getUserByLoginName(loginName, null);
    }
}
