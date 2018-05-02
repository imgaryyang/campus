package com.gzzm.platform.receiver;

/**
 * 接收者分组
 *
 * @author camel
 * @date 2010-4-28
 */
public class ReceiverGroup
{
    /**
     * 组的id
     */
    private String id;

    /**
     * 组名
     */
    private String name;

    /**
     * 是否包含子分组
     */
    private boolean hasChildGroups;

    public ReceiverGroup(String id, String name, boolean hasChildGroups)
    {
        this.id = id;
        this.name = name;
        this.hasChildGroups = hasChildGroups;
    }

    public ReceiverGroup(String id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public boolean hasChildGroups()
    {
        return hasChildGroups;
    }
}
