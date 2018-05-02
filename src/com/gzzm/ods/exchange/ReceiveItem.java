package com.gzzm.ods.exchange;

import java.util.Date;

/**
 * @author camel
 * @date 12-2-9
 */
public class ReceiveItem
{
    private String title;

    private String sourceDept;

    private Date sendTime;

    private String sendNumber;

    public ReceiveItem()
    {
    }

    public ReceiveItem(ReceiveBase receive)
    {
        title = receive.getDocument().getTitle();
        sourceDept = receive.getDocument().getSourceDept();
        sendNumber = receive.getDocument().getSendNumber();
        sendTime = receive.getSendTime();
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getSourceDept()
    {
        return sourceDept;
    }

    public void setSourceDept(String sourceDept)
    {
        this.sourceDept = sourceDept;
    }

    public Date getSendTime()
    {
        return sendTime;
    }

    public void setSendTime(Date sendTime)
    {
        this.sendTime = sendTime;
    }

    public String getSendNumber()
    {
        return sendNumber;
    }

    public void setSendNumber(String sendNumber)
    {
        this.sendNumber = sendNumber;
    }
}
