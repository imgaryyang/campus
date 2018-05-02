package com.gzzm.platform.organ;

import net.cyan.arachne.components.*;

import java.util.*;

/**
 * @author camel
 * @date 2015/7/2
 */
public class RoleScopeCatalogTree implements LazyPageTreeModel<RoleScope>, SearchablePageTreeModel<RoleScope>
{
    private OrganDao dao;

    private Integer deptId;

    private Integer scopeId;

    public RoleScopeCatalogTree()
    {
    }

    void setDao(OrganDao dao)
    {
        this.dao = dao;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Integer getScopeId()
    {
        return scopeId;
    }

    public void setScopeId(Integer scopeId)
    {
        this.scopeId = scopeId;
    }

    @Override
    public RoleScope getRoot() throws Exception
    {
        RoleScope roleScope = new RoleScope();
        roleScope.setScopeId(0);
        roleScope.setScopeName("根节点");

        return roleScope;
    }

    public List<RoleScope> getChildren(RoleScope roleScope) throws Exception
    {
        List<RoleScope> children = roleScope.getChildren();
        if (children == null && roleScope.getScopeId() == 0)
        {
            roleScope.setChildren(children = dao.getTopRoleScopeCatalogs(deptId));
        }

        return children;
    }

    @Override
    public boolean isLeaf(RoleScope roleScope) throws Exception
    {
        if (roleScope.getScopeId() == 0)
            return false;

        for (RoleScope child : roleScope.getChildren())
        {
            if (child.getType() == RoleScopeType.catalog)
                return false;
        }

        return true;
    }

    @Override
    public int getChildCount(RoleScope parent) throws Exception
    {
        int count = 0;
        for (RoleScope scope : getChildren(parent))
        {
            if (scope.getType() == RoleScopeType.catalog && (scopeId == null || !scopeId.equals(scope.getScopeId())))
                count++;
        }

        return count;
    }

    @Override
    public RoleScope getChild(RoleScope parent, int index) throws Exception
    {
        int i = 0;
        for (RoleScope scope : getChildren(parent))
        {
            if (scope.getType() == RoleScopeType.catalog && (scopeId == null || !scopeId.equals(scope.getScopeId())))
            {
                if (i == index)
                    return scope;

                i++;
            }
        }

        return null;
    }

    @Override
    public String getId(RoleScope scope) throws Exception
    {
        return scope.getScopeId().toString();
    }

    @Override
    public String toString(RoleScope scope) throws Exception
    {
        return scope.getScopeName();
    }

    @Override
    public RoleScope getNode(String id) throws Exception
    {
        if ("0".equals(id))
            return getRoot();
        else
            return dao.getRoleScope(Integer.valueOf(id));
    }

    @Override
    public boolean isLazyLoad(RoleScope scope) throws Exception
    {
        return scope.getScopeId() != 0;
    }

    @Override
    public void beforeLazyLoad(String id) throws Exception
    {
    }

    @Override
    public boolean isSearchable() throws Exception
    {
        return true;
    }

    @Override
    public Collection<RoleScope> search(String text) throws Exception
    {
        return dao.queryRoleScopeCatalogs(deptId, text);
    }

    @Override
    public RoleScope getParent(RoleScope scope) throws Exception
    {
        return scope.getParentScope();
    }

    @Override
    public Boolean isRootVisible()
    {
        return false;
    }

}
