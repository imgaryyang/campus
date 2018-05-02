package com.gzzm.ods.document;

import net.cyan.thunwind.annotation.*;

/**
 * 公文接收者列表
 *
 * @author camel
 * @date 11-9-22
 */
@Entity(table = "ODDOCUMENTRECEIVERLIST", keys = "receiverListId")
public class DocumentReceiverList
{
    /**
     * 公文ID
     */
    @Generatable(length = 12, name = "ODDOCUMENTRECEIVERLISTID")
    private Long receiverListId;

    /**
     * 接收者信息，用xml保存到数据库
     */
    @Xml
    private ReceiverListList receivers;

    public DocumentReceiverList()
    {
    }

    public Long getReceiverListId()
    {
        return receiverListId;
    }

    public void setReceiverListId(Long receiverListId)
    {
        this.receiverListId = receiverListId;
    }

    public ReceiverListList getReceivers()
    {
        return receivers;
    }

    public void setReceivers(ReceiverListList receivers)
    {
        this.receivers = receivers;
    }

    public String getReceives(String sendType)
    {
        return getReceivers().getReceives(sendType);
    }

    public ReceiverList getReceiverList(String sendType)
    {
        return getReceivers().getReceiverList(sendType);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof DocumentReceiverList))
            return false;

        DocumentReceiverList that = (DocumentReceiverList) o;

        return receiverListId.equals(that.receiverListId);
    }

    @Override
    public int hashCode()
    {
        return receiverListId.hashCode();
    }
}
