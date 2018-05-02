package com.gzzm.platform.organ;

import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author camel
 * @date 2015/10/15
 */
public class ScopeDepts
{
    @Inject
    private static Provider<DeptService> deptServiceProvider;

    private Integer deptId;

    private String scopeName;

    private Integer scopeId;

    public ScopeDepts()
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

    public String getScopeName()
    {
        return scopeName;
    }

    public void setScopeName(String scopeName)
    {
        this.scopeName = scopeName;
    }

    public Integer getScopeId()
    {
        return scopeId;
    }

    public void setScopeId(Integer scopeId)
    {
        this.scopeId = scopeId;
    }

    public List<? extends DeptInfo> getDepts() throws Exception
    {
        Integer deptId = this.deptId;
        if (deptId == null)
            deptId = 1;

        DeptService deptService = deptServiceProvider.get();

        Integer scopeId = this.scopeId;
        if (scopeId == null)
        {
            if (StringUtils.isEmpty(scopeName))
                throw new IllegalArgumentException("missing scopeId or scopeName");

            OrganDao dao = deptService.getDao();

            DeptInfo deptInfo = deptService.getDept(deptId);
            while (deptInfo != null)
            {
                RoleScope scope = dao.getRoleScopeByName(deptInfo.getDeptId(), scopeName);
                if (scope != null)
                {
                    scopeId = scope.getScopeId();
                    break;
                }

                deptInfo = deptInfo.parentDept();
            }
        }

        if (scopeId == null)
            return Collections.emptyList();

        Collection<Integer> deptIds = deptService.getDeptIdsByScopes(deptId, Collections.singleton(scopeId));

        return deptService.getDao().getDepts(deptIds);
    }
}
