package com.gzzm.platform.message.comet;

import com.gzzm.platform.commons.Tools;
import net.cyan.arachne.CometClosedException;
import net.cyan.commons.cache.*;
import net.cyan.nest.annotation.*;

import java.io.Serializable;
import java.util.*;

/**
 * @author camel
 * @date 2010-5-30
 */
@Injectable(singleton = true)
public class CometService
{
    private List<CometListener> listeners = new ArrayList<CometListener>();

    private Cache<String, CometInfo> userCometInfos;

    private final Map<String, CometInfo> localUserCometInfos = new HashMap<String, CometInfo>();

    private Cache<String, CometMessage> messages;

    private Cache<String, Set<String>> cometIds;

    private boolean inited;

    public CometService(@Inject("comet") Cache<String, CometInfo> userCometInfos,
                        @Inject("comet_message") Cache<String, CometMessage> messages,
                        @Inject("comet_ids") Cache<String, Set<String>> cometIds)
    {
        //由于init方法必须在userCometInfos被注入之后才能执行，所以在构造函数中注入两个缓存
        this.userCometInfos = userCometInfos;
        this.messages = messages;
        this.cometIds = cometIds;

        init();
    }

    public synchronized void addListener(CometListener listener)
    {
        List<CometListener> listeners = new ArrayList<CometListener>(this.listeners);

        listeners.add(listener);

        this.listeners = listeners;
    }

    private synchronized void init()
    {
        if (!inited)
        {
            inited = true;

            messages.addListener(new CacheListener<String, CometMessage>()
            {
                public void put(Cache<? extends String, ? extends CometMessage> cache, String key, CometMessage message)
                        throws Exception
                {
                    CometInfo cometInfo;
                    synchronized (localUserCometInfos)
                    {
                        cometInfo = localUserCometInfos.get(message.getCometId());
                    }
                    if (cometInfo != null && cometInfo.getContext() != null)
                    {
                        sendMessage0(message, cometInfo);
                        messages.remove(key);
                    }
                }

                public void remove(Cache<? extends String, ? extends CometMessage> cache, String key,
                                   CometMessage value)
                        throws Exception
                {
                }
            });

//            Jobs.addJob(new Runnable()
//            {
//                public void run()
//                {
//                    CometInfo[] cometInfos;
//
//                    synchronized (localUserCometInfos)
//                    {
//                        Collection<CometInfo> values = localUserCometInfos.values();
//                        cometInfos = values.toArray(new CometInfo[values.size()]);
//                    }
//
//                    for (CometInfo cometInfo : cometInfos)
//                    {
//                        if (cometInfo != null)
//                        {
//                            sendMessage0(new CometMessage(Ping.instance, cometInfo.getCometId()), cometInfo);
//                        }
//                    }
//                }
//            }, 1000 * 60 * 5);
        }
    }

    public void put(CometInfo cometInfo) throws Exception
    {
        sendMessage(new CometConnection(cometInfo.getId()), cometInfo);

        userCometInfos.update(cometInfo.getId(), cometInfo);

        cometIds.getSet(cometInfo.getCometId()).add(cometInfo.getId());

        synchronized (localUserCometInfos)
        {
            localUserCometInfos.put(cometInfo.getId(), cometInfo);
        }

        List<CometListener> listeners = this.listeners;
        for (CometListener listener : listeners)
        {
            try
            {
                listener.connect(cometInfo, this);
            }
            catch (Throwable ex)
            {
                //触发一个监听失败，只记录日志，不影响其他的
                Tools.log(ex);
            }
        }
    }

    public void remove(String id)
    {
        synchronized (localUserCometInfos)
        {
            remove(localUserCometInfos.get(id));
        }
    }

    public void remove(CometInfo cometInfo)
    {
        if (cometInfo == null)
            return;

        userCometInfos.remove(cometInfo.getId());
        synchronized (localUserCometInfos)
        {
            localUserCometInfos.remove(cometInfo.getId());
        }

        cometIds.getCache(cometInfo.getCometId()).remove(cometInfo.getId());

        List<CometListener> listeners = this.listeners;
        for (CometListener listener : listeners)
        {
            try
            {
                listener.disconnect(cometInfo, this);
            }
            catch (Throwable ex)
            {
                //触发一个监听失败，只记录日志，不影响其他的
                Tools.log(ex);
            }
        }

        cometInfo.getContext().close();
    }

    public boolean ping(String id)
    {
        CometInfo cometInfo;
        synchronized (localUserCometInfos)
        {
            cometInfo = localUserCometInfos.get(id);
        }

        return cometInfo != null && sendMessage0(new CometMessage(Ping.instance, cometInfo.getCometId()), cometInfo);
    }

    public void sendMessage(Serializable message, Integer userId)
    {
        sendMessage(message, "user:" + userId);
    }

    /**
     * 发送消息
     *
     * @param message 消息内容
     * @param cometId cometId
     */
    public void sendMessage(Serializable message, String cometId)
    {
        if (cometId == null)
            throw new NullPointerException();

        Set<String> ids = cometIds.getSet(cometId);
        if (ids != null)
        {
            for (String id : ids)
            {
                CometInfo cometInfo = userCometInfos.getCache(id);
                if (cometInfo != null && cometId.equals(cometInfo.getCometId()))
                    sendMessage(message, cometInfo);
            }
        }
    }

    /**
     * 发送以消息给某些用户
     *
     * @param message 消息内容
     * @param userIds 用户ID集合
     */
    public void sendMessage(Serializable message, Collection<Integer> userIds)
    {
        for (Integer userId : userIds)
        {
            sendMessage(message, "user:" + userId);
        }
    }

    /**
     * 发送消息给所有在线用户
     *
     * @param message 消息内容
     */
    public void sendMessageToAllUser(Serializable message)
    {
        for (String id : userCometInfos.getKeys())
        {
            CometInfo cometInfo = userCometInfos.getCache(id);

            if (cometInfo != null && cometInfo.getCometId().startsWith("user:"))
                sendMessage(message, cometInfo);
        }
    }

    /**
     * 发送一个消息给客户端
     *
     * @param message   消息内容
     * @param cometInfo 用户comet信息
     */
    public void sendMessage(Serializable message, CometInfo cometInfo)
    {
        CometMessage cometMessage = new CometMessage(message, cometInfo.getId());

        if (cometInfo.getContext() == null)
        {
            //用户连接到另外一台服务器节点，将消息放入队列
            messages.update(Tools.getUUID(), cometMessage);
        }
        else
        {
            sendMessage0(cometMessage, cometInfo);
        }
    }

    private boolean sendMessage0(CometMessage message, CometInfo cometInfo)
    {
        //用户连接到当前服务器节点，直接将消息通过cometContext发送给用户
        try
        {
            cometInfo.getContext().send(message);
        }
        catch (CometClosedException ex)
        {
            //客户端断开
            remove(cometInfo);

            return false;
        }
        catch (Exception ex)
        {
            Tools.log(ex);
        }

        return true;
    }

    public boolean isOnline(Integer userId)
    {
        String cometId = "user:" + userId;

        Set<String> ids = cometIds.getSet(cometId);

        return ids != null && !ids.isEmpty();
    }
}
