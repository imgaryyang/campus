package com.gzzm.oa.mail.syn;

import net.cyan.datasyn.*;

import java.util.Map;

/**
 * @author camel
 * @date 2017/3/13
 */
public class MailSynchronizedRecordListener implements SynchronizedRecordListener
{
    public MailSynchronizedRecordListener()
    {
    }

    @Override
    public boolean beforeInsert(SynchronizedInfo table, Map<String, Object> keys, Map<String, Object> values)
            throws Exception
    {
        if ("OAMAIL".equals(table.getTable()))
        {
            values.remove("NOTIFYED");
        }

        return true;
    }

    @Override
    public void afterInsert(SynchronizedInfo table, Map<String, Object> keys, Map<String, Object> values)
            throws Exception
    {
    }

    @Override
    public boolean beforeUpdate(SynchronizedInfo table, Map<String, Object> keys, Map<String, Object> values)
            throws Exception
    {
        if ("OAMAIL".equals(table.getTable()))
        {
            values.remove("NOTIFYED");
        }

        return true;
    }

    @Override
    public void afterUpdate(SynchronizedInfo table, Map<String, Object> keys, Map<String, Object> values)
            throws Exception
    {
    }

    @Override
    public boolean beforeDelete(SynchronizedInfo table, Map<String, Object> keys) throws Exception
    {
        return true;
    }

    @Override
    public void afterDelete(SynchronizedInfo table, Map<String, Object> keys) throws Exception
    {
    }

    @Override
    public void beforeExists(SynchronizedInfo table, Map<String, Object> keys) throws Exception
    {
    }

    @Override
    public void afterLoad(SynchronizedInfo table, Map<String, Object> keys, Map<String, Object> values) throws Exception
    {
    }
}
