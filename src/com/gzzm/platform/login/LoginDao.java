package com.gzzm.platform.login;

import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.*;

/**
 * @author camel
 * @date 2018/1/30
 */
public abstract class LoginDao extends GeneralDao
{
    public LoginDao()
    {
    }

    @OQL("select loginTime from LoginError where loginTime>?1 and loginName=:2 order by loginTime desc")
    public abstract List<Date> getLoginErrorTimes(Date date, String loginName) throws Exception;

    @OQL("select count(*) from LoginError where loginTime>?1 and loginName=:2 ")
    public abstract int getLoginErrorCount(Date date, String loginName) throws Exception;

    @OQL("select max(loginTime) from LoginError where loginTime>?1 and loginName=:2 ")
    public abstract Date getLastLoginErrorTime(Date date, String loginName) throws Exception;

    @OQLUpdate("delete from LoginError where loginName=:1")
    public abstract void clearLoginError(String loginName) throws Exception;
}
