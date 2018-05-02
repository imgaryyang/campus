package com.gzzm.platform.organ;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.menu.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.io.Serializable;
import java.util.*;

/**
 * 用户信息对象
 *
 * @author camel
 * @date 11-9-14
 */
public class UserInfo implements Serializable
{
    private static final long serialVersionUID = -1649018299374723963L;

    /**
     * 组织机构的数据接口
     */
    @Inject
    protected static Provider<OrganDao> organDaoProvider;

    @Inject
    protected static Provider<DeptService> deptServiceProvider;

    @Inject
    protected static Provider<MenuContainer> menuContainer;

    /**
     * 当前用户ID
     */
    protected Integer userId;

    /**
     * 当前用户登录的部门ID
     */
    protected Integer deptId;

    /**
     * 是否为管理员
     */
    protected boolean admin;


    /**
     * 是否看到所有部门的数据
     */
    protected boolean allDeptData;

    /**
     * 用户名
     */
    protected transient String userName;

    /**
     * 登录名
     */
    protected transient String loginName;

    /**
     * 操作者名称，可能由另外一个人待当前用户操作
     */
    protected String operatorName;

    /**
     * 部门名称
     */
    protected transient String deptName;

    protected transient DeptInfo dept;

    /**
     * 本用户所属的部门，当用户属于多个部门时保存多个部门的信息
     */
    protected transient List<SimpleDeptInfo> depts;


    /**
     * 所有上级部门，包括本部门的部门信息
     */
    protected transient List<SimpleDeptInfo> parentDepts;

    protected transient String allDeptName;

    private String phone;

    private Sex sex;

    private transient String station;

    protected UserType userType;

    /**
     * 拥有权限的功能信息，key为菜单id，value为AppInfo对象，保存在线用户对才的使用范围和权限的信息
     */
    protected transient Map<String, AppInfo> apps;

    /**
     * 用户属性
     */
    protected transient Map<String, Object> attributes;

    public UserInfo()
    {
    }

    public UserInfo(Integer userId, Integer deptId)
    {
        this(userId, deptId, false);
    }

    public UserInfo(Integer userId, Integer deptId, boolean admin)
    {
        this.userId = userId;
        this.deptId = deptId;
        this.admin = admin && deptId == 1 || userId == 1;

        allDeptData = deptId == null;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public boolean isAdmin()
    {
        return admin;
    }

    public boolean isAllDeptData()
    {
        return allDeptData;
    }

    public synchronized String getUserName()
    {
        if (userName == null)
        {
            try
            {
                userName = organDaoProvider.get().getUserName(userId);
            }
            catch (Exception ex)
            {
                Tools.wrapException(ex);
            }
        }
        return userName;
    }

    public synchronized String getLoginName()
    {
        if (loginName == null)
        {
            try
            {
                loginName = organDaoProvider.get().getLoginName(userId);
            }
            catch (Exception ex)
            {
                Tools.wrapException(ex);
            }
        }
        return loginName;
    }

    public String getOperatorName()
    {
        return operatorName;
    }

    public void setOperatorName(String operatorName)
    {
        this.operatorName = operatorName;
    }

    public synchronized String getDeptName()
    {
        if (deptId == null || deptId < 0)
            return null;

        if (deptName == null)
        {
            if (dept == null)
            {
                try
                {
                    deptName = organDaoProvider.get().getDeptName(deptId);
                }
                catch (Exception ex)
                {
                    Tools.wrapException(ex);
                }
            }
            else
            {
                deptName = dept.getDeptName();
            }
        }

        return deptName;
    }

    @SuppressWarnings("unchecked")
    public synchronized List<SimpleDeptInfo> getDepts()
    {
        if (depts == null)
        {
            try
            {
                List list = deptServiceProvider.get().getDeptsByUserId(userId);
                depts = Collections.unmodifiableList(list);
            }
            catch (Exception ex)
            {
                Tools.wrapException(ex);
            }
        }

        return depts;
    }

    public boolean containsDept(Integer deptId)
    {
        List<SimpleDeptInfo> deptInfos = getDepts();
        for (SimpleDeptInfo dept : deptInfos)
        {
            if (dept.getDeptId().equals(deptId))
                return true;
        }

        return false;
    }

    /**
     * 获得此用户所属的所有部门的id列表
     *
     * @return 所有部门的id列表
     */
    public List<Integer> getDeptIds()
    {
        List<SimpleDeptInfo> deptInfos = getDepts();
        List<Integer> ids = new ArrayList<Integer>(deptInfos.size());

        for (SimpleDeptInfo deptInfo : deptInfos)
            ids.add(deptInfo.getDeptId());

        return ids;
    }

    public List<Integer> getDeptIds(int level)
    {
        List<SimpleDeptInfo> deptInfos = getDepts();
        List<Integer> ids = new ArrayList<Integer>(deptInfos.size());

        for (SimpleDeptInfo deptInfo : deptInfos)
            ids.add(deptInfo.getParentDept(level).getDeptId());

        return ids;
    }

    /**
     * 判断用户是否拥有某个部门的权限
     *
     * @param deptId 部门id
     * @return 有权限返回true，没有权限返回false
     */
    public boolean hasDept(Integer deptId)
    {
        for (SimpleDeptInfo deptInfo : getDepts())
        {
            if (deptInfo.getDeptId().equals(deptId))
                return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public synchronized List<SimpleDeptInfo> getParentDepts()
    {
        if (parentDepts == null && deptId != null && deptId > 0)
        {
            try
            {
                List list = getDept0().allParentDepts();
                this.parentDepts = Collections.unmodifiableList(list);
            }
            catch (Exception ex)
            {
                Tools.wrapException(ex);
            }
        }

        return parentDepts;
    }

    /**
     * 获得某一级别的部门的信息
     *
     * @param level 部门的级别
     * @return 部门信息
     */
    public SimpleDeptInfo getDept(int level)
    {
        List<SimpleDeptInfo> deptInfos = getParentDepts();

        if (deptInfos != null)
        {
            for (SimpleDeptInfo deptInfo : deptInfos)
            {
                if (deptInfo.getLevel() >= level)
                    return deptInfo;
            }

            return deptInfos.get(deptInfos.size() - 1);
        }

        return null;
    }

    public SimpleDeptInfo getDept()
    {
        return getDept0();
    }

    private DeptInfo getDept0()
    {
        if (dept == null)
        {
            dept = getDeptInfo(deptId);
        }

        return dept;
    }

    public SimpleDeptInfo getBureau()
    {
        return getDept(1);
    }

    public Integer getDeptId(int level)
    {
        SimpleDeptInfo dept = getDept(level);
        return dept != null ? dept.getDeptId() : null;
    }

    public Integer getBureauId()
    {
        return getDeptId(1);
    }

    /**
     * 获得某一级别以下的部门名称的连接
     *
     * @param level 部门的级别
     * @return 部门信息
     */
    public String getDeptName(int level)
    {
        List<SimpleDeptInfo> deptInfos = getParentDepts();
        if (deptInfos != null)
        {
            List<String> deptNames = new ArrayList<String>(deptInfos.size());
            int n = deptInfos.size() - 1;

            int lastLevel = -1;

            for (int i = 0; i < n; i++)
            {
                SimpleDeptInfo deptInfo = deptInfos.get(i);

                //忽略虚拟部门
                if (i == 0 || deptInfo.getLevel() > lastLevel)
                {
                    deptNames.add(0, deptInfo.getDeptName());
                    lastLevel = deptInfo.getLevel();
                }

                if (deptInfo.getLevel() >= level)
                    break;
            }

            return Dept.getAllName(deptNames);
        }
        else
        {
            return null;
        }
    }

    /**
     * 获得根部门以下所有部门名称的连接
     *
     * @return 部门名称的连接
     */
    public String getAllDeptName()
    {
        if (allDeptName == null)
        {
            List<SimpleDeptInfo> deptInfos = getParentDepts();

            if (deptInfos != null)
            {
                int n = deptInfos.size();
                if (n == 1)
                {
                    allDeptName = deptInfos.get(0).getDeptName();
                }
                else
                {
                    n--;
                    List<String> deptNames = new ArrayList<String>(n);

                    int lastLevel = -1;
                    for (int i = 0; i < n; i++)
                    {
                        SimpleDeptInfo deptInfo = deptInfos.get(i);

                        //忽略虚拟部门
                        if (i == 0 || deptInfo.getLevel() > lastLevel)
                        {
                            deptNames.add(0, deptInfo.getDeptName());
                            lastLevel = deptInfo.getLevel();
                        }
                    }

                    allDeptName = Dept.getAllName(deptNames);
                }
            }
        }

        return allDeptName;
    }

    public String getSimpleDeptNames()
    {
        StringBuilder buffer = new StringBuilder();

        for (SimpleDeptInfo dept : getDepts())
        {
            if (buffer.length() > 0)
                buffer.append(",");
            buffer.append(dept.getDeptName());
        }

        return buffer.toString();
    }

    public String getAllDeptNames()
    {
        StringBuilder buffer = new StringBuilder();

        for (SimpleDeptInfo dept : getDepts())
        {
            if (buffer.length() > 0)
                buffer.append(",");
            buffer.append(dept.getAllName(1));
        }

        return buffer.toString();
    }

    /**
     * 获得所有上级节点的id，包括自己
     *
     * @return 所有上级部门的id，包括自己的部门id
     */
    public List<Integer> getParentDeptIds()
    {
        List<SimpleDeptInfo> deptInfos = getParentDepts();

        if (deptInfos != null)
        {
            List<Integer> ids = new ArrayList<Integer>(deptInfos.size());

            for (SimpleDeptInfo deptInfo : deptInfos)
                ids.add(deptInfo.getDeptId());

            return ids;
        }
        else
        {
            return null;
        }
    }

    /**
     * 判断此部门是否属于另外一个部门
     *
     * @param deptId 部门id
     * @return 属于则返回true，否则返回false
     */
    public boolean isBelongTo(Integer deptId)
    {
        List<SimpleDeptInfo> parentDepts = getParentDepts();
        if (parentDepts == null)
            return false;

        for (SimpleDeptInfo deptInfo : parentDepts)
        {
            if (deptInfo.getDeptId().equals(deptId))
                return true;
        }
        return false;
    }

    public static DeptInfo getDeptInfo(Integer deptId)
    {
        if (deptId == null)
            return null;

        if (deptId <= 0)
            return null;

        try
        {
            return deptServiceProvider.get().getDept(deptId);
        }
        catch (Exception ex)
        {
            Tools.wrapException(ex);
            return null;
        }
    }

    public synchronized String getPhone()
    {
        if (phone == null)
        {
            try
            {
                phone = organDaoProvider.get().getUser(userId).getPhone();
            }
            catch (Exception ex)
            {
                Tools.wrapException(ex);
            }
        }
        return phone;
    }

    public synchronized void setPhone(String phone)
    {
        this.phone = phone;
    }

    public synchronized UserType getUserType()
    {
        if (userType == null)
        {
            try
            {
                userType = organDaoProvider.get().getUser(userId).getType();
            }
            catch (Exception ex)
            {
                Tools.wrapException(ex);
            }
        }
        return userType;
    }

    public synchronized void setUserType(UserType userType)
    {
        this.userType = userType;
    }

    public synchronized Sex getSex()
    {
        if (sex == null)
        {
            try
            {
                sex = organDaoProvider.get().getUser(userId).getSex();
            }
            catch (Exception ex)
            {
                Tools.wrapException(ex);
            }
        }
        return sex;
    }

    public synchronized String getStation()
    {
        if (station == null)
        {
            try
            {
                station = StringUtils.concat(organDaoProvider.get().getUser(userId).getStations(), ",");
            }
            catch (Exception ex)
            {
                Tools.wrapException(ex);
            }
        }
        return station;
    }

    public User getUserEntity() throws Exception
    {
        return organDaoProvider.get().getUser(userId);
    }

    public Dept getDeptEntity() throws Exception
    {
        return organDaoProvider.get().getDept(deptId);
    }

    private synchronized Map<String, AppInfo> getApps()
    {
        if (apps == null)
        {
            Map<String, AppInfo> apps = new HashMap<String, AppInfo>();

            if (deptId != null && deptId > 0)
            {
                try
                {
                    OrganDao organDao = organDaoProvider.get();
                    List<UserRole> userRoles = organDao.getUserRoles(getUserId(), allDeptData ? null : deptId);

                    for (UserRole userRole : userRoles)
                    {
                        Integer deptId = userRole.getDeptId();
                        Role role = userRole.getRole();

                        if (role.getType() == RoleType.group)
                        {
                            //角色组
                            for (Role groupRole : role.getGroupRoles())
                                addRole(groupRole, deptId, apps);
                        }
                        else
                        {
                            //单个角色
                            addRole(role, deptId, apps);
                        }
                    }

                    Dept dept = organDao.getDept(deptId);
                    while (dept != null)
                    {
                        List<DeptRole> deptRoles = organDao.getDeptRoles(dept.getDeptId());

                        for (DeptRole deptRole : deptRoles)
                        {
                            if (dept.getDeptId().equals(deptId) ||
                                    deptRole.getInheritable() != null && deptRole.getInheritable())
                            {
                                Role role = deptRole.getRole();
                                if (role.getType() == RoleType.group)
                                {
                                    //角色组
                                    for (Role groupRole : role.getGroupRoles())
                                        addRole(groupRole, deptId, apps);
                                }
                                else
                                {
                                    //单个角色
                                    addRole(role, deptId, apps);
                                }
                            }
                        }

                        dept = dept.getParentDept();
                    }

                }
                catch (Exception ex)
                {
                    Tools.wrapException(ex);
                }
            }

            this.apps = Collections.unmodifiableMap(apps);
        }

        return apps;
    }

    private void addRole(Role role, Integer deptId, Map<String, AppInfo> apps) throws Exception
    {
        for (RoleApp roleApp : role.getRoleApps())
        {
            String appId = roleApp.getAppId();

            AppInfo appInfo = apps.get(appId);
            if (appInfo == null)
            {
                MenuItem menu = menuContainer.get().getMenu(appId);
                if (menu != null)
                    apps.put(appId, appInfo = new AppInfo(appId, menu.getAuths()));
                else
                    apps.put(appId, null);
            }

            if (appInfo != null)
            {
                appInfo.add(roleApp, deptId);
                appInfo.setSelf(appInfo.isSelf() && roleApp.isSelf() != null && roleApp.isSelf());
            }
        }
    }

    public AppInfo getApp(String appId)
    {
        return getApps().get(appId);
    }

    public Set<String> getAppIds()
    {
        return getApps().keySet();
    }

    /**
     * 能否访问某权限
     *
     * @param appId 功能ID
     * @return 能访问返回true，不能访问返回false
     */
    public boolean isAccessable(String appId)
    {
        return admin || hasApp(appId);
    }

    /**
     * 是否拥有访问某个功能的权限
     *
     * @param appId 功能ID
     * @return 有权限返回true，没有权限返回false
     */
    public boolean hasApp(String appId)
    {
        return getApps().containsKey(appId);
    }

    public boolean canAccessUrl(String url)
    {
        if (admin)
            return true;

        MenuItem menuItem = menuContainer.get().getMenuByUrl(url.trim());
        return menuItem != null && hasApp(menuItem.getMenuId());
    }

    /**
     * 是否能够访问某个功能的某些权限
     *
     * @param appId 功能ID
     * @param auths 权限列表，只要能访问其中一个权限则返回true
     * @return 如果能够访问返回一个AppInfo对象，如果不能返回null
     */
    public AppInfo getApp(String appId, Collection<String> auths)
    {
        return getApp(appId, auths, getApps());
    }

    /**
     * 是否能够访问某些功能的某些权限
     *
     * @param appMap key表示功能ID，value表示对应的权限列表
     * @return 返回能访问的功能列表，如果没有功能能访问，返回null
     */
    public List<AppInfo> getApps(Map<String, Collection<String>> appMap)
    {
        Map<String, AppInfo> apps = getApps();
        List<AppInfo> appInfos = null;
        for (Map.Entry<String, Collection<String>> entry : appMap.entrySet())
        {
            AppInfo appInfo = getApp(entry.getKey(), entry.getValue(), apps);
            if (appInfo != null)
            {
                if (appInfos == null)
                    appInfos = new ArrayList<AppInfo>();
                appInfos.add(appInfo);
            }
        }

        return appInfos;
    }


    private AppInfo getApp(String appId, Collection<String> auths, Map<String, AppInfo> apps)
    {
        AppInfo appInfo = apps.get(appId);
        if (appInfo != null)
        {
            if (auths.contains("default"))
                return appInfo;

            for (String auth : auths)
            {
                if (appInfo.hasAuth(auth))
                    return appInfo;
            }
        }

        return null;
    }

    public boolean hasAuth(String appId, String auth, boolean force)
    {
        if (admin)
            return true;

        AppInfo appInfo = getApps().get(appId);
        return appInfo != null && appInfo.hasAuth(auth, force);
    }

    public boolean hasAuth(String appId, String auth)
    {
        return hasAuth(appId, auth, false);
    }

    /**
     * 获得对某功能拥有权限的部门
     *
     * @param appId  功能ID
     * @param level  部门级别，－1表示所有部门
     * @param filter 部门过滤器
     * @return 满足条件的部门列表
     */
    @SuppressWarnings("unchecked")
    public Collection<Integer> getAuthDeptIds(String appId, int level, Filter<DeptInfo> filter)
    {
        if (appId != null)
        {
            AppInfo appInfo = getApp(appId);
            if (appInfo != null)
                return appInfo.getDeptIds(level, filter);
        }

        return admin ? null : Collections.EMPTY_SET;
    }

    public Collection<Integer> getAuthDeptIds(String appId, int level)
    {
        return getAuthDeptIds(appId, level, null);
    }

    public Collection<Integer> getAuthDeptIds(String appId, Filter<DeptInfo> filter)
    {
        return getAuthDeptIds(appId, -1, filter);
    }

    public Collection<Integer> getAuthDeptIds(String appId)
    {
        return getAuthDeptIds(appId, -1, null);
    }

    public Collection<Integer> getAuthDeptIdsByUrl(String url)
    {
        return getAuthDeptIdsByUrl(Collections.singleton(url));
    }

    public Collection<Integer> getAuthDeptIdsByUrl(String... urls)
    {
        return getAuthDeptIdsByUrl(Arrays.asList(urls));
    }

    public Collection<Integer> getAuthDeptIdsByUrl(Collection<String> urls)
    {
        List<String> appIds = getAppIdsByUrl(urls);

        if (appIds.size() == 1)
            return getAuthDeptIds(appIds.get(0));

        Set<Integer> result = new HashSet<Integer>();

        for (String appId : appIds)
        {
            Collection<Integer> authDeptIds = getAuthDeptIds(appId);
            if (authDeptIds == null)
                return null;
            result.addAll(authDeptIds);
        }

        return result;
    }

    public boolean isSelf(String appId)
    {
        AppInfo appInfo = getApp(appId);
        return appInfo != null && appInfo.isSelf();
    }

    @SuppressWarnings("unchecked")
    public synchronized <T> T getAttribute(String name)
    {
        return attributes == null ? null : (T) attributes.get(name);
    }

    public synchronized void setAttribute(String name, Object value)
    {
        if (attributes == null)
            attributes = new HashMap<String, Object>();
        attributes.put(name, value);
    }

    public static MenuItem getMenuByUrl(String url)
    {
        return menuContainer.get().getMenuByUrl(url);
    }

    public static String getAppIdByUrl(String url)
    {
        MenuItem menu = getMenuByUrl(url);

        if (menu != null)
            return menu.getMenuId();
        else
            return null;
    }

    public static List<String> getAppIdsByUrl(String url)
    {
        return menuContainer.get().getMenuIdsByUrl(url);
    }

    public static List<String> getAppIdsByUrl(String... urls)
    {
        return menuContainer.get().getMenuIdsByUrl(urls);
    }

    public static List<String> getAppIdsByUrl(Collection<String> urls)
    {
        return menuContainer.get().getMenuIdsByUrl(urls);
    }
}
