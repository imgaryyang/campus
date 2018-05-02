package com.gzzm.platform.appauth;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.organ.*;
import net.cyan.commons.transaction.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 和应用权限相关的服务
 *
 * @author camel
 * @date 2011-5-13
 */
public class AppAuthService
{
    @Inject
    private AppAuthDao dao;

    @Inject
    private DeptService deptService;

    @Inject
    private AppAuthConfig config;

    public AppAuthService()
    {
    }

    /**
     * 获得某个用户拥有的某种类型的应用
     *
     * @param userId  用户ID
     * @param deptId  用户当前登录的部门
     * @param deptIds 用户对当前模块拥有权限的部门，参考{@link com.gzzm.platform.annotation.AuthDeptIds}
     * @param type    应用类型，由具体的应用模块定义
     * @return 应用ID列表
     * @throws Exception 数据库查询错误
     */
    public Collection<Integer> getAppIds(Integer userId, Integer deptId, Collection<Integer> deptIds, String type)
            throws Exception
    {
        //根据用户id查询有用权限的应用
        List<Integer> appIds = null;

        if (userId != null)
            appIds = dao.getAppIds(type, AuthType.user, Collections.singleton(userId));

        //根据部门ID查询有用权限的应用
        List<Integer> appIdsForDeptId = dao.getAppIds(type, AuthType.dept, deptIds);

        if (appIds == null)
        {
            appIds = appIdsForDeptId;
        }
        else
        {
            for (Integer appId : appIdsForDeptId)
            {
                if (!appIds.contains(appId))
                    appIds.add(appId);
            }
        }

        if (userId != null)
        {
            //根据岗位ID查询有用权限的应用
            for (Integer appId : dao.getAppIds(type, AuthType.station,
                    deptService.getDao().getStationIds(userId, deptId)))
            {
                if (!appIds.contains(appId))
                    appIds.add(appId);
            }
        }

        return appIds;
    }

    @Transactional(mode = TransactionMode.not_supported)
    public Collection<Integer> getDeptIdsForAuth(String type, Integer appId, Integer userId, Integer deptId,
                                                 Collection<Integer> deptIds) throws Exception
    {
        if (config != null && config.isCacheAuth())
        {
            String tableName = getTableName(userId, deptId, deptIds, type);

            List<Integer> deptIds1 =
                    dao.sqlQuery("select distinct deptId from " + tableName + " where APPID=:1", Integer.class, appId);

            if (deptIds1.contains(null) || deptIds1.contains(0))
                return null;

            return deptIds1;
        }
        else
        {
            return getDeptIdsForAuth0(type, appId, userId, deptId, deptIds);
        }
    }

    private Collection<Integer> getDeptIdsForAuth0(String type, Integer appId, Integer userId, Integer deptId,
                                                   Collection<Integer> deptIds) throws Exception
    {
        AppAuth auth = dao.getAuth(type, appId);
        if (auth == null)
            return Collections.emptyList();

        return getDeptIdsForAuth(auth.getAuthId(), userId, deptId, deptIds);
    }

    /**
     * 某用户对某个具体的应用所拥有权限的部门id集合
     *
     * @param authId  权限id
     * @param userId  用户ID
     * @param deptId  用户当前登录的部门
     * @param deptIds 用户对当前应用模块拥有权限的部门,参考{@link com.gzzm.platform.annotation.AuthDeptIds}
     * @return 用户对此应用拥有权限的部门id集合，如果返回null，表示都所有部门拥有权限
     * @throws Exception 数据库查询错误
     */
    @Transactional(mode = TransactionMode.supported)
    public Collection<Integer> getDeptIdsForAuth(Integer authId, Integer userId, Integer deptId,
                                                 Collection<Integer> deptIds) throws Exception
    {
        //根据用户id查询有用权限的应用
        AppAuthItem userAuthItem = dao.getAuthItem(authId, AuthType.user, userId);

        //包含空的范围id，表示没有范围限制，对所有部门拥有权限
        if (userAuthItem != null && (userAuthItem.getScopeId() == null || userAuthItem.getScopeId() == -1))
            return null;

        //根据岗位ID查询有用权限的应用
        List<Integer> scopeIds =
                dao.getScopeIds(authId, AuthType.station, deptService.getDao().getStationIds(userId, deptId));

        //包含空的范围id，表示没有范围限制，对所有部门拥有权限
        if (scopeIds.contains(null) || scopeIds.contains(-1))
            return null;

        if (userAuthItem != null && !scopeIds.contains(userAuthItem.getScopeId()))
        {
            scopeIds.add(userAuthItem.getScopeId());
        }

        Set<Integer> result = null;
        if (scopeIds.size() > 0)
        {
            Collection<Integer> deptIds1 = deptService.getDeptIdsByScopes(deptId, scopeIds);
            if (deptIds1 == null)
                return null;

            result = new HashSet<Integer>(deptIds1);
        }

        for (Integer deptId1 : deptIds)
        {
            AppAuthItem deptAuthItem = dao.getAuthItem(authId, AuthType.dept, deptId1);
            if (deptAuthItem != null)
            {
                //包含空的范围id，表示没有范围限制，对所有部门拥有权限
                if (deptAuthItem.getScopeId() == null || deptAuthItem.getScopeId() == -1)
                    return null;

                Scopes scopes = new Scopes(deptAuthItem.getScopeId(), deptId1, deptService);

                Collection<Integer> deptIds1 = scopes.getDeptIds();
                if (deptId1 == null)
                    return null;

                if (result == null)
                    result = new HashSet<Integer>();

                result.addAll(deptIds1);
            }
        }

        return result;
    }

    public List<AuthItemShow> getAuthItemShows(App[] apps) throws Exception
    {
        List<AuthItemShow> result = new ArrayList<AuthItemShow>();

        int n = apps.length;

        for (int i = 0; i < n; i++)
        {
            App app = apps[i];

            for (AppAuthItem item : dao.getAuthItems(app.getType(), app.getId()))
            {
                AuthItemShow itemShow = null;
                for (AuthItemShow itemShow1 : result)
                {
                    if (itemShow1.getType() == item.getType() && itemShow1.getObjectId().equals(item.getObjectId()))
                    {
                        itemShow = itemShow1;
                        break;
                    }
                }

                if (itemShow == null)
                {
                    String name = null;
                    String deptName = null;
                    switch (item.getType())
                    {
                        case dept:
                            DeptInfo deptInfo = deptService.getDept(item.getObjectId());
                            if (deptInfo != null)
                            {
                                name = deptInfo.getDeptName();
                                deptName = deptInfo.allName();
                            }
                            break;
                        case user:
                            User user = deptService.getDao().getUser(item.getObjectId());
                            if (user != null)
                            {
                                name = user.getUserName();
                                deptName = user.allDeptName();
                            }
                            break;
                        case station:
                            Station station = deptService.getDao().getStation(item.getObjectId());
                            if (station != null)
                            {
                                name = station.getStationName();
                                deptName = deptService.getDept(station.getDeptId()).allName();
                            }
                            break;
                    }

                    if (name != null)
                    {
                        itemShow = new AuthItemShow();

                        itemShow.setType(item.getType());
                        itemShow.setObjectId(item.getObjectId());
                        itemShow.setValids(new Boolean[n]);

                        for (int k = 0; k < n; k++)
                            itemShow.getValids()[k] = false;


                        itemShow.setName(name);
                        itemShow.setDeptName(deptName);

                        result.add(itemShow);
                    }
                }

                if (itemShow != null)
                    itemShow.getValids()[i] = true;
            }
        }

        return result;
    }

    public void addAuthItems(App[] apps, AuthType authType, Integer[] objectIds, Integer scopeId) throws Exception
    {
        for (App app : apps)
            addAuthItems(app, authType, objectIds, scopeId);
    }

    public void addAuthItems(App app, AuthType authType, Integer[] objectIds, Integer scopeId) throws Exception
    {
        Integer authId = dao.getAuthId(app.getType(), app.getId());

        if (authId == null)
        {
            //AppAuth对象还未创建，先创建
            AppAuth appAuth = new AppAuth();
            appAuth.setType(app.getType());
            appAuth.setAppId(app.getId());

            dao.add(appAuth);
            authId = appAuth.getAuthId();
        }

        for (Integer objectId : objectIds)
        {
            AppAuthItem item = new AppAuthItem();

            item.setAuthId(authId);
            item.setType(authType);
            item.setObjectId(objectId);
            item.setScopeId(scopeId == null ? Null.Integer : scopeId);

            dao.save(item);
        }

        clearTables(app.getType());
    }

    public void setAuth(String appType, Integer appId, AuthType authType, Integer objectId, boolean valid)
            throws Exception
    {
        Integer authId = dao.getAuthId(appType, appId);

        if (authId == null && valid)
        {
            //AppAuth对象还未创建，先创建
            AppAuth appAuth = new AppAuth();
            appAuth.setType(appType);
            appAuth.setAppId(appId);

            dao.add(appAuth);
            authId = appAuth.getAuthId();
        }

        if (valid)
        {
            AppAuthItem item = new AppAuthItem();

            item.setAuthId(authId);
            item.setType(authType);
            item.setObjectId(objectId);

            dao.save(item);
        }
        else if (authId != null)
        {
            dao.deleteItem(authId, authType, objectId);
        }

        clearTables(appType);
    }

    public Integer getScopeId(String appType, Integer appId, AuthType authType, Integer objectId) throws Exception
    {
        Integer authId = dao.getAuthId(appType, appId);

        return dao.getAuthItem(authId, authType, objectId).getScopeId();
    }

    @Transactional
    public void setScope(String appType, Integer appId, AuthType authType, Integer objectId, Integer scopeId)
            throws Exception
    {
        Integer authId = dao.getAuthId(appType, appId);

        AppAuthItem item = dao.getAuthItem(authId, authType, objectId);
        item.setScopeId(scopeId == null ? Null.Integer : scopeId);
        dao.update(item);

        clearTables(appType);
    }

    /**
     * 复制权限
     *
     * @param sourceAppType 原应用类型
     * @param sourceAppId   原应用ID，和原应用类型合起来确定一个权限ID
     * @param targetAppType 目标应用类型
     * @param targetAppId   模板应用ID，和目标应用类型合起来确定一个应用ID
     * @throws Exception 数据库查询或者插入数据错误
     */
    @Transactional
    public void copyAuths(String sourceAppType, Integer sourceAppId, String targetAppType, Integer targetAppId)
            throws Exception
    {
        Integer sourceAuthId = dao.getAuthId(sourceAppType, sourceAppId);

        //原权限不存在，不需要复制
        if (sourceAppId == null)
            return;

        Integer targetAuthId = dao.getAuthId(targetAppType, targetAppId);

        if (targetAuthId == null)
        {
            //目标权限不存在，创建权限
            AppAuth appAuth = new AppAuth();
            appAuth.setType(targetAppType);
            appAuth.setAppId(targetAppId);

            dao.add(appAuth);
            targetAuthId = appAuth.getAuthId();
        }

        copyAuths(sourceAuthId, targetAuthId);
    }

    /**
     * 复制权限
     *
     * @param sourceAuthId 原权限ID
     * @param targetAuthId 目标权限ID
     * @throws Exception 数据库查询或者插入数据错误
     */
    private void copyAuths(Integer sourceAuthId, Integer targetAuthId) throws Exception
    {
        for (AppAuthItem sourceItem : dao.getAuthItems(sourceAuthId))
        {
            AppAuthItem targetItem = new AppAuthItem();
            targetItem.setAuthId(targetAuthId);
            targetItem.setType(sourceItem.getType());
            targetItem.setObjectId(sourceItem.getObjectId());
            if (sourceItem.getScopeId() == null)
                targetItem.setScopeId(Null.Integer);
            else
                targetItem.setScopeId(sourceItem.getScopeId());

            dao.save(targetItem);
        }
    }

    public void copyAuths(String sourceAppType, Integer sourceAppId, AuthType authType, Integer objectId,
                          String targetAppType, Integer targetAppId) throws Exception
    {
        Integer sourceAuthId = dao.getAuthId(sourceAppType, sourceAppId);

        //原权限不存在，不需要复制
        if (sourceAppId == null)
            return;

        Integer targetAuthId = dao.getAuthId(targetAppType, targetAppId);

        if (targetAuthId == null)
        {
            //目标权限不存在，创建权限
            AppAuth appAuth = new AppAuth();
            appAuth.setType(targetAppType);
            appAuth.setAppId(targetAppId);

            dao.add(appAuth);
            targetAuthId = appAuth.getAuthId();
        }

        AppAuthItem sourceItem = dao.getAuthItem(sourceAuthId, authType, objectId);
        if (sourceItem == null)
            return;

        AppAuthItem targetItem = new AppAuthItem();
        targetItem.setAuthId(targetAuthId);
        targetItem.setType(sourceItem.getType());
        targetItem.setObjectId(sourceItem.getObjectId());

        if (sourceItem.getScopeId() == null)
            targetItem.setScopeId(Null.Integer);
        else
            targetItem.setScopeId(sourceItem.getScopeId());

        dao.save(targetItem);
    }

    @Transactional(mode = TransactionMode.not_supported)
    public AppDeptList getAppDepts(Integer userId, Integer deptId, Collection<Integer> deptIds, String type,
                                   Collection<Integer> appIds) throws Exception
    {
        if (deptIds.size() == 0)
            throw new SystemException("create appdepts fail.userId=" + userId + ",deptId=" + deptId);

        if (config != null && config.isCacheAuth())
        {
            return new AppDeptList(getTableName(userId, deptId, deptIds, type));
        }
        else
        {
            return new AppDeptList(getAppDepts0(userId, deptId, deptIds, type, appIds));
        }
    }

    private String getTableName(Integer userId, Integer deptId, Collection<Integer> deptIds, String type)
            throws Exception
    {
        String tableName = AppAuthDao.getTablePrefix(type) + "_" + userId + "_" + deptId;
        String schema = config.getSchema();

        if (!dao.tableExists(schema, tableName))
        {
            dao.createTable(schema, tableName, getAppDepts0(userId, deptId, deptIds, type, null));
        }

        if (schema != null)
            tableName = schema + "." + tableName;

        return tableName;
    }

    private void putItemsMap(Map<Integer, Map<Integer, Collection<Integer>>> appScopeIdMap, List<AppAuthItem> items,
                             Integer deptId0)
    {
        for (AppAuthItem item : items)
        {
            Integer appId = item.getAuth().getAppId();
            Map<Integer, Collection<Integer>> deptScopeIdMap = appScopeIdMap.get(appId);

            if (deptScopeIdMap == null)
            {
                deptScopeIdMap = new HashMap<Integer, Collection<Integer>>();
                appScopeIdMap.put(appId, deptScopeIdMap);
            }

            Integer deptId;
            if (item.getType() == AuthType.dept)
                deptId = item.getObjectId();
            else
                deptId = deptId0;

            Collection<Integer> scopeIds = deptScopeIdMap.get(deptId);

            Integer scopeId = item.getScopeId();
            if (scopeIds == null)
            {
                deptScopeIdMap.put(deptId, scopeIds = new ArrayList<Integer>());
                scopeIds.add(scopeId);
            }
            else if (!scopeIds.contains(scopeId))
            {
                scopeIds.add(scopeId);
            }
        }
    }

    @Transactional(mode = TransactionMode.supported)
    protected List<AppDept> getAppDepts0(Integer userId, Integer deptId, Collection<Integer> deptIds, String type,
                                         Collection<Integer> appIds) throws Exception
    {
        //key为appId，vlue为数据访问列表
        List<AppDept> appDepts = new ArrayList<AppDept>();

        Map<Integer, Map<Integer, Collection<Integer>>> appScopeIdMap =
                new HashMap<Integer, Map<Integer, Collection<Integer>>>();

        if (userId != null)
        {
            putItemsMap(appScopeIdMap, dao.getAuthItems(type, AuthType.user, Collections.singleton(userId), appIds),
                    deptId);
        }

        putItemsMap(appScopeIdMap, dao.getAuthItems(type, AuthType.dept, deptIds, appIds), deptId);

        if (userId != null)
        {
            //根据岗位ID查询有用权限的应用
            putItemsMap(appScopeIdMap, dao.getAuthItems(type, AuthType.station,
                    deptService.getDao().getStationIds(userId, deptId), appIds), deptId);
        }

        for (Map.Entry<Integer, Map<Integer, Collection<Integer>>> entry : appScopeIdMap.entrySet())
        {
            Integer appId = entry.getKey();
            Map<Integer, Collection<Integer>> deptScopeIdMap = entry.getValue();

            for (Map.Entry<Integer, Collection<Integer>> entry1 : deptScopeIdMap.entrySet())
            {
                Integer deptId1 = entry1.getKey();
                Collection<Integer> scopeIds = entry1.getValue();

                if (scopeIds.contains(null) || scopeIds.contains(-1))
                {
                    addAppDept(appDepts, new AppDept(appId, null));
                }
                else
                {
                    if (scopeIds.size() > 0)
                    {
                        Collection<Integer> deptIds1 = deptService.getDeptIdsByScopes(deptId1, scopeIds);
                        if (deptIds1 == null)
                        {
                            addAppDept(appDepts, new AppDept(appId, null));
                        }
                        else
                        {
                            for (Integer deptId2 : deptIds1)
                            {
                                addAppDept(appDepts, new AppDept(appId, deptId2));
                            }
                        }
                    }
                }
            }
        }

        return appDepts;
    }

    private void addAppDept(List<AppDept> appDepts, AppDept appDept)
    {
        for (Iterator<AppDept> iterator = appDepts.iterator(); iterator.hasNext(); )
        {
            AppDept appDept1 = iterator.next();
            if (appDept1.getAppId().equals(appDept.getAppId()))
            {
                if (appDept1.getDeptId() == null || appDept1.getDeptId().equals(appDept.getDeptId()))
                    return;
                else if (appDept.getDeptId() == null)
                    iterator.remove();
            }
        }
        appDepts.add(appDept);
    }

    @Transactional(mode = TransactionMode.supported)
    public Collection<Integer> getDeptIdsForAuths(String type, Collection<Integer> appIds) throws Exception
    {
        List<ObjectScope> objectScopes = dao.getObjectScopes(type, appIds, AuthType.dept);

        List<Integer> deptIds = new ArrayList<Integer>();

        for (ObjectScope objectScope : objectScopes)
        {
            if (objectScope.getType() == AuthType.dept)
            {
                Integer deptId = objectScope.getObjectId();
                if (deptService.getDept(deptId) != null)
                {
                    Integer scopeId = objectScope.getScopeId();

                    if (scopeId != null && scopeId > 0)
                    {
                        CollectionUtils.union(deptIds,
                                deptService.getDeptIdsByScopes(deptId, Collections.singleton(scopeId)));
                    }
                }
            }
        }

        return deptIds;
    }

    public void clearTables(String type) throws Exception
    {
        if (config != null && config.isCacheAuth())
        {
            dao.clearTables(type, config.getSchema());
        }
    }
}
