package com.gzzm.platform.receiver;

import java.io.Serializable;
import java.util.List;

/**
 * 显示接收者组或接收者的树节点
 *
 * @author camel
 */
public class ReceiverTreeNode implements Serializable
{
    private static final long serialVersionUID = 1892901274756303599L;

    public static final String ROOT = "root";

    /**
     * 节点id，root为根节点，以#开头为组，以@开头为接收者，组和接收者的id为 类型:原id，如果没有:为组的根节点
     */
    private String id;

    private String text;

    private boolean leaf;

    private List<ReceiverTreeNode> children;

    /**
     * 对应的接收者对象，如果是接收者组的节点，则为null
     */
    private Receiver receiver;

    public ReceiverTreeNode()
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

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public boolean isLeaf()
    {
        return leaf;
    }

    public void setLeaf(boolean leaf)
    {
        this.leaf = leaf;
    }

    public List<ReceiverTreeNode> getChildren()
    {
        return children;
    }

    public void setChildren(List<ReceiverTreeNode> children)
    {
        this.children = children;
    }

    public Receiver getReceiver()
    {
        return receiver;
    }

    public void setReceiver(Receiver receiver)
    {
        this.receiver = receiver;
    }
}