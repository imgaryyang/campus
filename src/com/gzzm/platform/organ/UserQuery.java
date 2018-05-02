package com.gzzm.platform.organ;

import com.gzzm.platform.commons.GlobalConfig;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.login.CertLoginServiceItem;
import com.gzzm.platform.menu.MenuTreeModel;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.Provider;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.exporters.ExportParameters;
import net.cyan.crud.view.*;
import net.cyan.crud.view.components.CCombox;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * 用户维护
 *
 * @author camel
 * @date 2009-12-27
 */
@Service(url = "/UserQuery")
public class UserQuery extends UserConfigCrud
{
    @Inject
    private static Provider<MenuTreeModel> menuTreeModelProvider;

    /**
     * 用登录名做查询条件
     */
    @Like
    private String loginName;

    /**
     * 用电话号码做查询条件
     */
    @Like
    private String phone;

    /**
     * 用证书id做查询条件
     */
    @Like
    private String certId;

    /**
     * 注入ca服务列表
     */
    @Inject
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private List<CertLoginServiceItem> certServices;

    @Inject
    private GlobalConfig config;

    @NotSerialized
    private Integer[] roleIds;

    @NotSerialized
    private Integer[] stationIds;

    private AuthDeptTreeModel deptTree;

    /**
     * 能操作的菜单，作为查询条件，查询出能操作这些菜单的用户
     */
    private String[] appIds;

    /**
     * 岗位ID，作为查询条件，查询出拥有此岗位的用户
     */
    private Integer stationId;

    /**
     * 角色ID，作为查询条件，查询出拥有此角色的用户
     */
    private Integer roleId;

    private MenuTreeModel menuTree;

    @Inject
    private OrganDao dao;

    private boolean sortable;

    public UserQuery()
    {
    }

    public String getLoginName()
    {
        return loginName;
    }

    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getCertId()
    {
        return certId;
    }

    public void setCertId(String certId)
    {
        this.certId = certId;
    }

    public String[] getAppIds()
    {
        return appIds;
    }

    public void setAppIds(String[] appIds)
    {
        this.appIds = appIds;
    }

    public Integer getStationId()
    {
        return stationId;
    }

    public void setStationId(Integer stationId)
    {
        this.stationId = stationId;
    }

    public Integer getRoleId()
    {
        return roleId;
    }

    public void setRoleId(Integer roleId)
    {
        this.roleId = roleId;
    }

    public Integer[] getRoleIds()
    {
        return roleIds;
    }

    public void setRoleIds(Integer[] roleIds)
    {
        this.roleIds = roleIds;
    }

    public Integer[] getStationIds()
    {
        return stationIds;
    }

    public void setStationIds(Integer[] stationIds)
    {
        this.stationIds = stationIds;
    }

    public boolean isAdminUser()
    {
        return userOnlineInfo.isAdmin();
    }

    public AuthDeptTreeModel getDeptTree()
    {
        if (deptTree == null)
            deptTree = new AuthDeptTreeModel();

        return deptTree;
    }

    public boolean isSortable()
    {
        return sortable;
    }

    public void setSortable(boolean sortable)
    {
        this.sortable = sortable;
    }

    @Override
    protected String getCondition() throws Exception
    {
        StringBuilder buffer = new StringBuilder(super.getCondition())
                .append(" and user.loginName like ?loginName and user.phone like ?phone " +
                        "and user.certId like ?certId");

        if (stationId != null)
            buffer.append(" and exists s in stations : stationId=:stationId");

        if (roleId != null)
            buffer.append(" and exists r in roles : roleId=:roleId");

        if (appIds != null && appIds.length > 0)
        {
            buffer.append(" and exists r in roles : (exists a in role.roleApps : appId in :appIds " +
                    "or role.type=1 and (select a from role.groupRoles r,r.roleApps a where a.appId in :appIds)" +
                    " is not empty)");
        }

        return buffer.toString();
    }

    @Override
    protected Object createListView() throws Exception
    {
        Integer deptId = getDeptId();
        boolean out = deptId != null && deptId == -1;
        PageTableView view = out ? new PageTableView(false) :
                new ComplexTableView(new AuthDeptDisplay(), "deptId", false);

        view.addComponent("姓名", "userName");
        view.addComponent("登录名", "loginName");
        view.addComponent("证书ID", "certId");
        view.addComponent("包括下属部门", new CCombox("all").setNullable(false));

        view.addMoreComponent("电话", "phone");
        view.addMoreComponent("证件号码", "idCardNo");
        view.addMoreComponent("功能", "appIds");
        view.addMoreComponent("角色", "roleId").setWidth("100px");
        view.addMoreComponent("岗位", "stationId").setWidth("100px");

        view.addColumn("姓名", new FieldCell("userName").setOrderable(false)).setAutoExpand(out);
        view.addColumn("登录名", new FieldCell("loginName").setOrderable(false));

        if (certServices != null && certServices.size() > 0)
        {
            view.addColumn("证书ID", new FieldCell("certId", "trim").setOrderable(false));

            if (certServices.size() > 1)
                view.addColumn("证书类型", new EnumCell(new FieldCell("certType").setOrderable(false), certServices))
                        .setHidden(true);
        }

        if (!out)
        {
            view.addColumn("所属部门", "allSimpleDeptName()");
            view.addColumn("岗位", "allStationName()").setWidth("150").setWrap(true);
            view.addColumn("角色", "allRoleName()").setWidth("150").setWrap(true);
        }

        view.addColumn("性别", new FieldCell("sex").setOrderable(false));
        view.addColumn("电话", new FieldCell("phone").setOrderable(false));

        view.addButton(Buttons.query());
        view.addButton(Buttons.export("xls"));

        if (sortable)
            view.addButton(Buttons.sort());

        return view;
    }

    @Override
    protected String getSortListQueryString() throws Exception
    {
        //重载获得排序列表的方法，orderId在UserDept表中
        return "select user from UserDept where deptId=:deptId order by orderId";
    }

    @Override
    @Transactional
    public void sort(Integer[] userIds) throws Exception
    {
        //重装排序方法，更新UserDept表的OrderId
        if (userIds == null)
            userIds = getKeys();

        int index = 0;
        for (Integer userId : userIds)
        {
            UserDept userDept = new UserDept(userId, getDeptId());
            userDept.setOrderId(index++);
            dao.update(userDept);
        }
    }

    @Override
    protected ExportParameters getExportParameters() throws Exception
    {
        return new ExportParameters("用户列表");
    }

    @Select(field = "appIds")
    public MenuTreeModel getMenuTree() throws Exception
    {
        if (menuTree == null)
        {
            menuTree = menuTreeModelProvider.get();
            menuTree.setLazy(true);
        }

        return menuTree;
    }
}
