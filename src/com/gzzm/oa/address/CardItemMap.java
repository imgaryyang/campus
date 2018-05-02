package com.gzzm.oa.address;

import java.io.Serializable;

/**
 * 导入通讯录时列对应类，表示从导入文件的列和持久类的字段名的映射关系
 *
 * @author whf
 * @date 2010-3-19
 */
public class CardItemMap implements Serializable
{
    private static final long serialVersionUID = 4873834143142395645L;

    /**
     * 导入文件中对应的列名
     */
    private String col;

    /**
     * 对应到数据库表字段名
     */
    private String field;

    public CardItemMap()
    {
    }

    public CardItemMap(String col)
    {
        this.col = col;
    }

    public String getCol()
    {
        return col;
    }

    public void setCol(String col)
    {
        this.col = col;
    }

    public String getField()
    {
        return field;
    }

    public void setField(String field)
    {
        this.field = field;
    }
}
