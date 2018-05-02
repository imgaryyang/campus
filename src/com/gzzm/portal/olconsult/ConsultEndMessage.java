package com.gzzm.portal.olconsult;

import java.io.Serializable;

/**
 * 咨询者结束咨询的时候向客服发送的消息
 *
 * @author camel
 * @date 13-6-4
 */
public class ConsultEndMessage implements Serializable
{
    private Integer consultId;

    public ConsultEndMessage()
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
}
