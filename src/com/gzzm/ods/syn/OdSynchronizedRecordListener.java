package com.gzzm.ods.syn;

import com.gzzm.ods.exchange.*;
import net.cyan.commons.util.Provider;
import net.cyan.datasyn.*;
import net.cyan.nest.annotation.Inject;

import java.util.Map;

/**
 * @author camel
 * @date 2017/3/13
 */
public class OdSynchronizedRecordListener implements SynchronizedRecordListener
{
    @Inject
    private static Provider<ExchangeReceiveDao> daoProvider;

    public OdSynchronizedRecordListener()
    {
    }

    @Override
    public boolean beforeInsert(SynchronizedInfo table, Map<String, Object> keys, Map<String, Object> values)
            throws Exception
    {
        if ("ODRECEIVEBASE".equals(table.getTable()))
        {
            values.put("NOTIFIED", 0);
        }
        else if ("ODBACK".equals(table.getTable()))
        {
            values.put("NOTIFIED", 0);

            Long receiveId = ((Number) values.get("RECEIVEID")).longValue();

            if (receiveId != null)
            {
                ExchangeReceiveDao dao = daoProvider.get();
                ReceiveBase receiveBase = dao.getReceiveBase(receiveId);
                Send send = dao.getSendByDocumentId(receiveBase.getDocumentId());

                if (send != null)
                {
                    values.put("SENDID", send.getSendId());
                    values.put("DEPTID", send.getDeptId());
                }
            }
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
        if ("ODRECEIVEBASE".equals(table.getTable()) || "ODBACK".equals(table.getTable()))
        {
            values.remove("NOTIFIED");
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
