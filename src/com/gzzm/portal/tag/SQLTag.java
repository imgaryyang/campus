package com.gzzm.portal.tag;

import com.gzzm.platform.commons.SimpleDao;
import net.cyan.commons.util.DataConvert;

import java.util.*;

/**
 * @author camel
 * @date 2011-6-14
 */
@SuppressWarnings("UnusedDeclaration")
public class SQLTag<T> extends SimpleQueryTag<T>
{
    public SQLTag()
    {
        super("sql");
    }

    protected boolean isNative(Map<String, Object> parameters) throws Exception
    {
        boolean nativeSql = DataConvert.toBoolean(parameters.get("native"));
        parameters.remove("native");

        return nativeSql;
    }

    @Override
    protected List<T> query(String queryString, SimpleDao dao, Map<String, Object> parameters) throws Exception
    {
        dao.setNativeSql(isNative(parameters));

        return dao.sqlQuery(queryString, getReturnType(), parameters);
    }
}
