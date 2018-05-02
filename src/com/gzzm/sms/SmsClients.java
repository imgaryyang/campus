package com.gzzm.sms;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.*;

import java.util.*;

/**
 * 保存所有的sms客户端
 *
 * @author camel
 * @date 2010-11-5
 */
@Injectable(singleton = true)
public class SmsClients
{
    @Inject
    private Provider<SmsDao> daoProvider;

    /**
     * 保存所有的item
     */
    private Map<Integer, SmsClientItem> items = new HashMap<Integer, SmsClientItem>();

    public SmsClients()
    {
    }

    public synchronized SmsClientItem getItem(Integer gatewayId)
    {
        return items.get(gatewayId);
    }

    private void start(Gateway gateway) throws Exception
    {
        Integer gatewayId = gateway.getGatewayId();

        SmsClientItem oldItem;
        SmsClientItem newItem;

        synchronized (this)
        {
            oldItem = items.get(gatewayId);

            items.put(gatewayId, newItem = new SmsClientItem(gateway));
        }

        if (oldItem != null)
        {
            //原来已经有了，关闭之
            oldItem.close();
        }

        //启动
        newItem.start();
    }

    /**
     * 停止网关
     *
     * @param gatewayId 网关ID
     * @throws Exception 停止网关错误
     */
    public void stop(Integer gatewayId) throws Exception
    {
        SmsClientItem item;

        synchronized (this)
        {
            item = items.get(gatewayId);
        }

        if (item != null)
        {
            //网关已经初始化，关闭之
            item.stop();
        }
    }

    /**
     * 重置网关
     *
     * @param gatewayId 网关ID
     * @throws Exception 从数据库加在网关数据或启动网关失败
     */
    public void reset(Integer gatewayId) throws Exception
    {
        start(daoProvider.get().getGateway(gatewayId));
    }

    /**
     * 启动所有网关
     *
     * @throws Exception 从数据库加在网关数据或启动网关失败
     */
    public void startAll() throws Exception
    {
        for (Gateway gateway : daoProvider.get().getAllGateways())
        {
            if (gateway.isAvailable() == null || gateway.isAvailable())
            {
                try
                {
                    start(gateway);
                }
                catch (Throwable ex)
                {
                    //一个网关启动失败不影响下一个
                    Tools.log("启动网关失败,网关ID:" + gateway.getGatewayId(), ex);
                }
            }
        }
    }
}
