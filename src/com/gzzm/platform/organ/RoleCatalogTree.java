package com.gzzm.platform.organ;

import net.cyan.arachne.components.*;

import java.util.*;

/**
 * 角色目录树
 *
 * @author camel
 * @date 2015/2/8
 */
public class RoleCatalogTree implements LazyPageTreeModel<Role>, SearchablePageTreeModel<Role>
{
    private OrganDao dao;

    private Integer deptId;

    private Integer[] roleIds;

    public RoleCatalogTree()
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

    public Integer[] getRoleIds()
    {
        return roleIds;
    }

    public void setRoleIds(Integer[] roleIds)
    {
        this.roleIds = roleIds;
    }

    @Override
    public Role getRoot() throws Exception
    {
        Role role = new Role();
        role.setRoleId(0);
        role.setRoleName("根节点");

        return role;
    }

    public List<Role> getChildren(Role role) throws Exception
    {
        List<Role> children = role.getChildren();
        if (children == null && role.getRoleId() == 0)
        {
            role.setChildren(children = dao.getTopRoleCatalogs(deptId));
        }

        return children;
    }

    @Override
    public boolean isLeaf(Role role) throws Exception
    {
        if (role.getRoleId() == 0)
            return false;

        for (Role child : role.getChildren())
        {
            if (child.getType() == RoleType.catalog)
                return false;
        }

        return true;
    }

    @Override
    public int getChildCount(Role parent) throws Exception
    {
        int count = 0;
        for (Role role : getChildren(parent))
        {
            if (role.getType() == RoleType.catalog)
            {
                if (roleIds != null)
                {
                    boolean b = false;
                    for (Integer roleId : roleIds)
                    {
                        if (roleId != null && roleId.equals(role.getRoleId()))
                        {
                            b = true;
                            break;
                        }
                    }

                    if (b)
                        continue;
                }

                count++;
            }
        }

        return count;
    }

    @Override
    public Role getChild(Role parent, int index) throws Exception
    {
        int i = 0;
        for (Role role : getChildren(parent))
        {
            if (role.getType() == RoleType.catalog)
            {
                if (roleIds != null)
                {
                    boolean b = false;
                    for (Integer roleId : roleIds)
                    {
                        if (roleId != null && roleId.equals(role.getRoleId()))
                        {
                            b = true;
                            break;
                        }
                    }

                    if (b)
                        continue;
                }

                if (i == index)
                    return role;

                i++;
            }
        }

        return null;
    }

    @Override
    public String getId(Role role) throws Exception
    {
        return role.getRoleId().toString();
    }

    @Override
    public String toString(Role role) throws Exception
    {
        return role.getRoleName();
    }

    @Override
    public Role getNode(String id) throws Exception
    {
        if ("0".equals(id))
            return getRoot();
        else
            return dao.getRole(Integer.valueOf(id));
    }

    @Override
    public boolean isLazyLoad(Role role) throws Exception
    {
        return role.getRoleId() != 0;
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
    public Collection<Role> search(String text) throws Exception
    {
        return dao.queryRoleCatalogs(deptId, text);
    }

    @Override
    public Role getParent(Role role) throws Exception
    {
        return role.getParentRole();
    }

    @Override
    public Boolean isRootVisible()
    {
        return false;
    }
}
