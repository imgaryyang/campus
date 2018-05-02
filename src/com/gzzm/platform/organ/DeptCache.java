package com.gzzm.platform.organ;

import com.gzzm.platform.commons.Tools;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 部门树cache
 *
 * @author camel
 * @date 2009-7-29
 */
public class DeptCache implements DeptInfo
{
    private static final long serialVersionUID = -4066330017828161692L;

    @Inject
    private static Provider<OrganDao> daoProvider;

    /**
     * 部门id
     */
    private Integer deptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 部门级别
     */
    private int level;

    /**
     * 上级部门
     */
    private Integer parentDeptId;

    /**
     * 部门编码
     */
    private String deptCode;

    /**
     * 组织机构代码
     */
    private String orgCode;

    /**
     * 子部门
     */
    private List<DeptCache> subDepts;

    /**
     * 让部门不允许修改
     */
    private List<DeptCache> unmodifiableSubDepts;

    /**
     * 所在的树
     */
    private DeptCacheTree tree;

    /**
     * 部门属性
     */
    private Map<String, String> attributes;

    DeptCache(DeptCacheTree tree, Integer deptId)
    {
        this.tree = tree;
        this.deptId = deptId;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public String getDeptName()
    {
        return deptName;
    }

    void setDeptName(String deptName)
    {
        this.deptName = deptName;
    }

    public int getLevel()
    {
        return level;
    }

    void setLevel(int level)
    {
        this.level = level;
    }

    public Integer getParentDeptId()
    {
        return parentDeptId;
    }

    void setParentDeptId(Integer parentDeptId)
    {
        this.parentDeptId = parentDeptId;
    }

    public String getDeptCode()
    {
        return deptCode;
    }

    void setDeptCode(String deptCode)
    {
        this.deptCode = deptCode;
    }

    @Override
    public String getOrgCode()
    {
        return orgCode;
    }

    void setOrgCode(String orgCode)
    {
        this.orgCode = orgCode;
    }

    @SuppressWarnings("unchecked")
    public List<DeptCache> subDepts()
    {
        return unmodifiableSubDepts == null ? (subDepts == null ? Collections.EMPTY_LIST : subDepts) :
                unmodifiableSubDepts;
    }

    @NotSerialized
    public DeptCache parentDept()
    {
        return tree.getDept(parentDeptId);
    }

    @NotSerialized
    public DeptCacheTree getTree()
    {
        return tree;
    }

    void addSubDept(DeptCache cache)
    {
        if (subDepts == null)
        {
            subDepts = new ArrayList<DeptCache>();
            unmodifiableSubDepts = Collections.unmodifiableList(subDepts);
        }

        subDepts.add(cache);
    }

    @NotSerialized
    public synchronized Map<String, String> getAttributes()
    {
        if (attributes == null)
        {
            try
            {
                attributes = Collections.unmodifiableMap(
                        new HashMap<String, String>(daoProvider.get().getDept(deptId).getAttributes()));
            }
            catch (Exception ex)
            {
                Tools.wrapException(ex);
            }
        }

        return attributes;
    }

    /**
     * 获得所有上级部门
     *
     * @return 所有上级部门的列表
     */
    @NotSerialized
    public List<DeptCache> allParentDepts()
    {
        List<DeptCache> depts = new ArrayList<DeptCache>();
        DeptCache dept = this;
        depts.add(dept);
        while ((dept = dept.parentDept()) != null)
            depts.add(dept);
        return depts;
    }

    /**
     * 获得所有上级部门的id
     *
     * @return 所有上级部门的id列表
     */
    @NotSerialized
    public List<Integer> allParentDeptIds()
    {
        List<Integer> deptIds = new ArrayList<Integer>();
        DeptCache dept = this;
        deptIds.add(dept.getDeptId());
        while ((dept = dept.parentDept()) != null)
            deptIds.add(dept.getDeptId());
        return deptIds;
    }

    /**
     * 获得所有下属部门
     *
     * @return 所有下属部门的列表
     */
    @NotSerialized
    public List<DeptCache> allSubDepts()
    {
        return getSubDepts(null);
    }

    public List<DeptCache> getSubDepts(Collection<Integer> authDeptIds)
    {
        List<DeptCache> depts = new ArrayList<DeptCache>();
        getAllSubDepts(depts, authDeptIds);
        return depts;
    }

    private void getAllSubDepts(List<DeptCache> depts, Collection<Integer> authDeptIds)
    {
        if (authDeptIds == null || authDeptIds.contains(getDeptId()))
            depts.add(this);
        for (DeptCache dept : subDepts())
            dept.getAllSubDepts(depts, authDeptIds);
    }

    /**
     * 获得所有下属部门
     *
     * @return 所有下属部门的id列表
     */
    @NotSerialized
    public List<Integer> allSubDeptIds()
    {
        return getSubDeptIds(null);
    }

    public void getAllSubDeptIds(List<Integer> deptIds)
    {
        getSubDeptIds(deptIds, null);
    }

    public List<Integer> getSubDeptIds(Collection<Integer> authDeptIds)
    {
        List<Integer> deptIds = new ArrayList<Integer>();
        getSubDeptIds(authDeptIds, deptIds);
        return deptIds;
    }

    public void getSubDeptIds(Collection<Integer> authDeptIds, List<Integer> deptIds)
    {
        if (authDeptIds == null || authDeptIds.contains(getDeptId()))
            deptIds.add(getDeptId());
        for (DeptCache dept : subDepts())
            dept.getSubDeptIds(authDeptIds, deptIds);
    }

    public boolean isSubDeptIdsIn(Collection<Integer> deptIds)
    {
        if (deptIds.contains(deptId))
            return true;

        for (DeptCache dept : subDepts())
        {
            if (dept.isSubDeptIdsIn(deptIds))
                return true;
        }

        return false;
    }

    public boolean isParentDeptIdsIn(Collection<Integer> deptIds)
    {
        DeptCache dept = this;

        do
        {
            if (deptIds.contains(dept.getDeptId()))
                return true;
        } while ((dept = dept.parentDept()) != null);

        return false;
    }

    public boolean containsLevel(int level)
    {
        if (this.level == level)
            return true;

        for (DeptCache dept : subDepts())
        {
            if (dept.containsLevel(level))
                return true;
        }

        return false;
    }

    /**
     * 获得某一级别的上级部门
     *
     * @param level 上级部门的级别
     * @return 返回某一级别的上级部门
     */
    public DeptCache getParentDept(int level)
    {
        DeptCache dept = this;
        while (true)
        {
            if (dept.level >= level)
                return dept;

            DeptCache parentDept = dept.parentDept();
            if (parentDept == null)
                return dept;

            dept = parentDept;
        }
    }

    /**
     * 获得部门从某一级别的部门以下的名称链
     *
     * @param level 部门级别
     * @return 部门名称链
     */
    public List<String> getAllNameList(int level)
    {
        DeptCache dept = this;
        List<String> deptNames = new ArrayList<String>();

        int lastLevel = -1;

        while (true)
        {
            //不加入根节点的名称
            DeptCache parentDept = dept.parentDept();
            if (parentDept == null && dept != this)
                break;

            //忽略虚拟部门
            if (dept == this || dept.level > lastLevel)
            {
                deptNames.add(0, dept.getDeptName());
                lastLevel = dept.level;
            }

            if (dept.level >= level || parentDept == null)
                break;

            dept = parentDept;
        }

        return deptNames;
    }

    public String getAllName(int level)
    {
        return Dept.getAllName(getAllNameList(level));
    }

    /**
     * 获得部门从根节点以下的名称链
     *
     * @return 部门名称链
     */
    @NotSerialized
    public List<String> allNameList()
    {
        DeptCache dept = this;
        List<String> deptNames = new ArrayList<String>();

        int lastLevel = -1;

        while (true)
        {
            //不加入根节点的名称
            DeptCache parentDept = dept.parentDept();
            if (parentDept == null && dept != this)
                break;

            //忽略虚拟部门
            if (dept == this || dept.level > lastLevel)
            {
                deptNames.add(0, dept.getDeptName());
                lastLevel = dept.level;
            }

            if (parentDept == null)
                break;

            dept = parentDept;
        }

        return deptNames;
    }

    @NotSerialized
    public String allName()
    {
        return Dept.getAllName(allNameList());
    }

    /**
     * 复制一份数据，但是不复制子部门
     *
     * @param subDepts 子部门
     * @return 复制后的节点
     */
    DeptCache copy(List<DeptCache> subDepts)
    {
        if (subDepts == this.subDepts)
            return this;

        DeptCache cache = new DeptCache(tree, deptId);
        cache.deptName = deptName;
        cache.level = level;
        cache.deptCode = deptCode;
        cache.orgCode = orgCode;
        cache.parentDeptId = parentDeptId;
        cache.attributes = attributes;
        cache.subDepts = subDepts;

        return cache;
    }

    public boolean equals(Object o)
    {
        return this == o || o instanceof DeptCache && deptId.equals(((DeptCache) o).deptId);
    }

    public int hashCode()
    {
        return deptId.hashCode();
    }

    public String toString()
    {
        return deptName;
    }

    public Integer valueOf()
    {
        return deptId;
    }
}
