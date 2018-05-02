package com.gzzm.portal.tag;

import com.gzzm.platform.commons.Tools;
import com.gzzm.portal.commons.PortalConfig;
import net.cyan.commons.util.DataConvert;
import net.cyan.crud.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author camel
 * @date 12-8-31
 */
public abstract class EntityQueryTag<E, K> extends NormalQueryCrud<E, K> implements PortalTag
{
    @Inject
    protected PortalConfig config;

    public EntityQueryTag()
    {
        setPageSize(0);
    }

    protected int getDefaultPageSize() throws Exception
    {
        return config.getPageSize();
    }

    /**
     * 初始化，根据传进来的上下文初始化实体
     *
     * @param context 传进来的标签上下文
     * @throws Exception 允许子类抛出异常
     */
    protected void init(Map<String, Object> context) throws Exception
    {
    }

    protected int getLimit(Map<String, Object> context) throws Exception
    {
        Object value = context.get(QueryTag.LIMIT);
        if (value != null)
        {
            try
            {
                return DataConvert.toInt(value);
            }
            catch (NumberFormatException ex)
            {
                //解析数字异常,跳过
            }
        }

        return -1;
    }

    @SuppressWarnings("unchecked")
    public Object getValue(Map<String, Object> context) throws Exception
    {
        init(context);

        int limit = getLimit(context);
        if (limit > 0)
        {
            setPageSize(limit);
            setPageNo(1);
        }
        else if (getPageSize() == 0)
        {
            setPageSize(getDefaultPageSize());
        }

        List<Object> list;

        try
        {
            CrudAdvice.before(this);

            list = (List) getList();

            CrudAdvice.after(this, list);
        }
        catch (Throwable ex)
        {
            CrudAdvice.catchHandle(this, ex);

            Tools.handleException(ex);

            return null;
        }
        finally
        {
            CrudAdvice.finallyHandle(this);
        }

        int n = list.size();
        for (int i = 0; i < n; i++)
        {
            Object obj1 = list.get(i);
            Object obj2 = transform((E) obj1);

            if (obj1 != obj2)
                list.set(i, obj2);
        }


        TagQueryResult result = new TagQueryResult();
        result.setRecords(list);
        result.setContext(context);

        if (getPageSize() > 0)
        {
            //分页查询
            int pageNo = getPageNo();
            if (pageNo < 1)
                pageNo = 1;

            if (limit <= 0)
            {
                result.setPageSize(getPageSize());
                result.setPageNo(pageNo);
                result.setTotalCount(getTotalCount());
                result.setPageCount(getPageCount());
            }
            else
            {
                result.setCount(getTotalCount());
            }
        }
        else
        {
            result.setCount(list.size());
        }

        return result;
    }

    protected Object transform(E entity) throws Exception
    {
        return entity;
    }
}
