package com.gzzm.platform.commons;

import net.cyan.commons.util.imp.Mvel2ScriptEngine;

import java.util.List;

/**
 * @author camel
 * @date 2017/7/23
 */
public final class MvelFunctions
{
    private MvelFunctions()
    {
    }

    public static List<Object> query(String sql, String database) throws Exception
    {
        return SimpleDao.getInstance(database).sqlQuery(sql, Mvel2ScriptEngine.getContext());
    }

    public static <T> List<T> queryForType(String sql, Class<T> type, String database) throws Exception
    {
        return SimpleDao.getInstance(database).sqlQuery(sql, type, Mvel2ScriptEngine.getContext());
    }

    public static Object queryFirst(String sql, String database) throws Exception
    {
        return SimpleDao.getInstance(database).sqlQueryFirst(sql, Mvel2ScriptEngine.getContext());
    }

    public static <T> T queryFirstForType(String sql, T c, String database) throws Exception
    {
        return (T) SimpleDao.getInstance(database).sqlQueryFirst(sql, c, Mvel2ScriptEngine.getContext());
    }
}
