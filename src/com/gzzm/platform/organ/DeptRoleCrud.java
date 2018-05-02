package com.gzzm.platform.organ;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.*;
import net.cyan.nest.annotation.Inject;

import java.util.Collection;

/**
 * @author camel
 * @date 2016/9/20
 */
@Service(url = "/DeptRoleCrud")
public class DeptRoleCrud extends DeptOwnedNormalCrud<DeptRole, Integer>
{
    @Inject
    private OrganDao dao;

    public DeptRoleCrud()
    {
        addOrderBy("role.roleName");
    }

    @Override
    public String getOrderField()
    {
        return null;
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        setDefaultDeptId();
    }

    /**
     * 可选的的角色
     *
     * @return 可选的角色列表
     * @throws Exception 数据库查询错误
     */
    @NotSerialized
    @Select(field = "entity.roleId")
    public RoleTree getAvailableRoles() throws Exception
    {
        RoleTree roleTree = new RoleTree();

        Collection<Integer> deptIds = getAuthDeptIds();
        //本部门的权限和上级部门的权限
        for (Dept dept : dao.getDept(getDeptId()).allParentDepts())
        {
            Integer deptId = dept.getDeptId();
            if (deptIds == null || deptIds.contains(deptId))
            {
                for (Role role : dao.getRolesInDept(deptId))
                {
                    if (role.isSelectable() != null && role.isSelectable())
                        roleTree.addRole(role);
                }
            }
            else
            {
                for (Role role : dao.getRolesInDept(deptId))
                {
                    if ((role.isInheritable() != null && role.isInheritable() || deptIds == null ||
                            deptIds.contains(role.getDeptId())))
                    {
                        if (role.isSelectable() != null && role.isSelectable())
                        {
                            roleTree.addRole(role);
                        }
                    }
                }
            }
        }

        //操作员拥有的权限
        for (Role role : dao.getRoles(userOnlineInfo.getUserId(), userOnlineInfo.getDeptId()))
        {
            roleTree.addRole(role);
        }

        return roleTree;
    }

    @Override
    protected Object createListView() throws Exception
    {
        ComplexTableView view = new ComplexTableView(new AuthDeptDisplay(), "deptId", true);

        view.addColumn("角色", "role.roleName");
        view.addColumn("被子部门继承", "inheritable");

        view.defaultInit();

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("角色", "roleId").setProperty("text",
                getEntity().getRole() == null ? "" : getEntity().getRole().getRoleName());
        view.addComponent("被继承", "inheritable");

        view.addDefaultButtons();

        view.importCss("/platform/organ/deptrole.css");

        return view;
    }
}
