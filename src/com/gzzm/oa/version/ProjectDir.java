package com.gzzm.oa.version;

import net.cyan.commons.util.AdvancedEnum;

/**
 * @author xzb
 * @date 2017-8-1
 */
public class ProjectDir implements AdvancedEnum<String>
{
    private String projectCode;

    private String projectName;

    public ProjectDir()
    {
    }

    public String getProjectCode()
    {
        return projectCode;
    }

    public void setProjectCode(String projectCode)
    {
        this.projectCode = projectCode;
    }

    public String getProjectName()
    {
        return projectName;
    }

    public void setProjectName(String projectName)
    {
        this.projectName = projectName;
    }

    public String valueOf()
    {
        return projectCode;
    }

    @Override
    public String toString()
    {
        return projectName;
    }
}
