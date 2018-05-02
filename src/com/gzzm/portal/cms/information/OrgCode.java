package com.gzzm.portal.cms.information;

import com.gzzm.platform.organ.Dept;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.Entity;

/**
 * 组织机构代码表
 *
 * @author camel
 * @date 2016/6/3
 */
@Entity(table = "PLROGCODE", keys = "deptId")
public class OrgCode
{
    private Integer deptId;

    @NotSerialized
    private Dept dept;

    private String orgCode;

    public OrgCode()
    {
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Dept getDept()
    {
        return dept;
    }

    public void setDept(Dept dept)
    {
        this.dept = dept;
    }

    public String getOrgCode()
    {
        return orgCode;
    }

    public void setOrgCode(String orgCode)
    {
        this.orgCode = orgCode;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof OrgCode))
            return false;

        OrgCode orgCode = (OrgCode) o;

        return deptId.equals(orgCode.deptId);
    }

    @Override
    public int hashCode()
    {
        return deptId.hashCode();
    }
}
