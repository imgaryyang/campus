package com.gzzm.portal.olconsult;

import java.io.Serializable;

/**
 * 客服接受咨询的时候想咨询者发送的消息
 *
 * @author camel
 * @date 13-6-4
 */
public class ConsultAcceptMessage implements Serializable
{
    private Integer consultId;

    private String seatName;

    public ConsultAcceptMessage()
    {
    }

    public Integer getConsultId()
    {
        return consultId;
    }

    public void setConsultId(Integer consultId)
    {
        this.consultId = consultId;
    }

    public String getSeatName()
    {
        return seatName;
    }

    public void setSeatName(String seatName)
    {
        this.seatName = seatName;
    }
}
