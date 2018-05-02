package com.gzzm.platform.organ;

import com.gzzm.platform.commons.BaseConfig;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.menu.*;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.crud.TreeListCrud;
import net.cyan.crud.annotation.*;
import net.cyan.crud.exporters.ExportParameters;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 对roel的crud管理
 *
 * @author camel
 * @date 2009-7-18
 */
@Service(url = "/RoleCrud")
public class RoleCrud extends DeptOwnedNormalCrud<Role, Integer> implements TreeListCrud<Role, Integer>
{
    @Inject
    private static Provider<MenuTreeModel> menuTreeModelProvider;

    @Inject
    private OrganManager organManager;

    private MenuTreeModel menuTree;

    @Like
    private String name;

    /**
     * 类型，角色还是角色组
     */
    @NotCondition
    private RoleType type;

    @Inject
    private BaseConfig config;

    @Inject
    private OrganDao dao;

    @NotCondition
    private Integer[] roleIds;

    @NotSerialized
    @In("roleId")
    private List<Integer> queryRoleIds;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    /**
     * 功能点，作为查询条件，查询拥有此功能点的角色或者角色组
     */
    private String[] appIds;

    private Integer parentRoleId;

    @NotCondition
    private Integer roleId;

    private Integer scopeId;

    public RoleCrud()
    {
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String[] getAppIds()
    {
        return appIds;
    }

    public void setAppIds(String[] appIds)
    {
        this.appIds = appIds;
    }

    public Integer[] getRoleIds()
    {
        return roleIds;
    }

    public void setRoleIds(Integer[] roleIds)
    {
        this.roleIds = roleIds;
    }

    public List<Integer> getQueryRoleIds()
    {
        return queryRoleIds;
    }

    public void setQueryRoleIds(List<Integer> queryRoleIds)
    {
        this.queryRoleIds = queryRoleIds;
    }

    public RoleType getType()
    {
        return type;
    }

    public void setType(RoleType type)
    {
        this.type = type;
    }

    public Integer getParentRoleId()
    {
        return parentRoleId;
    }

    public void setParentRoleId(Integer parentRoleId)
    {
        this.parentRoleId = parentRoleId;
    }

    public Integer getRoleId()
    {
        return roleId;
    }

    public void setRoleId(Integer roleId)
    {
        this.roleId = roleId;
    }

    public Integer getScopeId()
    {
        return scopeId;
    }

    public void setScopeId(Integer scopeId)
    {
        this.scopeId = scopeId;
    }

    @NotSerialized
    public RoleTree getAvailableRoles() throws Exception
    {
        RoleTree roleTree = new RoleTree();

        List<Role> roles = dao.getSingleRolesInDept(getEntity().getDeptId());

        for (Role role : roles)
        {
            roleTree.addRole(role);
        }

        return roleTree;
    }

    @NotSerialized
    public Map<String, String> getDeptLevels() throws Exception
    {
        return config.getDeptLevelMap();
    }

    @NotSerialized
    @Service
    public Collection<MenuAuth> getAuths(String menuId) throws Exception
    {
        Collection<MenuAuth> auths = getCrudService().get(Menu.class, menuId).getAuths();
        if (userOnlineInfo.isAdmin())
            return auths;

        List<MenuAuth> result = new ArrayList<MenuAuth>();

        for (MenuAuth auth : auths)
        {
            if (userOnlineInfo.hasAuth(menuId, auth.getAuthCode()))
                result.add(auth);
        }

        return result;
    }

    @Select(field = "appIds")
    public MenuTreeModel getMenuTree()
    {
        if (menuTree == null)
        {
            menuTree = menuTreeModelProvider.get();
            menuTree.setShowHidden(true);

            Role role = getEntity();
            if (role == null)
            {
                menuTree.setLazy(true);
            }
            else
            {
                List<RoleApp> roleApps = role.getRoleApps();
                if (roleApps != null)
                {
                    List<String> appIds = new ArrayList<String>(roleApps.size());
                    for (RoleApp roleApp : roleApps)
                        appIds.add(roleApp.getAppId());
                    menuTree.setIds(appIds);
                }
            }
        }

        return menuTree;
    }

    @Select(field = {"parentRoleId", "entity.parentRoleId"})
    public RoleCatalogTree getCatalogTree()
    {
        RoleCatalogTree catalogTree = new RoleCatalogTree();

        catalogTree.setDao(dao);
        catalogTree.setDeptId(getDeptId());

        if (getEntity() != null)
            catalogTree.setRoleIds(new Integer[]{getRoleId()});
        else
            catalogTree.setRoleIds(getKeys());

        return catalogTree;
    }

    @Override
    public void afterLoad() throws Exception
    {
        super.afterLoad();

        setRoleId(getEntity().getRoleId());
        setDeptId(getEntity().getDeptId());
    }

    @Override
    protected void beforeQuery() throws Exception
    {
        super.beforeQuery();

        if (!StringUtils.isEmpty(name) || type != null || !CollectionUtils.isEmpty(appIds))
        {
            String s = "select r from Role r where r.deptId=:deptId and r.roleName like ?name and type=?type";
            if (!CollectionUtils.isEmpty(appIds))
            {
                s += " and (exists a in roleApps : appId in :appIds or type=1 and " +
                        "(select a from groupRoles r,r.roleApps a where a.appId in :appIds) is not empty)";
            }

            List<Role> roles = dao.oqlQuery(s, toMap());

            queryRoleIds = new ArrayList<Integer>();

            for (Role role : roles)
            {
                while (true)
                {
                    if (DataConvert.equal(parentRoleId, role.getParentRoleId()))
                    {
                        queryRoleIds.add(role.getRoleId());
                        break;
                    }

                    if (role.getParentRoleId() == null)
                        break;

                    role = role.getParentRole();
                }
            }
        }
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        if (Null.isNull(parentRoleId))
            return "parentRoleId is null";

        return null;
    }

    @Override
    protected String getSortListCondition() throws Exception
    {
        if ("dept".equals(getForward()))
            return super.getSortListCondition() + " and parentRoleId is null ";
        else
            return super.getSortListCondition();
    }

    @Override
    @Forwards({@Forward(page = "/platform/organ/role.ptl"),
            @Forward(name = "group", page = "/platform/organ/rolegroup.ptl"),
            @Forward(name = "catalog", page = "/platform/organ/rolecatalog.ptl")})
    public String add(String forward) throws Exception
    {
        super.add(forward);

        RoleType type;
        if ("group".equals(forward))
            type = RoleType.group;
        else if ("catalog".equals(forward))
            type = RoleType.catalog;
        else
            type = RoleType.role;

        getEntity().setType(type);
        if (type != RoleType.role)
            getEntity().setSelectable(true);

        return forward;
    }

    @Override
    @Forwards({@Forward(page = "/platform/organ/role.ptl"),
            @Forward(name = "group", page = "/platform/organ/rolegroup.ptl"),
            @Forward(name = "catalog", page = "/platform/organ/rolecatalog.ptl")})
    public String show(Integer key, String forward) throws Exception
    {
        super.show(key, forward);

        return forward();
    }

    @Override
    @Forwards({@Forward(page = "/platform/organ/role.ptl"),
            @Forward(name = "group", page = "/platform/organ/rolegroup.ptl"),
            @Forward(name = "catalog", page = "/platform/organ/rolecatalog.ptl")})
    public String duplicate(Integer key, String forward) throws Exception
    {
        super.duplicate(key, forward);

        return forward();
    }

    private String forward()
    {
        RoleType type = getEntity().getType();

        if (type == RoleType.group)
            return "group";

        if (type == RoleType.catalog)
            return "catalog";

        return null;
    }

    @Override
    @Forwards({
            @Forward(page = Pages.SORT),
            @Forward(name = "dept", page = Pages.SORT),
            @Forward(name = "parent", page = Pages.SORT)
    })
    public String showSortList(String forward) throws Exception
    {
        return super.showSortList(forward);
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        setDefaultDeptId();
    }

    @Override
    public boolean beforeSave() throws Exception
    {
        super.beforeSave();

        Role role = getEntity();

        if (role.isInheritable() == null)
            role.setInheritable(false);

        if (role.getType() == RoleType.role)
        {
            if (role.isSelectable() == null)
                role.setSelectable(false);
        }

        if (roleIds != null)
        {
            if (role.getType() == RoleType.group)
            {
                List<Role> roles = new ArrayList<Role>(roleIds.length);
                for (Integer roleId : roleIds)
                    roles.add(new Role(roleId));

                role.setGroupRoles(roles);
            }
        }

        return true;
    }

    @Override
    public boolean beforeDelete(Integer roleId) throws Exception
    {
        super.beforeDelete(roleId);

        getCrudService().get(Role.class, roleId).getGroupRoles().clear();

        return true;
    }

    @Override
    public boolean beforeDeleteAll() throws Exception
    {
        super.beforeDeleteAll();

        for (Integer roleId : getKeys())
            getCrudService().get(Role.class, roleId).getGroupRoles().clear();

        return true;
    }

    @Override
    public Role clone(Role role) throws Exception
    {
        Role c = super.clone(role);

        if (role.getType() == RoleType.group)
        {
            //角色组，复制角色
            c.setGroupRoles(new ArrayList<Role>(role.getGroupRoles()));
        }
        else if (role.getType() == RoleType.role)
        {
            //角色，复制应用
            List<RoleApp> roleApps = new ArrayList<RoleApp>(role.getRoleApps().size());
            for (RoleApp roleApp : role.getRoleApps())
                roleApps.add(roleApp.clone());

            c.setRoleApps(roleApps);
        }

        return c;
    }

    @NotSerialized
    @Select(field = "scopeId")
    public RoleScopeTree getScopeTree() throws Exception
    {
        RoleScopeTree tree = new RoleScopeTree();
        for (Dept dept : dao.getDept(getDeptId()).allParentDepts())
        {
            List<RoleScope> roleScopes = dao.getRoleScopes(dept.getDeptId());
            for (RoleScope roleScope : roleScopes)
            {
                tree.addScope(roleScope);
            }
        }

        return tree;
    }

    @Service(method = HttpMethod.post)
    public void addRoleToUser(Integer roleId, Integer[] userIds) throws Exception
    {
        organManager.addRoleToUser(roleId, userIds);
    }

    @Service
    public String selectCatalog() throws Exception
    {
        return "rolecatalog_select";
    }

    @Service(method = HttpMethod.post)
    public void moveCatalog(Integer parentRoleId) throws Exception
    {
        if (getKeys() != null)
        {
            dao.moveRole(getKeys(), parentRoleId);
        }
    }

    @Override
    public String getParentField()
    {
        return "parentRoleId";
    }

    @Override
    public boolean hasChildren(Role entity)
    {
        return entity.getType() == RoleType.catalog && entity.getChildren().size() > 0;
    }

    @Override
    protected String[] getOrderWithFields()
    {
        if ("dept".equals(getForward()))
            return new String[]{"deptId"};
        else if ("parent".equals(getForward()))
            return new String[]{"parentRoleId"};

        return super.getOrderWithFields();
    }

    @Override
    protected Object createListView() throws Exception
    {
        Integer deptId = getDeptId();
        PageTableView view = deptId != null && deptId == -1 ? new PageTableView(true) :
                new ComplexTableView(new AuthDeptDisplay(), "deptId", true);

        view.addComponent("名称", "name");
        view.addComponent("类型", new CCombox("type", new RoleType[]{RoleType.role, RoleType.group}));
        view.addComponent("功能", "appIds");

        view.addColumn("名称", "roleName");
        view.addColumn("所属部门", "dept.deptName");
        view.addColumn("说明", "remark").setWidth("200");
        view.addColumn("类型", "type");
        view.addColumn("被子部门使用", "inheritable");

        view.addButton(Buttons.query());
        view.addButton(Buttons.add(null, "新增权限"));
        view.addButton(Buttons.setId(Buttons.add("group", "新增角色"), "btn_add_role"));
        view.addButton(Buttons.setId(Buttons.add("catalog", "新增目录"), "btn_add_catalog"));
        view.addButton(Buttons.delete());
        view.addButton(new CButton("移到", "moveCatalog();"));
        view.addButton(Buttons.sort("dept", "部门中排序").setProperty("id", "sort_dept"));
        view.addButton(Buttons.sort("parent", "目录中排序").setProperty("id", "sort_parent"));
        view.addButton(Buttons.export("xls"));

        view.makeEditable(null);
        view.addDuplicateColumn(null);

        if (view instanceof ComplexTableView)
            ((ComplexTableView) view).enableDD();

        view.addColumn("预览",
                new ConditionComponent().add("type.name()!='catalog'", new CButton("预览", "display(${roleId})")))
                .setWidth("60");
        view.addColumn("批量赋权", new ConditionComponent()
                .add("type.name()!='catalog'", new CButton("批量赋权", "addRoleToUser(${roleId})"))).setWidth("80");

        view.importJs("/platform/organ/role.js");

        return view;
    }

    @Override
    protected ExportParameters getExportParameters() throws Exception
    {
        return new ExportParameters("角色列表");
    }
}
