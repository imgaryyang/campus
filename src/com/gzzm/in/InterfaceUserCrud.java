package com.gzzm.in;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.components.CPassword;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * @author camel
 * @date 2016/12/3
 */
@Service(url = "/interface/user")
public class InterfaceUserCrud extends BaseNormalCrud<InterfaceUser, Integer>
{
    /**
     * 旧密码，当接收到此密码时表示保留原来的密码
     */
    private static final String OLDPASSWOED = "######$$$$$$";

    @Inject
    private OrganDao organDao;

    /**
     * 用户名
     */
    @Like
    private String userName;

    @Like
    private String loginName;

    private Integer deptId;

    private DeptTreeModel deptTree;

    public InterfaceUserCrud()
    {
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getLoginName()
    {
        return loginName;
    }

    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    @Select(field = {"deptId", "entity.deptId"})
    public DeptTreeModel getDeptTree()
    {
        if (deptTree == null)
            deptTree = new DeptTreeModel();

        return deptTree;
    }

    @NotSerialized
    @Select(field = "entity.scopeId")
    public RoleScopeTree getScopeTree() throws Exception
    {
        RoleScopeTree tree = new RoleScopeTree();
        List<RoleScope> roleScopes = organDao.getRoleScopes(1);
        for (RoleScope roleScope : roleScopes)
        {
            tree.addScope(roleScope);
        }

        return tree;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addComponent("用户名", "userName");
        view.addComponent("登录名", "loginName");
        view.addComponent("所属部门", "deptId");

        view.addColumn("用户名", "userName");
        view.addColumn("登录名", "loginName");
        view.addColumn("部门", "dept.deptName").setWidth("150");

        view.defaultInit();

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("用户名", "userName");
        view.addComponent("登录名", "loginName");
        view.addComponent("所属部门", "deptId")
                .setProperty("text", getEntity().getDept() != null ? getEntity().getDept().getDeptName() : "");
        view.addComponent("数据范围", "scopeId")
                .setProperty("text", getEntity().getScopeId() != null && getEntity().getScopeId() == -1 ? "所有部门" :
                        getEntity().getScope() != null ? getEntity().getScope().getScopeName() : "");

        String password = isNew$() ? "" : OLDPASSWOED;

        view.addComponent("密码", new CPassword("password").setProperty("value", password));
        view.addComponent("密码确认",
                new CPassword(null).setProperty("name", "password_confirm").setProperty("equal", "entity.password")
                        .setProperty("value", password).setProperty("require", null));

        view.addDefaultButtons();

        view.importCss("/in/user.css");

        return view;
    }
}
