package com.gzzm.platform.commons.crud;

import net.cyan.arachne.annotation.Service;
import net.cyan.crud.QueryType;

/**
 * @author camel
 * @date 12-4-12
 */
@Service
public abstract class BaseSQLQueryCrud<R> extends BaseQLQueryCrud<R>
{
    public BaseSQLQueryCrud()
    {
    }

    @Override
    protected QueryType getQueryType()
    {
        return QueryType.sql;
    }
}
