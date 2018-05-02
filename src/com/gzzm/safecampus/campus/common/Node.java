package com.gzzm.safecampus.campus.common;

import java.text.CollationKey;
import java.text.Collator;
import java.util.*;

/**
 * 节点信息，用于接收从数据库查询出来的各种节点信息
 *
 * @author yuanfang
 * @date 18-02-07 17:14
 */
public class Node implements Comparable<Node>
{
    private String nodeId;

    private String nodeName;

    private Integer parentId;

    public Node()
    {
    }

    public Node(String nodeId, String nodeName, Integer parentId)
    {
        this.nodeId = nodeId;
        this.nodeName = nodeName;
        this.parentId = parentId;
    }

    public String getNodeId()
    {
        return nodeId;
    }

    public void setNodeId(String nodeId)
    {
        this.nodeId = nodeId;
    }

    public String getNodeName()
    {
        return nodeName;
    }

    public void setNodeName(String nodeName)
    {
        this.nodeName = nodeName;
    }

    public Integer getParentId()
    {
        return parentId;
    }

    public void setParentId(Integer parentId)
    {
        this.parentId = parentId;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(nodeId, node.nodeId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(nodeId);
    }

    @Override
    public String toString()
    {
        return nodeName;
    }

    Collator cmp = Collator.getInstance(java.util.Locale.CHINA);

    @Override
    public int compareTo(Node o)
    {
        CollationKey c1 = cmp.getCollationKey(o.getNodeName());
        CollationKey c2 = cmp.getCollationKey(this.nodeName);
        return cmp.compare(c2.getSourceString(), c1.getSourceString());
    }
}
