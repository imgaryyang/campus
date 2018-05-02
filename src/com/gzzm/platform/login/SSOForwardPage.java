package com.gzzm.platform.login;

import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.StringUtils;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 2016/7/6
 */
@Service
public class SSOForwardPage
{
    @Inject
    private UserOnlineInfo userOnlineInfo;

    @Inject
    private UserCheckService userCheckService;

    private String server;

    private String path;

    private String index;

    private String menuId;

    public SSOForwardPage()
    {
    }

    public String getServer()
    {
        return server;
    }

    public void setServer(String server)
    {
        this.server = server;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getIndex()
    {
        return index;
    }

    public void setIndex(String index)
    {
        this.index = index;
    }

    public String getMenuId()
    {
        return menuId;
    }

    public void setMenuId(String menuId)
    {
        this.menuId = menuId;
    }

    @Service(url = "/sso/forward")
    @Redirect
    public String forward()
    {
        String s = "http://" + server + "/sso/login?path=" + path;

        s += "&uid=" + userOnlineInfo.getUserId() + "&did=" + userOnlineInfo.getDeptId();

        if (!StringUtils.isEmpty(menuId))
            s += "&menuId=" + menuId;

        if (!StringUtils.isEmpty(index))
            s += "&index=" + index;

        return s;
    }
}
