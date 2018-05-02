package com.gzzm.platform.organ;

import com.gzzm.platform.annotation.CacheInstance;
import com.gzzm.platform.commons.Tools;
import net.cyan.commons.collections.tree.TreeLoader;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 部门缓存树
 *
 * @author camel
 * @date 2009-7-29
 */
@CacheInstance("Dept")
public class DeptCacheTree extends DeptService
{
    @Inject
    private static Provider<OrganDao> daoProvider;

    /**
     * 根部门
     */
    private DeptCache root;

    /**
     * 缓存所有部门的id和部门cache的对应关系
     */
    private Map<Integer, DeptCache> map = new HashMap<Integer, DeptCache>();

    private DeptCacheTree() throws Exception
    {
        load();
    }

    private void load() throws Exception
    {
        List<Dept> depts = daoProvider.get().getAllDepts();

        new TreeLoader<Dept, DeptCache, Integer>()
        {
            protected Integer getKey(Dept dept)
            {
                return dept.getDeptId();
            }

            protected Integer getParentKey(Dept dept)
            {
                return dept.getParentDeptId();
            }

            protected DeptCache create(Integer deptId)
            {
                return new DeptCache(DeptCacheTree.this, deptId);
            }

            protected void copy(Dept dept, DeptCache cache)
            {
                cache.setDeptName(dept.getDeptName());
                cache.setParentDeptId(dept.getParentDeptId());
                cache.setLevel(dept.getDeptLevel());
                cache.setDeptCode(dept.getDeptCode());
                cache.setOrgCode(dept.getOrgCode());
            }

            protected void addChild(DeptCache parent, DeptCache child)
            {
                parent.addSubDept(child);
            }

            protected void addRoot(DeptCache root)
            {
                DeptCacheTree.this.root = root;
            }
        }.load(depts, map);
    }

    public OrganDao getDao() throws Exception
    {
        return daoProvider.get();
    }

    public DeptCache getRoot()
    {
        return root;
    }

    public DeptCache getDept(Integer deptId)
    {
        return map.get(deptId);
    }

    public String getDeptName(Integer deptId)
    {
        DeptCache dept = map.get(deptId);
        return dept == null ? null : dept.getDeptName();
    }

    public Collection<DeptCache> allDepts() throws Exception
    {
        return map.values();
    }

    public List<DeptCache> getDeptsByUserId(Integer userId) throws Exception
    {
        List<Integer> deptIds = null;
        try
        {
            deptIds = daoProvider.get().getDeptIdsByUserId(userId);
        }
        catch (Exception ex)
        {
            Tools.wrapException(ex);
        }

        List<DeptCache> deptInfos = new ArrayList<DeptCache>(deptIds.size());

        for (Integer deptId : deptIds)
        {
            DeptCache deptInfo = getDept(deptId);
            if (deptInfo != null)
                deptInfos.add(deptInfo);
        }

        return deptInfos;
    }

    public DeptCache getDeptTree(Integer rootId, int level, Filter<DeptInfo> filter) throws Exception
    {
        DeptCache root = rootId == null ? getRoot() : getDept(rootId);
        if (filter == null && level <= 0)
            return root;

        DeptCache dept = filter(root, level, filter);

        return dept == null ? root.copy(null) : dept;
    }

    private DeptCache filter(DeptCache dept, int level, Filter<DeptInfo> filter) throws Exception
    {
        List<DeptCache> subDepts = dept.subDepts();
        if (subDepts == null)
            return filter.accept(dept) ? dept : null;

        List<DeptCache> subDepts2 = null;
        int lastIndex = 0;

        int n = subDepts.size();
        for (int i = 0; i < n; i++)
        {
            DeptCache subDept = subDepts.get(i);
            DeptCache subDept2 = subDept;
            if (level > 0)
            {
                int level2 = subDept.getLevel();
                //部门级别小于指定级别，不继续寻找
                if (level2 > 0 && level > level2)
                    subDept2 = null;
            }

            if (subDept2 != null)
                subDept2 = filter(subDept, level, filter);

            if (subDept == subDept2 && lastIndex == i)
            {
                //标识在此序号之前的节点和原来的节点完全相同
                lastIndex++;
            }
            else if (subDept2 != null)
            {
                //有一个子节点和原来的树不一样，必须copy一份节点
                if (subDepts2 == null)
                    subDepts2 = new ArrayList<DeptCache>(subDepts.subList(0, lastIndex));

                subDepts2.add(subDept2);
            }
        }

        //所有子节点都相同，返回本身
        if (lastIndex == n)
            return dept;

        //至少一个子节点满足条件或者节点本身满足条件，复制一个节点返回
        return subDepts2 != null || filter.accept(dept) ? dept.copy(subDepts2) : null;
    }

    public List<SimpleDeptInfo> getAuthDeptTree(Integer parentId, Filter<DeptInfo> filter,
                                                Collection<Integer> authDeptIds) throws Exception
    {
        List<SimpleDeptInfo> result = new ArrayList<SimpleDeptInfo>();

        if (parentId == null)
        {
            loadAuthDeptTree(getRoot(), filter, authDeptIds, result);
        }
        else
        {
            for (DeptCache dept : getDept(parentId).subDepts())
                loadAuthDeptTree(dept, filter, authDeptIds, result);
        }

        return result;
    }

    public boolean containsSubAuthDept(Integer deptId, Filter<DeptInfo> filter, Collection<Integer> authDeptIds)
            throws Exception
    {
        return containsSubAuthDept(getDept(deptId), filter, authDeptIds);
    }

    private static void loadAuthDeptTree(DeptCache dept, Filter<DeptInfo> filter, Collection<Integer> authDeptIds,
                                         List<SimpleDeptInfo> result) throws Exception
    {
        if (authDeptIds == null || authDeptIds.contains(dept.getDeptId()))
        {
            if (filter == null || filter.accept(dept) || containsSubAuthDept(dept, filter, authDeptIds))
                result.add(dept);
        }
        else
        {
            for (DeptCache subDept : dept.subDepts())
                loadAuthDeptTree(subDept, filter, authDeptIds, result);
        }
    }

    private static boolean containsSubAuthDept(DeptCache dept, Filter<DeptInfo> filter, Collection<Integer> authDeptIds)
            throws Exception
    {
        for (DeptCache subDept : dept.subDepts())
        {
            if (((authDeptIds == null || authDeptIds.contains(subDept.getDeptId())) &&
                    (filter == null || filter.accept(subDept)) || containsSubAuthDept(subDept, filter, authDeptIds)))
                return true;
        }

        return false;
    }
}
