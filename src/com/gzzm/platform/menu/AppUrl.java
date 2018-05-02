package com.gzzm.platform.menu;

import com.gzzm.platform.commons.Url;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 和功能点关联的url即，在系统中注册了的url
 *
 * @author camel
 * @date 2009-7-20
 */
public class AppUrl
{
    private Url url;

    /**
     * url关联的应用id，已经对应的权限列表
     */
    private Map<String, Collection<String>> appMap = new HashMap<String, Collection<String>>();

    AppUrl(String url, String method)
    {
        this.url = new Url(url, method);
    }

    /**
     * 判断请求和是否匹配
     *
     * @param request http请求
     * @return 匹配返回true，不匹配返回false
     */
    public boolean matches(HttpServletRequest request)
    {
        return url.matches(request);
    }

    void addApp(String appId, String auth)
    {
        Collection<String> auths = appMap.get(appId);
        if (auths == null)
        {
            appMap.put(appId, auths = new ArrayList<String>());
            auths.add(auth);
        }
        else if (!auths.contains(auth))
        {
            auths.add(auth);
        }
    }

    public Map<String, Collection<String>> getAppMap()
    {
        return appMap;
    }
}
