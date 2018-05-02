package com.gzzm.portal.commons;

import net.cyan.commons.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 门户的公共方法类
 *
 * @author lfx
 * @date 2018/4/10 0010 10:03
 */
public class PortalUtils
{
    public PortalUtils()
    {
    }

    public static String getIp(HttpServletRequest request)
    {
        String ip = request.getHeader("X-ClientIP");
        if (StringUtils.isEmpty(ip))
        {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
