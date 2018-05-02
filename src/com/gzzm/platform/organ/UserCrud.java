package com.gzzm.platform.organ;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.login.*;
import com.gzzm.platform.menu.MenuTreeModel;
import com.gzzm.platform.wordnumber.WordNumberService;
import net.cyan.arachne.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.*;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.exporters.ExportParameters;
import net.cyan.crud.view.*;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 用户维护
 *
 * @author camel
 * @date 2009-12-27
 */
@Service(url = "/UserCrud")
public class UserCrud extends UserConfigCrud
{
    @Inject
    private static Provider<MenuTreeModel> menuTreeModelProvider;

    @Inject
    private WordNumberService wordNumberService;

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

    @Like
    private String idCardNo;

    /**
     * 注入ca服务列表
     */
    @Inject
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private List<CertLoginServiceItem> certServices;

    @Inject
    private GlobalConfig config;

    @Inject
    private OrganManager manager;

    private transient List<Dept> allParentDepts;

    /**
     * 选中的角色列表
     */
    @NotSerialized
    private List<Role> roles;

    /**
     * 选中的岗位列表
     */
    @NotSerialized
    private List<Station> stations;

    @NotSerialized
    private Integer[] roleIds;

    @NotSerialized
    private Integer[] stationIds;

    private AuthDeptTreeModel deptTree;

    private Boolean certModifiable;

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

    private Byte state;

    private MenuTreeModel menuTree;

    private String page;

    private Integer[] defaultRoleIds;

    private List<DeptInfo> modifiableDepts;

    public UserCrud()
    {
        setLog(true);
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

    public String getIdCardNo()
    {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo)
    {
        this.idCardNo = idCardNo;
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

    public Byte getState()
    {
        return state;
    }

    public void setState(Byte state)
    {
        this.state = state;
    }

    public boolean isAdminUser()
    {
        return userOnlineInfo.isAdmin();
    }

    public String getPage()
    {
        return page;
    }

    public void setPage(String page)
    {
        this.page = page;
    }

    public Integer[] getDefaultRoleIds()
    {
        return defaultRoleIds;
    }

    public void setDefaultRoleIds(Integer[] defaultRoleIds)
    {
        this.defaultRoleIds = defaultRoleIds;
    }

    public AuthDeptTreeModel getDeptTree()
    {
        if (deptTree == null)
            deptTree = new AuthDeptTreeModel();

        return deptTree;
    }

    private OrganDao getDao()
    {
        return manager.getDao();
    }

    @Override
    protected String getCondition() throws Exception
    {
        StringBuilder buffer = new StringBuilder(super.getCondition())
                .append(" and user.loginName like ?loginName and user.phone like ?phone " +
                        "and user.certId like ?certId and user.state=?state");

        if (!isAdminUser())
        {
            buffer.append(" and user.adminUser=0");
        }

        if (stationId != null && !Null.isNull(stationId))
            buffer.append(" and exists s in stations : stationId=:stationId");

        if (roleId != null && !Null.isNull(roleId))
            buffer.append(" and exists r in roles : roleId=:roleId");

        if (appIds != null && appIds.length > 0)
        {
            buffer.append(" and exists r in roles : (exists a in role.roleApps : appId in :appIds " +
                    "or role.type=1 and (select a from role.groupRoles r,r.roleApps a where a.appId in :appIds)" +
                    " is not empty)");
        }

        return buffer.toString();
    }

    @Select(field = {"entity.certType"})
    @NotSerialized
    public List<CertLoginServiceItem> getCertTypes()
    {
        return certServices;
    }

    @NotSerialized
    public String getCertTypeName()
    {
        User user = getEntity();
        if (user != null)
        {
            String certType = user.getCertType();
            if (certType != null)
            {
                for (CertLoginServiceItem item : certServices)
                {
                    if (item.getType().equals(certType))
                        return item.getName();
                }
            }
        }

        return "";
    }

    @NotSerialized
    @Service
    public List<Role> getRoles() throws Exception
    {
        if (roles == null)
        {
            if (getEntity() != null)
            {
                if (getEntity().getUserId() != null)
                    roles = getDao().getRoles(getEntity().getUserId(), getDeptId());
                else if (getDuplicateKey() != null)
                    roles = getDao().getRoles(getDuplicateKey(), getDeptId());
            }
        }

        return roles;
    }

    @NotSerialized
    @Service
    public List<Station> getStations() throws Exception
    {
        if (stations == null)
        {
            if (getEntity() != null)
            {
                if (getEntity().getUserId() != null)
                    stations = getDao().getStations(getEntity().getUserId(), getDeptId());
                else if (getDuplicateKey() != null)
                    stations = getDao().getStations(getDuplicateKey(), getDeptId());
            }
        }

        return stations;
    }

    private List<Dept> getAllParentDepts() throws Exception
    {
        if (allParentDepts == null)
            allParentDepts = getDao().getDept(getDeptId()).allParentDepts();

        return allParentDepts;
    }

    /**
     * 可选的的角色
     *
     * @return 可选的角色列表
     * @throws Exception 数据库查询错误
     */
    @NotSerialized
    @Select(field = "roleId")
    public RoleTree getAvailableRoles() throws Exception
    {
        RoleTree roleTree = new RoleTree();

        Collection<Integer> deptIds = getAuthDeptIds();
        //本部门的权限和上级部门的权限
        for (Dept dept : getAllParentDepts())
        {
            Integer deptId = dept.getDeptId();
            if (deptIds == null || deptIds.contains(deptId))
            {
                for (Role role : getDao().getRolesInDept(deptId))
                {
                    if (role.isSelectable() != null && role.isSelectable())
                        roleTree.addRole(role);
                }
            }
            else
            {
                for (Role role : getDao().getRolesInDept(deptId))
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
        for (Role role : getDao().getRoles(userOnlineInfo.getUserId(), userOnlineInfo.getDeptId()))
        {
            roleTree.addRole(role);
        }

        return roleTree;
    }

    /**
     * 可选的岗位
     *
     * @return 可选的岗位列表
     * @throws Exception 数据库查询错误
     */
    @NotSerialized
    @Select(field = "stationId")
    @Service
    public List<Station> getAvailableStations() throws Exception
    {
        List<Station> stations = getStations();

        List<Station> availableStations = new ArrayList<Station>();
        for (Dept dept : getAllParentDepts())
        {
            for (Station station : getDao().getStationsInDept(dept.getDeptId()))
            {
                //没有业务职能的部门定义的岗位为通用岗位，可被下属部门使用，只加载本部门和没有业务只能的上级部门定义的岗位
                if (dept.getDeptId().equals(getDeptId()) || dept.getLevel() == -1 || dept.getDeptId() == 1 ||
                        (station.getInheritable() != null && station.getInheritable()))
                {
                    if (stations == null || !stations.contains(station))
                    {
                        //判断是否有同名的岗位，如果有同名的岗位，下级部门定义的岗位将覆盖上级部门所定义的
                        boolean exist = false;
                        for (Station station2 : availableStations)
                        {
                            if (station2.getStationName().equals(station.getStationName()))
                            {
                                exist = true;
                                break;
                            }
                        }

                        if (!exist)
                            availableStations.add(station);
                    }
                }
            }
        }

        return availableStations;
    }

    @Override
    @Forward(page = "/platform/organ/user.ptl")
    public String add(String forward) throws Exception
    {
        String result = super.add(forward);

        if (!StringUtils.isEmpty(page))
            result = page;

        return result;
    }

    @Override
    @Forwards({@Forward(page = "/platform/organ/user.ptl"),
            @Forward(name = "password", page = Pages.EDIT),
            @Forward(name = "userdept", page = "/platform/organ/userdept.ptl")})
    public String show(Integer key, String forward) throws Exception
    {
        String result = super.show(key, forward);

        boolean b = false;
        Integer deptId = null;
        for (Dept dept : getEntity().getDepts())
        {
            if (deptId == null)
                deptId = dept.getDeptId();
            if (dept.getDeptId().equals(getDeptId()))
            {
                b = true;
                break;
            }
        }

        if (!b)
            setDeptId(deptId);

        if (!StringUtils.isEmpty(page))
            result = page;

        return result;
    }

    @Override
    @Forward(page = "/platform/organ/user.ptl")
    public String duplicate(Integer key, String forward) throws Exception
    {
        return super.duplicate(key, forward);
    }

    @Override
    public void initEntity(User entity) throws Exception
    {
        User user = getEntity();
        if (user.getLoginType() == null)
            user.setLoginType(LoginType.auto);

        //复制用户的时候不复制以下字段
        user.setUserName(null);
        user.setLoginName(null);
        user.setCertId(null);
        user.setPassword(null);
        user.setPhone(null);
        user.setAdminUser(false);

        if (user.getType() == null)
            user.setType(UserType.in);
    }

    @Override
    public void afterLoad() throws Exception
    {
        User user = getEntity();
        user.setPassword(null);
        String certId = user.getCertId();
        if (certId != null)
            user.setCertId(certId.trim());
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        super.beforeInsert();

        User user = getEntity();

        if (StringUtils.isEmpty(user.getLoginName()))
            user.setLoginName(Tools.getUUID());

        if (StringUtils.isEmpty(user.getPassword()))
            user.setPassword(Tools.getUUID());

        user.setState((byte) 0);
        user.setDeptDataType(0);

        return true;
    }

    @Override
    public boolean beforeSave() throws Exception
    {
        User user = getEntity();

        String password = user.getPassword();
        if (isNew$())
        {
            //没有设置密码，使用默认密码
            if (password == null || password.length() == 0)
                user.setPassword(password = config.getDefaultPassword());
        }
        else
        {
            //不修改密码
            if (password != null && password.length() == 0)
                user.setPassword(password = null);
        }

        //md5密码
        if (password != null && !isNew$())
            user.setPassword(PasswordUtils.hashPassword(password, user.getUserId()));

        //防止非超级管理员修改超级管理员属性
        if (!userOnlineInfo.isAdmin())
        {
            user.setAdminUser(null);
            user.setState(null);
            user.setLoginType(isNew$() ? LoginType.auto : null);

            if (!isCertModifiable())
            {
                //不允许修改证书
                user.setCertId(null);
                user.setCertType(null);
            }
        }

        String certId = user.getCertId();
        if (certId != null && certId.length() > 0)
        {
            //如果只支持一种证书，设置证书类型
            if (certServices != null && certServices.size() == 1)
                user.setCertType(certServices.get(0).getType());

            //在证书id前后加上空格
            if (!certId.startsWith(" "))
                certId = " " + certId;
            if (!certId.endsWith(" "))
                certId += " ";
            user.setCertId(certId);
        }

        return true;
    }

    @Override
    public void afterInsert() throws Exception
    {
        super.afterInsert();

        User user = getEntity();

        //新增用户，同时新增用户部门，用户的角色，用户的岗位
        UserDept userDept = new UserDept(user.getUserId(), getDeptId());
        userDept.setOrderId(getOrderValue(true));

        initUserDept(userDept);

        getCrudService().add(userDept);

        String password = user.getPassword();
        if (password != null)
        {
            user.setPassword(PasswordUtils.hashPassword(password, user.getUserId()));
            getDao().update(user);
        }
    }

    @Override
    public void afterUpdate() throws Exception
    {
        super.afterUpdate();

        if (StringUtils.isEmpty(getForward()))
        {
            //更新用户，同时更新用户在所在部门的角色，岗位
            UserDept userDept = new UserDept(getEntity().getUserId(), getDeptId());

            initUserDept(userDept);

            getCrudService().update(userDept);
        }
    }

    @SuppressWarnings("unchecked")
    private void initUserDept(UserDept userDept)
    {
        if (roleIds == null && defaultRoleIds != null)
            roleIds = defaultRoleIds;

        //新增或更新时设置用户的角色和权限
        Integer userId = userDept.getUserId();
        Integer deptId = userDept.getDeptId();

        if (roleIds != null)
        {
            List<UserRole> userRoles = new ArrayList<UserRole>(roleIds.length);
            for (Integer roleId : roleIds)
                userRoles.add(new UserRole(userId, deptId, roleId));

            userDept.setRoles(userRoles);
        }
        else
        {
            userDept.setRoles(Collections.EMPTY_LIST);
        }

        if (stationIds != null)
        {
            List<UserStation> userStations = new ArrayList<UserStation>(stationIds.length);
            for (Integer stationId : stationIds)
                userStations.add(new UserStation(userId, deptId, stationId));

            userDept.setStations(userStations);
        }
        else
        {
            userDept.setStations(Collections.EMPTY_LIST);
        }
    }

    @Override
    protected String getSortListQueryString() throws Exception
    {
        //重载获得排序列表的方法，orderId在UserDept表中
        return "select user from UserDept where deptId=:deptId order by orderId";
    }

    @Service
    @ObjectResult
    public String genLoginName(String userName)
    {
        return User.getSpell(userName);
    }

    @Override
    public boolean delete(Integer userId) throws Exception
    {
        return manager.deleteUser(userId, getDeptId());
    }

    @Override
    public int deleteAll() throws Exception
    {
        int result = 0;
        Integer deptId = getDeptId();
        for (Integer userId : getKeys())
        {
            if (manager.deleteUser(userId, deptId))
                result++;
        }

        return result;
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
            getDao().update(userDept);
        }
    }

    @Override
    public void moveTo(Integer userId, Integer newDeptId, Integer oldDeptId) throws Exception
    {
        //检查权限
        checkDept(newDeptId, userId);
        checkDept(oldDeptId, userId);

        manager.moveUser(userId, oldDeptId, newDeptId, false);
    }

    @Override
    @Transactional
    public void moveAllTo(Integer[] userIds, Integer newDeptId, Integer oldDeptId) throws Exception
    {
        if (userIds != null)
            setKeys(userIds);

        checkDept(newDeptId, null);
        checkDept(oldDeptId, null);

        for (Integer userId : userIds)
            manager.moveUser(userId, oldDeptId, newDeptId, false);
    }

    @Override
    public void copyTo(Integer userId, Integer newDeptId, Integer oldDeptId) throws Exception
    {
        //检查权限
        checkDept(newDeptId, userId);
        checkDept(oldDeptId, userId);

        manager.moveUser(userId, oldDeptId, newDeptId, true);
    }

    @Override
    @Transactional
    public void copyAllTo(Integer[] userIds, Integer newDeptId, Integer oldDeptId) throws Exception
    {
        if (userIds != null)
            setKeys(userIds);

        checkDept(newDeptId, null);
        checkDept(oldDeptId, null);

        for (Integer userId : userIds)
            manager.moveUser(userId, oldDeptId, newDeptId, true);
    }

    @Service
    public void freeze(Integer userId, boolean freezed) throws Exception
    {
        manager.getDao().freezeUser(userId, freezed);
    }

    /**
     * 修改用户所在的部门
     *
     * @param deptIds 修改后的部门ID列表
     * @return 用户所在的部门是否发生变化，发生变化返回true，没有发生变化返回false
     * @throws Exception 数据库错误
     */
    @Transactional
    @Service(method = HttpMethod.post)
    public boolean saveDepts(Integer[] deptIds) throws Exception
    {
        Integer userId = getEntity().getUserId();

        //先检查是否有权限操作这些部门
        for (Integer deptId : deptIds)
            checkDept(deptId, userId);

        return manager.setUserDepts(userId, deptIds);
    }

    /**
     * 获得证书的ID
     *
     * @param cert     base64编码的证书
     * @param certType 证书类型
     * @return 证书ID
     * @throws Exception 获得证书ID错误，由CertLoginService的实现类抛出
     */
    @Service(method = HttpMethod.post)
    @ObjectResult
    public String getCertId(String cert, String certType) throws Exception
    {
        if (certServices == null)
            return null;

        CertLoginService certLoginService = null;
        if (certServices.size() == 1)
        {
            certLoginService = certServices.get(0).getService();
        }
        else
        {
            for (CertLoginServiceItem item : certServices)
            {
                if (item.getType().equals(certType))
                {
                    certLoginService = item.getService();
                    break;
                }
            }
        }

        if (certLoginService == null)
            throw new SystemException("service for cert " + certType + " is undefined");

        return certLoginService.getCertIds(cert)[0];
    }

    /**
     * 能否修改证书信息
     *
     * @return 能返回true，不能返回false
     */
    @NotSerialized
    public boolean isCertModifiable()
    {
        if (certModifiable == null)
            certModifiable = hasAuth("cert");

        return certModifiable;
    }

    public boolean hasAuth(String auth)
    {
        return hasAuth(auth, false);
    }

    public boolean hasAuth(String auth, boolean force)
    {
        return userOnlineInfo.hasAuth(RequestContext.getContext().getRequest(), auth, force);
    }

    @NotSerialized
    public List<? extends DeptInfo> getModifiableDepts() throws Exception
    {
        if (modifiableDepts == null)
        {
            List<Dept> depts = getEntity().getDepts();
            if (depts != null)
            {
                Collection<Integer> authDeptIds = getAuthDeptIds();
                if (authDeptIds == null)
                    return depts;

                modifiableDepts = new ArrayList<DeptInfo>(depts.size());

                for (Dept dept : depts)
                {
                    if (authDeptIds.contains(dept.getDeptId()))
                    {
                        modifiableDepts.add(dept);
                    }
                }
            }
            else
            {
                modifiableDepts = Collections.singletonList(deptService.getDept(getDeptId()));
            }
        }

        return modifiableDepts;
    }

    @Override
    protected Object createListView() throws Exception
    {
        Integer deptId = getDeptId();
        boolean out = deptId != null && deptId == -1;
        PageTableView view = out ? new PageTableView(true) :
                new ComplexTableView(new AuthDeptDisplay(), "deptId", true);

        view.addComponent("姓名", "userName");

        if (!"simple_user".equals(page))
        {
            view.addComponent("登录名", "loginName");
            view.addComponent("证书ID", "certId");
        }
        view.addComponent("包括下属部门", new CCombox("all").setNullable(false));
        view.addComponent("状态", new CCombox("state", new Object[]{
                new KeyValue<String>("0", "正常"),
                new KeyValue<String>("1", "冻结"),
        }));

        view.addMoreComponent("手机号码", "phone");
        view.addMoreComponent("证件号码", "idCardNo");
        view.addMoreComponent("功能", "appIds");
        view.addMoreComponent("角色", "roleId").setWidth("100px");
        view.addMoreComponent("岗位", "stationId").setWidth("100px");

        view.addColumn("姓名", new FieldCell("userName").setOrderable(false)).setAutoExpand(out);
        if (!"simple_user".equals(page))
            view.addColumn("登录名", new FieldCell("loginName").setOrderable(false)).setWidth("100");

        view.addColumn("所属部门", "allDeptName()").setAutoExpand(true);
        view.addColumn("性别", new FieldCell("sex").setOrderable(false));

        if (!"simple_user".equals(page) && certServices != null && certServices.size() > 0)
        {
            view.addColumn("证书ID", new FieldCell("certId", "trim").setOrderable(false));

            if (certServices.size() > 1)
                view.addColumn("证书类型", new EnumCell(new FieldCell("certType").setOrderable(false), getCertTypes()))
                        .setHidden(true);

            if (userOnlineInfo.isAdmin())
                view.addColumn("登录类型", new FieldCell("loginType").setOrderable(false)).setHidden(true);
        }

        view.addColumn("手机号码", new FieldCell("phone").setOrderable(false)).setHidden(true);

        view.addColumn("状态", new CImage("/platform/commons/icons/${state.intValue()==0?'unlocked.gif':'locked.gif'}",
                "freeze(${userId},${(state.intValue()==0).toString()})").setProperty("style", "cursor:pointer")
                .setPrompt("单击${state.intValue()==0?'冻结':'激活'}用户账号")).setOrderFiled("state");

        view.addColumn("修改密码", Buttons.editImg("password", "修改密码"));
        if (!out)
            view.addColumn("所属部门", Buttons.editImg("userdept", "所属部门"));

        view.defaultInit(false);
        view.addButton(Buttons.export("xls"));
        view.addButton(Buttons.sort());

        view.importLanguage("platform/organ/user");

        view.importJs("/platform/organ/user.js");

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        if ("password".equals(getForward()))
        {
            SimpleDialogView view = new SimpleDialogView();

            view.setTitle("修改密码");
            view.addComponent("密码", new CPassword("password")).setProperty("require", null);
            view.addComponent("密码确认", new CPassword(null)).setProperty("name", "password_confirm")
                    .setProperty("require", null).setProperty("equal", "entity.password");

            view.addDefaultButtons();

            return view;
        }

        return null;
    }

    @Override
    protected ExportParameters getExportParameters() throws Exception
    {
        ExportParameters exportParameters = super.getExportParameters();
        exportParameters.setFileName("用户列表");

        return exportParameters;
    }

    @Select(field = "appIds")
    public MenuTreeModel getMenuTree() throws Exception
    {
        if (menuTree == null)
        {
            menuTree = menuTreeModelProvider.get();
            menuTree.setLazy(true);
            menuTree.setShowHidden(true);
        }

        return menuTree;
    }

    @Service
    @ObjectResult
    public String generateWorkno(Integer deptId) throws Exception
    {
        return wordNumberService.getWordNumber("workno", deptId, null);
    }
}
