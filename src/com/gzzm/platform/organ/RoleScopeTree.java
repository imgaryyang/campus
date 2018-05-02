package com.gzzm.platform.organ;

import com.gzzm.platform.commons.Tools;
import net.cyan.arachne.components.*;
import net.cyan.commons.util.DataConvert;

import java.util.*;

/**
 * @author camel
 * @date 2015/2/8
 */
public class RoleScopeTree
        implements PageTreeModel<RoleScope>, SelectableModel<RoleScope>, SearchablePageTreeModel<RoleScope>
{
    private List<RoleScope> topScopes = new ArrayList<RoleScope>();

    private Map<Integer, RoleScope> scopeMap = new HashMap<Integer, RoleScope>();

    public RoleScopeTree()
    {
        RoleScope all = new RoleScope();
        all.setScopeId(-1);
        all.setScopeName("所有部门");
        topScopes.add(all);
    }

    @Override
    public boolean isSelectable(RoleScope role) throws Exception
    {
        return role.getType() != RoleScopeType.catalog;
    }

    public RoleScope addScope(RoleScope scope)
    {
        if (scopeMap.containsKey(scope.getScopeId()))
            return scopeMap.get(scope.getScopeId());

        Integer parentRoleId = scope.getParentScopeId();
        if (parentRoleId == null)
        {
            return addScope(scope, topScopes);
        }
        else
        {
            RoleScope parentScope = scopeMap.get(parentRoleId);
            if (parentScope == null)
                parentScope = addScope(scope.getParentScope());

            List<RoleScope> children = parentScope.getChildren();
            if (children == null)
            {
                parentScope.setChildren(children = new ArrayList<RoleScope>());
            }

            return addScope(scope, children);
        }
    }

    private RoleScope addScope(RoleScope scope, List<RoleScope> scopes)
    {
        RoleScope scope0 = scope;
        scope = new RoleScope();
        scope.setScopeId(scope0.getScopeId());
        scope.setScopeName(scope0.getScopeName());
        scope.setType(scope0.getType());
        scope.setParentScopeId(scope0.getParentScopeId());
        scope.setDeptId(scope0.getDeptId());
        scope.setOrderId(scope0.getOrderId());

        scopeMap.put(scope.getScopeId(), scope);

        if (scopes.size() == 0)
        {
            scopes.add(scope);
        }
        else
        {
            boolean b = false;
            for (int i = scopes.size() - 1; i >= 0; i--)
            {
                RoleScope scope1 = scopes.get(i);

                if (!DataConvert.equal(scope1.getDeptId(), scope.getDeptId()) ||
                        DataConvert.compare(scope1.getOrderId(), scope.getOrderId()) <= 0)
                {
                    scopes.add(i + 1, scope);
                    b = true;
                    break;
                }
            }

            if (!b)
                scopes.add(0, scope);
        }

        return scope;
    }

    @Override
    public RoleScope getRoot() throws Exception
    {
        RoleScope scope = new RoleScope();
        scope.setScopeId(0);
        scope.setScopeName("根节点");

        return scope;
    }

    @Override
    public boolean isLeaf(RoleScope scope) throws Exception
    {
        return scope.getScopeId() != 0 &&
                (scope.getType() != RoleScopeType.catalog || scope.getChildren() == null ||
                        scope.getChildren().size() == 0);
    }

    @Override
    public int getChildCount(RoleScope parent) throws Exception
    {
        if (parent.getScopeId() == 0)
            return topScopes.size();

        if (parent.getChildren() == null)
            return 0;

        return parent.getChildren().size();
    }

    @Override
    public RoleScope getChild(RoleScope parent, int index) throws Exception
    {
        if (parent.getScopeId() == 0)
            return topScopes.get(index);

        return parent.getChildren().get(index);
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

        return scopeMap.get(Integer.valueOf(id));
    }

    @Override
    public Boolean isRootVisible()
    {
        return false;
    }

    @Override
    public boolean isSearchable() throws Exception
    {
        return true;
    }

    @Override
    public Collection<RoleScope> search(String text) throws Exception
    {
        List<RoleScope> scopes = new ArrayList<RoleScope>();
        for (RoleScope scope : scopeMap.values())
        {
            if (Tools.matchText(scope.getScopeName(), text))
                scopes.add(scope);
        }

        return scopes;
    }

    @Override
    public RoleScope getParent(RoleScope role) throws Exception
    {
        Integer parentScopeId = role.getParentScopeId();
        if (parentScopeId == null)
            return null;

        return scopeMap.get(parentScopeId);
    }
}
