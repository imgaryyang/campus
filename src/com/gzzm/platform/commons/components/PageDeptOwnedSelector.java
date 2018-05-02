package com.gzzm.platform.commons.components;

import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 属于某个部门的数据的选择器
 *
 * @author camel
 * @date 12-5-28
 */
@Service
public abstract class PageDeptOwnedSelector extends PageOwnedSelector
{
    @Inject
    protected static Provider<DeptService> deptServiceProvider;

    @Inject
    protected static Provider<HttpServletRequest> requestProvider;


    @Inject
    protected static Provider<OrganDao> organDaoProvider;

    /**
     * 根部门ID
     */
    protected Integer rootId;

    /**
     * 部门过滤器，只显示此过滤器接受的部门
     */
    @NotSerialized
    protected Filter<DeptInfo> filter;

    /**
     * 条件，之显示满足此条件的部门，当filter为null是有效
     */
    protected String condition;

    /**
     * 对应的权限ID，如果为null表示为当前功能
     */
    protected String appId;

    /**
     * 当没有权限时加载此部门的子部门
     */
    protected Integer deptId;

    protected DeptService deptService;

    protected Collection<Integer> authDeptIds;

    protected OrganDao organDao;

    public PageDeptOwnedSelector()
    {
    }

    public Integer getRootId()
    {
        return rootId;
    }

    public void setRootId(Integer rootId)
    {
        if (rootId != null && Null.isNull(rootId))
            rootId = null;

        this.rootId = rootId;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        if (deptId != null && Null.isNull(deptId))
            deptId = null;

        this.deptId = deptId;
    }

    protected Filter<DeptInfo> getFilter()
    {
        if (filter == null && condition != null)
            filter = ExpressionDeptFilter.getFilter(condition);
        return filter;
    }

    public void setFilter(Filter<DeptInfo> filter)
    {
        this.filter = filter;
    }

    public String getCondition()
    {
        return condition;
    }

    public void setCondition(String condition)
    {
        this.condition = condition;
    }

    public String getAppId()
    {
        return appId;
    }

    public void setAppId(String appId)
    {
        this.appId = appId;
    }

    protected DeptService getDeptService()
    {
        if (deptService == null)
            deptService = deptServiceProvider.get();
        return deptService;
    }

    protected OrganDao getOrganDao() throws Exception
    {
        if (organDao == null)
        {
            if (deptService != null)
                organDao = deptService.getDao();
            else
                organDao = organDaoProvider.get();
        }

        return organDao;
    }

    @SuppressWarnings("unchecked")
    protected Collection<Integer> getAuthDeptIds()
    {
        HttpServletRequest request = requestProvider.get();
        UserOnlineInfo userOnlineInfo = UserOnlineInfo.getUserOnlineInfo(request);

        if (authDeptIds == null && !"".equals(appId))
        {
            String appId = getAppId();
            if (appId == null)
                authDeptIds = userOnlineInfo.getAuthDeptIds(request, getFilter());
            else
                authDeptIds = userOnlineInfo.getAuthDeptIds(appId, getFilter());
        }

        if (authDeptIds != null && authDeptIds.size() == 0)
        {
            if (deptId == null && !userOnlineInfo.isAdmin())
                deptId = userOnlineInfo.getBureauId();

            return null;
        }


        return authDeptIds;
    }

    @Override
    protected void init(String tagName, Map<String, Object> attributes) throws Exception
    {
        Integer rootId = DataConvert.convertType(Integer.class, attributes.get("rootId"));
        if (rootId != null && !Null.isNull(rootId))
            this.rootId = rootId;

        Integer deptId = DataConvert.convertType(Integer.class, attributes.get("deptId"));
        if (deptId != null && !Null.isNull(deptId))
            this.deptId = deptId;

        String condition = StringUtils.toString(attributes.get("condition"));
        if (!StringUtils.isEmpty(condition))
            this.condition = condition;

        String appId = StringUtils.toString(attributes.get("appId"));
        if (!StringUtils.isEmpty(appId))
            setAppId(appId);
    }

    @SuppressWarnings("unchecked")
    private Node getTreeNode(SimpleDeptInfo dept) throws Exception
    {
        Node node = new Node();

        node.setId(dept.getDeptId().toString());
        node.setText(dept.getDeptName());
        node.setLeaf(isLeaf(dept));

        if (dept == SimpleDeptInfo.ROOT)
            node.setChildren(loadChildren(-1));

        return node;
    }

    private boolean isLeaf(SimpleDeptInfo dept) throws Exception
    {
        return dept != SimpleDeptInfo.ROOT &&
                !getDeptService().containsSubAuthDept(dept.getDeptId(), getFilter(), getAuthDeptIds());
    }

    @Override
    protected Node getRoot() throws Exception
    {
        return getTreeNode(SimpleDeptInfo.ROOT);
    }

    @Override
    @Service
    public List<Node> loadChildren(String parent) throws Exception
    {
        return loadChildren(Integer.valueOf(parent));
    }

    protected List<Node> loadChildren(Integer parent) throws Exception
    {
        Collection<Integer> authDeptIds = getAuthDeptIds();

        Integer rootId = this.rootId;

        if (rootId == null && authDeptIds == null)
            rootId = this.deptId;

        if (parent == -1)
        {
            if (rootId != null)
            {
                if (authDeptIds == null || authDeptIds.contains(rootId))
                    return Collections.singletonList(getTreeNode(getDeptService().getDept(rootId)));
            }

            parent = rootId;
        }

        List<SimpleDeptInfo> depts = getDeptService().getAuthDeptTree(parent, getFilter(), authDeptIds);

        List<Node> result = new ArrayList<Node>(depts.size());

        for (SimpleDeptInfo dept : depts)
            result.add(getTreeNode(dept));

        return result;
    }

    @Override
    @Service
    @SuppressWarnings("unchecked")
    public List<String>[] searchNode(String text) throws Exception
    {
        Collection<Integer> authDeptIds = getAuthDeptIds();

        List<? extends DeptInfo> depts = getDeptService().searchDept(text, authDeptIds);

        List<String>[] result = new List[depts.size()];

        int index = 0;
        for (DeptInfo dept : depts)
        {
            List<String> path = result[index++] = new ArrayList<String>();
            path.add("-1");

            while (dept != null)
            {
                Integer deptId = dept.getDeptId();
                if (authDeptIds != null && !authDeptIds.contains(deptId))
                    break;

                path.add(1, deptId.toString());
                dept = dept.parentDept();
            }
        }

        return result;
    }

    @Override
    public List<Item> loadItems(String nodeId) throws Exception
    {
        return loadItems(Integer.valueOf(nodeId), false);
    }

    protected List<Item> loadItems(Integer deptId, boolean containsSub) throws Exception
    {
        OrganDao dao = getOrganDao();

        if (containsSub)
        {
            List<Item> items = new ArrayList<Item>();

            loadItems(deptServiceProvider.get().getDept(deptId), items, dao);

            return items;
        }
        else
        {
            List<Item> items = loadItems(deptId);

            if (items == null || items.size() == 0)
                return loadItems(deptId, true);

            return items;
        }
    }

    protected void loadItems(DeptInfo dept, List<Item> items, OrganDao dao) throws Exception
    {
        Integer deptId = dept.getDeptId();

        if (getAuthDeptIds() == null || getAuthDeptIds().contains(deptId))
        {
            List<Item> items1 = loadItems(deptId);
            if (items1 != null)
            {
                for (Item item : items1)
                {
                    String id = item.getId();
                    boolean exists = false;
                    for (Item item1 : items)
                    {
                        if (item1.getId().equals(id))
                        {
                            exists = true;
                            break;
                        }
                    }

                    if (!exists)
                        items.add(item);

                    if (items.size() > 100)
                        return;
                }
            }
        }

        for (DeptInfo subDept : dept.subDepts())
            loadItems(subDept, items, dao);
    }

    protected abstract List<Item> loadItems(Integer deptId) throws Exception;
}
