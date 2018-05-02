package com.gzzm.platform.organ;

import com.gzzm.platform.commons.Tools;
import net.cyan.arachne.components.*;
import net.cyan.commons.util.DataConvert;

import java.util.*;

/**
 * @author camel
 * @date 2015/2/8
 */
public class RoleTree implements PageTreeModel<Role>, SelectableModel<Role>, SearchablePageTreeModel<Role>
{
    private List<Role> topRoles = new ArrayList<Role>();

    private Map<Integer, Role> roleMap = new HashMap<Integer, Role>();

    public RoleTree()
    {
    }

    @Override
    public boolean isSelectable(Role role) throws Exception
    {
        return role.getType() != RoleType.catalog;
    }

    public Role addRole(Role role)
    {
        if (roleMap.containsKey(role.getRoleId()))
            return roleMap.get(role.getRoleId());

        Integer parentRoleId = role.getParentRoleId();
        if (parentRoleId == null)
        {
            return addRole(role, topRoles);
        }
        else
        {
            Role parentRole = roleMap.get(parentRoleId);
            if (parentRole == null)
                parentRole = addRole(role.getParentRole());

            List<Role> children = parentRole.getChildren();
            if (children == null)
            {
                parentRole.setChildren(children = new ArrayList<Role>());
            }

            return addRole(role, children);
        }
    }

    private Role addRole(Role role, List<Role> roles)
    {
        Role role0 = role;
        role = new Role();
        role.setRoleId(role0.getRoleId());
        role.setRoleName(role0.getRoleName());
        role.setType(role0.getType());
        role.setParentRoleId(role0.getParentRoleId());
        role.setDeptId(role0.getDeptId());
        role.setOrderId(role0.getOrderId());

        roleMap.put(role.getRoleId(), role);

        if (roles.size() == 0)
        {
            roles.add(role);
        }
        else
        {
            boolean b = false;
            for (int i = roles.size() - 1; i >= 0; i--)
            {
                Role role1 = roles.get(i);

                if (!role1.getDeptId().equals(role.getDeptId()) ||
                        DataConvert.compare(role1.getOrderId(), role.getOrderId()) <= 0)
                {
                    roles.add(i + 1, role);
                    b = true;
                    break;
                }
            }

            if (!b)
                roles.add(0, role);
        }

        return role;
    }

    @Override
    public Role getRoot() throws Exception
    {
        Role role = new Role();
        role.setRoleId(0);
        role.setRoleName("根节点");

        return role;
    }

    @Override
    public boolean isLeaf(Role role) throws Exception
    {
        return role.getRoleId() != 0 &&
                (role.getType() != RoleType.catalog || role.getChildren() == null || role.getChildren().size() == 0);
    }

    @Override
    public int getChildCount(Role parent) throws Exception
    {
        if (parent.getRoleId() == 0)
            return topRoles.size();

        if (parent.getChildren() == null)
            return 0;

        return parent.getChildren().size();
    }

    @Override
    public Role getChild(Role parent, int index) throws Exception
    {
        if (parent.getRoleId() == 0)
            return topRoles.get(index);

        return parent.getChildren().get(index);
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

        return roleMap.get(Integer.valueOf(id));
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
    public Collection<Role> search(String text) throws Exception
    {
        List<Role> roles = new ArrayList<Role>();
        for (Role role : roleMap.values())
        {
            if (Tools.matchText(role.getRoleName(), text))
                roles.add(role);
        }

        return roles;
    }

    @Override
    public Role getParent(Role role) throws Exception
    {
        Integer parentRoleId = role.getParentRoleId();
        if (parentRoleId == null)
            return null;

        return roleMap.get(parentRoleId);
    }
}
