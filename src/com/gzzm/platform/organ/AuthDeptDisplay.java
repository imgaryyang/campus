package com.gzzm.platform.organ;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.SelectableTreeView;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.crud.*;
import net.cyan.nest.annotation.Inject;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 显示有权限的部门列表
 *
 * @author camel
 * @date 2010-2-22
 */
@Service(url = "/AuthDept")
public class AuthDeptDisplay extends TreeCrud<SimpleDeptInfo, Integer>
        implements SearchTreeCrud<SimpleDeptInfo, Integer>
{
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
    @NotSerialized
    private Filter<DeptInfo> filter;

    private String filterClass;

    /**
     * 条件，之显示满足此条件的部门，当filter为null是有效
     */
    private String condition;

    /**
     * 对应的权限ID，如果为null表示为当前功能
     */
    private String appId;

    private DeptService service;

    private Collection<Integer> authDeptIds;

    private boolean authDeptIdsLoaded;

    public AuthDeptDisplay()
    {
    }

    public SimpleDeptInfo getRoot() throws Exception
    {
        //返回一个虚拟的根节点
        return SimpleDeptInfo.ROOT;
    }

    public List<SimpleDeptInfo> getChildren(Integer parent) throws Exception
    {
        Collection<Integer> authDeptIds = getAuthDeptIds();

        if (parent == -1)
        {
            if (rootId != null && (authDeptIds == null || authDeptIds.contains(rootId)))
                return Collections.singletonList((SimpleDeptInfo) getService().getDept(rootId));

            parent = rootId;
        }

        return getService().getAuthDeptTree(parent, getFilter(), authDeptIds);
    }

    public boolean hasChildren(SimpleDeptInfo node) throws Exception
    {
        return node == SimpleDeptInfo.ROOT ||
                getService().containsSubAuthDept(node.getDeptId(), getFilter(), getAuthDeptIds());
    }

    public DeptInfo getNode(Integer key) throws Exception
    {
        return getService().getDept(key);
    }

    public Integer getKey(SimpleDeptInfo node) throws Exception
    {
        return node.getDeptId();
    }

    @Override
    protected Object createTreeView() throws Exception
    {
        SelectableTreeView view = new SelectableTreeView();

        view.setRootVisible(false);

        return view;
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

    @SuppressWarnings("unchecked")
    protected Filter<DeptInfo> getFilter()
    {
        if (filter == null && condition != null)
        {
            if (!StringUtils.isEmpty(condition))
            {
                filter = ExpressionDeptFilter.getFilter(condition);
            }
            else if (!StringUtils.isEmpty(filterClass))
            {
                try
                {
                    Class<Filter<DeptInfo>> c = (Class<Filter<DeptInfo>>) Class.forName(filterClass);
                    filter = Tools.getBean(c);
                }
                catch (Throwable ex)
                {
                    ExceptionUtils.wrapException(ex);
                }
            }
        }

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

    public String getFilterClass()
    {
        return filterClass;
    }

    public void setFilterClass(String filterClass)
    {
        this.filterClass = filterClass;
    }

    public String getAppId()
    {
        return appId;
    }

    public void setAppId(String appId)
    {
        this.appId = appId;
    }

    private Collection<Integer> getAuthDeptIds()
    {
        if (!authDeptIdsLoaded)
        {
            HttpServletRequest request = requestProvider.get();

            UserOnlineInfo userOnlineInfo = UserOnlineInfo.getUserOnlineInfo(request);
            if (appId == null)
                authDeptIds = userOnlineInfo.getAuthDeptIds(request, getLevel(), getFilter());
            else
                authDeptIds = userOnlineInfo.getAuthDeptIds(appId, getLevel(), getFilter());

            authDeptIdsLoaded = true;
        }

        return authDeptIds;
    }

    private DeptService getService()
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

    public boolean supportSearch() throws Exception
    {
        return true;
    }
}
