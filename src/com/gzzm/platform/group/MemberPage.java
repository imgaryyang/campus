package com.gzzm.platform.group;

import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.menu.*;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 11-9-23
 */
@Service
public class MemberPage
{
    @Inject
    private static Provider<MenuContainer> menuContainer;

    @Inject
    private DeptService deptService;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    private Integer deptId;

    private Integer scopeId;

    private String scopeName;

    private String app;

    private MemberType[] type;

    public MemberPage()
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

    public Integer getScopeId()
    {
        return scopeId;
    }

    public void setScopeId(Integer scopeId)
    {
        this.scopeId = scopeId;
    }

    public String getScopeName()
    {
        return scopeName;
    }

    public void setScopeName(String scopeName)
    {
        this.scopeName = scopeName;
    }

    public String getApp()
    {
        return app;
    }

    public void setApp(String app)
    {
        this.app = app;
    }

    public MemberType[] getType()
    {
        return type;
    }

    public void setType(MemberType[] type)
    {
        this.type = type;
    }

    @NotSerialized
    public boolean isCustom()
    {
        if (type == null)
            return true;

        for (MemberType type : this.type)
        {
            if (type == MemberType.custom)
                return true;
        }

        return false;
    }

    public boolean containsType(String type)
    {
        return containsType(MemberType.valueOf(type));
    }

    public boolean containsType(MemberType type)
    {
        if (type == null)
            return true;

        for (MemberType type1 : getType())
        {
            if (type1 == type)
                return true;
        }

        return false;
    }

    public boolean isAddDeptGroupable()
    {
        if (deptId != null && containsType(MemberType.dept) || containsType(MemberType.deptgroup))
        {
            if (userOnlineInfo.isAdmin())
                return true;

            MenuItem menuItem = menuContainer.get().getMenuByUrl("/DeptGroup");

            if (menuItem != null)
            {
                AppInfo appInfo = userOnlineInfo.getApp(menuItem.getMenuId());
                if (appInfo != null && appInfo.getDeptIds() != null && appInfo.getDeptIds().contains(deptId))
                    return true;
            }
        }

        return false;
    }

    public boolean isAddUserDeptGroupable()
    {
        return containsType(MemberType.user);
    }

    /**
     * 转到选择接收者的页面
     *
     * @return 转向选择接收者的页面
     */
    @Service(url = "/member/select")
    @Forward(page = "/platform/group/select.ptl")
    public String showPage() throws Exception
    {
        if (scopeId == null && scopeName != null && deptId != null)
        {
            DeptInfo deptInfo = deptService.getDept(deptId);
            while (deptInfo != null)
            {
                RoleScope scope = deptService.getDao().getRoleScopeByName(deptInfo.getDeptId(), scopeName);
                if (scope != null)
                {
                    scopeId = scope.getScopeId();
                    break;
                }

                deptInfo = deptInfo.parentDept();
            }
        }

        return null;
    }
}
