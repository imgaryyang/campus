package com.gzzm.platform.commons.filestore;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.*;

import java.util.List;

/**
 * @author camel
 * @date 13-12-3
 */
public class CatalogItem implements Value<String>
{
    private String type;

    private String id;

    private String name;

    private boolean leaf;

    @NotSerialized
    private List<CatalogItem> children;

    public CatalogItem(String type, String id, String name, boolean leaf)
    {
        this.type = type;
        this.id = id;
        this.name = name;
        this.leaf = leaf;
    }

    public static CatalogItem getRoot()
    {
        return new CatalogItem("root", null, "", false);
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
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

    public List<CatalogItem> getChildren() throws Exception
    {
        return children;
    }

    public void setChildren(List<CatalogItem> children)
    {
        this.children = children;
    }

    @Override
    public String toString()
    {
        return name;
    }

    public String valueOf()
    {
        if (StringUtils.isEmpty(id))
            return type;
        return type + ":" + id;
    }
}
