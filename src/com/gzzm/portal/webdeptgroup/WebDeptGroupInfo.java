package com.gzzm.portal.webdeptgroup;

import com.gzzm.portal.commons.ListItem;

/**
 * @author Xrd
 * @date 2018/3/20 17:28
 */
public class WebDeptGroupInfo implements ListItem
{
    private WebDeptGroup webDeptGroup;

    public WebDeptGroupInfo(WebDeptGroup webDeptGroup)
    {
        this.webDeptGroup = webDeptGroup;
    }

    public String getTitle()
    {
        return null;
    }

    public String getUrl()
    {
        return null;
    }

    public String getTarget()
    {
        return null;
    }

    public String getPhoto()
    {
        return null;
    }

    public Integer getDeptId()
    {
        return webDeptGroup.getDeptId();
    }

    public String getDeptName()
    {
        return webDeptGroup.getDept().getDeptName();
    }

}
