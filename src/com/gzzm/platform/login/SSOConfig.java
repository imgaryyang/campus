package com.gzzm.platform.login;

import net.cyan.nest.annotation.Injectable;

import java.util.Map;

/**
 * sso配置信息，配置到哪里检查sso合法性
 *
 * @author camel
 * @date 2016/7/5
 */
@Injectable(singleton = true)
public class SSOConfig
{
    /**
     * key是服务器的名称，value是服务器对应的URl，sso登录时对方传过来服务器的名称，查找对应的url去服务器验证登录是否合法
     */
    private Map<String, String> items;

    public SSOConfig()
    {
    }

    public Map<String, String> getItems()
    {
        return items;
    }

    public void setItems(Map<String, String> items)
    {
        this.items = items;
    }

    public String getUrl(String name)
    {
        return items.get(name);
    }
}
