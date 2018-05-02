package com.gzzm.platform.appauth;

/**
 * @author camel
 * @date 2011-5-13
 */
public class App
{
    private String type;

    private Integer id;

    private String name;

    public App()
    {
    }

    public App(String type, Integer id, String name)
    {
        this.type = type;
        this.id = id;
        this.name = name;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
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

    public String getKey()
    {
        return type + "$" + id;
    }

    public void setKey(String key)
    {
        int index = key.indexOf("$");

        type = key.substring(0, index);
        id = Integer.parseInt(key.substring(index + 1));
    }
}
