package com.gzzm.platform.syn;

import net.cyan.commons.util.ExceptionUtils;
import net.cyan.thunwind.dao.GeneralDao;

import java.lang.reflect.*;
import java.util.List;

/**
 * @author camel
 * @date 13-6-25
 */
public abstract class BaseSynService
{
    private int limit = 30;

    public BaseSynService()
    {
    }

    public int getLimit()
    {
        return limit;
    }

    public void setLimit(int limit)
    {
        this.limit = limit;
    }

    protected abstract BaseSynDao getSynDao();

    protected abstract GeneralDao getDao();

    protected <T extends Syn, E> boolean syn(Class<T> synClass, Class<E> entityClass) throws Exception
    {
        BaseSynDao synDao = getSynDao();

        List<T> syns = synDao.oqlQuery("select s from " + synClass.getName() + " s limit " + limit);

        int n = syns.size();

        if (n == 0)
            return false;

        Field field = synClass.getDeclaredFields()[0];
        field.setAccessible(true);

        Long[] synIds = new Long[n];
        Object[] keys = new Object[n];

        for (int i = 0; i < n; i++)
        {
            T syn = syns.get(i);

            synIds[i] = syn.getSynId_();
            keys[i] = field.get(syn);
        }

        List<E> entitys = getEntitys(synClass, entityClass, keys);
        synEntitys(entityClass, entitys);


        synDao.oqlDelete(synClass, "synId_ in :1", (Object) synIds);

        return true;
    }

    protected <T extends Syn, E> List<E> getEntitys(Class<T> synClass, Class<E> entityClass, Object[] keys)
            throws Exception
    {
        Field field = synClass.getDeclaredFields()[0];

        String oql = "select e from " + entityClass.getName() + " e  where " + field.getName() + " in :1";

        return getDao().oqlQuery(oql, (Object) keys);
    }

    protected <E> void synEntitys(Class<E> entityClass, List<E> entitys) throws Exception
    {
        for (E entity : entitys)
        {
            if (entity != null)
                synEntity(entityClass, entity);
        }
    }

    protected <E> void synEntity(Class<E> entityClass, E entity) throws Exception
    {
        Method method = getClass().getMethod("syn", entityClass);

        method.setAccessible(true);

        try
        {
            method.invoke(this, entity);
        }
        catch (InvocationTargetException ex)
        {
            ExceptionUtils.handleException(ex);
        }
    }
}
