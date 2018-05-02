package com.gzzm.platform.organ;

import com.gzzm.platform.commons.*;
import net.cyan.commons.transaction.*;
import net.cyan.commons.util.Filter;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 和组织机构相关的一些逻辑，目的在于将一个复杂逻辑放在一个session中，或者一个事务中
 *
 * @author camel
 * @date 2009-7-21
 */
public class OrganService extends DeptService
{
    /**
     * 组织机构的数据接口
     */
    @Inject
    private OrganDao dao;

    public OrganService()
    {
    }

    public OrganDao getDao() throws Exception
    {
        return dao;
    }

    public User getUser(Integer userId) throws Exception
    {
        return dao.getUser(userId);
    }

    public Dept getRoot() throws Exception
    {
        return dao.getRootDept();
    }

    public Dept getDept(Integer deptId) throws Exception
    {
        return dao.getDept(deptId);
    }

    public String getDeptName(Integer deptId) throws Exception
    {
        return dao.getDeptName(deptId);
    }

    public Collection<Dept> allDepts() throws Exception
    {
        return dao.getAllDepts();
    }

    public List<Dept> getDeptsByUserId(Integer userId) throws Exception
    {
        return dao.getDeptsByUserId(userId);
    }

    public Dept getDeptTree(Integer rootId, int level, Filter<DeptInfo> filter) throws Exception
    {
        Dept root = rootId == null ? getRoot() : getDept(rootId);

        filter(root, level, filter);

        return root;
    }

    private boolean filter(Dept dept, int level, Filter<DeptInfo> filter) throws Exception
    {
        Set<Dept> subDepts = dept.getSubDepts();
        if (subDepts == null)
            return filter.accept(dept);

        List<Dept> subDepts2 = Collections.emptyList();

        for (Dept subDept : subDepts)
        {
            if (subDept.getState() == 0)
                continue;

            if (level > 0)
            {
                int level2 = subDept.getLevel();
                //部门级别小于指定级别，不继续寻找
                if (level2 > 0 && level > level2)
                    continue;
            }

            if (filter(dept, level, filter))
            {
                //筛选出满足条件的子节点
                if (subDepts2 == Collections.EMPTY_LIST)
                    subDepts2 = new ArrayList<Dept>();
                subDepts2.add(subDept);
            }
        }

        dept.setSubDeptList(subDepts2);

        return subDepts2 != null || filter.accept(dept);
    }

    @Transactional(mode = TransactionMode.supported)
    public Collection<Integer> loadDeptIds(Map<Integer, Filter<DeptInfo>> depts, int level, Filter<DeptInfo> filter)
            throws Exception
    {
        //设置事务，让整个加载过程在同一个session进行，让一级缓存生效，避免对象的重复加载
        return super.loadDeptIds(depts, level, filter);
    }

    public List<SimpleDeptInfo> getAuthDeptTree(Integer parentId, Filter<DeptInfo> filter,
                                                Collection<Integer> authDeptIds)
            throws Exception
    {
        List<SimpleDeptInfo> result = new ArrayList<SimpleDeptInfo>();

        if (parentId == null)
        {
            loadAuthDeptTree(getRoot(), filter, authDeptIds, result);
        }
        else
        {
            for (Dept dept : getDept(parentId).getSubDepts())
                loadAuthDeptTree(dept, filter, authDeptIds, result);
        }

        return result;
    }

    public boolean containsSubAuthDept(Integer deptId, Filter<DeptInfo> filter, Collection<Integer> authDeptIds)
            throws Exception
    {
        return containsSubAuthDept(getDept(deptId), filter, authDeptIds);
    }

    private void loadAuthDeptTree(Dept dept, Filter<DeptInfo> filter, Collection<Integer> authDeptIds,
                                  List<SimpleDeptInfo> result) throws Exception
    {
        if (authDeptIds == null || authDeptIds.contains(dept.getDeptId()))
        {
            if (filter == null || filter.accept(dept) || containsSubAuthDept(dept, filter, authDeptIds))
                result.add(dept);
        }
        else
        {
            for (Dept subDept : dept.getSubDepts())
                loadAuthDeptTree(subDept, filter, authDeptIds, result);
        }
    }

    private boolean containsSubAuthDept(Dept dept, Filter<DeptInfo> filter, Collection<Integer> authDeptIds)
            throws Exception
    {
        for (Dept subDept : dept.getSubDepts())
        {
            if (((authDeptIds == null || authDeptIds.contains(subDept.getDeptId())) &&
                    (filter == null || filter.accept(subDept)) || containsSubAuthDept(subDept, filter, authDeptIds)))
                return true;
        }

        return false;
    }

    @Override
    public List<Dept> searchDept(String text, Collection<Integer> deptIds) throws Exception
    {
        List<Dept> depts = dao.queryDeptsByName("%" + text + "%");

        if (deptIds != null)
        {
            for (Iterator<Dept> iterator = depts.iterator(); iterator.hasNext(); )
            {
                if (!deptIds.contains(iterator.next().getDeptId()))
                    iterator.remove();
            }
        }

        return depts;
    }

    public Integer addUser(User user, String module) throws Exception
    {
        return addUser(user, null, module);
    }

    public Integer addUser(User user, Integer deptId, String module) throws Exception
    {
        user.setType(UserType.out);

        if (dao.getUserByLoginName(user.getLoginName(), user.getUserId(), user.getType()) != null)
        {
            throw new NoErrorException("user.loginname_exists1");
        }

        OutterUserConfig config = Tools.getBean(OutterUserConfig.class, module);

        if (deptId == null)
        {
            if (config != null)
                deptId = config.getDeptId();
        }

        dao.add(user);

        if (user.getPassword().length() < 32)
        {
            user.setPassword(PasswordUtils.hashPassword(user.getPassword(), user.getUserId()));
            dao.update(user);
        }

        Integer userId = user.getUserId();

        if (deptId != null)
        {
            UserDept userDept = new UserDept(userId, deptId);
            dao.add(userDept);

            if (config != null && config.getRoleIds() != null)
            {
                for (Integer roleId : config.getRoleIds())
                {
                    UserRole userRole = new UserRole();
                    userRole.setUserId(userId);
                    userRole.setDeptId(deptId);
                    userRole.setRoleId(roleId);

                    dao.add(userRole);
                }
            }
        }

        return userId;
    }
}
