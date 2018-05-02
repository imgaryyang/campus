package com.gzzm.platform.login;

import java.io.Serializable;

/**
 * 用户登录时需要获取的证书信息
 *
 * @author camel
 * @date 2009-7-23
 */
public class CertUserInfo implements Serializable
{
    private static final long serialVersionUID = -4158238324216611566L;

    /**
     * 证书id，一个证书中允许解析出多个证书id，以兼容历史原因使用不同的证书字段作为唯一标识的问题
     */
    private String[] certIds;

    /**
     * 证书名称
     */
    private String certName;

    public CertUserInfo()
    {
    }

    public CertUserInfo(String certId, String certName)
    {
        this.certIds = new String[]{certId};
        this.certName = certName;
    }


    public CertUserInfo(String[] certIds, String certName)
    {
        this.certIds = certIds;
        this.certName = certName;
    }


    public String[] getCertIds()
    {
        return certIds;
    }

    public String getCertName()
    {
        return certName;
    }

    public void setCertIds(String[] certIds)
    {
        this.certIds = certIds;
    }

    public void setCertName(String certName)
    {
        this.certName = certName;
    }
}
