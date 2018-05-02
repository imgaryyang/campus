package com.gzzm.platform.sign;

import net.cyan.thunwind.annotation.LoadByKey;
import net.cyan.thunwind.dao.GeneralDao;

/**
 * @author camel
 * @date 2015/6/3
 */
public abstract class SignDao extends GeneralDao
{
    public SignDao()
    {
    }

    @LoadByKey
    public abstract UserSign getUserSign(Integer userId) throws Exception;
}
