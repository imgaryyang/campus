package com.gzzm.platform.login;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.Value;

/**
 * 证书登录服务项，包含证书类型，证书类型名称，以及对应的CertLoginService
 *
 * @author camel
 * @date 2009-12-28
 */
@SuppressWarnings("NonSerializableFieldInSerializableClass")
public class CertLoginServiceItem implements Value
{
    private static final long serialVersionUID = -8182460299119605053L;

    private String type;

    private String name;

    @NotSerialized
    private CertLoginService service;

    public CertLoginServiceItem()
    {
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public CertLoginService getService()
    {
        return service;
    }

    public void setService(CertLoginService service)
    {
        this.service = service;
    }

    public Object valueOf()
    {
        return type;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
