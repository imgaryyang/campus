package com.gzzm.portal.cms.information.syn;

import com.gzzm.portal.cms.channel.ChannelDao;
import net.cyan.commons.util.*;
import net.cyan.datasyn.*;
import net.cyan.nest.annotation.Inject;

import java.util.Map;

/**
 * @author camel
 * @date 2017/1/4
 */
public class InformationSynchronizedRecordListener implements SynchronizedRecordListener
{
    @Inject
    private static Provider<ChannelDao> channelDaoSynchronizedServiceProvider;

    public InformationSynchronizedRecordListener()
    {
    }

    @Override
    public boolean beforeInsert(SynchronizedInfo table, Map<String, Object> keys, Map<String, Object> values)
            throws Exception
    {
        if ("PLINFORMATION".equals(table.getTable()))
        {
            String channelCode = ((String) values.get("CHANNELCODE"));
            if (StringUtils.isEmpty(channelCode))
                return false;

            values.remove("CHANNELCODE");

            Integer channelId = channelDaoSynchronizedServiceProvider.get().getChannelIdByCode(channelCode);
            if (channelId == null)
                return false;

            values.put("CHANNELID", channelId);
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
        return beforeInsert(table, keys, values);
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
        if ("PLINFORMATION".equals(table.getTable()) && values != null)
        {
            Integer channelId = ((Number) values.get("CHANNELID")).intValue();
            String channelCode = channelDaoSynchronizedServiceProvider.get().getChannelCode(channelId);

            values.put("CHANNELCODE", channelCode);
        }
    }
}
