package com.gzzm.oa.userfile;

/**
 * 共享功能左边目录树结构
 *
 * @author : wmy
 * @date : 2010-5-5
 */

public class UserFileShareNode
{

    /**
     * 树形节点ID，-1表示根节点，u开头表示用户，f开头表示目录
     */
    private String nodeId;

    /**
     * 目录树节点名称
     */
    private String nodeName;

    //虚拟根节点
    public final static UserFileShareNode ROOT = new UserFileShareNode("-1", "所有共享文件");

    public UserFileShareNode(String nodeId, String nodeName)
    {
        this.nodeId = nodeId;
        this.nodeName = nodeName;
    }

    public UserFileShareNode(String nodeId)
    {
        this.nodeId = nodeId;
    }

    public String getNodeId()
    {
        return nodeId;
    }

    public String getNodeName()
    {
        return nodeName;
    }

    public boolean isRoot()
    {
        return "-1".equals(nodeId);
    }

    public boolean isUserNode()
    {
        return nodeId.startsWith("u");
    }

    public boolean isFolderNode()
    {
        return nodeId.startsWith("f");
    }

    public String getIcon()
    {
        if (isRoot())
            return null;
        else if (isUserNode())
            return "/platform/commons/icons/user.gif";
        else
            return "/platform/commons/icons/folder.gif";
    }
}
