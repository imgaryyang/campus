package com.gzzm.sms;

import net.cyan.commons.log.LogManager;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;
import net.cyan.sms.*;

import java.util.*;

/**
 * @author camel
 * @date 2010-11-5
 */
public class SmsClientItem
{
    @Inject
    private static Provider<SmsService> serviceProvider;

    /**
     * 对应的网关对象
     */
    private Gateway gateway;

    /**
     * 短消息客户端，如果未启动则为null
     */
    private volatile BaseSmsClient client;

    /**
     * 启动失败的信息
     */
    private volatile String error;

    /**
     * 网关状态
     */
    private volatile GatewayState state = GatewayState.stoped;

    public SmsClientItem(Gateway gateway)
    {
        this.gateway = gateway;
    }

    public Gateway getGateway()
    {
        return gateway;
    }

    public GatewayState getState()
    {
        return state;
    }

    public BaseSmsClient getClient()
    {
        return client;
    }

    public String getError()
    {
        return error;
    }

    public void start() throws Exception
    {
        if (state == GatewayState.stoped || state == GatewayState.error)
        {
            //如果服务已经停止，将其启动

            synchronized (this)
            {
                //标识为正在启动
                state = GatewayState.starting;
            }

            try
            {
                BaseSmsClient client = createClient();

                //启动完成
                synchronized (this)
                {
                    this.client = client;
                    state = GatewayState.running;
                }

                final Integer gatewayId = gateway.getGatewayId();

                client.setLog(LogManager.getLog("com.gzzm.sms"));
                client.setSmsProcessor(new SmsProcessor()
                {
                    public void process(Mo mo, SmsClient client) throws Exception
                    {
                        serviceProvider.get()
                                .receiveSms(mo.getSrcPhone(), mo.getContent(), mo.getSerial(), mo.getTime(), gatewayId);
                    }

                    public void process(Mr mr, SmsClient client) throws Exception
                    {
                        serviceProvider.get().updateSmsState(mr.getMessageId(), mr.getTargetPhone(),
                                gatewayId, mr.getTime(), mr.getError());
                    }
                });

                if (client instanceof MultiSmsClient)
                {
                    ((MultiSmsClient) client).setUsersProvider(new MUsersProvider()
                    {
                        public List<MUser> getUsers() throws Exception
                        {
                            List<UserGateway> userGateways =
                                    serviceProvider.get().getDao().getUserGatewaysByGatewayId(gatewayId);

                            List<MUser> users = new ArrayList<MUser>(userGateways.size());
                            for (UserGateway userGateway : userGateways)
                            {
                                if (!StringUtils.isEmpty(userGateway.getUserName()))
                                {
                                    MUser user = new MUser();
                                    user.setLoginName(userGateway.getUserName());
                                    user.setPassword(userGateway.getPassword());

                                    users.add(user);
                                }
                            }

                            return users;
                        }
                    });
                }
            }
            catch (Throwable ex)
            {
                //启动失败
                synchronized (this)
                {
                    state = GatewayState.error;
                    error = ex.getMessage();
                }

                if (ex instanceof Exception)
                    throw (Exception) ex;
                else if (ex instanceof Error)
                    throw (Error) ex;
            }
        }
    }

    public void stop() throws Exception
    {
        if (state == GatewayState.running)
        {
            //如果服务正在运行，将其停止

            BaseSmsClient client;

            synchronized (this)
            {
                client = this.client;

                //标识为正在停止
                state = GatewayState.stoping;
                this.client = null;
            }


            try
            {
                client.close();
            }
            finally
            {
                synchronized (this)
                {
                    //标识为已经停止
                    state = GatewayState.stoped;
                }
            }
        }
    }

    synchronized void close() throws Exception
    {
        stop();
        state = GatewayState.closed;
    }

    private BaseSmsClient createClient() throws Exception
    {
        BaseSmsClient client = gateway.getGatewayClass().getClientClass().newInstance();

        Map<String, Object> args = new HashMap<String, Object>(gateway.getArgs());
        args.put("spId", gateway.getSpId());
        args.put("receivePort", gateway.getReceivePort());

        client.initArgs(args);

        client.setSpNumber(gateway.getSpNumber());

        client.init(gateway.getIp(), gateway.getPort());
        client.login(gateway.getUserName(), gateway.getPassword());

        return client;
    }

    public boolean isAvailable()
    {
        return gateway.isAvailable() != null && gateway.isAvailable();
    }
}
