package com.gzzm.ods.document;

import com.gzzm.platform.group.Member;
import net.cyan.commons.util.StringUtils;

import java.io.Serializable;
import java.util.*;

/**
 * 接收者列表
 *
 * @author camel
 * @date 11-9-22
 */
public class ReceiverList implements Serializable
{
    private static final long serialVersionUID = -3319558315665441566L;

    /**
     * 接收类型，如主送等
     */
    private String sendType;

    /**
     * 接收者列表
     */
    private List<Member> receivers;

    public ReceiverList()
    {
    }

    public ReceiverList(String sendType)
    {
        this.sendType = sendType;
    }

    public String getSendType()
    {
        return sendType;
    }

    public void setSendType(String sendType)
    {
        this.sendType = sendType;
    }

    public List<Member> getReceivers()
    {
        return receivers;
    }

    public void setReceivers(List<Member> receivers)
    {
        this.receivers = receivers;
    }

    public void addReceiver(Member receiver)
    {
        if (receivers == null)
        {
            receivers = new ArrayList<Member>();
        }
        else if (receivers.contains(receiver))
        {
            return;
        }

        receivers.add(receiver);
    }

    @Override
    public String toString()
    {
        return sendType + "：" + StringUtils.concat(receivers, "，");
    }
}
