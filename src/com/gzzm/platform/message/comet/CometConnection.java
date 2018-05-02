package com.gzzm.platform.message.comet;

import java.io.Serializable;

/**
 * comet链接信息，当客户端连接comet后发送一个消息给客户端，告诉客户端当前连接的id
 *
 * @author camel
 * @date 2011-3-31
 */
public class CometConnection implements Serializable
{
    private static final long serialVersionUID = 696654895745262584L;

    private String id;

    public CometConnection(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }
}
