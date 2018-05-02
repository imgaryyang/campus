package com.gzzm.platform.organ;

import net.cyan.thunwind.annotation.*;

/**
 * 作用范围和部门的对应关系，定义某个作用范围包括哪些部门
 *
 * @author camel
 * @date 2010-1-31
 */
@Entity(table = "PFROLESCOPEDEPT", keys = {"scopeId", "deptId"})
public class RoleScopeDept
{
    /**
     * 作用范围ID
     */
    private Integer scopeId;

    /**
     * 部门ID，>0表示对应某个部门，<0表示当前某级部门,-1为0级部门,-2为1级部门以此类推，0表示当前部门
     */
    @ColumnDescription(nullable = false, type = "number(7)")
    private Integer deptId;

    /**
     * 是否包括下属部门
     */
    @ColumnDescription(nullable = false, defaultValue = "0")
    private Boolean includeSub;

    /**
     * 是否包括上级部门
     */
    @ColumnDescription(nullable = false, defaultValue = "0")
    private Boolean includeSup;

    /**
     * 是否包括自身
     */
    @ColumnDescription(nullable = false, defaultValue = "1")
    private Boolean includeSelf;

    /**
     * 是否排除
     */
    @ColumnDescription(nullable = false, defaultValue = "0")
    private Boolean excluded;

    /**
     * 对下属部门的过滤条件，仅当includeSub为true时有效
     */
    @ColumnDescription(type = "varchar(500)")
    private String filter;

    /**
     * 是否优先排除此部门
     */
    private Boolean priority;

    public RoleScopeDept()
    {
    }

    public RoleScopeDept(Integer deptId, Boolean includeSub, Boolean includeSup, String filter)
    {
        this.deptId = deptId;
        this.includeSub = includeSub;
        this.includeSup = includeSup;
        this.filter = filter;
    }

    public Integer getScopeId()
    {
        return scopeId;
    }

    public void setScopeId(Integer scopeId)
    {
        this.scopeId = scopeId;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public String getFilter()
    {
        return filter;
    }

    public void setFilter(String filter)
    {
        this.filter = filter;
    }

    public Boolean isIncludeSub()
    {
        return includeSub;
    }

    public void setIncludeSub(Boolean includeSub)
    {
        this.includeSub = includeSub;
    }

    public Boolean isIncludeSup()
    {
        return includeSup;
    }

    public void setIncludeSup(Boolean includeSup)
    {
        this.includeSup = includeSup;
    }

    public Boolean isIncludeSelf()
    {
        return includeSelf;
    }

    public void setIncludeSelf(Boolean includeSelf)
    {
        this.includeSelf = includeSelf;
    }

    public Boolean isExcluded()
    {
        return excluded;
    }

    public void setExcluded(Boolean excluded)
    {
        this.excluded = excluded;
    }

    public Boolean isPriority()
    {
        return priority;
    }

    public void setPriority(Boolean priority)
    {
        this.priority = priority;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof RoleScopeDept))
            return false;

        RoleScopeDept that = (RoleScopeDept) o;

        return deptId.equals(that.deptId) && scopeId.equals(that.scopeId);
    }

    @Override
    public int hashCode()
    {
        return 31 * scopeId.hashCode() + deptId.hashCode();
    }
}
