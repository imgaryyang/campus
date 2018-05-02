package com.gzzm.portal.tag;

import net.cyan.commons.util.DataConvert;
import net.cyan.commons.util.json.JsonSerializer;

import java.util.*;

/**
 * @author camel
 * @date 12-9-4
 */
public abstract class TreeTag<N> implements PortalTag
{
    /**
     * 初始化树的函数名
     */
    private String functionName;

    public TreeTag()
    {
    }

    public String getFunctionName()
    {
        if (functionName == null)
            return getDefaultFunctionName();

        return functionName;
    }

    public void setFunctionName(String functionName)
    {
        this.functionName = functionName;
    }

    public Object getValue(Map<String, Object> context) throws Exception
    {
        N root = getRoot(context);

        PortalTreeNode node = toNode(root);

        loadChildren(node, root, context);

        StringBuilder buffer = new StringBuilder("<script>\n");
        buffer.append(getFunctionName()).append("(");
        new JsonSerializer(buffer).serialize(node);
        buffer.append(");\n</script>");

        return buffer.toString();
    }

    private void loadChildren(PortalTreeNode node, N parent, Map<String, Object> context) throws Exception
    {
        for (N child : getChildren(parent, context))
        {
            if (accept(child, context))
            {
                PortalTreeNode childrenNode = toNode(child);
                node.addChild(childrenNode);

                loadChildren(childrenNode, child, context);
            }
        }
    }

    protected PortalTreeNode toNode(N node) throws Exception
    {
        return new PortalTreeNode(valueOf(node), toString(node));
    }

    protected abstract N getRoot(Map<String, Object> context) throws Exception;

    protected abstract Collection<N> getChildren(N parent, Map<String, Object> context) throws Exception;

    protected abstract String getDefaultFunctionName();

    @SuppressWarnings("UnusedDeclaration")
    protected boolean accept(N node, Map<String, Object> context) throws Exception
    {
        return true;
    }

    protected String toString(N node) throws Exception
    {
        return node.toString();
    }

    protected String valueOf(N node) throws Exception
    {
        return DataConvert.getValueString(node);
    }
}