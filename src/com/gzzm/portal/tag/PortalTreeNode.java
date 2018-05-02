package com.gzzm.portal.tag;

import java.util.*;

/**
 * @author camel
 * @date 12-9-4
 */
public class PortalTreeNode
{
    private String id;

    private String name;

    private List<PortalTreeNode> children;

    public PortalTreeNode()
    {
    }

    public PortalTreeNode(String id, String name)
    {
        this.id = id;
        this.name = name;
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

    public List<PortalTreeNode> getChildren()
    {
        return children;
    }

    public void setChildren(List<PortalTreeNode> children)
    {
        this.children = children;
    }

    public void addChild(PortalTreeNode node)
    {
        if (children == null)
            children = new ArrayList<PortalTreeNode>();

        children.add(node);
    }
}

