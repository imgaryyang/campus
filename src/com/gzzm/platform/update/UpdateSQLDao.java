package com.gzzm.platform.update;

import net.cyan.thunwind.dao.GeneralDao;

/**
 * @author camel
 * @date 13-5-22
 */
public abstract class UpdateSQLDao extends GeneralDao
{
    public UpdateSQLDao()
    {
    }

    public UpdateSQL getUpdateSQL(String fileName) throws Exception
    {
        return load(UpdateSQL.class, fileName);
    }
}
