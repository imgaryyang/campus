package com.gzzm.ods.flow;

import com.gzzm.platform.organ.Dept;
import net.cyan.thunwind.annotation.*;

/**
 * 记录每个部门用过哪些简称
 *
 * @author camel
 * @date 13-11-13
 */
@Entity(table = "ODSIMPLENAME", keys = "simpleNameId")
public class SimpleName
{
    @Generatable(length = 5)
    private Integer simpleNameId;

    private Integer deptId;

    private Dept dept;

    @ColumnDescription(type = "varchar(50)")
    private String simpleName;

    public SimpleName()
    {
    }

    public Integer getSimpleNameId()
    {
        return simpleNameId;
    }

    public void setSimpleNameId(Integer simpleNameId)
    {
        this.simpleNameId = simpleNameId;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public String getSimpleName()
    {
        return simpleName;
    }

    public void setSimpleName(String simpleName)
    {
        this.simpleName = simpleName;
    }

    public Dept getDept()
    {
        return dept;
    }

    public void setDept(Dept dept)
    {
        this.dept = dept;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof SimpleName))
            return false;

        SimpleName that = (SimpleName) o;

        return simpleNameId.equals(that.simpleNameId);
    }

    @Override
    public int hashCode()
    {
        return simpleNameId.hashCode();
    }
}
