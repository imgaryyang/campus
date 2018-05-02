package com.gzzm.platform.organ;

import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.arachne.components.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.*;

/**
 * @author camel
 * @date 2010-3-11
 */
public class AuthDeptTreeModel implements LazyPageTreeModel<SimpleDeptInfo>, SearchablePageTreeModel<SimpleDeptInfo>,
        CheckBoxTreeModel<SimpleDeptInfo>, SelectableModel<SimpleDeptInfo>, Serializable
{
    private static final long serialVersionUID = 5874788274969051671L;

    /**
     * 虚拟部门，对于某些没有权限，但是显示的中级节点
     *
     * @see #full
     */
    public static class VirtualDept implements SimpleDeptInfo
    {
        private static final long serialVersionUID = -3319767724427239610L;

        private Integer deptId;

        private String deptName;

        public VirtualDept(Integer deptId, String deptName)
        {
            this.deptId = deptId;
            this.deptName = deptName;
        }

        public Integer getDeptId()
        {
            return deptId;
        }

        public String getDeptName()
        {
            return deptName;
        }

        public int getLevel()
        {
            return -1;
        }

        public Integer getParentDeptId()
        {
            return null;
        }

        public String getDeptCode()
        {
            return null;
        }

        @Override
        public String getOrgCode()
        {
            return null;
        }

        public SimpleDeptInfo parentDept()
        {
            return null;
        }

        public SimpleDeptInfo getParentDept(int level)
        {
            return null;
        }

        public List<String> getAllNameList(int level)
        {
            return null;
        }

        public String getAllName(int level)
        {
            return null;
        }

        public List<String> allNameList()
        {
            return null;
        }

        public String allName()
        {
            return null;
        }

        public Integer valueOf()
        {
            return deptId;
        }
    }

    @Inject
    private static Provider<DeptService> serviceProvider;

    @Inject
    private static Provider<HttpServletRequest> requestProvider;

    /**
     * 根部门ID
     */
    private Integer rootId;

    /**
     * 部门级别，只加载此级别的部门
     */
    private int level = -1;

    /**
     * 部门过滤器，只显示此过滤器接受的部门
     */
    private transient Filter<DeptInfo> filter;

    /**
     * 条件，之显示满足此条件的部门，当filter为null是有效
     */
    private String condition;

    /**
     * 对应的权限ID，如果为null表示为当前功能
     */
    private String appId;

    private Integer scopeId;

    private String scopeName;

    @NotSerialized
    private transient DeptService service;

    private Collection<Integer> authDeptIds;

    private boolean authDeptIdsLoaded;

    private Map<Integer, List<SimpleDeptInfo>> childrenMap;

    private boolean showBox;

    /**
     * 如果为true，完整的树，包括中间一些没有权限的节点，并用虚拟节点代替这些节点
     * 如果为false，只显示有权限的节点
     *
     * @see VirtualDept
     */
    private boolean full;

    private transient Filter<DeptInfo> selectable;

    public AuthDeptTreeModel()
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

    public boolean isFull()
    {
        return full;
    }

    public void setFull(boolean full)
    {
        this.full = full;
    }

    @NotSerialized
    public SimpleDeptInfo getRoot() throws Exception
    {
        //返回一个虚拟的根节点
        return SimpleDeptInfo.ROOT;
    }

    public boolean isLeaf(SimpleDeptInfo dept) throws Exception
    {
        return dept != SimpleDeptInfo.ROOT &&
                !getService().containsSubAuthDept(dept.getDeptId(), getFilter(), getAuthDeptIds());
    }

    private List<SimpleDeptInfo> getChildren(Integer parent) throws Exception
    {
        if (childrenMap == null)
            childrenMap = new HashMap<Integer, List<SimpleDeptInfo>>();

        List<SimpleDeptInfo> children = childrenMap.get(parent);
        if (children == null)
        {
            Collection<Integer> authDeptIds = getAuthDeptIds();

            if (parent == -1)
            {
                Integer rootId = this.rootId;
                if (rootId == null)
                    rootId = 1;

                if (rootId != null && (authDeptIds == null || authDeptIds.contains(rootId)))
                    children = Collections.<SimpleDeptInfo>singletonList(getService().getDept(rootId));
                else
                    parent = rootId;
            }

            if (children == null)
            {
                if (full)
                {
                    DeptService service = getService();
                    Filter<DeptInfo> filter = getFilter();

                    children = new ArrayList<SimpleDeptInfo>();

                    boolean root = DataConvert.equal(parent, rootId);

                    DeptInfo parentDeptInfo = parent == null ? service.getRoot() : service.getDept(parent);

                    while (true)
                    {
                        for (DeptInfo deptInfo : parentDeptInfo.subDepts())
                        {
                            Integer deptId = deptInfo.getDeptId();

                            if ((authDeptIds == null || authDeptIds.contains(deptId)) &&
                                    (filter == null || filter.accept(deptInfo)))
                            {
                                children.add(deptInfo);
                            }
                            else if (service.containsSubAuthDept(deptId, filter, authDeptIds))
                            {
                                children.add(new VirtualDept(deptId, deptInfo.getDeptName()));

                                parentDeptInfo = deptInfo;
                            }
                        }

                        if (!root || children.size() > 1 || !(children.get(0) instanceof VirtualDept))
                            break;

                        children.clear();
                    }
                }
                else
                {
                    children = getService().getAuthDeptTree(parent, getFilter(), authDeptIds);
                }
            }

            childrenMap.put(parent, children);
        }

        return children;
    }

    public int getChildCount(SimpleDeptInfo parent) throws Exception
    {
        return getChildren(parent.getDeptId()).size();
    }

    public SimpleDeptInfo getChild(SimpleDeptInfo parent, int index) throws Exception
    {
        return getChildren(parent.getDeptId()).get(index);
    }

    public String getId(SimpleDeptInfo dept) throws Exception
    {
        return dept.getDeptId().toString();
    }

    public String toString(SimpleDeptInfo dept) throws Exception
    {
        return dept.getDeptName();
    }

    public SimpleDeptInfo getNode(String id) throws Exception
    {
        if ("-1".equals(id))
            return SimpleDeptInfo.ROOT;

        return getService().getDept(Integer.valueOf(id));
    }

    public boolean isLazyLoad(SimpleDeptInfo dept) throws Exception
    {
        return dept != SimpleDeptInfo.ROOT;
    }

    public void beforeLazyLoad(String id) throws Exception
    {
    }

    public Integer getRootId()
    {
        return rootId;
    }

    public void setRootId(Integer rootId)
    {
        this.rootId = rootId;
    }

    public int getLevel()
    {
        return level;
    }

    public void setLevel(int level)
    {
        this.level = level;
    }

    private Filter<DeptInfo> getFilter()
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

    public Integer getScopeId()
    {
        return scopeId;
    }

    public void setScopeId(Integer scopeId)
    {
        this.scopeId = scopeId;
    }

    public String getScopeName()
    {
        return scopeName;
    }

    public void setScopeName(String scopeName)
    {
        this.scopeName = scopeName;
    }

    public void setSelectable(Filter<DeptInfo> selectable)
    {
        this.selectable = selectable;
    }

    private Collection<Integer> getAuthDeptIds() throws Exception
    {
        HttpServletRequest request = requestProvider.get();
        UserOnlineInfo userOnlineInfo = UserOnlineInfo.getUserOnlineInfo(request);

        if (!authDeptIdsLoaded)
        {
            if (scopeId == null && !StringUtils.isEmpty(scopeName))
            {
                RoleScope scope = getService().getDao().getRoleScopeByName(1, scopeName);
                if (scope != null)
                    scopeId = scope.getScopeId();
            }

            if (scopeId != null)
            {
                Scopes scopes = new Scopes(scopeId, 1, 1, getService());
                authDeptIds = scopes.getDeptIds();
            }
            else if (appId == null)
            {
                authDeptIds = userOnlineInfo.getAuthDeptIds(request, getLevel(), getFilter());
            }
            else
            {
                authDeptIds = userOnlineInfo.getAuthDeptIds(appId, getLevel(), getFilter());
            }

            authDeptIdsLoaded = true;
        }

        if (authDeptIds != null && authDeptIds.size() == 0 && userOnlineInfo != null)
        {
            if (rootId == null && !userOnlineInfo.isAdmin())
                rootId = userOnlineInfo.getBureauId();

            return null;
        }

        return authDeptIds;
    }

    public void setAuthDeptIds(Collection<Integer> authDeptIds)
    {
        this.authDeptIds = authDeptIds;
        authDeptIdsLoaded = true;
    }

    public DeptService getService()
    {
        if (service == null)
            service = serviceProvider.get();
        return service;
    }

    @SuppressWarnings("unchecked")
    public Collection<SimpleDeptInfo> search(String text) throws Exception
    {
        List result = getService().searchDept(text, getAuthDeptIds());
        return (Collection<SimpleDeptInfo>) result;
    }

    public SimpleDeptInfo getParent(SimpleDeptInfo dept) throws Exception
    {
        if (dept == getRoot())
            return null;

        if (dept instanceof DeptInfo)
        {
            Collection<Integer> authDeptIds = getAuthDeptIds();

            DeptInfo parentDept = ((DeptInfo) dept).parentDept();

            if (authDeptIds == null)
                return parentDept;

            while (parentDept != null && !authDeptIds.contains(parentDept.getDeptId()))
                parentDept = parentDept.parentDept();

            if (parentDept != null)
                return parentDept;
        }

        return getRoot();
    }

    public Boolean isRootVisible()
    {
        return false;
    }

    public boolean hasCheckBox(SimpleDeptInfo simpleDeptInfo) throws Exception
    {
        return showBox;
    }

    public Boolean isChecked(SimpleDeptInfo simpleDeptInfo) throws Exception
    {
        return false;
    }

    public boolean isSearchable() throws Exception
    {
        return true;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean isSelectable(SimpleDeptInfo simpleDeptInfo) throws Exception
    {
        if (simpleDeptInfo instanceof VirtualDept)
            return false;

        if (selectable != null && (simpleDeptInfo instanceof DeptInfo))
        {
            return selectable.accept((DeptInfo) simpleDeptInfo);
        }

        return true;
    }
}
