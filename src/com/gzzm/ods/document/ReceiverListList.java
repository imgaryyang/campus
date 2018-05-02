package com.gzzm.ods.document;

import net.cyan.commons.util.StringUtils;

import java.util.*;

/**
 * @author camel
 * @date 11-9-22
 */
public class ReceiverListList
{
    private List<ReceiverList> receiverLists;

    public ReceiverListList()
    {
    }

    public List<ReceiverList> getReceiverLists()
    {
        return receiverLists == null ? null : receiverLists;
    }

    public void setReceiverLists(List<ReceiverList> receiverLists)
    {
        this.receiverLists = receiverLists;
    }

    public void addReceiverList(ReceiverList receiverList)
    {
        if (receiverLists == null)
            receiverLists = new ArrayList<ReceiverList>();
        receiverLists.add(receiverList);
    }

    public ReceiverList getReceiverList(String sendType)
    {
        if (receiverLists == null)
            return null;

        for (ReceiverList receiverList : receiverLists)
        {
            if (receiverList.getSendType().equals(sendType))
                return receiverList;
        }

        return null;
    }

    public String getReceives(String sendType)
    {
        ReceiverList receiverList = getReceiverList(sendType);
        if (receiverList == null || receiverList.getReceivers() == null)
            return "";

        return StringUtils.concat(receiverList.getReceivers(), ",");
    }

    @Override
    public String toString()
    {
        return receiverLists == null ? "" : StringUtils.concat(receiverLists, "\n");
    }
}
