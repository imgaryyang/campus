package com.gzzm.safecampus.wx.common;

import net.cyan.nest.annotation.Injectable;

import java.util.Set;

/**
 * 微信端的配置类
 *
 * @author Neo
 * @date 2018/3/27 18:13
 */
@Injectable
public class WxConfig
{
    private Set<String> excludeUrls;

    public WxConfig()
    {
    }

    public Set<String> getExcludeUrls()
    {
        return excludeUrls;
    }

    public void setExcludeUrls(Set<String> excludeUrls)
    {
        this.excludeUrls = excludeUrls;
    }
}
