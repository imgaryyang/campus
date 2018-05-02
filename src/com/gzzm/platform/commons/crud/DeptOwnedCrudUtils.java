package com.gzzm.platform.commons.crud;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.organ.*;
import net.cyan.commons.util.*;
import net.cyan.crud.Crud;
import net.cyan.crud.importers.*;
import net.cyan.nest.annotation.Inject;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 部门所拥有的数据的crud相关操作
 *
 * @author camel
 * @date 2010-2-15
 */
public final class DeptOwnedCrudUtils
{
    @Inject
    private static Provider<UserOnlineInfo> userOnlineInfoProvider;

    @Inject
    private static Provider<DeptService> serviceProvider;

    @Inject
    private static Provider<HttpServletRequest> requestProvider;

    private static ColumnDataLoader deptIdLoader;

    private DeptOwnedCrudUtils()
    {
    }

    public static void beforeQuery(DeptOwnedCrud crud) throws Exception
    {
        UserOnlineInfo userOnlineInfo = userOnlineInfoProvider.get();

        if (userOnlineInfo == null)
            throw new LoginExpireException();

        crud.setQueryDeptIds(null);

        Collection<Integer> authDeptIds = crud.getAuthDeptIds();

        if (authDeptIds != null)
        {
            //对非管理员用户做数据校验
            if (authDeptIds.size() == 0)
                throw new SystemMessageException(Messages.NO_AUTH, "no auth," + crud.getClass().getName());

            if (crud.getDeptId() != null && !Null.isNull(crud.getDeptId()))
            {
                if (!authDeptIds.contains(crud.getDeptId()))
                    throw new SystemMessageException(Messages.NO_AUTH_RECORD,
                            "no auth," + crud.getClass().getName() + ",deptId:" + crud.getDeptId());
            }
            else
            {
                Collection<Integer> deptIds = crud.getDeptIds();
                if (deptIds != null)
                {
                    for (Integer deptId : deptIds)
                    {
                        if (!authDeptIds.contains(deptId))
                            throw new SystemMessageException(Messages.NO_AUTH_RECORD,
                                    "no auth," + crud.getClass().getName() + ",deptId:" + deptId);
                    }
                }
            }
        }

        if (crud.getDeptId() == null || Null.isNull(crud.getDeptId()))
        {
            if (crud.getDeptIds() == null)
            {
                Collection<Integer> topDeptIds = crud.getTopDeptIds();
                if (topDeptIds != null && topDeptIds.size() > 0)
                {
                    crud.setQueryDeptIds(getDeptIds(authDeptIds, topDeptIds));
                }
                else
                {
                    crud.setQueryDeptIds(authDeptIds);
                }
            }
            else if (authDeptIds == null)
            {
                crud.setQueryDeptIds(crud.getDeptIds());
            }
            else
            {
                List<Integer> deptIds = new ArrayList<Integer>(crud.getDeptIds());
                deptIds.retainAll(authDeptIds);
                crud.setQueryDeptIds(deptIds);
            }
        }

        if (crud instanceof DeptOwnedEntityCrud)
        {
            if (userOnlineInfo.isSelf(requestProvider.get()))
            {
                ((DeptOwnedEntityCrud) crud).setQueryUserId(userOnlineInfo.getUserId());
            }
        }
    }

    public static <E, K> boolean checkDept(DeptOwnedEntityBaseCrud<E, K> crud, E entity,
                                           Collection<Integer> authDeptIds) throws Exception
    {
        Integer deptId = crud.getDeptId(crud.getEntity());
        return authDeptIds.contains(deptId);
    }

    public static <E, K> void afterLoad(DeptOwnedEntityBaseCrud<E, K> crud) throws Exception
    {
        UserOnlineInfo userOnlineInfo = userOnlineInfoProvider.get();

        if (userOnlineInfo == null)
            throw new LoginExpireException();


        Collection<Integer> authDeptIds = crud.getAuthDeptIds();
        if (authDeptIds != null)
        {
            //判断访问的数据是不是在权限之内
            if (!crud.checkDeptId(crud.getEntity(), authDeptIds))
            {
                throw new SystemMessageException(Messages.NO_AUTH_RECORD, "no auth," +
                        crud.getEntityType().getName() + ",key:" + crud.getKey(crud.getEntity()) + ",deptId:" +
                        crud.getDeptId(crud.getEntity()));
            }

            if (userOnlineInfo.isSelf(requestProvider.get()))
            {
                Integer userId = crud.getUserId(crud.getEntity());

                if (userId != null && !userOnlineInfo.getUserId().equals(userId))
                {
                    throw new SystemMessageException(Messages.NO_AUTH_RECORD,
                            "no auth," + crud.getEntityType().getName() + ",key:" + crud.getKey(crud.getEntity()) +
                                    ",userId:" + userId
                    );
                }
            }
        }
    }

    public static <E, K> void beforeInsert(DeptOwnedEntityBaseCrud<E, K> crud) throws Exception
    {
        E entity = crud.getEntity();
        Integer deptId = crud.getDeptId(entity);

        if (crud instanceof DeptOwnedEntityCrud)
        {
            if (deptId == null)
            {
                Integer deptId1 = crud.getDeptId();
                if (deptId1 != null && !Null.isNull(deptId1))
                {
                    ((DeptOwnedEntityCrud<E, K>) crud).setDeptId(entity, deptId1);
                    deptId = deptId1;
                }
                else
                {
                    throw new SystemException("deptId cannot be null");
                }
            }
        }

        UserOnlineInfo userOnlineInfo = userOnlineInfoProvider.get();

        if (userOnlineInfo == null)
            throw new LoginExpireException();


        Collection<Integer> authDeptIds = crud.getAuthDeptIds();
        if (authDeptIds != null)
        {
            //判断修改后的数据是不是在权限之内
            if (!crud.checkDeptId(entity, authDeptIds))
            {
                throw new SystemMessageException(Messages.NO_AUTH_RECORD,
                        "no auth," + crud.getEntityType().getName() + ",deptId:" + deptId
                );
            }
        }
    }

    public static <E, K> void beforeUpdate(DeptOwnedEntityBaseCrud<E, K> crud) throws Exception
    {
        UserOnlineInfo userOnlineInfo = userOnlineInfoProvider.get();

        if (userOnlineInfo == null)
            throw new LoginExpireException();


        Collection<Integer> authDeptIds = crud.getAuthDeptIds();
        if (authDeptIds != null)
        {
            K key = crud.getKey(crud.getEntity());

            //判断原来的数据是不是在权限之内
            E oldEntity = crud.getEntity(key);
            Integer deptId = crud.getDeptId(oldEntity);
            if (!authDeptIds.contains(deptId))
            {
                throw new SystemMessageException(Messages.NO_AUTH_RECORD,
                        "no auth," + crud.getEntityType().getName() + ",key:" + key);
            }

            if (userOnlineInfo.isSelf(requestProvider.get()))
            {
                Integer userId = crud.getUserId(oldEntity);
                if (userId != null && !userOnlineInfo.getUserId().equals(userId))
                {
                    throw new SystemMessageException(Messages.NO_AUTH_RECORD,
                            "no auth," + crud.getEntityType().getName() + ",key:" + crud.getKey(crud.getEntity()) +
                                    ",userId:" + userId
                    );
                }
            }

            //判断修改后的数据是不是在权限之内
            deptId = crud.getDeptId(crud.getEntity());
            if (deptId != null && !authDeptIds.contains(deptId))
            {
                throw new SystemMessageException(Messages.NO_AUTH_RECORD,
                        "no auth," + crud.getEntityType().getName() + ",key:" + key + ",deptId:" + deptId);
            }
        }
    }

    public static <E, K> void beforeDelete(Collection<K> keys, DeptOwnedEntityBaseCrud<E, K> crud) throws Exception
    {
        checkKeys(keys, null, crud);
    }

    public static Integer getDefaultDeptId(DeptOwnedCrud crud) throws Exception
    {
        return getDefaultDeptId(crud.getAuthDeptIds(), crud);
    }

    public static Integer getDefaultDeptId(Collection<Integer> authDeptIds, Crud crud) throws Exception
    {
        return getDefaultDeptId(authDeptIds, (Object) crud);
    }

    public static Integer getDefaultDeptId(Collection<Integer> authDeptIds, Object page) throws Exception
    {
        UserOnlineInfo userOnlineInfo = userOnlineInfoProvider.get();

        if (userOnlineInfo == null)
            throw new LoginExpireException();

        Integer bureauId = userOnlineInfo.getBureauId();
        if (authDeptIds == null)
            return bureauId;

        int n = authDeptIds.size();

        if (n == 0)
        {
            if (userOnlineInfo.isAdmin())
                return bureauId;

            throw new SystemMessageException(Messages.NO_AUTH, "no auth," + page.getClass().getName());
        }

        if (n == 1)
            return authDeptIds.iterator().next();

        if (authDeptIds.contains(bureauId))
            return bureauId;

        if (authDeptIds.contains(userOnlineInfo.getDeptId()))
            return userOnlineInfo.getDeptId();

        List<SimpleDeptInfo> depts = serviceProvider.get().getAuthDeptTree(null, null, authDeptIds);
        return depts.get(0).getDeptId();
    }

    private static Collection<Integer> getDeptIds(Collection<Integer> authDeptIds, Collection<Integer> topDeptIds)
            throws Exception
    {
        Collection<Integer> result = new ArrayList<Integer>();
        Collection<Integer> loadDeptIds = new HashSet<Integer>();

        DeptService service = serviceProvider.get();

        for (Integer topDeptId : topDeptIds)
        {
            if (!loadDeptIds.contains(topDeptId))
                loadDeptIds(authDeptIds, service.getDept(topDeptId), loadDeptIds, result);
        }

        if (result.size() == 0)
            result = null;

        return result;
    }

    private static void loadDeptIds(Collection<Integer> authDeptIds, DeptInfo deptInfo,
                                    Collection<Integer> loadedDeptIds, Collection<Integer> result) throws Exception
    {
        if (deptInfo == null)
            return;

        Integer deptId = deptInfo.getDeptId();

        loadedDeptIds.add(deptId);

        if (authDeptIds == null || authDeptIds.contains(deptId))
            result.add(deptId);

        for (DeptInfo subDept : deptInfo.subDepts())
        {
            if (!loadedDeptIds.contains(subDept.getDeptId()))
                loadDeptIds(authDeptIds, subDept, loadedDeptIds, result);
        }
    }

    public static <K, E> void moveTo(Collection<K> keys, Integer newDeptId, Integer oldDeptId,
                                     DeptOwnedNormalCrud<E, K> crud)
            throws Exception
    {
        checkKeys(keys, newDeptId, crud);

        OwnedCrudUtils.moveTo(keys, newDeptId, oldDeptId, crud);
    }

    public static <K, E> void copyTo(Collection<K> keys, Integer newDeptId, Integer oldDeptId,
                                     DeptOwnedNormalCrud<E, K> crud)
            throws Exception
    {
        checkKeys(keys, newDeptId, crud);

        OwnedCrudUtils.copyTo(keys, newDeptId, oldDeptId, crud);
    }

    public static <K, E> void checkKeys(Collection<K> keys, Integer newDeptId, DeptOwnedEntityBaseCrud<E, K> crud)
            throws Exception
    {
        UserOnlineInfo userOnlineInfo = userOnlineInfoProvider.get();

        if (userOnlineInfo == null)
            throw new LoginExpireException();

        if (userOnlineInfo.isAdmin())
            return;

        Collection<Integer> authDeptIds = crud.getAuthDeptIds();
        //只允许删除拥有权限的数据
        if (authDeptIds != null)
        {
            //判断修改后的数据是不是在权限之内
            if (newDeptId != null)
            {
                if (!authDeptIds.contains(newDeptId))
                    throw new SystemMessageException(Messages.NO_AUTH_RECORD,
                            "no auth," + crud.getEntityType().getName() + ",keys:" + keys + ",deptId:" + newDeptId);
            }

            List<Integer> deptIds = crud.getDeptIds(keys);

            //判断修改前的数据是不是在权限之内
            for (Integer deptId : deptIds)
            {
                if (!authDeptIds.contains(deptId))
                    throw new SystemMessageException(Messages.NO_AUTH_RECORD,
                            "no auth," + crud.getEntityType().getName() + ",keys:" + keys + ",deptId:" + deptId);
            }

            if (userOnlineInfo.isSelf(requestProvider.get()))
            {
                List<Integer> userIds = crud.getUserIds(keys);
                if (userIds != null)
                {
                    for (Integer userId : userIds)
                    {
                        if (userId != null && !userOnlineInfo.getUserId().equals(userId))
                        {
                            throw new SystemMessageException(Messages.NO_AUTH_RECORD, "no auth," +
                                    crud.getEntityType().getName() + ",keys:" + keys + ",userId:" + userId);
                        }
                    }
                }
            }
        }
    }

    public static <E, K> String getUserIdField(DeptOwnedEntityCrud<E, K> crud)
    {
        Class<E> entityClass = crud.getEntityType();

        return getUserIdField(entityClass);
    }

    public static String getUserIdField(Class entityClass)
    {
        try
        {
            PropertyInfo property = BeanUtils.getProperty(entityClass, "userId");
            if (property.getType() == Integer.class && property.isWritable())
                return "userId";
        }
        catch (Throwable ex)
        {
            //没有此属性，继续下
        }

        try
        {
            PropertyInfo property = BeanUtils.getProperty(entityClass, "creator");
            if (property.getType() == Integer.class && property.isWritable())
                return "creator";
        }
        catch (Throwable ex)
        {
            //没有此属性，继续下
        }

        return "";
    }

    public static synchronized ColumnDataLoader getDeptIdLoader() throws Exception
    {
        if (deptIdLoader == null)
            deptIdLoader = new ToOneColumnDataLoader(Dept.class, "deptId", "deptName");
        return deptIdLoader;
    }
}
