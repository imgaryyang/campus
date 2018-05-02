package com.gzzm.oa.version;

import net.cyan.thunwind.annotation.*;

/**
 * @author xzb
 * @date 2017-8-1
 */
@Entity(table = "OAVERSIONPUSHUSER", keys = {"userId", "project"})
public class VersionPushUser
{
    /**
     * 用户id，长度为9,前2位为系统id，后7位为序列号
     */
    private Integer userId;

    /**
     * 系统
     */
    @ColumnDescription(type = "varchar(50)")
    private ProjectDir project;

    public VersionPushUser()
    {
    }

    public VersionPushUser(Integer userId, ProjectDir project)
    {
        this.userId = userId;
        this.project = project;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public ProjectDir getProject()
    {
        return project;
    }

    public void setProject(ProjectDir project)
    {
        this.project = project;
    }
}
