package com.gzzm.safecampus.campus.service;

import com.gzzm.platform.organ.*;
import net.cyan.arachne.components.*;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 角色权限模态窗口
 * @author zy
 * @date 2018/3/12 19:22
 */
public class RoleTreeModel implements LazyPageTreeModel<Role>, SearchablePageTreeModel<Role>,
        CheckBoxTreeModel<Role>
{
    @Inject
    private static Provider<ServiceDao> serviceProvider;

    private boolean showBox;

    private List<Integer> roleIds;

    public RoleTreeModel()
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

    public List<Integer> getRoleIds()
    {
        return roleIds;
    }

    public void setRoleIds(List<Integer> roleIds)
    {
        this.roleIds = roleIds;
    }

    @Override
    public boolean hasCheckBox(Role role) throws Exception
    {
        return showBox;
    }

    @Override
    public Boolean isChecked(Role role) throws Exception
    {
        if(roleIds!=null&&roleIds.size()>0)
        {
            for (Integer roleId:roleIds)
            {
                if(Objects.equals(roleId,role.getRoleId()))
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isLazyLoad(Role role) throws Exception
    {
        return false;
    }

    @Override
    public void beforeLazyLoad(String s) throws Exception
    {
    }

    @Override
    public boolean isSearchable() throws Exception
    {
        return true;
    }

    @Override
    public Collection<Role> search(String s) throws Exception
    {
        return serviceProvider.get().searchRoles("%"+s+"%");
    }

    @Override
    public Role getParent(Role role) throws Exception
    {
        return role.getParentRole();
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
    public int getChildCount(Role role) throws Exception
    {
        if (role.getRoleId() == 0)
            return serviceProvider.get().getRootSubRole().size();

        if (role.getChildren() == null)
            return 0;

        return role.getChildren().size();
    }

    @Override
    public Role getChild(Role role, int i) throws Exception
    {
        if (role.getRoleId() == 0)
            return serviceProvider.get().getRootSubRole().get(i);
        return role.getChildren().get(i);
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
    public Role getNode(String s) throws Exception
    {
        if ("0".equals(s))
            return getRoot();
        return serviceProvider.get().get(Role.class,Integer.valueOf(s));
    }

    @Override
    public Boolean isRootVisible()
    {
        return false;
    }
}
