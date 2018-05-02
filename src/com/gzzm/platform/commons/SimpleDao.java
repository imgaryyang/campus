package com.gzzm.platform.commons;

import net.cyan.thunwind.dao.GeneralDao;

/**
 * 空的dao，没有定义任何扩展的方法
 *
 * @author camel
 * @date 2011-6-14
 */
public abstract class SimpleDao extends GeneralDao
{
    public static SimpleDao getInstance(String database) throws Exception
    {
        return Tools.getBean(SimpleDao.class, database);
    }

    public static SimpleDao getInstance() throws Exception
    {
        return Tools.getBean(SimpleDao.class);
    }

    public SimpleDao()
    {
    }
}
