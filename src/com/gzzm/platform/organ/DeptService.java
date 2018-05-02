package com.gzzm.platform.organ;

import com.gzzm.platform.commons.*;
import net.cyan.commons.util.Filter;

import java.util.*;

/**
 * 和部门相关的服务的接口 其有两个实现，一为DeptCacheTree，使用缓存，另一个为OrganService，不使用缓存
 *
 * @author camel
 * @date 2009-7-29
 */
public abstract class DeptService
{
    public abstract OrganDao getDao() throws Exception;

    /**
     * 获得根部门
     *
     * @return 根部门
     * @throws Exception 数据库读取数据错误
     */
    public abstract DeptInfo getRoot() throws Exception;

    /**
     * 获得部门
     *
     * @param deptId 部门id
     * @return 对应的部门信息
     * @throws Exception 数据库读取数据错误
     */
    public abstract DeptInfo getDept(Integer deptId) throws Exception;

    /**
     * 根据部门id获得部门名称
     *
     * @param deptId 部门id
     * @return 对应的部门名称
     * @throws Exception 数据库读取数据错误
     */
    public abstract String getDeptName(Integer deptId) throws Exception;

    /**
     * 加载所有部门
     *
     * @return 所有部门的集合
     * @throws Exception 加载数据异常
     */
    public abstract Collection<? extends DeptInfo> allDepts() throws Exception;

    /**
     * 获得某用户所属的所有部门的信息
     *
     * @param userId 用户id
     * @return 部门列表
     * @throws Exception 数据库异常
     */
    public abstract List<? extends DeptInfo> getDeptsByUserId(Integer userId) throws Exception;

    /**
     * 过滤出一棵满足节点的树
     *
     * @param rootId 根节点id，如果为null为系统根部门
     * @param level  部门的级别，只记载此级别的部门，如果level为-1，表示记载任何级别分部门
     * @param filter 过滤器
     * @return 满足节点的树的根节点
     * @throws Exception 加载数据异常
     */
    public abstract DeptInfo getDeptTree(Integer rootId, int level, Filter<DeptInfo> filter) throws Exception;

    public DeptInfo getDeptTree(Integer rootId, int level) throws Exception
    {
        return getDeptTree(rootId, level, null);
    }

    public DeptInfo getDeptTree(Integer rootId, Filter<DeptInfo> filter) throws Exception
    {
        return getDeptTree(rootId, -1, filter);
    }

    public abstract List<SimpleDeptInfo> getAuthDeptTree(Integer parentId, Filter<DeptInfo> filter,
                                                         Collection<Integer> authDeptIds) throws Exception;

    public abstract boolean containsSubAuthDept(Integer deptId, Filter<DeptInfo> filter,
                                                Collection<Integer> authDeptIds) throws Exception;


    /**
     * 加载所有子部门的id
     *
     * @param depts  初始部门信息，key为初始部门id，value为一个DeptFilter，
     *               表示加载满足此过滤器的子部门，null表示只加载本部门id
     * @param level  部门级别，表示只加载此级别的部门，-1<0表示加载所有级别的部门
     * @param filter 部门过滤，过滤不满足条件的部门
     * @return 子部门id
     * @throws Exception 加载数据库异常
     */
    public Collection<Integer> loadDeptIds(Map<Integer, Filter<DeptInfo>> depts, int level, Filter<DeptInfo> filter)
            throws Exception
    {
        Map<Integer, Filter<DeptInfo>> map = new HashMap<Integer, Filter<DeptInfo>>();
        Set<Integer> result = new HashSet<Integer>();

        boolean excluded = false;

        for (Map.Entry<Integer, Filter<DeptInfo>> entry : depts.entrySet())
        {
            Integer deptId = entry.getKey();
            Filter<DeptInfo> filter1 = entry.getValue();

            if (filter1 == null && filter == null)
            {
                //只加载本部门，并且不需要过滤条件
                if (!result.contains(deptId))
                {
                    result.add(deptId);
                    if (!map.containsKey(deptId))
                        map.put(deptId, null);
                }
            }
            else if (filter1 == AllDeptFilter.EXCLUDED || filter1 == AllDeptFilter.EXCLUDEDALL ||
                    filter == AllDeptFilter.PRIORITY_EXCLUDEDALL || filter == AllDeptFilter.PRIORITY_EXCLUDED)
            {
                excluded = true;
            }
            else
            {
                DeptInfo dept = getDept(deptId);
                if (dept != null)
                    loadDeptId(dept, filter1, map, result, level, filter);
            }
        }

        if (excluded)
        {
            for (Map.Entry<Integer, Filter<DeptInfo>> entry : depts.entrySet())
            {
                Integer deptId = entry.getKey();
                Filter<DeptInfo> filter1 = entry.getValue();

                if (filter1 == AllDeptFilter.EXCLUDED || filter1 == AllDeptFilter.PRIORITY_EXCLUDED)
                {
                    result.remove(deptId);
                }
                else if (filter1 == AllDeptFilter.EXCLUDEDALL || filter == AllDeptFilter.PRIORITY_EXCLUDEDALL)
                {
                    DeptInfo dept = getDept(deptId);
                    if (dept != null)
                    {
                        List<Integer> subDeptIds = dept.allSubDeptIds();
                        for (Integer subDeptId : subDeptIds)
                        {
                            if (filter == AllDeptFilter.PRIORITY_EXCLUDEDALL || !map.containsKey(subDeptId) ||
                                    map.get(subDeptId) != null)
                            {
                                result.remove(subDeptId);
                            }
                        }
                    }
                }
            }
        }


        return result;
    }

    private void loadDeptId(DeptInfo dept, Filter<DeptInfo> filter, Map<Integer, Filter<DeptInfo>> map,
                            Collection<Integer> result, int level, Filter<DeptInfo> filter0) throws Exception
    {
        //部门级别小于指定级别，不继续寻找
        if (level >= 0)
        {
            int level2 = dept.getLevel();
            if (level2 > 0 && level > level2)
                return;
        }

        Integer deptId = dept.getDeptId();

        if (!result.contains(deptId))
        {
            //此部门未加载过，如果接受此部门，将其放入结果中
            if ((filter == null || filter == AllDeptFilter.All || filter.accept(dept)) &&
                    (filter0 == null || filter0.accept(dept)) && (level < 0 || dept.getLevel() == level))
                result.add(dept.getDeptId());
        }

        if (filter == null)
        {
            if (!map.containsKey(deptId))
                map.put(deptId, null);
        }
        else if (map.get(deptId) == null)
        {
            map.put(deptId, filter);

            //原来只加载本部门，现在需要加载子部门
            for (DeptInfo subDept : getDept(deptId).subDepts())
            {
                if (!(subDept instanceof Dept) || ((Dept) subDept).getState() == 0)
                    loadDeptId(subDept, filter, map, result, level, filter0);
            }
        }
    }

    public void travelUsers(final String appId, final List<Integer> deptIds, final UserTraveller traveller)
            throws Exception
    {
        //另开一个线程遍历用户，不影响当前线程
        Tools.run(new Runnable()
        {
            public void run()
            {
                try
                {
                    travelUserImmediately(appId, deptIds, traveller);
                }
                catch (Throwable ex)
                {
                    Tools.log("travel users fail,appId:" + appId + ",deptIds:" + deptIds, ex);
                }
            }
        });
    }

    /**
     * 根据功能ID和部门ID找出用户，并立即遍历用户
     *
     * @param appId     用户拥有的功能ID
     * @param deptIds   部门ID列表
     * @param traveller 遍历器，指定遍历用户时对每个用户执行什么操作
     * @return 遍历过程没有发生错误返回true，否则返回false
     * @throws Exception 从数据库读取用户错误
     * @see #travelUsers
     */
    public boolean travelUserImmediately(String appId, List<Integer> deptIds, UserTraveller traveller) throws Exception
    {
        OrganDao dao = getDao();

        boolean result = true;
        for (Integer userId : getUserIdsByApp(appId, null, deptIds, dao))
        {
            try
            {
                traveller.dispose(dao.getUser(userId));
            }
            catch (Throwable ex)
            {
                //遍历时发生错误，记录错误并记录日志
                result = false;

                Tools.log("travel user fail,userId:" + userId, ex);
            }
        }

        return result;
    }

    public Collection<Integer> getUserIdsByApp(String appId) throws Exception
    {
        OrganDao dao = getDao();

        return dao.getUserIdsByRoles(dao.getRoleIdsByApp(appId));
    }

    public Set<Integer> getUserIdsByApp(String appId, String condition, Integer... deptIds) throws Exception
    {
        return getUserIdsByApp(appId, condition, Arrays.asList(deptIds));
    }

    public Set<Integer> getUserIdsByApp(String appId, Integer... deptIds) throws Exception
    {
        return getUserIdsByApp(appId, Arrays.asList(deptIds));
    }

    /**
     * 获得某个范围内拥有使用某一应用的权限的用户的id
     *
     * @param appId   功能id
     * @param deptIds 部门列表，在这个范围内查询
     * @return 用户id列表
     * @throws Exception 数据库异常
     */
    public Set<Integer> getUserIdsByApp(String appId, List<Integer> deptIds) throws Exception
    {
        return getUserIdsByApp(appId, null, deptIds, getDao());
    }

    public Set<Integer> getUserIdsByApp(String appId, String condition, List<Integer> deptIds) throws Exception
    {
        return getUserIdsByApp(appId, condition, deptIds, getDao());
    }

    private Set<Integer> getUserIdsByApp(String appId, String condition, List<Integer> deptIds, OrganDao dao)
            throws Exception
    {
        List<RoleApp> roleApps = dao.getRoleAppsByApp(appId);

        List<Integer> roleIds = null;
        Map<Integer, Set<Integer>> roleDeptsMap = null;

        for (RoleApp roleApp : roleApps)
        {
            if (condition != null)
            {
                if (roleApp.getCondition() != null && !roleApp.getCondition().equals(condition))
                    continue;
            }

            Integer roleId = roleApp.getRoleId();

            if (roleApp.getScopeId() == -1)
            {
//                if (roleIds == null)
//                    roleIds = new ArrayList<Integer>();
//
//                roleIds.add(roleId);
//
//                if (roleDeptsMap != null)
//                    roleDeptsMap.remove(roleId);
            }
            else
            {
                for (RoleScopeDept scopeDept : dao.getRoleScopeDepts(roleApp.getScopeId()))
                {
                    Integer scopeDeptId = scopeDept.getDeptId();

                    boolean includeSub = scopeDept.isIncludeSub() != null && scopeDept.isIncludeSub();
                    boolean includeSup = scopeDept.isIncludeSup() != null && scopeDept.isIncludeSup();

                    if (scopeDeptId > 0)
                    {
                        //指定了某部门
                        boolean b = false;

                        if (includeSub || includeSup)
                        {
                            DeptInfo dept = getDept(scopeDeptId);

                            //包括上级部门
                            if (includeSup)
                                b = dept.isParentDeptIdsIn(deptIds);

                            //包括下属部门
                            if (!b && includeSub)
                                b = dept.isSubDeptIdsIn(deptIds);
                        }
                        else
                        {
                            //不包括上级和下属部门
                            b = deptIds.contains(scopeDeptId);
                        }

                        if (b)
                        {
                            if (roleIds == null)
                                roleIds = new ArrayList<Integer>();

                            roleIds.add(roleId);

                            if (roleDeptsMap != null)
                                roleDeptsMap.remove(roleId);

                            break;
                        }
                    }
                    else
                    {
                        if (roleDeptsMap == null)
                            roleDeptsMap = new HashMap<Integer, Set<Integer>>();

                        Set<Integer> roleDeptIds = roleDeptsMap.get(roleId);
                        if (roleDeptIds == null)
                            roleDeptsMap.put(roleId, roleDeptIds = new HashSet<Integer>());

                        if (scopeDeptId == 0)
                        {
                            //当前部门
                            if (includeSub || includeSup)
                            {
                                for (Integer deptId : deptIds)
                                {
                                    DeptInfo dept = getDept(deptId);

                                    //包括下属部门
                                    if (includeSub)
                                        roleDeptIds.addAll(dept.allParentDeptIds());

                                    if (includeSup)
                                        roleDeptIds.addAll(dept.allSubDeptIds());
                                }
                            }
                            else
                            {
                                roleDeptIds.addAll(deptIds);
                            }
                        }
                        else
                        {
                            int level = -scopeDeptId - 1;

                            List<Integer> levelDeptIds = new ArrayList<Integer>();
                            for (Integer deptId : deptIds)
                            {
                                DeptInfo dept = getDept(deptId);

                                if (dept.getLevel() >= level)
                                {
                                    if (!levelDeptIds.contains(deptId))
                                        levelDeptIds.add(deptId);
                                }

                                if (includeSub)
                                {
                                    //包含子部门，往上搜索部门
                                    DeptInfo parentDept = dept;
                                    while ((parentDept = parentDept.parentDept()) != null)
                                    {
                                        if (parentDept.getLevel() >= level)
                                        {
                                            Integer deptId2 = parentDept.getDeptId();
                                            if (!levelDeptIds.contains(deptId2))
                                                levelDeptIds.add(deptId2);
                                        }
                                    }
                                }

                                if (includeSup)
                                {
                                    //包含上级部们，往下搜索部门
                                    addSubDepts(dept, levelDeptIds, level, 0);
                                }
                            }

                            for (Integer deptId : levelDeptIds)
                            {
                                roleDeptIds.add(deptId);

                                DeptInfo dept = getDept(deptId);
                                addSubDepts(dept, roleDeptIds, level, 1);
                            }
                        }
                    }
                }
            }
        }

        Set<Integer> result = new HashSet<Integer>();
        if (roleIds != null)
            result.addAll(dao.getUserIdsByRoles(roleIds));

        if (roleDeptsMap != null)
        {
            for (Map.Entry<Integer, Set<Integer>> entry : roleDeptsMap.entrySet())
                result.addAll(dao.getUserIdsByRole(entry.getKey(), entry.getValue()));
        }

        return result;
    }

    private void addSubDepts(DeptInfo dept, Collection<Integer> deptIds, int level, int type)
    {
        for (DeptInfo subDept : dept.subDepts())
        {
            Integer deptId = subDept.getDeptId();
            if (!deptIds.contains(deptId))
            {
                if (type == 0)
                {
                    if (subDept.getLevel() >= level)
                    {
                        deptIds.add(deptId);
                        addSubDepts(subDept, deptIds, level, type);
                    }
                    else if (subDept.getLevel() < 0)
                    {
                        addSubDepts(subDept, deptIds, level, type);
                    }
                }
                else if (subDept.getLevel() < level)
                {
                    deptIds.add(deptId);
                    addSubDepts(subDept, deptIds, level, type);
                }
            }
        }
    }

    public List<User> getUsersByApp(String appId, String condition, Integer... deptIds) throws Exception
    {
        return getUsersByApp(appId, condition, Arrays.asList(deptIds));
    }

    public List<User> getUsersByApp(String appId, Integer... deptIds) throws Exception
    {
        return getUsersByApp(appId, Arrays.asList(deptIds));
    }

    public List<User> getUsersByApp(String appId, List<Integer> deptIds) throws Exception
    {
        return getUsersByApp(appId, null, deptIds);
    }

    public List<User> getUsersByApp(String appId, String condition, List<Integer> deptIds) throws Exception
    {
        OrganDao dao = getDao();
        Set<Integer> userIds = getUserIdsByApp(appId, condition, deptIds, dao);

        return dao.getUsers(userIds);
    }

    /**
     * 搜索满足条件的部门
     *
     * @param filter  过滤器
     * @param deptIds 拥有权限的部门的id的集合，在此范围内搜索，如果为空表示在所有的部门中搜索
     * @return 满足条件的部门集合
     * @throws Exception 搜索或者加载部门异常
     */
    public List<? extends DeptInfo> searchDept(Filter<DeptInfo> filter, Collection<Integer> deptIds) throws Exception
    {
        List<DeptInfo> result = new ArrayList<DeptInfo>();

        if (deptIds == null)
        {
            for (DeptInfo deptInfo : allDepts())
            {
                if (filter.accept(deptInfo))
                    result.add(deptInfo);
            }
        }
        else
        {
            for (Integer deptId : deptIds)
            {
                DeptInfo deptInfo = getDept(deptId);
                if (deptInfo != null)
                {
                    if (filter.accept(deptInfo))
                        result.add(deptInfo);
                }
            }
        }

        return result;
    }

    public List<? extends DeptInfo> searchDept(String text, Collection<Integer> deptIds) throws Exception
    {
        return searchDept(new TextDeptFilter(text), deptIds);
    }

    /**
     * 获得某些作用范围包含的部门ID列表
     *
     * @param deptId   当前部门ID
     * @param scopeIds 作用范围ID
     * @return 作用范围对应部门ID列表
     * @throws Exception 一般由数据库查询错误引起
     */
    public Collection<Integer> getDeptIdsByScopes(Integer deptId, Collection<Integer> scopeIds) throws Exception
    {
        OrganDao dao = getDao();

        Scopes scopes = new Scopes();

        for (Integer scopeId : scopeIds)
        {
            RoleScope roleScope = dao.getRoleScope(scopeId);
            if (roleScope != null)
            {
                scopes.add(roleScope.getRoleScopeDepts(), deptId, this);
            }
            else
            {
                Tools.log(Integer.toString(scopeId));
            }
        }

        return scopes.getDeptIds();
    }

    public <T> List<T> loadDatasFromParentDepts(Integer deptId, DeptOwnedDataProvider<T> dataProvider,
                                                Collection<Integer> authDeptIds, Filter<DeptInfo> deptFilter)
            throws Exception
    {
        List<T> result = new ArrayList<T>();
        List<Integer> deptIds = new ArrayList<Integer>();

        DeptInfo selfDept = getDept(deptId);
        if (selfDept == null)
        {
            throw new SystemException("deptId:" + deptId);
        }

        for (DeptInfo dept : selfDept.allParentDepts())
        {
            deptIds.add(dept.getDeptId());

            if ((authDeptIds == null ||
                    authDeptIds.contains(dept.getDeptId())) && (deptFilter == null || deptFilter.accept(dept)))
            {
                for (T t : dataProvider.get(dept.getDeptId()))
                {
                    boolean exist = false;
                    for (T t2 : result)
                    {
                        if (t.toString().equals(t2.toString()))
                        {
                            exist = true;
                            break;
                        }
                    }

                    if (!exist)
                        result.add(t);
                }
            }
        }

        if (authDeptIds != null && authDeptIds.size() < 10)
        {
            List<? extends DeptInfo> subDepts = selfDept.getSubDepts(authDeptIds);
            if (subDepts.size() < 15)
            {
                for (DeptInfo dept : subDepts)
                {
                    deptIds.add(dept.getDeptId());

                    if (!dept.getDeptId().equals(deptId) && (deptFilter == null || deptFilter.accept(dept)))
                    {
                        for (T t : dataProvider.get(dept.getDeptId()))
                        {
                            boolean exist = false;
                            for (T t2 : result)
                            {
                                if (t.toString().equals(t2.toString()))
                                {
                                    exist = true;
                                    break;
                                }
                            }

                            if (!exist)
                                result.add(t);
                        }
                    }
                }
            }

            for (Integer deptId1 : authDeptIds)
            {
                DeptInfo dept = getDept(deptId1);
                if (dept != null)
                {
                    if (!deptIds.contains(deptId1) && !dept.getDeptId().equals(deptId) &&
                            (deptFilter == null || deptFilter.accept(dept)))
                    {
                        for (T t : dataProvider.get(deptId1))
                        {
                            boolean exist = false;
                            for (T t2 : result)
                            {
                                if (t.toString().equals(t2.toString()))
                                {
                                    exist = true;
                                    break;
                                }
                            }

                            if (!exist)
                                result.add(t);
                        }
                    }
                }
            }
        }

        return result;
    }

    public DeptInfo[] getDepts(Integer[] deptIds) throws Exception
    {
        if (deptIds == null)
            return null;

        DeptInfo[] depts = new DeptInfo[deptIds.length];

        for (int i = 0; i < deptIds.length; i++)
        {
            depts[i] = getDept(deptIds[i]);
        }

        return depts;
    }

    public List<DeptInfo> getDepts(Collection<Integer> deptIds) throws Exception
    {
        if (deptIds == null)
            return null;

        List<DeptInfo> depts = new ArrayList<DeptInfo>(deptIds.size());

        for (Integer deptId : deptIds)
        {
            DeptInfo dept = getDept(deptId);
            if (dept != null)
                depts.add(dept);
        }

        return depts;
    }

    public RoleScope getRoleScopeByName(Integer deptId, String scopeName) throws Exception
    {
        DeptInfo deptInfo = getDept(deptId);
        while (deptInfo != null)
        {
            RoleScope scope = getDao().getRoleScopeByName(deptInfo.getDeptId(), scopeName);
            if (scope != null)
                return scope;

            deptInfo = deptInfo.parentDept();
        }

        return null;
    }

    public Collection<Integer> getDeptIdsByScopeName(String scopeName, Integer deptId, Integer userDeptId)
            throws Exception
    {
        RoleScope scope = getRoleScopeByName(deptId, scopeName);
        if (scope != null)
        {
            Scopes scopes = new Scopes(scope.getScopeId(), userDeptId, deptId);

            return scopes.getDeptIds();
        }

        return null;
    }
}