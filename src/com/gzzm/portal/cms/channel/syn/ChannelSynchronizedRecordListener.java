package com.gzzm.portal.cms.channel.syn;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.pool.job.SingleRunnable;
import net.cyan.commons.util.*;
import net.cyan.datasyn.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author camel
 * @date 13-6-14
 */
public class ChannelSynchronizedRecordListener implements SynchronizedRecordListener
{
    @Inject
    private static Provider<ChannelSynchronizedDao> channelSynchronizedServiceProvider;

    private static final Runnable CHANNEL_INIT = new SingleRunnable(new Runnable()
    {
        public void run()
        {
            try
            {
                Tools.log("start init channel tree");
                channelSynchronizedServiceProvider.get().initChannelTree(schema);
                Tools.log("end init channel tree");
            }
            catch (Throwable ex)
            {
                Tools.log(ex);
            }
        }
    });

    private static String schema;

    public ChannelSynchronizedRecordListener()
    {
    }

    public boolean beforeInsert(SynchronizedInfo table, Map<String, Object> keys, Map<String, Object> values)
            throws Exception
    {
        if ("PLCHANNEL".equals(table.getTable()))
        {
            Integer channelId = ((Number) keys.get("CHANNELID")).intValue();
            if (channelId == 0)
                return false;

            values.remove("LEFTVALUE");
            values.remove("RIGHTVALUE");
        }

        return true;
    }

    public void afterInsert(SynchronizedInfo table, Map<String, Object> keys, Map<String, Object> values)
            throws Exception
    {
        if ("PLCHANNEL".equals(table.getTable()))
        {
            Integer channelId = ((Number) keys.get("CHANNELID")).intValue();
            if (channelId == 0)
                return;

            initChannelTree(table.getSynSchema());
        }
    }

    public boolean beforeUpdate(SynchronizedInfo table, Map<String, Object> keys, Map<String, Object> values)
            throws Exception
    {
        if ("PLCHANNEL".equals(table.getTable()))
        {
            Integer channelId = ((Number) keys.get("CHANNELID")).intValue();
            if (channelId == 0)
                return false;

            if (((Number) values.get("PARENTCHANNELID")).intValue() == 0)
            {
                values.remove("ORDERID");
            }

            values.remove("LEFTVALUE");
            values.remove("RIGHTVALUE");
        }

        return true;
    }

    public void afterUpdate(SynchronizedInfo table, Map<String, Object> keys, Map<String, Object> values)
            throws Exception
    {
        if ("PLCHANNEL".equals(table.getTable()))
        {
            Integer channelId = ((Number) keys.get("CHANNELID")).intValue();
            if (channelId == 0)
                return;

            initChannelTree(table.getSynSchema());
        }
    }

    public boolean beforeDelete(SynchronizedInfo table, Map<String, Object> keys) throws Exception
    {
        return true;
    }

    public void afterDelete(SynchronizedInfo table, Map<String, Object> keys) throws Exception
    {
        if ("PLCHANNEL".equals(table.getTable()))
            initChannelTree(table.getSynSchema());
    }

    public void beforeExists(SynchronizedInfo table, Map<String, Object> keys) throws Exception
    {
    }

    @Override
    public void afterLoad(SynchronizedInfo table, Map<String, Object> keys, Map<String, Object> values) throws Exception
    {
    }

    private void initChannelTree(final String schema) throws Exception
    {
        ChannelSynchronizedRecordListener.schema = schema;

        if (!StringUtils.isEmpty(schema))
        {
            Jobs.addJob(CHANNEL_INIT, new Date(System.currentTimeMillis() + 1000 * 60 * 5));
        }
    }
}
