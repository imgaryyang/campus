package com.gzzm.platform.message.comet;

import java.io.Serializable;

/**
 * comet消息
 *
 * @author camel
 * @date 2010-5-31
 */
public class CometMessage implements Serializable
{
    private static final long serialVersionUID = -2781856906470719439L;

    /**
     * 消息的内容
     */
    private Serializable message;

    /**
     * 对应的comet客户端的ID
     */
    private String cometId;

    CometMessage(Serializable message, String cometId)
    {
        this.message = message;
        this.cometId = cometId;
    }

    public Serializable getMessage()
    {
        return message;
    }

    public String getType()
    {
        return message.getClass().getName();
    }

    String getCometId()
    {
        return cometId;
    }
}
