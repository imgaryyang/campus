package com.gzzm.platform.commons.crud;

import net.cyan.arachne.annotation.Service;
import net.cyan.crud.QueryType;

/**
 * oql查询的crud
 *
 * @author camel
 * @date 12-4-12
 */
@Service
public abstract class BaseOQLQueryCrud<R> extends BaseQLQueryCrud<R>
{
    public BaseOQLQueryCrud()
    {
    }

    @Override
    protected QueryType getQueryType()
    {
        return QueryType.oql;
    }
}
