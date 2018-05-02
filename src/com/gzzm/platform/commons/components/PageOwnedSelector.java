package com.gzzm.platform.commons.components;

import com.gzzm.platform.organ.DeptService;
import net.cyan.arachne.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.arachne.components.AbstractPageComponent;
import net.cyan.commons.util.*;
import net.cyan.commons.util.json.JsonSerializer;
import net.cyan.nest.annotation.Inject;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.*;

/**
 * @author camel
 * @date 12-5-28
 */
@Service
public abstract class PageOwnedSelector extends AbstractPageComponent<Object>
{
    public static class Node implements Serializable
    {
        private static final long serialVersionUID = 1892901274756303599L;

        private String id;

        private String text;

        private boolean leaf;

        private List<Node> children;

        public Node()
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

        public List<Node> getChildren()
        {
            return children;
        }

        public void setChildren(List<Node> children)
        {
            this.children = children;
        }
    }

    public static class Item implements Value<String>
    {
        private static final long serialVersionUID = -6174579061944554916L;

        private String id;

        private String text;

        public Item(String id, String text)
        {
            this.id = id;
            this.text = text;
        }

        public String getId()
        {
            return id;
        }

        public String getText()
        {
            return text;
        }

        public String valueOf()
        {
            return id;
        }

        @Override
        public String toString()
        {
            return text;
        }
    }

    @Inject
    private static Provider<DeptService> serviceProvider;

    @Inject
    private static Provider<HttpServletRequest> requestProvider;

    public PageOwnedSelector()
    {
    }

    protected abstract String getDefaultId();

    protected abstract Node getRoot() throws Exception;

    @Service
    public abstract List<Node> loadChildren(String parent) throws Exception;

    @Service
    public abstract List<Item> loadItems(String nodeId) throws Exception;

    @Service
    public abstract List<String>[] searchNode(String text) throws Exception;

    @Service
    public abstract List<Item> queryItems(String s) throws Exception;

    public String createHtml(String tagName, Map<String, Object> attributes) throws Exception
    {
        String id = StringUtils.toString(attributes.get("id"));
        if (id == null)
            id = expression;
        if (id == null)
            id = getDefaultId();

        String name = StringUtils.toString(attributes.get("name"));
        if (name == null)
            name = expression;

        init(tagName, attributes);

        Object selected = attributes.get("selected");
        if (selected instanceof String)
            selected = BeanUtils.eval((String) selected, RequestContext.getContext().getForm(), null);

        ForwardContext context = RequestContext.getContext().getForwardContext();
        if (context != null)
        {
            context.importJs("/platform/commons/ownedselector.js");
            context.importJs(context.getJsPath() + "/widgets/tree.js");
            context.importJs(context.getJsPath() + "/widgets/itemselector.js");
        }


        StringBuilder buffer = new StringBuilder("<").append(tagName);
        buffer.append(" id=\"").append(HtmlUtils.escapeAttribute(id)).append("\" class=\"ownedselector\">\n");

        buffer.append("<script>\n");

        buffer.append(PageClass.getClassInfo(getClass()).getObjectScript(this, id + "$object"));

        buffer.append("window.").append(id).append("=new System.OwnedSelector(\"").append(HtmlUtils.escapeString(id))
                .append("\",");
        JsonSerializer serializer = new JsonSerializer(buffer, PageConfig.getIgnoreFilter());
        serializer.serialize(getRoot());

        buffer.append(",");
        buffer.append("[");
        if (selected != null)
        {
            boolean first = true;
            for (Iterator iterator = CollectionUtils.getIterator(selected); iterator.hasNext(); )
            {
                if (first)
                    first = false;
                else
                    buffer.append(",");

                Object value = iterator.next();

                buffer.append("{");
                buffer.append("key:");
                serializer.serialize(DataConvert.valueOf(value));
                buffer.append(",");
                buffer.append("text:");
                serializer.serialize(DataConvert.toString(value));
                buffer.append("}");
            }
        }
        buffer.append("],");
        buffer.append("\"").append(HtmlUtils.escapeString(name)).append("\"").append(");\n");

        if (!"false".equals(StringUtils.toString(attributes.get(INIT))))
            buffer.append(id).append(".create();\n");

        buffer.append("</script>\n");

        buffer.append("\n</").append(tagName).append(">");

        return buffer.toString();
    }

    protected void init(String tagName, Map<String, Object> attributes) throws Exception
    {
    }
}
