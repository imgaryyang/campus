package com.gzzm.platform.organ.export;

import com.gzzm.platform.menu.*;

import java.io.Serializable;
import java.util.*;

/**
 * @author camel
 * @date 13-12-26
 */
public class ExportMenuAuth implements Serializable
{
    private static final long serialVersionUID = 79535553100007007L;

    private String authCode;

    private String authName;

    private List<ExportMenuAuthUrl> urls;

    public ExportMenuAuth()
    {
    }

    public ExportMenuAuth(MenuAuth auth)
    {
        authCode = auth.getAuthCode();
        authName = auth.getAuthName();

        urls = new ArrayList<ExportMenuAuthUrl>();

        for (MenuAuthUrl authUrl : auth.getAuthUrls())
        {
            urls.add(new ExportMenuAuthUrl(authUrl));
        }
    }

    public String getAuthCode()
    {
        return authCode;
    }

    public void setAuthCode(String authCode)
    {
        this.authCode = authCode;
    }

    public String getAuthName()
    {
        return authName;
    }

    public void setAuthName(String authName)
    {
        this.authName = authName;
    }

    public List<ExportMenuAuthUrl> getUrls()
    {
        return urls;
    }

    public void setUrls(List<ExportMenuAuthUrl> urls)
    {
        this.urls = urls;
    }
}
