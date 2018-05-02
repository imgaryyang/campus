package com.gzzm.platform.group;

import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.*;
import net.cyan.nest.annotation.Inject;

/**
 * 用户选择界面
 *
 * @author camel
 * @date 13-11-21
 */
@Service
public class UserSelectPage
{
    @Inject
    private OrganDao dao;

    /**
     * 已选的用户
     */
    private Integer[] selected;

    private PageUserSelector userSelector;

    private String appId = Member.USER_SELECT_APP;

    public UserSelectPage()
    {
    }

    public Integer[] getSelected()
    {
        return selected;
    }

    public void setSelected(Integer[] selected)
    {
        this.selected = selected;
    }

    public String getAppId()
    {
        return appId;
    }

    public void setAppId(String appId)
    {
        this.appId = appId;
    }

    public PageUserSelector getUserSelector()
    {
        if (userSelector == null)
        {
            userSelector = new PageUserSelector();
            userSelector.setAppId(appId);
        }
        return userSelector;
    }

    @NotSerialized
    public User[] getSelectedUsers() throws Exception
    {
        if (selected != null)
        {
            User[] users = new User[selected.length];
            for (int i = 0; i < selected.length; i++)
            {
                users[i] = dao.getUser(selected[i]);
            }

            return users;
        }
        else
        {
            return null;
        }
    }

    @Service(url = "/userselect")
    public String showPage()
    {
        return "userselect";
    }
}
