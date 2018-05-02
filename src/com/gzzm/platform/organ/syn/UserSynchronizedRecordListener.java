package com.gzzm.platform.organ.syn;

import net.cyan.datasyn.*;

import java.util.Map;

/**
 * @author camel
 * @date 13-6-14
 */
public class UserSynchronizedRecordListener implements SynchronizedRecordListener
{
    public UserSynchronizedRecordListener()
    {
    }

    public boolean beforeInsert(SynchronizedInfo table, Map<String, Object> keys, Map<String, Object> values)
            throws Exception
    {
        if ("PFUSER".equals(table.getTable()))
        {
            values.remove("LOGINNAME");
            values.remove("CERTID");
        }

        return true;
    }

    public void afterInsert(SynchronizedInfo table, Map<String, Object> keys, Map<String, Object> values)
            throws Exception
    {
    }

    public boolean beforeUpdate(SynchronizedInfo table, Map<String, Object> keys, Map<String, Object> values)
            throws Exception
    {
        if ("PFUSER".equals(table.getTable()))
        {
            values.remove("LOGINNAME");
            values.remove("CERTID");
        }

        return true;
    }

    public void afterUpdate(SynchronizedInfo table, Map<String, Object> keys, Map<String, Object> values)
            throws Exception
    {
    }

    public boolean beforeDelete(SynchronizedInfo table, Map<String, Object> keys) throws Exception
    {
        return true;
    }

    public void afterDelete(SynchronizedInfo table, Map<String, Object> keys) throws Exception
    {
    }

    public void beforeExists(SynchronizedInfo table, Map<String, Object> keys) throws Exception
    {
    }

    @Override
    public void afterLoad(SynchronizedInfo table, Map<String, Object> keys, Map<String, Object> values) throws Exception
    {
    }
}
