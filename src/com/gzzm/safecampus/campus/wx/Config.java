package com.gzzm.safecampus.campus.wx;

/**
 * 微信相关配置
 *
 * @author Neo
 * @date 2018/4/8 13:48
 */
public class Config
{
    /**
     * 微信回调域名
     */
    private String domain;

    public Config()
    {
    }

    public String getDomain()
    {
        return domain;
    }

    public void setDomain(String domain)
    {
        this.domain = domain;
    }
}
