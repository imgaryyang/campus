package com.gzzm.platform.desktop;

import java.io.Serializable;
import java.util.List;

/**
 * 桌面定义中的一列
 * @author camel
 * @date 2010-6-4
 */
public class DesktopColumn implements Serializable
{
    private static final long serialVersionUID = -6096655734385355308L;

    private List<DesktopModule> modules;

    public DesktopColumn()
    {
    }

    public List<DesktopModule> getModules()
    {
        return modules;
    }

    public void setModules(List<DesktopModule> modules)
    {
        this.modules = modules;
    }
}
