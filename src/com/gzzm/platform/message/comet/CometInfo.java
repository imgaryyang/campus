package com.gzzm.platform.message.comet;

import com.gzzm.platform.commons.Tools;
import net.cyan.arachne.CometContext;

import java.io.Serializable;

/**
 * 用户comet在线信息
 *
 * @author camel
 * @date 2010-5-29
 */
public class CometInfo implements Serializable
{
    private static final long serialVersionUID = 8204467736879017308L;

    /**
     * id，通过uuid生成，唯一标识
     */
    private String id;

    /**
     * comet的业务ID，由各模块自己决定，每个模块由自己的前缀，如user：
     */
    private String cometId;

    /**
     * comet上下文，用于向客户端发送数据
     */
    private transient CometContext context;

    CometInfo(String cometId, CometContext context)
    {
        this.cometId = cometId;
        this.context = context;
        this.id = Tools.getUUID();
    }

    public String getId()
    {
        return id;
    }

    public String getCometId()
    {
        return cometId;
    }

    public CometContext getContext()
    {
        return context;
    }
}
