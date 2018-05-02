package com.gzzm.platform.organ;

import com.gzzm.platform.commons.SystemException;
import net.cyan.commons.transaction.Transactional;
import net.cyan.crud.CrudUtils;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.*;

/**
 * 组织机构相关的数据接口
 *
 * @author camel
 * @date 2009-7-18
 */
public abstract class OrganDao extends GeneralDao
{
    public OrganDao()
    {
    }

    public User getUser(Integer userId) throws Exception
    {
        return load(User.class, userId);
    }

    public Dept getDept(Integer deptId) throws Exception
    {
        return load(Dept.class, deptId);
    }

    public Role getRole(Integer roleId) throws Exception
    {
        return load(Role.class, roleId);
    }

    public UserDept getUserDept(Integer userId, Integer deptId) throws Exception
    {
        return load(UserDept.class, userId, deptId);
    }

    public Station getStation(Integer stationId) throws Exception
    {
        return load(Station.class, stationId);
    }

    public RoleScope getScope(Integer scopeId) throws Exception
    {
        return load(RoleScope.class, scopeId);
    }

    /**
     * 获得根部门
     *
     * @return 根部门
     * @throws Exception 获取数据错误
     */
    @OQL("select d from Dept d where d.parentDeptId is null")
    public abstract Dept getRootDept() throws Exception;

    @OQL("select d from Dept d where d.deptName=:1 and d.state=0")
    public abstract List<Dept> getDeptsByName(String deptName) throws Exception;

    /**
     * 根据登录名获得用户信息
     *
     * @param loginName 登录名
     * @param userId    不包括此用户ID，用于唯一性校验
     * @return User对象
     * @throws Exception 数据库异常
     */
    @OQL("select u from User u where u.loginName=:1 and u.userId<>?2 and type=?3 and state=0")
    public abstract User getUserByLoginName(String loginName, Integer userId, UserType type) throws Exception;

    public User getUserByLoginName(String loginName, UserType type) throws Exception
    {
        return getUserByLoginName(loginName, null, type);
    }

    public User getUserByLoginName(String loginName, Integer userId) throws Exception
    {
        return getUserByLoginName(loginName, userId, null);
    }

    public User getUserByLoginName(String loginName) throws Exception
    {
        return getUserByLoginName(loginName, null, null);
    }

    /**
     * 通过姓名和手机号码获得用户列表
     *
     * @param userName 用户姓名
     * @param phone    手机号码
     * @return 用户列表
     * @throws Exception 数据库错误
     */
    @OQL("select u from User u where u.userName=:1 and u.phone=:2")
    public abstract List<User> getUserByNameAndPhone(String userName, String phone) throws Exception;

    /**
     * 根据证书id获得用户信息
     *
     * @param certId   证书id，一个用户可以有多个证书，多个证书的id之间用空格分开
     * @param certType 证书类型
     * @param userId   排除掉这个用户id，用于唯一性校验
     * @return User对象
     * @throws Exception 数据库异常
     */
    @OQL("select u from User u where u.certId like '% '||:1||' %' and (u.certType=?2 or u.certType is null) and u.userId<>?3 and u.state=0")
    public abstract User getUserByCert(String certId, String certType, Integer userId) throws Exception;

    public User getUserByCert(String certId, String certType) throws Exception
    {
        return getUserByCert(certId, certType, null);
    }

    /**
     * 根据电话号码获得用户信息
     *
     * @param phone 电话号码
     * @return User对象
     * @throws Exception 数据库异常
     */
    @GetByField("phone")
    public abstract User getUserByPhone(String phone) throws Exception;

    /**
     * 根据用户id获得用户名称
     *
     * @param userId 用户id
     * @return 用户名称
     * @throws Exception 数据库异常
     */
    @OQL("select userName from User where userId=:1")
    public abstract String getUserName(Integer userId) throws Exception;

    /**
     * 根据用户id获得登录名
     *
     * @param userId 用户id
     * @return 登录名
     * @throws Exception 数据库异常
     */
    @OQL("select loginName from User where userId=:1")
    public abstract String getLoginName(Integer userId) throws Exception;

    /**
     * 根据部门id获得部门名称
     *
     * @param deptId 部门id
     * @return 部门名称
     * @throws Exception 数据库异常
     */
    @OQL("select deptName from Dept where deptId=:1")
    public abstract String getDeptName(Integer deptId) throws Exception;

    /**
     * 获得某用户所属的所有部门的信息
     *
     * @param userId 用户id
     * @return 部门列表
     * @throws Exception 数据库异常
     */
    @OQL("select distinct u.dept from UserDept u where u.userId=:1 and u.dept.state=0 order by u.userOrder")
    public abstract List<Dept> getDeptsByUserId(Integer userId) throws Exception;

    /**
     * 获得某用户所属的所有部门的id
     *
     * @param userId 用户id
     * @return 部门id列表
     * @throws Exception 数据库异常
     */
    @OQL("select u.deptId from UserDept u where u.userId=:1 and u.dept.state=0 order by u.userOrder,u.deptId")
    public abstract List<Integer> getDeptIdsByUserId(Integer userId) throws Exception;

    /**
     * 获得某用户所属的所有对应的UserDept对象
     *
     * @param userId 用户id
     * @return UserDept对象列表
     * @throws Exception 数据库异常
     */
    @OQL("select u from UserDept u where u.userId=:1 and u.dept.state=0 order by u.userOrder,u.deptId")
    public abstract List<UserDept> getUserDepts(Integer userId) throws Exception;

    /**
     * 获得某用户所属的所有对应的UserDept对象
     *
     * @param userId 用户id
     * @return UserDept对象列表
     * @throws Exception 数据库异常
     */
    @OQL("select u from UserDept u where u.userId=:1 order by u.userOrder,u.deptId")
    public abstract List<UserDept> getAllUserDepts(Integer userId) throws Exception;

    /**
     * 获得单个部门下的所有用户
     *
     * @param deptId 部门id
     * @return 用户列表
     * @throws Exception 数据库异常
     */
    @OQL("select u.user from UserDept u where deptId=:1 and u.user.state=0 order by u.orderId")
    public abstract List<User> getUsersInDept(Integer deptId) throws Exception;

    /**
     * 获得多个部门下的所有用户
     *
     * @param deptIds 部门id列表
     * @return 用户列表
     * @throws Exception 数据库异常
     */
    @OQL("select u.u from (select u, min(select leftValue from u.depts d where d.state=0) as leftValue from User u where " +
            " state=0 and userId in (select d.userId from UserDept d where d.deptId in :1)) u order by u.leftValue" +
            ",first(select d.orderId from UserDept d where d.userId=u.u.userId and d.dept.leftValue=u.leftValue and d.dept.state=0)")
    public abstract List<User> queryUsersInDepts(Collection<Integer> deptIds) throws Exception;

    /**
     * 获得某个用户默认登录的部门
     *
     * @param userId 用户id
     * @return 此用户默认登录的部门id
     * @throws Exception 数据库异常
     */
    @OQL("select u.deptId from UserDept u where u.userId=:1 and u.dept.state=0 and u.defaultDept=1 order by u.userOrder,u.deptId")
    public abstract Integer getDefaultDeptId(Integer userId) throws Exception;

    /**
     * 设置某个用户的默认登录部门
     *
     * @param userId 用户ID
     * @param deptId 默认登录部门ID
     * @throws Exception 数据库异常
     */
    public void setDefaultDeptId(Integer userId, Integer deptId) throws Exception
    {
        for (UserDept userDept : getUserDepts(userId))
        {
            if (userDept.getDeptId().equals(deptId))
            {
                if (userDept.isDefaultDept() == null || !userDept.isDefaultDept())
                {
                    userDept.setDefaultDept(true);
                    update(userDept);
                }
            }
            else if (userDept.isDefaultDept() != null && userDept.isDefaultDept())
            {
                userDept.setDefaultDept(false);
                update(userDept);
            }
        }
    }

    @OQL("select userId from User u where state=0 and type=0")
    public abstract List<Integer> getAllUserIds() throws Exception;

    @OQL("select u from User u where userId in :1 order by sortId")
    public abstract List<User> getUsers(Collection<Integer> userIds) throws Exception;

    @OQL("select u,[u.depts] from User u where userId in :1")
    public abstract List<User> getUsersWithDepts(Collection<Integer> userIds) throws Exception;

    @OQL("select d from Dept d where d.state=0 order by d.leftValue")
    public abstract List<Dept> getAllDepts() throws Exception;

    /**
     * 获得某些部门
     *
     * @param deptIds 部门id列表
     * @return 部门列表
     * @throws Exception 数据库查询异常
     */
    @OQL("select d from Dept d where d.state=0 and d.deptId in :1 order by d.leftValue")
    public abstract List<Dept> getDepts(Collection<Integer> deptIds) throws Exception;

    /**
     * 获得全部部门，包括被删除的
     *
     * @param deptIds 部门id列表
     * @return 部门列表
     * @throws Exception 数据库查询异常
     */
    @OQL("select d from Dept d where d.deptId in :1 order by d.leftValue")
    public abstract List<Dept> getAllDepts(Collection<Integer> deptIds) throws Exception;

    /**
     * 将一个用户从一个部门移到另外一个部门
     *
     * @param userId       用户id
     * @param sourceDeptId 源部门id
     * @param targetDeptId 目标部门id
     * @param sourceRemain 源部门的信息是否保留
     * @return 用户原来是否已经存在目标部门，如果是返回true，不是返回false
     * @throws Exception 数据库异常
     */
    public boolean moveUser(Integer userId, Integer sourceDeptId, Integer targetDeptId, boolean sourceRemain)
            throws Exception
    {
        UserDept userDept = load(UserDept.class, userId, targetDeptId);

        //用户原来已经存在目标部门，不作任何操作，返回true
        if (userDept != null)
            return true;

        moveUser0(userId, sourceDeptId, targetDeptId, sourceRemain);

        return false;
    }

    @Transactional
    protected UserDept moveUser0(Integer userId, Integer sourceDeptId, Integer targetDeptId, boolean sourceRemain)
            throws Exception
    {
        //往UserDept表增加新记录
        UserDept userDept = new UserDept(userId, targetDeptId);
        userDept.setOrderId(CrudUtils.getOrderValue(6, true));
        add(userDept);

        //将角色复制
        for (Integer roleId : getRoleIds(userId, sourceDeptId))
            add(new UserRole(userId, targetDeptId, roleId));

        //将岗位复制
        for (Integer stationId : getStationIds(userId, sourceDeptId))
            add(new UserStation(userId, targetDeptId, stationId));

        if (!sourceRemain)
        {
            //不保留用户在源部门的信息
            delete(UserDept.class, userId, sourceDeptId);

            //删除角色和岗位信息
            oqlDelete(UserRole.class, "userId=:1 and deptId=:2", userId, sourceDeptId);
            oqlDelete(UserStation.class, "userId=:1 and deptId=:2", userId, sourceDeptId);
        }

        return userDept;
    }

    /**
     * 将用户从某个部门移除
     *
     * @param userId 用户id
     * @param deptId 部门id
     * @return 用户是否彻底删除
     * @throws Exception 异常
     */
    @Transactional
    public boolean deleteUser(Integer userId, Integer deptId) throws Exception
    {
        UserDept userDept = load(UserDept.class, userId, deptId);
        if (userDept != null)
        {
            //删除角色和岗位信息
            userDept.getRoles().clear();
            userDept.getStations().clear();

            delete(userDept);

            Boolean exists = oqlQueryFirst("exists UserDept where userId=:1", userId);
            if (!exists)
            {
                //用户已经不存在于其它部门，将用户删除
                User user = new User();
                user.setUserId(userId);
                user.setState((byte) 2);

                //清空登录名和证书id
                user.setLoginName("");
                user.setCertId("");
                update(user);

                return true;
            }
        }
        else
        {
            boolean b = false;
            User user = get(User.class, userId);
            for (Dept dept : user.getDepts())
            {
                b |= deleteUser(userId, dept.getDeptId());
            }

            return b;
        }

        return false;
    }

    /**
     * 删除用户
     *
     * @param userId 要删除的用户的ID
     * @throws Exception 异常
     */
    @Transactional
    public void deleteUser(Integer userId) throws Exception
    {
        deleteUserFormAllDept(userId);

        User user = new User();
        user.setUserId(userId);
        user.setState((byte) 2);

        //清空登录名和证书id
        user.setLoginName("");
        user.setCertId("");
        update(user);
    }

    @Transactional
    public void deleteUserFormAllDept(Integer userId) throws Exception
    {
        executeOql("delete from UserRole where userId=:1", userId);
        executeOql("delete from UserStation where userId=:1", userId);
        executeOql("delete from UserDept where userId=:1", userId);
    }

    public void freezeUser(Integer userId, boolean freezed) throws Exception
    {
        User user = new User();
        user.setUserId(userId);
        user.setState(freezed ? (byte) 1 : (byte) 0);

        update(user);
    }

    /**
     * 设置用户所属的部门
     *
     * @param userId  用户id
     * @param deptIds 部门id列表
     * @return 用户发生了变化返回true，没有发生变化返回false
     * @throws Exception 数据库操作异常
     */
    @Transactional
    public boolean setUserDepts(Integer userId, Integer... deptIds) throws Exception
    {
        try
        {
            boolean result = false;

            List<UserDept> userDepts = getAllUserDepts(userId);

            Integer deptId0 = null;

            if (userDepts.size() > 0)
                deptId0 = userDepts.get(0).getDeptId();

            for (int i = 0; i < deptIds.length; i++)
            {
                Integer deptId = deptIds[i];

                if (deptId != null)
                {
                    UserDept userDept = null;
                    for (Iterator<UserDept> iterator = userDepts.iterator(); iterator.hasNext(); )
                    {
                        UserDept userDept1 = iterator.next();
                        if (userDept1.getDeptId().equals(deptId))
                        {
                            //已经匹配到，从原部门列表中删除，原部门列表最后剩下的即没有匹配到的，等待删除
                            iterator.remove();

                            userDept = userDept1;
                            break;
                        }
                    }

                    if (userDept == null)
                    {
                        result = true;

                        //部门原来不存在，添加用户到此部门
                        if (deptId0 != null)
                        {
                            userDept = moveUser0(userId, deptId0, deptId, true);
                        }
                        else
                        {
                            userDept = new UserDept();
                            userDept.setUserId(userId);
                            userDept.setDeptId(deptId);
                            add(userDept);
                        }
                    }

                    //调整部门顺序
                    userDept.setUserOrder(i);
                    update(userDept);
                }
            }

            //删除剩下的部门
            for (UserDept userDept : userDepts)
            {
                result = true;

                //删除角色和岗位信息
                userDept.getRoles().clear();
                userDept.getStations().clear();

                delete(userDept);
            }

            return result;
        }
        catch (Throwable ex)
        {
            throw new SystemException("set depts fail,userId:" + userId + ",deptIds:" + Arrays.toString(deptIds), ex);
        }
    }

    /**
     * 获得用户在某个部门下的所有角色
     *
     * @param userId 用户id
     * @param deptId 部门id
     * @return 角色列表
     * @throws Exception 数据库异常
     */
    @OQL("select role from UserRole where userId=:1 and deptId=:2")
    public abstract List<Role> getRoles(Integer userId, Integer deptId) throws Exception;

    /**
     * 获得用户在某个部门下的所有角色Id
     *
     * @param userId 用户id
     * @param deptId 部门id
     * @return 角色id列表
     * @throws Exception 数据库异常
     */
    @OQL("select roleId from UserRole where userId=:1 and deptId=?2")
    public abstract List<Integer> getRoleIds(Integer userId, Integer deptId) throws Exception;

    /**
     * 获得用户在某个部门下的所有角色
     *
     * @param userId 用户id
     * @param deptId 部门id
     * @return 角色列表
     * @throws Exception 数据库异常
     */
    @OQL("select r from UserRole r where userId=:1 and deptId=?2")
    public abstract List<UserRole> getUserRoles(Integer userId, Integer deptId) throws Exception;

    @OQL("select r from DeptRole r where deptId=:1")
    public abstract List<DeptRole> getDeptRoles(Integer deptId) throws Exception;

    /**
     * 获得用户在某个部门下的所有岗位
     *
     * @param userId 用户id
     * @param deptId 部门id
     * @return 岗位列表
     * @throws Exception 数据库异常
     */
    @OQL("select station from UserStation where userId=:1 and deptId=:2")
    public abstract List<Station> getStations(Integer userId, Integer deptId) throws Exception;

    /**
     * 获得用户在某个部门下的所有岗位id
     *
     * @param userId 用户id
     * @param deptId 部门id
     * @return 岗位id列表
     * @throws Exception 数据库异常
     */
    @OQL("select stationId from UserStation where userId=:1 and deptId=:2")
    public abstract List<Integer> getStationIds(Integer userId, Integer deptId) throws Exception;

    @OQL("select r from RoleApp r where appId=:1")
    public abstract List<RoleApp> getRoleAppsByApp(String appId) throws Exception;

    @OQL("select s from RoleScopeDept s where scopeId=:1 order by deptId desc")
    public abstract List<RoleScopeDept> getRoleScopeDepts(Integer scopeId) throws Exception;

    @OQL("select s from RoleScopeDept s where scopeId in :1 order by deptId desc")
    public abstract List<RoleScopeDept> getRoleScopeDepts(Collection<Integer> scopeIds) throws Exception;

    public RoleScope getRoleScope(Integer scpoeId) throws Exception
    {
        return load(RoleScope.class, scpoeId);
    }

    @OQL("select distinct u.userId from UserRole u where u.roleId in :1 or " +
            "(select 1 from u.role.groupRoles g where g.roleId in :1) is not empty and u.role.type=1 and u.user.state=0")
    public abstract List<Integer> getUserIdsByRoles(List<Integer> roleIds) throws Exception;

    @OQL("select distinct u.userId from UserRole u where u.deptId in :2 and (u.roleId=:1 or " +
            "(select 1 from u.role.groupRoles g where g.roleId=:1) is not empty) and u.user.state=0")
    public abstract List<Integer> getUserIdsByRole(Integer roleId, Collection<Integer> deptIds) throws Exception;

    public List<Integer> getRoleIdsByApp(String appId) throws Exception
    {
        List<Integer> roleIds = getSingleRoleIdsByApp(appId);

        roleIds.addAll(getRoleGroupIds(roleIds));

        return roleIds;
    }

    @OQL("select distinct r.roleId from RoleApp r where r.appId=:1")
    protected abstract List<Integer> getSingleRoleIdsByApp(String appId) throws Exception;

    @OQL("select distinct r.roleId from Role r where " +
            "(select 1 from r.groupRoles g where g.roleId in :1) is not empty and r.type=1")
    protected abstract List<Integer> getRoleGroupIds(List<Integer> roleIds) throws Exception;

    /**
     * 查询某部门里的角色
     *
     * @param deptId 部门ID
     * @return 角色列表
     * @throws Exception 数据库查询异常
     */
    @OQL("select r from Role r where r.deptId=:1 and type<2 order by r.orderId")
    public abstract List<Role> getRolesInDept(Integer deptId) throws Exception;

    /**
     * 查询某部门里的单角色，即排除掉角色组
     *
     * @param deptId 部门ID
     * @return 角色列表
     * @throws Exception 数据库查询异常
     */
    @OQL("select r from Role r where r.deptId = :1 and (r.type=0 or r.type is null) order by r.orderId")
    public abstract List<Role> getSingleRolesInDept(Integer deptId) throws Exception;

    /**
     * 查询某些部门里的岗位
     *
     * @param deptId 部门ID
     * @return 岗位列表
     * @throws Exception 数据库查询异常
     */
    @OQL("select s from Station s where s.deptId = :1 order by s.orderId")
    public abstract List<Station> getStationsInDept(Integer deptId) throws Exception;

    @OQL("select s from RoleScope s where s.deptId = :1 order by s.orderId")
    public abstract List<RoleScope> getRoleScopes(Integer deptId) throws Exception;

    @GetByField({"deptId", "scopeName"})
    public abstract RoleScope getRoleScopeByName(Integer deptId, String scopeName) throws Exception;

    @OQL("select s from RoleScope s where s.deptId in ?1 order by s.dept.leftValue,s.orderId")
    public abstract List<RoleScope> getRoleScopes(Collection<Integer> deptIds) throws Exception;

    public List<Role> getRolesByAppIds(Collection<String> appIds, Integer deptId) throws Exception
    {
        List<Role> roles = getSingleRolesByAppIds(appIds, deptId);

        List<Integer> roleIds = new ArrayList<Integer>(roles.size());
        for (Role role : roles)
        {
            roleIds.add(role.getRoleId());
        }

        roles.addAll(getRoleGroups(roleIds, deptId));

        return roles;
    }

    @OQL("select distinct r.role from RoleApp r where r.appId in :1 and role.deptId=:2")
    public abstract List<Role> getSingleRolesByAppIds(Collection<String> appIds, Integer deptId) throws Exception;

    @OQL("select r from Role r where " +
            "(select 1 from r.groupRoles g where g.roleId in :1) is not empty and r.type=1 and r.deptId=:2")
    protected abstract List<Role> getRoleGroups(List<Integer> roleIds, Integer deptId) throws Exception;

    public List<User> queryUsersByName(String name, Collection<Integer> deptIds, int max) throws Exception
    {
        String spell = name + "%";
        name = "%" + spell;

        return deptIds == null ? queryUsersByName0(name, spell, max) : queryUsersByName0(name, spell, deptIds, max);
    }

    /**
     * 通过名称，全拼，简拼查询用户
     *
     * @param name    用户姓名
     * @param spell   拼音
     * @param deptIds 部门id，表示只在这些部门中查询
     * @return 匹配的用户列表
     * @throws Exception 异常
     */
    @OQL("select u.u from (select u, min(select leftValue from u.depts d where d.state=0) as leftValue from User u where " +
            "(userName like :1 or spell like :2 or simpleSpell like :2) and " +
            " state=0 and type=0 and userId in (select d.userId from UserDept d where d.deptId in :3)) u order by u.leftValue" +
            ",first(select d.orderId from UserDept d where d.userId=u.u.userId and d.dept.leftValue=u.leftValue and d.dept.state=0) limit :4")
    protected abstract List<User> queryUsersByName0(String name, String spell, Collection<Integer> deptIds, int max)
            throws Exception;

    /**
     * 通过名称，全拼，简拼查询用户
     *
     * @param name  用户姓名
     * @param spell 拼音
     * @return 匹配的用户列表
     * @throws Exception 异常
     */
    @OQL("select u from User u where (userName like :1 or spell like :2 or simpleSpell like :2) " +
            " and state=0 and type=0 order by userName limit :3")
    protected abstract List<User> queryUsersByName0(String name, String spell, int max) throws Exception;

    @OQL("select d from Dept d where state=0 and (deptName like :1 or spell like :1 or simpleSpell like :1)")
    public abstract List<Dept> queryDeptsByName(String name) throws Exception;

    @OQL("select d.deptId from Dept d where state=0 and (deptName like :1 or spell like :1 or simpleSpell like :1)")
    public abstract List<Integer> queryDeptIdsByName(String name) throws Exception;

    @OQL("select distinct ur.user from UserStation ur where ur.stationId=:1")
    public abstract List<User> getUsersOnStation(Integer stationId) throws Exception;

    @OQL("select ud.user from UserDept ud where ud.deptId=:1 and " +
            "exists us in stations : us.stationId=:2 order by ud.orderId")
    public abstract List<User> getUsersOnStation(Integer deptId, Integer stationId) throws Exception;

    @OQL("select ud.user from UserDept ud where ud.deptId=:1 and " +
            "exists us in stations : us.stationId in :2 order by ud.orderId")
    public abstract List<User> getUsersOnStations(Integer deptId, List<Integer> stationId) throws Exception;

    @OQL("select ud.user from UserDept ud where ud.deptId=:1 and " +
            "(exists us in stations : us.station.stationName in :2) and ud.user.state=0 order by ud.orderId")
    public abstract List<User> getUsersByStationNames(Integer deptId, List<String> stationNames) throws Exception;

    @OQL("select ud,[ud.user],[ud.dept] from UserDept ud where ud.deptId in :1 and " +
            "(exists us in stations : us.station.stationName in :2) and ud.user.state=0 " +
            "order by ud.dept.leftValue,ud.orderId")
    public abstract List<UserDept> getUserDeptsByStationNames(Collection<Integer> deptIds, List<String> stationNames)
            throws Exception;

    @OQL("select ud.user from UserDept ud where ud.deptId=:1 and " +
            "(exists ur in roles : ur.role.roleName in :2) and ud.user.state=0 order by ud.orderId")
    public abstract List<User> getUsersByRoleNames(Integer deptId, List<String> roleNames) throws Exception;

    @OQL("select l from UserLevel l order by orderId")
    public abstract List<UserLevel> getUserLevels() throws Exception;

    @OQL("select r from Role r where deptId=:1 and type=2 and parentRoleId is null order by orderId")
    public abstract List<Role> getTopRoleCatalogs(Integer deptId) throws Exception;

    @OQL("select r from Role r where deptId=:1 and type=2 and roleName like :2")
    public abstract List<Role> queryRoleCatalogs(Integer deptId, String roleName) throws Exception;

    @OQL("select r from RoleScope r where deptId=:1 and type=1 and parentScopeId is null order by orderId")
    public abstract List<RoleScope> getTopRoleScopeCatalogs(Integer deptId) throws Exception;

    @OQL("select r from RoleScope r where deptId=:1 and type=1 and scopeName like :2")
    public abstract List<RoleScope> queryRoleScopeCatalogs(Integer deptId, String scopeName) throws Exception;

    @OQLUpdate("update Role set parentRoleId=:2 where roleId in :1")
    public abstract void moveRole(Integer[] roleIds, Integer parentRoleId) throws Exception;

    @OQL("select propertyValue from UserProperty where userId=:1 and propertyName=:2")
    public abstract String getUserPropertyValue(Integer userId, String propertyName) throws Exception;

    public void saveUserPropertyValue(Integer userId, String propertyName, String propertyValue) throws Exception
    {
        UserProperty userProperty = new UserProperty();
        userProperty.setUserId(userId);
        userProperty.setPropertyName(propertyName);
        userProperty.setPropertyValue(propertyValue);

        save(userProperty);
    }
}