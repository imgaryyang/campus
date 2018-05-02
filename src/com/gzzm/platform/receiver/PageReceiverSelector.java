package com.gzzm.platform.receiver;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.organ.DeptService;
import net.cyan.arachne.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.arachne.components.AbstractPageComponent;
import net.cyan.commons.util.*;
import net.cyan.commons.util.json.JsonSerializer;
import net.cyan.nest.annotation.Inject;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 接收者选择组件，左边是分类树，中间是可选的接收者列表，右边是已选择的接收者
 *
 * @author camel
 * @date 2010-5-9
 */
@Service
public class PageReceiverSelector extends AbstractPageComponent<Object>
{
    @Inject
    private static Provider<HttpServletRequest> requestProvider;

    @Inject
    private DeptService deptService;

    /**
     * 匹配查询时最多返回多少条数据
     */
    private int maxCount = 10;

    /**
     * 类型，如邮件，手机号码等
     *
     * @see com.gzzm.platform.receiver.ReceiversLoadContext#type
     * @see com.gzzm.platform.receiver.ReceiversLoadContext#EMAIL
     * @see com.gzzm.platform.receiver.ReceiversLoadContext#PHONE
     */
    private String type;

    private String typeName;

    private String appId;

    private Integer deptId;

    /**
     * 用户在线信息
     */
    @Inject
    private UserOnlineInfo userOnlineInfo;

    /**
     * 拥有权限的部门
     */
    private Collection<Integer> authDeptIds;

    private boolean authDeptIdsloaded;

    @Inject
    private ReceiverService service;

    /**
     * 是否以简单方式展示，简单方式是指接收者和分组在同一个棵树中，true表示用简单方式展示
     */
    private boolean simple;

    private ReceiversLoadContext context;

    public PageReceiverSelector()
    {
    }

    public int getMaxCount()
    {
        return maxCount;
    }

    public void setMaxCount(int maxCount)
    {
        this.maxCount = maxCount;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getTypeName()
    {
        return typeName;
    }

    public void setTypeName(String typeName)
    {
        this.typeName = typeName;
    }

    public String getAppId()
    {
        return appId;
    }

    public void setAppId(String appId)
    {
        this.appId = appId;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public boolean isSimple()
    {
        return simple;
    }

    public void setSimple(boolean simple)
    {
        this.simple = simple;
    }

    @SuppressWarnings("unchecked")
    public Collection<Integer> getAuthDeptIds() throws Exception
    {
        if (!authDeptIdsloaded)
        {
            HttpServletRequest request = requestProvider.get();
            UserOnlineInfo userOnlineInfo = UserOnlineInfo.getUserOnlineInfo(request);

            if (appId == null)
                authDeptIds = userOnlineInfo.getAuthDeptIds(request, null);
            else
                authDeptIds = userOnlineInfo.getAuthDeptIds(appId, null);

            if (authDeptIds != null && authDeptIds.isEmpty())
            {
                authDeptIds =
                        deptService.getDept(deptId == null ? userOnlineInfo.getBureauId() : deptId).allSubDeptIds();
            }

            authDeptIdsloaded = true;
        }

        return authDeptIds;
    }

    private ReceiversLoadContext getContext() throws Exception
    {
        if (context == null)
        {
            context = new ReceiversLoadContext();

            context.setUser(userOnlineInfo);
            context.setType(type);
            context.setAuthDeptIds(getAuthDeptIds());
            context.setMaxCount(maxCount);
        }

        return context;
    }


    public String createHtml(String tagName, Map<String, Object> attributes) throws Exception
    {
        String id = StringUtils.toString(attributes.get("id"));
        if (id == null)
            id = "receiverSelector";

        String name = StringUtils.toString(attributes.get("name"));

        Object selected = attributes.get("selected");
        if (selected instanceof String)
            selected = BeanUtils.eval((String) selected, RequestContext.getContext().getForm(), null);

        ForwardContext context = RequestContext.getContext().getForwardContext();
        context.importJs("/platform/receiver/receiverselector.js");
        context.importCss("/platform/receiver/receiverselector.css");
        context.importJs(context.getJsPath() + "/widgets/tree.js");
        context.importJs(context.getJsPath() + "/widgets/itemselector.js");

        StringBuilder buffer = new StringBuilder("<").append(tagName);
        buffer.append(" id=\"").append(HtmlUtils.escapeAttribute(id)).append("\" class=\"receiverselector\">\n");

        buffer.append("<script>\n");

        buffer.append(PageClass.getClassInfo(PageReceiverSelector.class).getObjectScript(this, id));

        buffer.append("new System.ReceiverSelector(\"").append(HtmlUtils.escapeString(id)).append("\",");
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
        buffer.append("\"").append(HtmlUtils.escapeString(name)).append("\"");

        buffer.append(").create();\n");

        buffer.append("</script>\n");

        buffer.append("\n</").append(tagName).append(">");

        return buffer.toString();
    }

    private ReceiverTreeNode getRoot() throws Exception
    {
        ReceiverTreeNode node = new ReceiverTreeNode();
        node.setId(ReceiverTreeNode.ROOT);
        node.setText("");
        node.setChildren(loadChildren(ReceiverTreeNode.ROOT));

        return node;
    }

    /**
     * 加载树的子节点
     *
     * @param parent 父节点的id，规则见ReceiverTreeNode.id的注释
     * @return 子节点列表
     * @throws Exception 异常
     * @see com.gzzm.platform.receiver.ReceiverTreeNode#id
     */
    @Service
    public List<ReceiverTreeNode> loadChildren(String parent) throws Exception
    {
        ReceiversLoadContext context = getContext();

        if (ReceiverTreeNode.ROOT.equals(parent))
        {
            //根节点，显示所有接收者
            List<? extends ReceiverProvider> providers = service.getReceiverProviders();
            List<ReceiverTreeNode> nodes = new ArrayList<ReceiverTreeNode>(providers.size());
            for (ReceiverProvider provider : providers)
            {
                if (provider.accept(context))
                {
                    ReceiverTreeNode node = new ReceiverTreeNode();
                    node.setId("#" + provider.getId());
                    node.setText(Tools.getMessage(provider.getName()));
                    node.setLeaf(!provider.isGroup() && !simple);

                    nodes.add(node);
                }
            }

            if (nodes.size() == 1)
            {
                //只有一种类型的接收者，跳过，直接显示其下的分组
                return loadChildren(nodes.get(0).getId());
            }

            return nodes;
        }

        if (parent.startsWith("#"))
        {
            //组
            parent = parent.substring(1);
            int index = parent.indexOf(':');

            String type;
            String parentGroupId;

            if (index < 0)
            {
                type = parent;
                parentGroupId = null;
            }
            else
            {
                type = parent.substring(0, index);
                parentGroupId = parent.substring(index + 1);
            }

            List<ReceiverTreeNode> nodes = new ArrayList<ReceiverTreeNode>();

            List<ReceiverGroup> groups = service.getGroups(context, type, parentGroupId);
            for (ReceiverGroup group : groups)
            {
                ReceiverTreeNode node = new ReceiverTreeNode();
                node.setId("#" + type + ":" + group.getId());
                node.setText(group.getName());
                node.setLeaf(!group.hasChildGroups() && !simple);

                nodes.add(node);
            }

            if (simple)
            {
                List<Receiver> receivers = service.getReceivers(context, type, parentGroupId);
                for (Receiver receiver : receivers)
                {
                    ReceiverTreeNode node = new ReceiverTreeNode();
                    node.setId("@" + type + ":" + receiver.getId());
                    node.setText(receiver.getName());
                    node.setLeaf(true);
                    node.setReceiver(receiver);

                    nodes.add(node);
                }
            }

            return nodes;
        }

        return null;
    }

    /**
     * 加载某个组里的接收者
     *
     * @param group 组，命名规则为：#组类型:id
     * @return 接收者列表
     * @throws Exception 查page询数据异常
     */
    @Service
    public List<Receiver> loadReceivers(String group) throws Exception
    {
        //组
        group = group.substring(1);
        int index = group.indexOf(':');

        String type;
        String groupId;

        if (index < 0)
        {
            type = group;
            groupId = null;
        }
        else
        {
            type = group.substring(0, index);
            groupId = group.substring(index + 1);
        }

        return service.getReceivers(getContext(), type, groupId);
    }

    /**
     * 根据一个字符串查询相关的接收人，
     *
     * @param s 输入的字符串，可以是姓名的，也可以是拼音，或者是简拼或者是邮件地址
     * @return 匹配的接收者列表
     * @throws Exception 异常
     */
    @Service
    public List<Receiver> queryReceiver(String s) throws Exception
    {
        ReceiversLoadContext context = new ReceiversLoadContext();

        context.setUser(userOnlineInfo);
        context.setType(type);
        context.setAuthDeptIds(getAuthDeptIds());
        context.setMaxCount(maxCount);

        return service.queryReceiver(s, context);
    }

    @Service
    public List<List<String>> queryGroup(String s) throws Exception
    {
        ReceiversLoadContext context = getContext();

        List<List<String>> result = null;

        for (ReceiverProvider provider : service.getReceiverProviders())
        {
            List<String> groupIds = provider.queryGroup(context, s);

            if (groupIds != null && groupIds.size() > 0)
            {
                if (result == null)
                    result = new ArrayList<List<String>>();

                for (String groupId : groupIds)
                {
                    String type = provider.getId();

                    List<String> path = new ArrayList<String>();

                    path.add(ReceiverTreeNode.ROOT);
                    path.add("#" + type);

                    while (groupId != null)
                    {
                        path.add(2, "#" + type + ":" + groupId);
                        groupId = provider.getParentGroupId(context, groupId);
                    }

                    result.add(path);
                }
            }
        }

        return result;
    }
}
