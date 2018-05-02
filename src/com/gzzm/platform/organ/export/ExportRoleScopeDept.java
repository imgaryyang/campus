package com.gzzm.platform.organ.export;

import com.gzzm.platform.organ.RoleScopeDept;

import java.io.Serializable;

/**
 * @author camel
 * @date 13-12-26
 */
public class ExportRoleScopeDept implements Serializable
{
    private static final long serialVersionUID = -7294665530796337471L;

    private Integer deptId;

    private boolean includeSub;

    private boolean includeSup;

    private boolean includeSelf;

    private boolean excluded;

    private String filter;

    public ExportRoleScopeDept()
    {
    }

    public ExportRoleScopeDept(RoleScopeDept roleScopeDept)
    {
        deptId = roleScopeDept.getDeptId();
        includeSub = roleScopeDept.isIncludeSub() != null && roleScopeDept.isIncludeSub();
        includeSup = roleScopeDept.isIncludeSup() != null && roleScopeDept.isIncludeSup();
        includeSelf = roleScopeDept.isIncludeSelf() != null && roleScopeDept.isIncludeSelf();
        excluded = roleScopeDept.isExcluded() != null && roleScopeDept.isExcluded();
        filter = roleScopeDept.getFilter();
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public boolean isIncludeSub()
    {
        return includeSub;
    }

    public void setIncludeSub(boolean includeSub)
    {
        this.includeSub = includeSub;
    }

    public boolean isIncludeSup()
    {
        return includeSup;
    }

    public void setIncludeSup(boolean includeSup)
    {
        this.includeSup = includeSup;
    }

    public boolean isIncludeSelf()
    {
        return includeSelf;
    }

    public void setIncludeSelf(boolean includeSelf)
    {
        this.includeSelf = includeSelf;
    }

    public boolean isExcluded()
    {
        return excluded;
    }

    public void setExcluded(boolean excluded)
    {
        this.excluded = excluded;
    }

    public String getFilter()
    {
        return filter;
    }

    public void setFilter(String filter)
    {
        this.filter = filter;
    }
}
