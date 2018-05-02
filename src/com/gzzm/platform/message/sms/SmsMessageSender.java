package com.gzzm.platform.message.sms;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.message.Message;
import com.gzzm.platform.message.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 手机短消息发送器
 *
 * @author camel
 * @date 2010-5-20
 */
public class SmsMessageSender implements MessageSender
{
    @Inject
    private static Provider<SmsService> serviceProvider;

    @Inject
    private static Provider<SmsConfig> configProvider;

    /**
     * 短信
     */
    public static final String SMS = "sms";

    /**
     * 待发送的消息队列
     */
    private final Queue<Message> messageQueue = new LinkedList<Message>();

    /**
     * 消息队列发送任务是否准备好，即是否已经创建了这个任务
     */
    private boolean queueJobPrepared;

    private Runnable queueMessagesSend = new Runnable()
    {
        public void run()
        {
            SmsService service = null;
            while (true)
            {
                Message message;
                synchronized (messageQueue)
                {
                    message = messageQueue.poll();
                }

                if (message != null)
                {
                    if (service == null)
                        service = serviceProvider.get();
                    try
                    {
                        service.send(message);
                    }
                    catch (Exception ex)
                    {
                        Tools.log("send sms fail," + message, ex);
                    }
                }
                else
                {
                    break;
                }
            }

            synchronized (messageQueue)
            {
                //任务执行完毕，表示任务未准备好，如果下次需要再发送则，重新创建任务
                queueJobPrepared = false;
            }
        }
    };

    public SmsMessageSender()
    {
    }

    public void send(final Message message) throws Exception
    {
        if (!StringUtils.isEmpty(message.getPhone()))
        {
            Date now = new Date();
            int hour = DateUtils.getHour(now);


            SmsConfig config = null;

            //强制发送，不需要等适当的时间再发送
            boolean send = message.isForce();
            if (!send)
            {
                //当前时间在消息发送范围内，立即发送消息
                config = configProvider.get();
                send = hour >= config.getStartHour() && hour < config.getEndHour();
            }

            if (send)
            {
                //立即发送

                //当前消息另起一个线程发送短信
                Tools.run(new Runnable()
                {
                    public void run()
                    {
                        try
                        {
                            serviceProvider.get().send(message);
                        }
                        catch (Throwable ex)
                        {
                            Tools.log("send sms fail," + message, ex);
                        }
                    }
                });
            }
            else
            {
                //当前时间不在消息发送范围内，消息先进队列，等待发送
                synchronized (messageQueue)
                {
                    messageQueue.add(message);

                    if (!queueJobPrepared)
                    {
                        //任务未创建，创建定时将队列中的信息发送出去的任务

                        Date time = DateUtils.addHour(DateUtils.truncate(now), config.getStartHour());
                        if (hour >= config.getEndHour())
                        {
                            //已经过了发送消息的结束时间，第二天再开始发送消息
                            time = DateUtils.addDate(time, 1);
                        }

                        Jobs.addJob(queueMessagesSend, time);
                    }
                }
            }
        }
    }

    public String getType()
    {
        return SMS;
    }
}
