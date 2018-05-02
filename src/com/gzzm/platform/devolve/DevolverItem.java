package com.gzzm.platform.devolve;

/**
 * 定义一个工作移项目
 *
 * @author camel
 * @date 13-1-7
 */
public class DevolverItem
{
    private String id;

    private String name;

    private Devolver devolver;

    public DevolverItem()
    {
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Devolver getDevolver()
    {
        return devolver;
    }

    public void setDevolver(Devolver devolver)
    {
        this.devolver = devolver;
    }
}
