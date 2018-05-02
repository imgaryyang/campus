package com.gzzm.portal.tag;

import com.gzzm.platform.commons.*;
import net.cyan.commons.util.*;

import java.util.*;

/**
 * 通过查询语句查询数据的标签
 *
 * @author camel
 * @date 2011-6-14
 */
public abstract class SimpleQueryTag<T> extends QueryTag
{
    public static final String DATABASE = "database";

    private String queryAttribute;


    private Class<T> returnType;

    @SuppressWarnings("unchecked")
    public SimpleQueryTag()
    {
        try
        {
            returnType = BeanUtils.toClass(BeanUtils.getRealType(SimpleQueryTag.class, "T", getClass()));
        }
        catch (Exception ex)
        {
            Tools.wrapException(ex);
        }

        if (returnType == null || returnType == Object.class)
            returnType = (Class<T>) Map.class;
    }

    protected SimpleQueryTag(String name)
    {
        this();
        this.queryAttribute = PORTAL + name;
    }

    protected Class<T> getReturnType()
    {
        return returnType;
    }


    @Override
    protected List<?> query(Map<String, Object> parameters) throws Exception
    {
        //database属性，为null或者空字符串表示默认数据库
        String database = getDatabase(parameters);

        //查询语句
        String queryString = getQueryString(parameters);

        SimpleDao dao = database == null ? SimpleDao.getInstance() : SimpleDao.getInstance(database);

        return query0(queryString, dao, parameters);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected QueryResult<?> query(int pageSize, int pageNo, Map<String, Object> parameters) throws Exception
    {
        //database属性，为null或者空字符串表示默认数据库
        String database = getDatabase(parameters);

        //查询语句
        String queryString = getQueryString(parameters);

        SimpleDao dao = database == null ? SimpleDao.getInstance() : SimpleDao.getInstance(database);

        try
        {
            dao.setPageSize(pageSize);
            dao.setPageNo(pageNo);

            List<Object> result = query0(queryString, dao, parameters);
            return new QueryResult(result, pageSize, pageNo, dao.getTotalCount());
        }
        finally
        {
            dao.setPageSize(-1);
            dao.setPageNo(0);
        }
    }


    protected String getDatabase(Map<String, Object> parameters) throws Exception
    {
        String database = (String) parameters.get(DATABASE);
        parameters.remove(DATABASE);

        return database;
    }

    protected String getQueryString(Map<String, Object> parameters) throws Exception
    {
        String queryString = (String) parameters.get(queryAttribute);
        parameters.remove(queryAttribute);

        return queryString;
    }

    protected Object transform(T t) throws Exception
    {
        return t;
    }

    @SuppressWarnings("unchecked")
    protected List<Object> query0(String queryString, SimpleDao dao, Map<String, Object> parameters) throws Exception
    {
        List<Object> result = (List<Object>) (List) query(queryString, dao, parameters);

        int n = result.size();
        for (int i = 0; i < n; i++)
        {
            Object obj1 = result.get(i);
            Object obj2 = transform((T) obj1);

            if (obj1 != obj2)
                result.set(i, obj2);
        }

        return result;
    }

    protected abstract List<T> query(String queryString, SimpleDao dao, Map<String, Object> parameters)
            throws Exception;
}
