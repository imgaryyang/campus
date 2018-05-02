package com.gzzm.portal.tag;

import com.gzzm.platform.commons.SimpleDao;

import java.util.*;

/**
 * @author camel
 * @date 2011-6-14
 */
@SuppressWarnings("UnusedDeclaration")
public class OQLTag<T> extends SimpleQueryTag<T>
{
    public OQLTag()
    {
        super("oql");
    }

    @Override
    @SuppressWarnings("unchecked")
    protected List<T> query(String queryString, SimpleDao dao, Map<String, Object> parameters) throws Exception
    {
        return dao.oqlQuery(queryString, getReturnType(), parameters);
    }
}
