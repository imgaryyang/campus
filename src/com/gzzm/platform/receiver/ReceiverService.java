package com.gzzm.platform.receiver;

import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 与接收者操作相关的Service类
 *
 * @author camel
 * @date 2010-5-9
 */
public class ReceiverService
{
    private final static List<? extends ReceiverProvider> DEFAULTRECEIVERPROVIDERS =
            Arrays.asList(new UserReceiverProvider());

    @Inject
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private List<ReceiverProvider> receiverProviders;

    public ReceiverService()
    {
    }

    public List<? extends ReceiverProvider> getReceiverProviders()
    {
        return receiverProviders == null ? DEFAULTRECEIVERPROVIDERS : receiverProviders;
    }

    /**
     * 获得组列表
     *
     * @param context    上下文信息
     * @param type       接收者的类型，即ReceiverProvider的getId方法返回的值
     * @param parentGroupId 上一级的组id，如果是根目录，则为null
     * @return 组列表
     * @throws Exception 由ReceiverProvider产生的异常，一般是数据查询错误
     * @see com.gzzm.platform.receiver.ReceiverProvider#getId()
     */
    public List<ReceiverGroup> getGroups(ReceiversLoadContext context, String type, String parentGroupId) throws Exception
    {
        for (ReceiverProvider provider : getReceiverProviders())
        {
            if (provider.getId().equals(type))
                return provider.getGroups(context, parentGroupId);
        }

        return null;
    }

    /**
     * 获得某个组里的接收者列表
     *
     * @param context 上下文信息
     * @param type    接收者的类型，即ReceiverProvider的getId方法返回的值
     * @param groupId 上一级的组id，如果是根目录，则为null
     * @return 组列表
     * @throws Exception 由ReceiverProvider产生的异常，一般是数据查询错误
     * @see com.gzzm.platform.receiver.ReceiverProvider#getId()
     */
    public List<Receiver> getReceivers(ReceiversLoadContext context, String type, String groupId) throws Exception
    {
        for (ReceiverProvider provider : getReceiverProviders())
        {
            if (provider.getId().equals(type))
                return provider.getReceivers(context, groupId);
        }

        return null;
    }

    /**
     * 根据一个字符串查询相关的接收人，
     *
     * @param s       输入的字符串，可以是姓名的，也可以是拼音，或者是简拼或者是邮件地址
     * @param context 上下文信息
     * @return 匹配的接收者列表
     * @throws Exception 异常
     */
    public List<Receiver> queryReceiver(String s, ReceiversLoadContext context) throws Exception
    {
        int maxCount = context.getMaxCount();

        List<Receiver> receivers = new ArrayList<Receiver>();

        for (ReceiverProvider provider : getReceiverProviders())
        {
            if (provider.accept(context))
            {
                List<Receiver> receivers1 = provider.queryReceivers(context, s);

                if (receivers1 != null)
                {
                    if (maxCount <= 0)
                    {
                        receivers.addAll(receivers1);
                    }
                    else if (receivers1.size() <= maxCount)
                    {
                        receivers.addAll(receivers1);

                        maxCount -= receivers1.size();

                        if (maxCount == 0)
                            break;

                        context.setMaxCount(maxCount);
                    }
                    else
                    {
                        receivers.addAll(receivers1.subList(0, maxCount));
                        break;
                    }
                }
            }
        }

        return receivers;
    }
}
