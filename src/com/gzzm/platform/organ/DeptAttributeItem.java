package com.gzzm.platform.organ;

/**
 * @author camel
 * @date 11-12-27
 */
public class DeptAttributeItem
{
    private String name;

    private String label;

    private String selectableSql;

    public DeptAttributeItem()
    {
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getSelectableSql()
    {
        return selectableSql;
    }

    public void setSelectableSql(String selectableSql)
    {
        this.selectableSql = selectableSql;
    }
}
