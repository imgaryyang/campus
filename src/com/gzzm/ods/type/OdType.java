package com.gzzm.ods.type;

import net.cyan.commons.util.Value;

/**
 * 公文分类，第一级为收文，发文，内部公文，群众来信等等，第二级
 *
 * @author camel
 * @date 13-11-11
 */
public class OdType implements Value<String>
{
    private static final long serialVersionUID = 4009197641520178108L;

    public static final OdType ROOT = new OdType("root", "全部公文", false);

    /**
     * 分类的节点，root表示根节点（全部公文），s:开头表示一个发文分类(发文字号类型)，r:开头表示一个收文分类，
     * 其他表示一个业务类型，如send表示发文，receive表示收文等等，为BusinessModel中的type或者typeName
     * <p/>
     * time表示时间分类，y:开头表示年份，m：开头表示月份
     *
     * @see com.gzzm.ods.business.BusinessModel#type
     * @see com.gzzm.ods.business.BusinessModel#typeName
     */
    private String id;

    /**
     * 名称，根节点为全部公文，发文分类为字号类型名称，收文分类为收文分类名称。
     * 其他为type对应的资源文件中的名称或者typeName
     *
     * @see com.gzzm.ods.business.BusinessModel#type
     * @see com.gzzm.ods.business.BusinessModel#typeName
     */
    private String name;

    /**
     * 是否叶子节点
     */
    private boolean leaf;

    public OdType()
    {
    }

    public OdType(String id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public OdType(String id, String name, boolean leaf)
    {
        this.id = id;
        this.name = name;
        this.leaf = leaf;
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

    public boolean isLeaf()
    {
        return leaf;
    }

    public void setLeaf(boolean leaf)
    {
        this.leaf = leaf;
    }

    @Override
    public String toString()
    {
        return name;
    }

    public String valueOf()
    {
        return id;
    }
}
