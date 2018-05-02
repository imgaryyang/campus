package com.gzzm.ods.exchange.back;

/**
 * 用于退文理由选择页面数据封装
 *
 * @author ldp
 * @date 2018/1/10
 */
public class BackReasonBean
{
    private Integer id;

    private String name;

    /**
     * 类型
     * 0=类型；1=理由
     */
    private int type;

    public BackReasonBean()
    {
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

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
