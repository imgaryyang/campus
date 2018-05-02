package com.gzzm.platform.organ;

import net.cyan.arachne.components.*;

import java.util.*;

/**
 * @author camel
 * @date 2015/11/30
 */
public class PageUserTreeModel
        implements LazyPageTreeModel<PageUserTreeModel.Node>, SearchablePageTreeModel<PageUserTreeModel.Node>,
        CheckBoxTreeModel<PageUserTreeModel.Node>, SelectableModel<PageUserTreeModel.Node>
{
    public static class Node
    {
        private String id;

        private String name;

        private List<Node> children;

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
    }

    private boolean showBox;

    public PageUserTreeModel()
    {
    }

    public boolean isShowBox()
    {
        return showBox;
    }

    public void setShowBox(boolean showBox)
    {
        this.showBox = showBox;
    }

    @Override
    public boolean hasCheckBox(Node node) throws Exception
    {
        return showBox;
    }

    @Override
    public Boolean isChecked(Node node) throws Exception
    {
        return false;
    }

    @Override
    public boolean isLazyLoad(Node node) throws Exception
    {
        throw new UnsupportedOperationException("Method not implemented.");
    }

    @Override
    public void beforeLazyLoad(String id) throws Exception
    {
        throw new UnsupportedOperationException("Method not implemented.");
    }

    @Override
    public boolean isSearchable() throws Exception
    {
        throw new UnsupportedOperationException("Method not implemented.");
    }

    @Override
    public Collection<Node> search(String text) throws Exception
    {
        throw new UnsupportedOperationException("Method not implemented.");
    }

    @Override
    public Node getParent(Node node) throws Exception
    {
        throw new UnsupportedOperationException("Method not implemented.");
    }

    @Override
    public Node getRoot() throws Exception
    {
        throw new UnsupportedOperationException("Method not implemented.");
    }

    @Override
    public boolean isLeaf(Node node) throws Exception
    {
        throw new UnsupportedOperationException("Method not implemented.");
    }

    @Override
    public int getChildCount(Node parent) throws Exception
    {
        throw new UnsupportedOperationException("Method not implemented.");
    }

    @Override
    public Node getChild(Node parent, int index) throws Exception
    {
        throw new UnsupportedOperationException("Method not implemented.");
    }

    @Override
    public String getId(Node node) throws Exception
    {
        throw new UnsupportedOperationException("Method not implemented.");
    }

    @Override
    public String toString(Node node) throws Exception
    {
        throw new UnsupportedOperationException("Method not implemented.");
    }

    @Override
    public Node getNode(String id) throws Exception
    {
        throw new UnsupportedOperationException("Method not implemented.");
    }

    @Override
    public Boolean isRootVisible()
    {
        return false;
    }

    @Override
    public boolean isSelectable(Node node) throws Exception
    {
        throw new UnsupportedOperationException("Method not implemented.");
    }
}
