package com.gzzm.platform.form;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.valmiki.form.FormRole;

import java.util.*;

/**
 * @author camel
 * @date 2014/6/25
 */
public class FormRoleInfo
{
    @Unique
    @Require
    private String roleName;

    private FormRole role;

    private Map<String, Set<String>> pageAuths;

    private Map<String, Map<String, Set<String>>> componentAuths;

    public FormRoleInfo()
    {
    }

    public FormRoleInfo(String roleName, FormRole role)
    {
        this.roleName = roleName;
        this.role = role;

        init();
    }

    private void init()
    {
        pageAuths = new HashMap<String, Set<String>>();
        componentAuths = new HashMap<String, Map<String, Set<String>>>();

        for (Map.Entry<String, Set<String>> entry : role.getAuthorities().entrySet())
        {
            String name = entry.getKey();
            Set<String> auths = entry.getValue();
            int index = name.indexOf('.');
            if (index > 0)
            {
                String pageName = name.substring(0, index);
                String componentName = name.substring(index + 1);

                Map<String, Set<String>> map = componentAuths.get(pageName);
                if (map == null)
                {
                    componentAuths.put(pageName, map = new HashMap<String, Set<String>>());
                }

                map.put(componentName, auths);
            }
            else
            {
                pageAuths.put(name, auths);
            }
        }
    }

    public void fill()
    {
        role = new FormRole();

        if (pageAuths != null)
        {
            for (Map.Entry<String, Set<String>> entry : pageAuths.entrySet())
            {
                role.setAuthority(entry.getKey(), entry.getValue());
            }
        }

        if (componentAuths != null)
        {
            for (Map.Entry<String, Map<String, Set<String>>> entry : componentAuths.entrySet())
            {
                String pageName = entry.getKey();

                for (Map.Entry<String, Set<String>> entry2 : entry.getValue().entrySet())
                {
                    role.setAuthority(pageName + "." + entry2.getKey(), entry2.getValue());
                }
            }
        }
    }

    public String getRoleName()
    {
        return roleName;
    }

    public void setRoleName(String roleName)
    {
        this.roleName = roleName;
    }

    public Map<String, Set<String>> getPageAuths()
    {
        return pageAuths;
    }

    public void setPageAuths(Map<String, Set<String>> pageAuths)
    {
        this.pageAuths = pageAuths;
    }

    public Map<String, Map<String, Set<String>>> getComponentAuths()
    {
        return componentAuths;
    }

    public void setComponentAuths(Map<String, Map<String, Set<String>>> componentAuths)
    {
        this.componentAuths = componentAuths;
    }

    @NotSerialized
    public FormRole getRole()
    {
        return role;
    }

    public void setRole(FormRole role)
    {
        this.role = role;
        init();
    }
}
